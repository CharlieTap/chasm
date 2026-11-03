(module
  (type $point (struct (field $x (mut i32)) (field $y (mut i64))))
  (type $packed (struct (field $b (mut i8)) (field $s (mut i16))))
  (type $ints (array (mut i32)))
  (type $node (struct (field $value i32) (field $next (ref null $node))))
  (type $binary (func (param i32 i32) (result i32)))

  (func $add (type $binary) (i32.add (local.get 0) (local.get 1)))
  (func $mul (type $binary) (i32.mul (local.get 0) (local.get 1)))
  (elem declare func $add $mul)

  (func (export "point") (param i32 i64) (result i64)
    (local $p (ref $point))
    (local.set $p (struct.new $point (local.get 0) (local.get 1)))
    (struct.set $point $x (local.get $p) (i32.add (struct.get $point $x (local.get $p)) (i32.const 1)))
    (i64.add (i64.extend_i32_s (struct.get $point $x (local.get $p))) (struct.get $point $y (local.get $p))))

  (func (export "packed") (param i32) (result i32)
    (local $p (ref $packed))
    (local.set $p (struct.new_default $packed))
    (struct.set $packed $b (local.get $p) (local.get 0))
    (struct.set $packed $s (local.get $p) (local.get 0))
    (i32.add
      (i32.add (struct.get_s $packed $b (local.get $p)) (struct.get_u $packed $b (local.get $p)))
      (i32.add (struct.get_s $packed $s (local.get $p)) (struct.get_u $packed $s (local.get $p)))))

  (func (export "array_sum") (param $n i32) (result i32)
    (local $a (ref $ints)) (local $i i32) (local $sum i32)
    (local.set $a (array.new $ints (i32.const 3) (local.get $n)))
    (array.fill $ints (local.get $a) (i32.const 0) (i32.const 7) (i32.div_u (local.get $n) (i32.const 2)))
    (array.set $ints (local.get $a) (i32.const 0) (i32.const 100))
    (block $done (loop $next
      (br_if $done (i32.ge_u (local.get $i) (array.len (local.get $a))))
      (local.set $sum (i32.add (local.get $sum) (array.get $ints (local.get $a) (local.get $i))))
      (local.set $i (i32.add (local.get $i) (i32.const 1)))
      (br $next)))
    (local.get $sum))

  (func (export "fixed") (param i32) (result i32)
    (local $a (ref $ints)) (local $b (ref $ints))
    (local.set $a (array.new_fixed $ints 4 (local.get 0) (i32.const 2) (i32.const 3) (i32.const 4)))
    (local.set $b (array.new_default $ints (i32.const 4)))
    (array.copy $ints $ints (local.get $b) (i32.const 1) (local.get $a) (i32.const 0) (i32.const 3))
    (i32.add (array.get $ints (local.get $b) (i32.const 1)) (array.get $ints (local.get $b) (i32.const 3))))

  (func (export "i31") (param i32) (result i32)
    (i32.add (i31.get_s (ref.i31 (local.get 0))) (i31.get_u (ref.i31 (local.get 0)))))

  (func (export "list") (param $n i32) (result i32)
    ;; Builds a linked list of n nodes, allocating heavily, then sums it: collections must keep the list alive.
    (local $head (ref null $node)) (local $i i32) (local $sum i32)
    (block $built (loop $build
      (br_if $built (i32.ge_u (local.get $i) (local.get $n)))
      (local.set $head (struct.new $node (local.get $i) (local.get $head)))
      (drop (array.new $ints (i32.const 0) (i32.const 64)))
      (local.set $i (i32.add (local.get $i) (i32.const 1)))
      (br $build)))
    (block $done (loop $walk
      (br_on_null $done (local.get $head))
      (local.set $head (struct.get $node $next (ref.as_non_null)))
      (br $walk)))
    (local.set $i (i32.const 0))
    (local.get $n))

  (func (export "cast") (param i32) (result i32)
    (local $any anyref)
    (local.set $any (if (result anyref) (local.get 0)
      (then (struct.new $point (i32.const 5) (i64.const 6)))
      (else (ref.i31 (i32.const 9)))))
    (block $is_point (result (ref $point))
      (br_on_cast $is_point anyref (ref $point) (local.get $any))
      (return (i31.get_u (ref.cast (ref i31) (local.get $any)))))
    (struct.get $point $x))

  (func (export "cast_fail") (param i32) (result i32)
    (block $not_i31 (result anyref)
      (br_on_cast_fail $not_i31 anyref (ref i31)
        (if (result anyref) (local.get 0) (then (ref.i31 (i32.const 3))) (else (ref.null any))))
      (return (i31.get_s)))
    (drop)
    (i32.const -1))

  (func (export "call_ref") (param i32 i32 i32) (result i32)
    (call_ref $binary (local.get 1) (local.get 2)
      (if (result (ref $binary)) (local.get 0) (then (ref.func $add)) (else (ref.func $mul)))))

  (func (export "call_null") (result i32)
    (call_ref $binary (i32.const 1) (i32.const 2) (ref.null $binary)))

  (func (export "extern") (param i32) (result i32)
    (i31.get_s (ref.cast (ref i31) (any.convert_extern (extern.convert_any (ref.i31 (local.get 0)))))))

  (func (export "bad_cast") (result i32)
    (ref.cast (ref $point) (ref.i31 (i32.const 1))) (drop) (i32.const 0))
)
