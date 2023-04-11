if __name__ != "__main__":
    # Imported by uvicorn -> run FastAPI

    from fastapi import FastAPI
    from fastapi.responses import ORJSONResponse
    from pydantic import BaseModel

    class HelloRequest(BaseModel):
        name: str

    class HelloResponse(BaseModel):
        greeting: str

    app = FastAPI()

    @app.post("/", response_class=ORJSONResponse)
    async def read_root(req: HelloRequest):
        return HelloResponse(greeting=f"Hello {req.name}!")

else:
    # Standalone -> run gRPC

    from concurrent import futures
    import sys

    import grpc
    from inventory_pb2 import HelloRequest, HelloResponse
    from inventory_pb2_grpc import (
        add_GreetingServicer_to_server,
        GreetingServicer,
    )

    class Greeter(GreetingServicer):
        def Greet(self, request: HelloRequest, context):
            return HelloResponse(greeting=f"Hello {request.name}!")

    def serve():
        port = sys.argv[1] if len(sys.argv) >= 2 else "1337"
        server = grpc.server(futures.ThreadPoolExecutor())
        add_GreetingServicer_to_server(Greeter(), server)
        server.add_insecure_port(f"0.0.0.0:{port}")
        server.start()
        server.wait_for_termination()

    serve()
