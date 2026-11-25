(module
  (import "env" "imported" (func))
  (func $plain (export "plain") (result i32) (i32.const 1))
  (func $vector (export "vector") (result i32) (i32x4.extract_lane 0 (v128.const i32x4 1 2 3 4))))
