use std::time::{Duration, Instant};

use anyhow::Result;
use async_trait::async_trait;
use proto::inventory::inventory_client::InventoryClient;
use reqwest::Client;
use tokio::{
    select, spawn,
    sync::mpsc::{channel, Sender},
    time::{sleep, sleep_until},
};

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

    let arr = match mode {
        Mode::Rest => {
            rest::RestSpammer {
                addr,
                size,
                client: Client::builder().http1_only().build()?,
            }
            .run()
            .await?
        }
        Mode::Grpc => {
            grpc::GrpcSpammer {
                size,
                client: InventoryClient::connect(addr).await?,
            }
            .run()
            .await?
        }
    };

    println!("{arr:?}");

    Ok(())
}

enum Mode {
    Rest,
    Grpc,
}

#[derive(Clone, Copy)]
pub enum PayloadSize {
    XS,
    S,
    M,
    L,
}

enum AdjustmentStrategy {
    Exponential,
    BinarySearch,
    Stopped,
}

#[async_trait]
trait RequestSpammer: Clone + Send + Sync + 'static {
    /// Run a stress test by spawning clients with `self.spam`,
    /// counting the completed requests. The number of clients
    /// adjust to find the optimal throughput.
    async fn run(&self) -> Result<[i32; DISCOVERY_SECONDS]> {
        let mut clients = 1;
        let mut best_avg = 0.0;
        let mut strategy = AdjustmentStrategy::Exponential;
        let mut step_size = 1;
        let mut spammers = Vec::with_capacity(128);
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
            let avg = f64::from(arr.iter().sum::<i32>()) / f64::from(arr.len() as u32);
            println!("{arr:?}, {avg}");

            // stop spammers
            for s in &spammers {
                s.abort();
            }
            spammers.clear();

            match strategy {
                AdjustmentStrategy::Exponential => {
                    if avg > best_avg {
                        best_avg = avg;
                        step_size = clients;
                        clients *= 2;
                    } else {
                        strategy = AdjustmentStrategy::BinarySearch;
                        step_size /= 2;
                        clients -= step_size;
                    }
                }
                AdjustmentStrategy::BinarySearch => {
                    step_size /= 2;
                    if avg > best_avg && step_size > 0 {
                        best_avg = avg;
                        clients += step_size;
                    } else if step_size > 0 {
                        clients -= step_size;
                    } else {
                        strategy = AdjustmentStrategy::Stopped;
                        clients = 1.max(clients - 1);
                    }
                }
                _ => panic!(),
            }
            if matches!(strategy, AdjustmentStrategy::Stopped) {
                println!("Choosing {} clients.", clients);
                break;
            }

            sleep(Duration::from_secs(5)).await;
        }
        let arr = [0i32; DISCOVERY_SECONDS];
        Ok(arr)
    }

    /// Sends requests sequentially in an infinite loop.
    async fn spam(self, start: Instant, tx: Sender<usize>) -> Result<()>;
}

mod rest {
    use std::time::Instant;

    use anyhow::Result;
    use async_trait::async_trait;
    use reqwest::Client;
    use tokio::sync::mpsc::Sender;

    use crate::{PayloadSize, RequestSpammer};

    #[derive(Clone)]
    pub struct RestSpammer {
        pub addr: String,
        pub size: PayloadSize,
        pub client: Client,
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
}

mod grpc {
    use std::time::Instant;

    use anyhow::Result;
    use async_trait::async_trait;
    use proto::inventory::{inventory_client::InventoryClient, Empty};
    use tokio::sync::mpsc::Sender;
    use tonic::{transport::Channel, Request};

    use crate::{PayloadSize, RequestSpammer};

    #[derive(Clone)]
    pub struct GrpcSpammer {
        pub size: PayloadSize,
        pub client: InventoryClient<Channel>,
    }

    #[async_trait]
    impl RequestSpammer for GrpcSpammer {
        async fn spam(mut self, start: Instant, tx: Sender<usize>) -> Result<()> {
            loop {
                let req = Request::new(Empty {});
                match self.size {
                    PayloadSize::XS => {
                        self.client.heart_beat(req).await.unwrap();
                    }
                    PayloadSize::S => {
                        self.client.items_status(req).await.unwrap();
                    }
                    PayloadSize::M => {
                        self.client.items_summary(req).await.unwrap();
                    }
                    PayloadSize::L => {
                        self.client.items_full(req).await.unwrap();
                    }
                };
                let now = Instant::now();
                let diff = now.duration_since(start).as_secs() as usize;
                tx.send(diff).await?;
            }
        }
    }
}
