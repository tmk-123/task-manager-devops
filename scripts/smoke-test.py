"""Exercise the real HTTP -> Nginx -> API -> MySQL path. Python standard library only."""
import json
import os
import urllib.request

base = os.getenv("BASE_URL", "http://localhost:8080").rstrip("/")

def request(method, path, payload=None):
    data = None if payload is None else json.dumps(payload).encode()
    req = urllib.request.Request(base + path, data=data, method=method,
                                 headers={"Content-Type": "application/json"})
    with urllib.request.urlopen(req, timeout=15) as response:
        body = response.read()
        return response.status, json.loads(body) if body else None

status, health = request("GET", "/health")
assert status == 200 and health["status"] == "UP", health
status, task = request("POST", "/api/tasks", {"title": "Smoke test", "status": "TODO"})
assert status == 201, status
path = "/api/tasks/" + str(task["id"])
try:
    fetched = request("GET", path)[1]
    assert fetched["title"] == "Smoke test"
    assert fetched["createdAt"] == task["createdAt"], "Timestamp changed after saving to database"
    status, updated = request("PUT", path, {"title": "Smoke test", "status": "DONE"})
    assert status == 200 and updated["status"] == "DONE", updated
    page = request("GET", "/api/tasks?status=DONE&size=100")[1]
    assert any(item["id"] == task["id"] for item in page["content"]), page
finally:
    assert request("DELETE", path)[0] == 204
print("PASS: health, create, read, update, filter, delete through Nginx")
