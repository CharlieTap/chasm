#!/usr/bin/env python3
"""Select indexed corpus versions, retaining the repository's long-tag exclusions."""
import argparse
import json
import pathlib

parser = argparse.ArgumentParser()
parser.add_argument("source", type=pathlib.Path)
parser.add_argument("destination", type=pathlib.Path)
parser.add_argument("--versions", default="1.0", help="Comma-separated versions, or all")
parser.add_argument("--exclude-target", action="append", default=[])
options = parser.parse_args()
excluded_tags = {"stress-test", "benchmark", "duration-extra-long"}
versions = None if options.versions == "all" else options.versions.split(",")
fixtures = json.loads(options.source.read_text())
selected = [
    fixture for fixture in fixtures
    if (versions is None or fixture["version"] in versions)
    and fixture["name"] not in options.exclude_target
    and not excluded_tags.intersection(fixture.get("tags", []))
]
options.destination.parent.mkdir(parents=True, exist_ok=True)
options.destination.write_text(json.dumps(selected, indent=2) + "\n")
print(f"Selected {len(selected)} fixtures; versions: {sorted({f['version'] for f in selected})}; "
      f"excluded tags: {sorted(excluded_tags)}; excluded targets: {options.exclude_target}")
