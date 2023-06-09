from concurrent import futures
import sys

import grpc
from inventory_pb2 import (
    COMPUTER,
    Empty,
    ItemCitySummary,
    ItemDetails,
    ItemSummary,
    ItemsFullResponse,
    ItemsStatusResponse,
    ItemsSummaryResponse,
    Location,
)
from inventory_pb2_grpc import (
    InventoryServicer,
    add_InventoryServicer_to_server,
)

class Inventory(InventoryServicer):
    # HeartBeat endpoint returns an empty message 
    def HeartBeat(self, request: Empty, context):
        return Empty()

    # Items Status endpoint returns a mock items status response
    def ItemsStatus(self, request: Empty, context):
        return ItemsStatusResponse(
            status="success",
            errors=[
                "Invalid barcode/serial number entered.",
                "Unable to update inventory at this time. Please try again later.",
                "Not enough stock available to fulfill the request.",
                "Unable to read inventory data. Please check data source and try again.",
                "Incomplete inventory records detected. Please verify counts and resubmit.",
                "Duplicate item entries detected. Please review inventory records.",
                "Invalid input format. Please ensure the data is in the correct format and submit again.",
                "Unable to retrieve inventory history. Please try again later.",
            ],
            load=0.95,
            total_item_count=12345,
            total_price=670432.55,
            average_price=56.07,
        )

    # Items Summary endpoint
    def ItemsSummary(self, request: Empty, context):
        return ItemsSummaryResponse(
            item_city_summaries=[
                ItemCitySummary(
                    city_uuid="50f15f5b-78b5-45b4-9bf0-6d3691e606fe",
                    item_summary=ItemSummary(
                        item_type=COMPUTER,
                        count=12,
                    ),
                )
            ] * 480,
        )
    
    # Items Full endpoint
    def ItemsFull(self, request: Empty, context):
        return ItemsFullResponse(
            all_items=[
                ItemDetails(
                    uuid="6b99bcc5-4db2-4f15-95e9-0eb4d7762eb9",
                    item_type=COMPUTER,
                    brand="Ferris",
                    model="Crab Hammer",
                    serial_number="SN1605984635",
                    purchase_price=59.90,
                    discarded=False,
                    location=Location(
                        city_uuid="a6a94b16-9aae-432a-9ef3-b11ff4d49709",
                        country="SE",
                        state=None,
                        city="Stockholm",
                        building="Electrum",
                        floor=3,
                        room=301,
                        cabinet_position=None,
                    ),
                )
            ] * 1395,
        )


def serve():
    port = sys.argv[1] if len(sys.argv) >= 2 else "1337"
    # Create the gRPC server
    server = grpc.server(futures.ThreadPoolExecutor())
    add_InventoryServicer_to_server(Inventory(), server)
    server.add_insecure_port(f"0.0.0.0:{port}")
    server.start()

    # Keep the server running until terminated
    server.wait_for_termination()

serve() # Run the server
