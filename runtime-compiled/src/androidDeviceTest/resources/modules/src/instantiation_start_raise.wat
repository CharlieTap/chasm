(module
  (import "tags" "boom" (tag $boom (param i32)))
  (import "host" "raise" (func $raise (param i32)))
  (global $caught (export "caught") (mut i32) (i32.const -1))
  (func $start
    (block $handler (result i32)
      (try_table (catch $boom $handler) (call $raise (i32.const 4)))
      (return))
    (global.set $caught))
  (start $start))
