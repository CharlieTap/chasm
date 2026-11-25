(module
  (type $box (struct (field i32)))
  (import "host" "echo" (func $echo (param structref) (result structref)))
  (import "host" "mix" (func $mix (param i32 externref f64) (result externref)))
  (import "host" "add" (func $add (param i32 i32) (result i32)))
  (import "host" "reenter" (func $reenter (param externref) (result externref)))
  (export "mix_import" (func $mix))
  (global $parked (mut externref) (ref.null extern))
  (tag $failure)

  (func (export "echo_same") (result i32)
    (local $box (ref null $box))
    (local.set $box (struct.new $box (i32.const 1)))
    (ref.eq (local.get $box) (ref.cast (ref null $box) (call $echo (local.get $box)))))

  (func (export "identity") (param externref) (result externref)
    (local.get 0))

  (func (export "park") (param externref)
    (global.set $parked (local.get 0)))

  (func (export "parked") (result externref)
    (global.get $parked))

  (func (export "mix") (param i32 externref f64) (result externref)
    (call $mix (local.get 0) (local.get 1) (local.get 2)))

  (func (export "add") (param i32 i32) (result i32)
    (call $add (local.get 0) (local.get 1)))

  (func (export "reenter") (param externref) (result externref)
    (call $reenter (local.get 0)))

  (func (export "trap")
    (unreachable))

  (func (export "throw")
    (throw $failure))
)
