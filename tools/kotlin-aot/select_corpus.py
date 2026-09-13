#!/usr/bin/env python3
"""Select every Wasm 1.0 fixture except the repository's long-workload tags."""
import json
import pathlib
import sys

source, destination = map(pathlib.Path, sys.argv[1:])
excluded_tags = {"stress-test", "benchmark", "duration-extra-long"}
fixtures = json.loads(source.read_text())
selected = [
    fixture for fixture in fixtures
    if fixture["version"] == "1.0"
    and not excluded_tags.intersection(fixture.get("tags", []))
]
destination.parent.mkdir(parents=True, exist_ok=True)
destination.write_text(json.dumps(selected, indent=2) + "\n")
print(f"Selected {len(selected)} Wasm 1.0 fixtures; excluded tags: {sorted(excluded_tags)}")
