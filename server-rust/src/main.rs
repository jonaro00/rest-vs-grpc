use proto::inventory::{
    greeting_service_server::{GreetingService, GreetingServiceServer},
    HelloRequest, HelloResponse,
};
use tonic::{transport::Server, Request, Response, Status};

struct GreetingServer {}

#[tonic::async_trait]
impl GreetingService for GreetingServer {
    async fn greet(
        &self,
        request: Request<HelloRequest>,
    ) -> Result<Response<HelloResponse>, Status> {
        let req = request.into_inner();
        let res = HelloResponse {
            greeting: format!("Hello {}!", req.name),
        };
        Ok(Response::new(res))
    }
}

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let addr = "127.0.0.1:1337".parse()?;
    let server = GreetingServer {};

    Server::builder()
        .add_service(GreetingServiceServer::new(server))
        .serve(addr)
        .await?;

    Ok(())
}
