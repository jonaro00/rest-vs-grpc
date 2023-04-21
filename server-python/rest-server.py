from enum import Enum

from fastapi import FastAPI
from fastapi.responses import ORJSONResponse, Response
from pydantic import BaseModel


class ItemType(str, Enum):
    UNSPECIFIED = 'UNSPECIFIED'
    CHAIR = 'CHAIR'
    TABLE = 'TABLE'
    COMPUTER = 'COMPUTER'
    MONITOR = 'MONITOR'
    KEYBOARD = 'KEYBOARD'
    MOUSE = 'MOUSE'

class Location(BaseModel):
    city_uuid: str
    country: str | None
    state: str | None
    city: str | None
    building: str | None
    floor: int | None
    room: int | None
    cabinet_position: int | None

class ItemSummary(BaseModel):
    item_type: ItemType
    count: int

class ItemCitySummary(BaseModel):
    city_uuid: str
    item_summary: ItemSummary

class ItemDetails(BaseModel):
    uuid: str
    item_type: ItemType
    brand: str | None
    model: str | None
    serial_number: str | None
    purchase_price: float | None
    discarded: bool | None
    location: Location | None

class ItemsStatusResponse(BaseModel):
    status: str
    errors: list[str]
    load: float
    total_item_count: int
    total_price: float
    average_price: float

class ItemsSummaryResponse(BaseModel):
    item_city_summaries: list[ItemCitySummary]

class ItemsFullResponse(BaseModel):
    all_items: list[ItemDetails]

app = FastAPI()

@app.get("/heart_beat")
async def heart_beat():
    return Response() # empty 200 response

@app.get("/items_status", response_class=ORJSONResponse)
async def items_status():
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
        total_price=670432.51,
        average_price=56.07,
    )

@app.get("/items_summary", response_class=ORJSONResponse)
async def items_summary():
    return ItemsSummaryResponse(
        item_city_summaries=[
            ItemCitySummary(
                city_uuid="50f15f5b-78b5-45b4-9bf0-6d3691e606fe",
                item_summary=ItemSummary(
                    item_type=ItemType.COMPUTER,
                    count=12,
                ),
            )
        ] * 480,
    )

@app.get("/items_full", response_class=ORJSONResponse)
async def items_full():
    return ItemsFullResponse(
        all_items=[
            ItemDetails(
                uuid="6b99bcc5-4db2-4f15-95e9-0eb4d7762eb9",
                item_type=ItemType.COMPUTER,
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
