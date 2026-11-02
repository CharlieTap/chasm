(module
  (import "owner" "count" (func $count (param i32) (result i32)))

  (func (export "call_count") (param $n i32) (result i32)
    (call $count (local.get $n))))
