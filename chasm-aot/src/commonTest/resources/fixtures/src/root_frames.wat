(module
  (type $box (struct (field i32)))
  (type $bytes (array (mut i8)))
  (type $nothing (func))
  (import "env" "collect" (func $collect))
  (table $collectors 1 funcref)
  (elem (table $collectors) (i32.const 0) func $collects_through_host)
  (elem declare func $collects_through_host)
  (tag $escape)
  (tag $other)

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
    (return_call $keep_across_ref_tail (call $make (i32.const 42))))

  ;; Each box is written after the iteration's collection, so only the loop's back edge leads to the next one, which
  ;; must find it intact. A box that was not kept traps.
  (func (export "survives_collection_across_a_back_edge") (result i32)
    (local $box (ref null $box))
    (local $count i32)
    (local.set $box (call $make (i32.const -1)))
    (loop $again
      (call $collect)
      (drop (call $make (i32.const 99)))
      (if (i32.ne (struct.get $box 0 (local.get $box)) (i32.sub (local.get $count) (i32.const 1)))
        (then (unreachable)))
      (local.set $box (call $make (local.get $count)))
      (local.set $count (i32.add (local.get $count) (i32.const 1)))
      (br_if $again (i32.lt_u (local.get $count) (i32.const 3))))
    (local.get $count))

  ;; The box is written inside a try region and followed only by rethrowing an exception that already exists, which
  ;; allocates nothing, so only the handler leads to the collection.
  (func (export "survives_collection_in_a_handler") (result i32)
    (local $box (ref null $box))
    (local $exception exnref)
    (local.set $exception
      (block $first (result exnref)
        (try_table (catch_all_ref $first)
          (throw $escape))
        (unreachable)))
    (block $caught
      (try_table (catch_all $caught)
        (local.set $box (call $make (i32.const 42)))
        (throw_ref (local.get $exception))))
    (call $collect)
    (drop (call $make (i32.const 99)))
    (struct.get $box 0 (ref.as_non_null (local.get $box))))

  ;; The inner region's handler leaves both regions without collecting, so only the enclosing region's handler leads to
  ;; the collection.
  (func (export "survives_collection_in_an_enclosing_handler") (result i32)
    (local $box (ref null $box))
    (local $exception exnref)
    (local.set $exception
      (block $first (result exnref)
        (try_table (catch_all_ref $first)
          (throw $escape))
        (unreachable)))
    (block $skip
      (block $outer
        (try_table (catch_all $outer)
          (try_table (catch $other $skip)
            (local.set $box (call $make (i32.const 42)))
            (throw_ref (local.get $exception))))
        (unreachable))
      (call $collect)
      (drop (call $make (i32.const 99)))
      (return (struct.get $box 0 (ref.as_non_null (local.get $box)))))
    (i32.const -1))

  ;; Calling itself in tail position lowers to a jump to its start, which collects before reading the box it was given.
  (func $count_down (param $count i32) (param $box (ref $box)) (result i32)
    (call $collect)
    (drop (call $make (i32.const 99)))
    (if (result i32) (i32.eqz (local.get $count))
      (then (struct.get $box 0 (local.get $box)))
      (else (return_call $count_down (i32.sub (local.get $count) (i32.const 1)) (call $make (local.get $count))))))

  (func (export "survives_collection_after_a_self_tail_call") (result i32)
    (return_call $count_down (i32.const 3) (call $make (i32.const 4)))))
