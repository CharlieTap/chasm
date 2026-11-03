#!/usr/bin/env python3
"""Writes limits.wat: functions beyond DEX's limits, a 130-parameter function (more argument words than an
invoke can pass) and one touching 33,000 locals (more registers than a method can have), each with a caller.
Generate the binary from the repository root with:
  python3 compiler/backend-dex/fixtures/limits.py > /tmp/limits.wat
  wasm-tools parse /tmp/limits.wat -o compiler/backend-dex/src/jvmTest/resources/fixtures/limits.wasm
"""
params = " ".join(["i64"] * 130)
args = " ".join(f"(i64.const {i})" for i in range(130))
n = 33000
body = "\n".join(f"    (local.set {i + 1} (local.get {i}))" for i in range(n))
print(f"""(module
  (func $wide (param {params}) (result i64)
    (i64.add (local.get 0) (local.get 129)))
  (func (export "call_wide") (result i64)
    (call $wide {args}))
  (func $huge (export "huge") (param i64) (result i64)
    (local {" ".join(["i64"] * n)})
{body}
    (local.get {n}))
  (func (export "call_huge") (result i64)
    (call $huge (i64.const 7)))
)""")
