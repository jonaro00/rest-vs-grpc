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

/// The number of seconds to test each step to wards finding optimal thread count
const DISCOVERY_SECONDS: usize = 10;

/// The number of seconds to run the test for
const TEST_SECONDS: usize = 100;

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
            .run()
            .await;
        }
        Mode::Grpc => {
            GrpcSpammer {
                size,
                client: InventoryClient::connect(addr).await?,
            }
            .run()
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
    /// Run a stress test by spawning clients with `self.spam`,
    /// counting the completed requests. The number of clients
    /// adjust to find the optimal throughput.
    async fn run(&self) {
        let mut clients = 1;
        let mut best_avg = 0.0;
        let mut spammers = Vec::new();
        loop {
            println!("Trying with {} clients...", clients);
            let start = Instant::now();
            let end = start
                .checked_add(Duration::from_secs(DISCOVERY_SECONDS as u64))
                .unwrap()
                .into();

            // Adjust spammer count
            let (tx, mut rx) = channel::<usize>(4096);
            for _ in 0..clients {
                spammers.push(spawn(self.clone().spam(start, tx.clone())));
            }

            // Collect requests until time is up
            let mut arr = [0i32; DISCOVERY_SECONDS];
            while let Some(sec) = select! {
                sec = rx.recv() => sec,
                _ = sleep_until(end) => None,
            } {
                if sec < DISCOVERY_SECONDS {
                    arr[sec] += 1;
                }
            }

            // stop spammers
            for s in &spammers {
                s.abort();
            }
            spammers.clear();

            let avg = f64::from(arr.iter().sum::<i32>()) / f64::from(arr.len() as u32);
            println!("{arr:?} avg {avg}");

            if avg > 1.02 * best_avg {
                best_avg = avg;
                clients *= 2;
            } else {
                clients /= 2;
                println!("Choosing {} clients.", clients);
                break;
            }

            sleep(Duration::from_secs(5)).await;
        }

        // Run final test
        println!("Running test with {} clients...", clients);
        let start = Instant::now();
        let end = start
            .checked_add(Duration::from_secs(TEST_SECONDS as u64))
            .unwrap()
            .into();

        let (tx, mut rx) = channel::<usize>(4096);
        for _ in 0..clients {
            spammers.push(spawn(self.clone().spam(start, tx.clone())));
        }
        let mut arr = [0i32; TEST_SECONDS];
        while let Some(sec) = select! {
            sec = rx.recv() => sec,
            _ = sleep_until(end) => None,
        } {
            if sec < TEST_SECONDS {
                arr[sec] += 1;
            }
        }
        for s in &spammers {
            s.abort();
        }
        spammers.clear();
        let avg = f64::from(arr.iter().sum::<i32>()) / f64::from(arr.len() as u32);
        println!("{arr:?}, {avg}");
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
            tx.send(diff).await?;
        }
    }
}
