(module
  (import "host" "during_start" (func $during_start))
  (global $value (export "value_global") (mut i32) (i32.const 3))
  (func (export "value") (result i32) (global.get $value))
  (func $start (call $during_start))
  (start $start))
