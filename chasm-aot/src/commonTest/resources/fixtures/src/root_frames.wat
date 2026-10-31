(module
  (type $box (struct (field i32)))
  (type $bytes (array (mut i8)))
  (type $nothing (func))
  (import "env" "collect" (func $collect))
  (table $collectors 1 funcref)
  (elem (table $collectors) (i32.const 0) func $collects_through_host)
  (elem declare func $collects_through_host)

  (func $make (param $value i32) (result (ref $box))
    (struct.new $box (local.get $value)))

  ;; Collects through the host, then allocates a box that may take a freed one's place.
  (func $host_collects
    (call $collect)
    (drop (call $make (i32.const 99))))

  (func $collects_through_host
    (call $host_collects))

  ;; Allocates enough garbage to collect more than once, then a box that may take a freed one's place.
  (func $guest_collects
    (local $count i32)
    (local.set $count (i32.const 256))
    (loop $again
      (drop (array.new_default $bytes (i32.const 4096)))
      (br_if $again (local.tee $count (i32.sub (local.get $count) (i32.const 1)))))
    (drop (call $make (i32.const 99))))

  (func $collects_through_guest
    (call $guest_collects))

  ;; Neither allocates: a callee two calls away collects while only their own frame holds the box.
  (func $keep_across_host (param $box (ref $box)) (result i32)
    (call $collects_through_host)
    (struct.get $box 0 (local.get $box)))

  (func $keep_across_guest (param $box (ref $box)) (result i32)
    (call $collects_through_guest)
    (struct.get $box 0 (local.get $box)))

  ;; Each tail calls on to the host collection, so it runs after the tail caller's frame has gone.
  (func $tail_to_host
    (return_call $collects_through_host))

  (func $tail_indirect_to_host
    (return_call_indirect $collectors (type $nothing) (i32.const 0)))

  (func $tail_ref_to_host
    (return_call_ref $nothing (ref.func $collects_through_host)))

  (func $keep_across_tail (param $box (ref $box)) (result i32)
    (call $tail_to_host)
    (struct.get $box 0 (local.get $box)))

  (func $keep_across_indirect_tail (param $box (ref $box)) (result i32)
    (call $tail_indirect_to_host)
    (struct.get $box 0 (local.get $box)))

  (func $keep_across_ref_tail (param $box (ref $box)) (result i32)
    (call $tail_ref_to_host)
    (struct.get $box 0 (local.get $box)))

  ;; Tail calls end the exported frame first, so nothing else holds the box.
  (func (export "survives_host_collection") (result i32)
    (return_call $keep_across_host (call $make (i32.const 42))))

  (func (export "survives_guest_collection") (result i32)
    (return_call $keep_across_guest (call $make (i32.const 42))))

  (func (export "survives_collection_beneath_tail_call") (result i32)
    (return_call $keep_across_tail (call $make (i32.const 42))))

  (func (export "survives_collection_beneath_indirect_tail_call") (result i32)
    (return_call $keep_across_indirect_tail (call $make (i32.const 42))))

  (func (export "survives_collection_beneath_ref_tail_call") (result i32)
    (return_call $keep_across_ref_tail (call $make (i32.const 42)))))
