use std::net::{IpAddr, Ipv4Addr, SocketAddr};

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let mut args = std::env::args().skip(1);
    let mode = args.next();
    let port = args.next().unwrap_or("1337".into()).parse()?;

    let addr = SocketAddr::new(IpAddr::V4(Ipv4Addr::UNSPECIFIED), port);

    if mode == Some("rest".into()) {
        rest::run(addr).await
    } else if mode == Some("grpc".into()) {
        grpc::run(addr).await
    } else {
        panic!("provide rest or grpc")
    }
}

mod rest {
    use std::net::SocketAddr;

    use axum::{routing::post, Json, Router, Server};
    use serde::{Deserialize, Serialize};

    #[derive(Serialize, Deserialize)]
    struct HelloRequest {
        name: String,
    }
    #[derive(Serialize, Deserialize)]
    struct HelloResponse {
        greeting: String,
    }

    async fn hello(Json(req): Json<HelloRequest>) -> Json<HelloResponse> {
        Json(HelloResponse {
            greeting: format!("Hello {}!", req.name),
        })
    }

    pub async fn run(addr: SocketAddr) -> Result<(), Box<dyn std::error::Error>> {
        Server::bind(&addr)
            .serve(Router::new().route("/", post(hello)).into_make_service())
            .await?;
        Ok(())
    }
}

mod grpc {
    use std::net::SocketAddr;

    use proto::inventory::{
        greeting_service_server::{GreetingService, GreetingServiceServer},
        HelloRequest, HelloResponse,
    };
    use tonic::{transport::Server, Request, Response, Status};

    struct GreetingServer;

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

    pub async fn run(addr: SocketAddr) -> Result<(), Box<dyn std::error::Error>> {
        let server = GreetingServer;
        Server::builder()
            .add_service(GreetingServiceServer::new(server))
            .serve(addr)
            .await?;
        Ok(())
    }
}
