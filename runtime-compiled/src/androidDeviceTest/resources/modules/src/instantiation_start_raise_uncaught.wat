(module
  (import "tags" "boom" (tag $boom (param i32)))
  (import "host" "raise" (func $raise (param i32)))
  (func $start
    (call $raise (i32.const 5)))
  (start $start))
