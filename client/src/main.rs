/// The number of seconds to run the test for
const SECONDS: usize = 10;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let mut args = std::env::args().skip(1);
    let mode = args.next();

    let addr = "http://127.0.0.1:1337";

    let arr = if mode == Some("rest".into()) {
        rest::run(addr).await?
    } else if mode == Some("grpc".into()) {
        grpc::run(addr).await?
    } else {
        panic!("provide rest or grpc")
    };

    println!("{arr:?}");

    Ok(())
}

mod rest {
    use std::time::Instant;

    use reqwest::Client;
    use serde::{Deserialize, Serialize};
    use tokio::task::JoinSet;

    use crate::SECONDS;

    #[derive(Debug, Serialize, Deserialize)]
    struct HelloRequest {
        name: String,
    }
    #[derive(Debug, Serialize, Deserialize)]
    struct HelloResponse {
        greeting: String,
    }

    pub async fn run(addr: &'static str) -> Result<[i32; SECONDS], Box<dyn std::error::Error>> {
        let client = Client::builder().http1_only().build()?;
        let start = std::time::Instant::now();
        let n = 128;
        let mut set = JoinSet::new();
        for _ in 0..n {
            set.spawn(spam(client.clone(), start, addr));
        }
        let mut results = Vec::with_capacity(n);
        while let Some(res) = set.join_next().await {
            results.push(res.unwrap());
        }
        let mut counts = [0i32; SECONDS];
        for r in results {
            for (i,n) in r.into_iter().enumerate() {
                counts[i] += n;
            }
        }
        Ok(counts)
    }

    /// Sends requests sequentially for N seconds.
    /// Returns array with number of requests completed each second.
    async fn spam(client: Client, start: Instant, addr: &'static str) -> [i32; SECONDS] {
        let mut arr = [0i32; SECONDS];
        loop {
            let req = HelloRequest {
                name: "Hasbulla".into(),
            };
            // println!("Sending {:?}", req);
            let _res = client.post(addr).json(&req).send().await.unwrap();
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

    use proto::inventory::{greeting_client::GreetingClient, HelloRequest};
    use tokio::task::JoinSet;
    use tonic::{Request, transport::Channel};

    use crate::SECONDS;

    pub async fn run(addr: &'static str) -> Result<[i32; SECONDS], Box<dyn std::error::Error>> {
        let client = GreetingClient::connect(addr).await?;
        let start = std::time::Instant::now();
        let n = 128;
        let mut set = JoinSet::new();
        for _ in 0..n {
            set.spawn(spam(client.clone(), start));
        }
        let mut results = Vec::with_capacity(n);
        while let Some(res) = set.join_next().await {
            results.push(res.unwrap());
        }
        let mut counts = [0i32; SECONDS];
        for r in results {
            for (i,n) in r.into_iter().enumerate() {
                counts[i] += n;
            }
        }
        Ok(counts)
    }

    /// Sends requests sequentially for N seconds.
    /// Returns array with number of requests completed each second.
    async fn spam(mut client: GreetingClient<Channel>, start: Instant) -> [i32; SECONDS] {
        let mut arr = [0i32; SECONDS];
        loop {
            let req = Request::new(HelloRequest {
                name: "Hasbulla".into(),
            });
            // println!("Sending {:?}", req);
            let _res = client.greet(req).await.unwrap();
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
