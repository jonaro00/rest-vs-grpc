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

    use axum::{routing::get, Json, Router, Server};
    use serde::{Deserialize, Serialize};

    #[derive(Clone, Serialize, Deserialize)]
    enum ItemType {
        Unspecified,
        Chair,
        Table,
        Computer,
        Monitor,
        Keyboard,
        Mouse,
    }
    #[derive(Clone, Serialize, Deserialize)]
    struct Location {
        city_uuid: String,
        country: Option<String>,
        state: Option<String>,
        city: Option<String>,
        building: Option<String>,
        floor: Option<i32>,
        room: Option<i32>,
        cabinet_position: Option<i32>,
    }
    #[derive(Clone, Serialize, Deserialize)]
    struct ItemSummary {
        item_type: ItemType,
        count: u32,
    }
    #[derive(Clone, Serialize, Deserialize)]
    struct ItemCitySummary {
        city_uuid: String,
        item_summary: ItemSummary,
    }
    #[derive(Clone, Serialize, Deserialize)]
    struct ItemDetails {
        uuid: String,
        item_type: ItemType,
        brand: Option<String>,
        model: Option<String>,
        serial_number: Option<String>,
        purchase_price: Option<f32>,
        discarded: Option<bool>,
        location: Option<Location>,
    }
    #[derive(Clone, Serialize, Deserialize)]
    struct ItemsStatusResponse {
        status: String,
        errors: Vec<String>,
        load: f32,
        total_item_count: u32,
        total_price: f32,
        average_price: f32,
    }
    #[derive(Clone, Serialize, Deserialize)]
    struct ItemsSummaryResponse {
        item_city_summaries: Vec<ItemCitySummary>,
    }
    #[derive(Clone, Serialize, Deserialize)]
    struct ItemsFullResponse {
        all_items: Vec<ItemDetails>,
    }

    async fn heart_beat() {} // empty 200 response

    async fn items_status() -> Json<ItemsStatusResponse> {
        Json(ItemsStatusResponse {
            status: "success".into(),
            errors: vec![
                "Invalid barcode/serial number entered.".into(),
                "Unable to update inventory at this time. Please try again later.".into(),
                "Not enough stock available to fulfill the request.".into(),
                "Unable to read inventory data. Please check data source and try again.".into(),
                "Incomplete inventory records detected. Please verify counts and resubmit.".into(),
                "Duplicate item entries detected. Please review inventory records.".into(),
                "Invalid input format. Please ensure the data is in the correct format and submit again.".into(),
                "Unable to retrieve inventory history. Please try again later.".into(),
            ],
            load: 0.95,
            total_item_count: 12345,
            total_price: 670432.51,
            average_price: 56.07,
        })
    }

    async fn items_summary() -> Json<ItemsSummaryResponse> {
        Json(ItemsSummaryResponse {
            item_city_summaries: vec![
                ItemCitySummary {
                    city_uuid: "50f15f5b-78b5-45b4-9bf0-6d3691e606fe".into(),
                    item_summary: ItemSummary {
                        item_type: ItemType::Computer.into(),
                        count: 12
                    }
                };
                480
            ],
        })
    }

    async fn items_full() -> Json<ItemsFullResponse> {
        Json(ItemsFullResponse {
            all_items: vec![
                ItemDetails {
                    uuid: "6b99bcc5-4db2-4f15-95e9-0eb4d7762eb9".into(),
                    item_type: ItemType::Computer.into(),
                    brand: Some("Ferris".into()),
                    model: Some("Crab Hammer".into()),
                    serial_number: Some("SN1605984635".into()),
                    purchase_price: Some(59.90),
                    discarded: Some(false),
                    location: Some(Location {
                        city_uuid: "a6a94b16-9aae-432a-9ef3-b11ff4d49709".into(),
                        country: Some("SE".into()),
                        state: None,
                        city: Some("Stockholm".into()),
                        building: Some("Electrum".into()),
                        floor: Some(3),
                        room: Some(301),
                        cabinet_position: None
                    })
                };
                1395
            ],
        })
    }

    pub async fn run(addr: SocketAddr) -> Result<(), Box<dyn std::error::Error>> {
        Server::bind(&addr)
            .serve(
                Router::new()
                    .route("/heart_beat", get(heart_beat))
                    .route("/items_status", get(items_status))
                    .route("/items_summary", get(items_summary))
                    .route("/items_full", get(items_full))
                    .into_make_service(),
            )
            .await?;
        Ok(())
    }
}

