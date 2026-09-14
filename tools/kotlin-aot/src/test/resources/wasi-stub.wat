(module
  (import "wasi_snapshot_preview1" "random_get" (func $random (param i32 i32) (result i32)))
  (func (export "sample") (result i32)
    i32.const 0 i32.const 0 call $random)
)
