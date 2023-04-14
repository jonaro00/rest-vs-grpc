/// The number of seconds to run the test for
const SECONDS: usize = 10;

enum Mode {
    Rest,
    GRPC,
}
#[derive(Clone, Copy)]
pub enum PayloadSize {
    XS,
    S,
    M,
    L,
}

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let mut args = std::env::args().skip(1);
    let mode = args
        .next()
        .map(|m| match m.as_str() {
            "rest" => Mode::Rest,
            "grpc" => Mode::GRPC,
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

    let addr = "http://127.0.0.1:1337";

    let arr = match mode {
        Mode::Rest => rest::run(addr, size).await?,
        Mode::GRPC => grpc::run(addr, size).await?,
    };

    println!("{arr:?}");

    Ok(())
}

mod rest {
    use std::time::Instant;

    use reqwest::Client;
    use serde::{Deserialize, Serialize};
    use tokio::task::JoinSet;

    use crate::{PayloadSize, SECONDS};

    #[derive(Debug, Serialize, Deserialize)]
    struct HelloRequest {
        name: String,
    }
    #[derive(Debug, Serialize, Deserialize)]
    struct HelloResponse {
        greeting: String,
    }

    pub async fn run(
        addr: &str,
        size: PayloadSize,
    ) -> Result<[i32; SECONDS], Box<dyn std::error::Error>> {
        let client = Client::builder().http1_only().build()?;
        let addr = format!(
            "{}{}",
            addr,
            match size {
                PayloadSize::XS => "/heart_beat",
                PayloadSize::S => "/items_status",
                PayloadSize::M => "/items_summary",
                PayloadSize::L => "/items_full",
            }
        );
        let start = std::time::Instant::now();
        let n = 128;
        let mut set = JoinSet::new();
        for _ in 0..n {
            set.spawn(spam(client.clone(), start, addr.clone()));
        }
        let mut results = Vec::with_capacity(n);
        while let Some(res) = set.join_next().await {
            results.push(res.unwrap());
        }
        let mut counts = [0i32; SECONDS];
        for r in results {
            for (i, n) in r.into_iter().enumerate() {
                counts[i] += n;
            }
        }
        Ok(counts)
    }

    /// Sends requests sequentially for N seconds.
    /// Returns array with number of requests completed each second.
    async fn spam(client: Client, start: Instant, endpoint: String) -> [i32; SECONDS] {
        let mut arr = [0i32; SECONDS];
        loop {
            let _res = client.get(&endpoint).send().await.unwrap();
            // println!("Got response {:?}", res);
            let now = std::time::Instant::now();
            let diff = now.duration_since(start).as_secs() as usize;
            if diff >= arr.len() {
                break;
            }
            arr[diff] += 1;
        }
        arr
    }
}

mod grpc {
    use std::time::Instant;

    use proto::inventory::{inventory_client::InventoryClient, Empty};
    use tokio::task::JoinSet;
    use tonic::{transport::Channel, Request};

    use crate::{PayloadSize, SECONDS};

    pub async fn run(
        addr: &'static str,
        size: PayloadSize,
    ) -> Result<[i32; SECONDS], Box<dyn std::error::Error>> {
        let client = InventoryClient::connect(addr).await?;
        let start = std::time::Instant::now();
        let n = 128;
        let mut set = JoinSet::new();
        for _ in 0..n {
            set.spawn(spam(client.clone(), start, size));
        }
        let mut results = Vec::with_capacity(n);
        while let Some(res) = set.join_next().await {
            results.push(res.unwrap());
        }
        let mut counts = [0i32; SECONDS];
        for r in results {
            for (i, n) in r.into_iter().enumerate() {
                counts[i] += n;
            }
        }
        Ok(counts)
    }

    /// Sends requests sequentially for N seconds.
    /// Returns array with number of requests completed each second.
    async fn spam(
        mut client: InventoryClient<Channel>,
        start: Instant,
        size: PayloadSize,
    ) -> [i32; SECONDS] {
        let mut arr = [0i32; SECONDS];
        loop {
            let req = Request::new(Empty {});
            match size {
                PayloadSize::XS => {
                    client.heart_beat(req).await.unwrap();
                    ()
                },
                PayloadSize::S => {
                    client.items_status(req).await.unwrap();
                    ()
                },
                PayloadSize::M => {
                    client.items_summary(req).await.unwrap();
                    ()
                },
                PayloadSize::L => {
                    client.items_full(req).await.unwrap();
                    ()
                },
            };
            let now = std::time::Instant::now();
            let diff = now.duration_since(start).as_secs() as usize;
            if diff >= arr.len() {
                break;
            }
            arr[diff] += 1;
        }
        arr
    }
}