mod grpc {
    use std::net::SocketAddr;

    use proto::inventory::{
        inventory_server::{Inventory, InventoryServer},
        Empty, ItemCitySummary, ItemDetails, ItemSummary, ItemType, ItemsFullResponse,
        ItemsStatusResponse, ItemsSummaryResponse, Location,
    };
    use tonic::{transport::Server, Request, Response, Status};

    struct InventoryServerImpl;

    #[tonic::async_trait]
    impl Inventory for InventoryServerImpl {
        async fn heart_beat(&self, _request: Request<Empty>) -> Result<Response<Empty>, Status> {
            Ok(Response::new(Empty {}))
        }
        async fn items_status(
            &self,
            _request: Request<Empty>,
        ) -> Result<Response<ItemsStatusResponse>, Status> {
            Ok(Response::new(ItemsStatusResponse {
                status: "success".into(),
                errors: vec![
                    "Invalid barcode/serial number entered.".into(),
                    "Unable to update inventory at this time. Please try again later.".into(),
                    "Not enough stock available to fulfill the request.".into(),
                    "Unable to read inventory data. Please check data source and try again.".into(),
                    "Incomplete inventory records detected. Please verify counts and resubmit.".into(),
                    "Duplicate item entries detected. Please review inventory records.".into(),
                    "Invalid input format. Please ensure the data is in the correct format and submit again.".into(),
                    "Unable to retrieve inventory history. Please try again later.".into(),
                ],
                load: 0.95,
                total_item_count: 12345,
                total_price: 670432.51,
                average_price: 56.07,
            }))
        }
        async fn items_summary(
            &self,
            _request: Request<Empty>,
        ) -> Result<Response<ItemsSummaryResponse>, Status> {
            Ok(Response::new(ItemsSummaryResponse {
                item_city_summaries: vec![
                    ItemCitySummary {
                        city_uuid: "50f15f5b-78b5-45b4-9bf0-6d3691e606fe".into(),
                        item_summary: Some(ItemSummary {
                            item_type: ItemType::Computer.into(),
                            count: 12
                        })
                    };
                    480
                ],
            }))
        }
        async fn items_full(
            &self,
            _request: Request<Empty>,
        ) -> Result<Response<ItemsFullResponse>, Status> {
            Ok(Response::new(ItemsFullResponse {
                all_items: vec![
                    ItemDetails {
                        uuid: "6b99bcc5-4db2-4f15-95e9-0eb4d7762eb9".into(),
                        item_type: ItemType::Computer.into(),
                        brand: Some("Ferris".into()),
                        model: Some("Crab Hammer".into()),
                        serial_number: Some("SN1605984635".into()),
                        purchase_price: Some(59.90),
                        discarded: Some(false),
                        location: Some(Location {
                            city_uuid: "a6a94b16-9aae-432a-9ef3-b11ff4d49709".into(),
                            country: Some("SE".into()),
                            state: None,
                            city: Some("Stockholm".into()),
                            building: Some("Electrum".into()),
                            floor: Some(3),
                            room: Some(301),
                            cabinet_position: None
                        })
                    };
                    1395
                ],
            }))
        }
    }

    pub async fn run(addr: SocketAddr) -> Result<(), Box<dyn std::error::Error>> {
        let server = InventoryServerImpl;
        Server::builder()
            .add_service(InventoryServer::new(server))
            .serve(addr)
            .await?;
        Ok(())
    }
}
