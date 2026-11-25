(module
  (import "tags" "boom" (tag $boom (param i32)))
  (import "host" "raise_with_cleanup" (func $raise (param i32)))
  (global $cleanups (export "cleanups") (mut i32) (i32.const 0))

  (func (export "cleanup")
    (global.set $cleanups (i32.add (global.get $cleanups) (i32.const 1))))

  (func (export "throw_in_cleanup")
    (throw $boom (i32.const 99)))

  (func (export "catch_after_cleanup") (param i32) (result i32)
    (block $caught (result i32)
      (try_table (catch $boom $caught) (call $raise (local.get 0)))
      (return (i32.const -1))))

  (func (export "uncaught_after_cleanup") (param i32)
    (call $raise (local.get 0))))
