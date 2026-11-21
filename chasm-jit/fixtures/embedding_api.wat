(module
  (type $box (sub (struct (field (mut i32)) (field i8))))
  (type $big_box (sub $box (struct (field (mut i32)) (field i8) (field i64))))
  (type $bytes (array (mut i8)))
  (type $unary (func (param i32) (result i32)))

  (import "host" "touch_memory" (func $touch_memory))
  (import "host" "touch_table" (func $touch_table))
  (import "host" "touch_global" (func $touch_global))
  (import "host" "relay" (func $relay (param i32) (result i32)))
  (import "host" "types" (func $types (result i32)))
  (import "env" "shared_memory" (memory 1 2))
  (import "env" "shared_global" (global $shared (mut i64)))
  (import "env" "shared_table" (table 2 funcref))
  (import "env" "shared_tag" (tag $shared_tag (param i32)))

  (memory $memory (export "memory") 1 4)
  (table $table (export "table") 4 8 funcref)
  (global $counter (export "counter") (mut i32) (i32.const 10))
  (tag $failed (export "failed") (param i32))
  (elem declare func $inc)

  (func $inc (export "inc") (type $unary) (i32.add (local.get 0) (i32.const 1)))

  (func (export "call_touch_memory") (call $touch_memory))
  (func (export "call_touch_table") (call $touch_table))
  (func (export "call_touch_global") (call $touch_global))
  (func (export "call_types") (result i32) (call $types))

  (func (export "inc_ref") (result funcref) (ref.func $inc))

  (func (export "load") (param i32) (result i32) (i32.load $memory (local.get 0)))

  (func (export "call_slot") (param i32 i32) (result i32)
    (call_indirect $table (type $unary) (local.get 1) (local.get 0)))

  (func (export "fail") (param i32) (result i32)
    (throw $failed (local.get 0)))

  (func (export "relay_caught") (param i32) (result i32)
    (block $caught (result i32)
      (try_table (catch $failed $caught)
        (return (call $relay (local.get 0))))
      (unreachable)))

  (func (export "box") (param i32) (result (ref $box))
    (struct.new $box (local.get 0) (i32.const 1)))

  (func (export "big_box") (result (ref $big_box))
    (struct.new $big_box (i32.const 1) (i32.const 2) (i64.const 3)))

  (func (export "bytes") (param i32) (result (ref $bytes))
    (array.new_default $bytes (local.get 0)))

  (func (export "shared") (result i64) (global.get $shared))

  (func (export "null_func") (result funcref) (ref.null func))
  (func (export "null_exn") (result exnref) (ref.null exn))
  (func (export "i31") (param i32) (result i31ref) (ref.i31 (local.get 0)))

  (func (export "spin") (loop $again (br $again)))
)
