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
    use reqwest::Client;
    use serde::{Deserialize, Serialize};

    #[derive(Debug, Serialize, Deserialize)]
    struct HelloRequest {
        name: String,
    }
    #[derive(Debug, Serialize, Deserialize)]
    struct HelloResponse {
        greeting: String,
    }

    pub async fn run(addr: &'static str) -> Result<[i32; 10], Box<dyn std::error::Error>> {
        let client = Client::builder().http1_only().build()?;
        let mut arr = [0i32; 10];
        let start = std::time::Instant::now();
        loop {
            let req = HelloRequest {
                name: "Hasbulla".into(),
            };
            // println!("Sending {:?}", req);
            let _res = client.post(addr).json(&req).send().await?;
            // println!("Got response {:?}", res);
            let now = std::time::Instant::now();
            let diff = now.duration_since(start).as_secs() as usize;
            if diff >= arr.len() {
                break;
            }
            arr[diff] += 1;
        }
        Ok(arr)
    }
}

mod grpc {
    use proto::inventory::{greeting_client::GreetingClient, HelloRequest};
    use tonic::Request;

    pub async fn run(addr: &'static str) -> Result<[i32; 10], Box<dyn std::error::Error>> {
        let mut client = GreetingClient::connect(addr).await?;
        let mut arr = [0i32; 10];
        let start = std::time::Instant::now();
        loop {
            let req = Request::new(HelloRequest {
                name: "Hasbulla".into(),
            });
            // println!("Sending {:?}", req);
            let _res = client.greet(req).await?;
            // println!("Got response {:?}", res);
            let now = std::time::Instant::now();
            let diff = now.duration_since(start).as_secs() as usize;
            if diff >= arr.len() {
                break;
            }
            arr[diff] += 1;
        }
        Ok(arr)
    }
}
