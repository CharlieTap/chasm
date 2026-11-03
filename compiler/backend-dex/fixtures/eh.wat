(module
  (tag $a (param i32))
  (tag $b (param i64 i32))
  (tag $empty)
  (memory 1 4)
  (type $box (struct (field i32)))

  (func $throw_a (param i32) (throw $a (local.get 0)))

  ;; The try_table starts at the first instruction; the throw after it must not be caught.
  (func (export "after_try") (param i32) (result i32)
    (block $h (result i32)
      (try_table (catch $a $h) (nop))
      (if (local.get 0) (then (throw $a (i32.const 7))))
      (return (i32.const 1)))
    (i32.const 100)
    (i32.add))

  (func (export "same_function") (param i32) (result i32)
    (block $h (result i32)
      (try_table (result i32) (catch $a $h)
        (if (i32.gt_s (local.get 0) (i32.const 5)) (then (throw $a (local.get 0))))
        (local.get 0))
      (return))
    (i32.const 1000)
    (i32.add))

  (func (export "nested") (param i32) (result i64)
    (local $x i32)
    (block $outer (result i64 i32)
      (block $inner (result i32)
        (try_table (catch $a $inner)
          (try_table (catch $b $outer)
            (if (i32.eq (local.get 0) (i32.const 1)) (then (throw $a (i32.const 11))))
            (if (i32.eq (local.get 0) (i32.const 2)) (then (throw $b (i64.const 22) (i32.const 3))))
            (if (i32.eq (local.get 0) (i32.const 3)) (then (call $throw_a (i32.const 33))))
            (if (i32.eq (local.get 0) (i32.const 4)) (then (throw $empty)))))
        (return (i64.const -1)))
      (local.set $x)
      (return (i64.extend_i32_s (local.get $x))))
    (local.set $x)
    (i64.add (i64.extend_i32_s (local.get $x))))

  (func (export "catch_all") (param i32) (result i32)
    (block $h
      (try_table (catch_all $h)
        (if (local.get 0) (then (throw $empty)))
        (if (i32.eq (local.get 0) (i32.const 2)) (then (unreachable))))
      (return (i32.const 0)))
    (i32.const 1))

  ;; The callee grows memory and then throws; the handler must see the new size and backing array.
  (func $grow_then_throw (drop (memory.grow (i32.const 1))) (i32.store (i32.const 65540) (i32.const 77)) (throw $empty))
  (func (export "grow_throw") (result i32)
    (block $h
      (try_table (catch $empty $h) (call $grow_then_throw))
      (return (i32.const -1)))
    (i32.add (memory.size) (i32.load (i32.const 65540))))

  ;; catch_ref, then allocations that may collect, then throw_ref the same exception to an outer handler.
  (func $rethrow_after_gc (param i32) (result i32)
    (local $i i32) (local $e exnref)
    (block $h (result i32 exnref)
      (try_table (catch_ref $a $h) (throw $a (local.get 0)))
      (unreachable))
    (local.set $e)
    (drop)
    (block $done (loop $alloc
      (br_if $done (i32.ge_u (local.get $i) (i32.const 2000)))
      (drop (struct.new $box (local.get $i)))
      (local.set $i (i32.add (local.get $i) (i32.const 1)))
      (br $alloc)))
    (throw_ref (local.get $e)))
  (func (export "catch_ref") (param i32) (result i32)
    (block $h (result i32)
      (try_table (result i32) (catch $a $h) (call $rethrow_after_gc (local.get 0)))
      (return))
    (i32.const 5)
    (i32.add))

  (func (export "throw_null") (block (result exnref) (ref.null exn)) (throw_ref))

  (func (export "uncaught") (param i32) (result i32) (call $throw_a (local.get 0)) (i32.const 0))
)
