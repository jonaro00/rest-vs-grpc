use std::time::{Duration, Instant};

use anyhow::Result;
use async_trait::async_trait;
use proto::inventory::{inventory_client::InventoryClient, Empty};
use reqwest::Client;
use tokio::{
    select, spawn,
    sync::mpsc::{channel, Sender},
    time::{sleep, sleep_until},
};
use tonic::{transport::Channel, Request};

/// The number of seconds to test each step when finding optimal thread count
const DISCOVERY_SECONDS: usize = 20;

/// The number of seconds to run the final test for
const TEST_SECONDS: usize = 100;

/// The number of seconds to run spammers before measuring anything
const WARMUP_SECONDS: usize = 1;

#[tokio::main]
async fn main() -> Result<()> {
    let mut args = std::env::args().skip(1);
    let mode = args
        .next()
        .map(|m| match m.as_str() {
            "rest" => Mode::Rest,
            "grpc" => Mode::Grpc,
            _ => panic!("provide 'rest' or 'grpc'"),
        })
        .unwrap();
    let size = args
        .next()
        .map(|s| match s.as_str() {
            "xs" => PayloadSize::XS,
            "s" => PayloadSize::S,
            "m" => PayloadSize::M,
            "l" => PayloadSize::L,
            _ => panic!("provide 'xs' or 's' or 'm' or 'l'"),
        })
        .unwrap();
    let host = args.next().unwrap_or("127.0.0.1".into());
    let port = args.next().unwrap_or("1337".into());

    let addr = format!("http://{host}:{port}");

    match mode {
        Mode::Rest => {
            RestSpammer {
                addr,
                size,
                client: Client::builder().http1_only().build()?,
            }
            .run_full_test()
            .await;
        }
        Mode::Grpc => {
            GrpcSpammer {
                size,
                client: InventoryClient::connect(addr).await?,
            }
            .run_full_test()
            .await;
        }
    };

    Ok(())
}

enum Mode {
    Rest,
    Grpc,
}

#[derive(Clone, Copy)]
enum PayloadSize {
    XS,
    S,
    M,
    L,
}

#[async_trait]
trait RequestSpammer: Clone + 'static {
    /// Runs small stress tests to find the optimal throughput.
    /// Then runs a large stress test.
    async fn run_full_test(&self) {
        let mut clients = 2;

        // Increase number of clients until throughput flattens out
        let mut best_avg = 0.0;
        loop {
            println!("Trying with {} clients...", clients);
            let (vec, avg) = self.run_one_test(clients, DISCOVERY_SECONDS).await;

            println!("{vec:?} avg {avg}");

            if avg > 1.02 * best_avg {
                best_avg = avg;
                clients *= 2;
            } else {
                if avg <= best_avg {
                    clients /= 2;
                }
                println!("Choosing {} clients.", clients);
                break;
            }

            sleep(Duration::from_secs(3)).await;
        }

        // Run final test
        println!("Running test with {} clients...", clients);
        let (vec, avg) = self.run_one_test(clients, TEST_SECONDS).await;

        println!("{vec:?} avg {avg}");
    }

    /// Run a stress test by spawning clients with `self.spam`,
    /// counting the completed requests. Returns array with number
    /// of completed requests per second and the average.
    async fn run_one_test(&self, clients: usize, seconds: usize) -> (Vec<i32>, f64) {
        let mut spammers = Vec::with_capacity(clients);
        let start = Instant::now();
        let end = start
            .checked_add(Duration::from_secs((seconds + WARMUP_SECONDS) as u64))
            .unwrap()
            .into();

        // Adjust spammer count
        let (tx, mut rx) = channel::<usize>(4096);
        for _ in 0..clients {
            spammers.push(spawn(self.clone().spam(start, tx.clone())));
        }

        // Collect requests until time is up
        let mut vec = vec![0i32; seconds];
        while let Some(sec) = select! {
            sec = rx.recv() => sec,
            _ = sleep_until(end) => None,
        } {
            if sec >= WARMUP_SECONDS && sec - WARMUP_SECONDS < seconds {
                vec[sec - WARMUP_SECONDS] += 1;
            }
        }

        // stop spammers
        for s in &spammers {
            s.abort();
        }
        spammers.clear();

        let avg = f64::from(vec.iter().sum::<i32>()) / f64::from(vec.len() as u32);

        (vec, avg)
    }

    /// Sends requests sequentially in an infinite loop.
    async fn spam(self, start: Instant, tx: Sender<usize>) -> Result<()>;
}

#[derive(Clone)]
struct RestSpammer {
    addr: String,
    size: PayloadSize,
    client: Client,
}

#[async_trait]
impl RequestSpammer for RestSpammer {
    async fn spam(mut self, start: Instant, tx: Sender<usize>) -> Result<()> {
        let endpoint = format!(
            "{}{}",
            self.addr,
            match self.size {
                PayloadSize::XS => "/heart_beat",
                PayloadSize::S => "/items_status",
                PayloadSize::M => "/items_summary",
                PayloadSize::L => "/items_full",
            }
        );
        loop {
            if let Ok(res) = self.client.get(&endpoint).send().await {
                // println!("Got response {:?}", res);
                // Consume the body
                if res.bytes().await.is_err() {
                    eprintln!("Request failed");
                } else {
                    let now = Instant::now();
                    let diff = now.duration_since(start).as_secs() as usize;
                    tx.send(diff).await.unwrap();
                };
            } else {
                eprintln!("Request failed");
            }
        }
    }
}

#[derive(Clone)]
struct GrpcSpammer {
    size: PayloadSize,
    client: InventoryClient<Channel>,
}

#[async_trait]
impl RequestSpammer for GrpcSpammer {
    async fn spam(mut self, start: Instant, tx: Sender<usize>) -> Result<()> {
        loop {
            let req = Request::new(Empty {});
            let success = match self.size {
                PayloadSize::XS => {
                    if self.client.heart_beat(req).await.is_err() {
                        eprintln!("Request failed");
                        false
                    } else {
                        true
                    }
                }
                PayloadSize::S => {
                    if self.client.items_status(req).await.is_err() {
                        eprintln!("Request failed");
                        false
                    } else {
                        true
                    }
                }
                PayloadSize::M => {
                    if self.client.items_summary(req).await.is_err() {
                        eprintln!("Request failed");
                        false
                    } else {
                        true
                    }
                }
                PayloadSize::L => {
                    if self.client.items_full(req).await.is_err() {
                        eprintln!("Request failed");
                        false
                    } else {
                        true
                    }
                }
            };
            if !success {
                continue;
            }
            let now = Instant::now();
            let diff = now.duration_since(start).as_secs() as usize;
            tx.send(diff).await.unwrap();
        }
    }
}
