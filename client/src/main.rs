use proto::inventory::{greeting_service_client::GreetingServiceClient, HelloRequest};
use tonic::Request;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let mut client = GreetingServiceClient::connect("http://127.0.0.1:1337").await?;

    let arr = &mut [0i32; 10];
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

    println!("{arr:?}");

    Ok(())
}
