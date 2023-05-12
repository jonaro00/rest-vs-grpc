from inventory_pb2 import SearchRequest

req = SearchRequest(query="apples", page_number=2, result_per_page=50)

with open("demo_out", "bw") as f:
    f.write(req.SerializeToString())
