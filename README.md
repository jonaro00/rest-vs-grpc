# Benchmarking the request throughput of conventional API calls and gRPC

By [@jonaro00](https://github.com/jonaro00) and [@Danielmebrahtu](https://github.com/Danielmebrahtu)

Bachelor's degree project in Computer Science at KTH Royal Institute of Technology

Read the thesis: [link coming soon]


## Project Details

The project consists of six servers and one client, implemented in three different programming languages: Java, Python, and Rust. The servers provide the same API using both REST (with JSON) and gRPC protocols

## Server Details

### REST API Servers:
  - Java server using Spring framework
  - Python server using FastAPI
  - Rust server using Axum

### gRPC Servers:
  - Java server using official gRPC library
  - Python server using official grpcio library 
  - Rust server using Tonic 

## Client Details

- The client program is implemented in Rust and serves the purpose of spamming requests to the servers while counting the number of completed requests per second.
- The client runs tests against one server at a time and can request four different payload sizes.
