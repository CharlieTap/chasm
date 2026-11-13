#!/usr/bin/env python3
import sys

locals_count = 3300
exceptions = sys.argv[1:] == ["eh"]


def conflict(value):
    return f"""    (local.set 1 (if (result f64) (i32.eqz (i32.wrap_i64 (local.get 2)))
      (then (f64.reinterpret_i64 (i64.or (local.get 2) (i64.const {value}))))
      (else (local.get 1))))"""


def pinned(name, stores):
    body = "\n".join(f"    (i32.store offset={4 * i} (local.get 0) (local.get 0))" for i in range(stores))
    return f"""  (func ${name} (export "{name}") (param i32 f64 i64) (result f64)
{conflict(1)}
{body}
{conflict(2)}
    (local.get 1))"""


raise_exception = """  (tag $raised)
  (func $raise (export "raise") (throw $raised))""" if exceptions else ""

print(f"""(module
  (memory 1)
  (func $frame (export "frame") (param i32) (result i32)
    (local {" ".join(["i32"] * locals_count)})
    (local.set {locals_count} (local.get 0))
    (local.get {locals_count}))
  (func $helper (export "helper") (param i32) (result i32)
    (local {" ".join(["i32"] * locals_count)})
    (local.set {locals_count} (i32.load (local.get 0)))
    (local.get {locals_count}))
{pinned("pinned_helper", 800)}
{pinned("pinned_split", 1150)}
{raise_exception}
)""")
