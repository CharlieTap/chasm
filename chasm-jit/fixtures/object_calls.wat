(module
  (rec
    (type $point (sub (struct
      (field (mut i32)) (field (mut i64)) (field (mut f64)) (field (mut i8)) (field (mut i16))
      (field (mut (ref null $point))) (field (mut externref)) (field i32))))
    (type $point3 (sub $point (struct
      (field (mut i32)) (field (mut i64)) (field (mut f64)) (field (mut i8)) (field (mut i16))
      (field (mut (ref null $point))) (field (mut externref)) (field i32) (field (mut i32))))))
  (type $other (struct (field (mut i32))))
  (type $pair (struct (field (ref $point))))
  (rec
    (type $held (func (param i32) (result i32)))
    (type $holder (struct (field (mut (ref null $held))))))
  (type $bytes (array (mut i8)))
  (type $chars (array (mut i16)))
  (type $longs (array (mut i64)))
  (type $points (array (mut (ref null $point))))
  (type $strict (array (mut (ref $point))))
  (type $point3s (array (mut (ref null $point3))))
  (type $anys (array (mut anyref)))
  (type $frozen (array i8))
  (type $unary (func (param i32) (result i32)))
  (type $echo (func (param (ref null $point)) (result (ref null $point))))

  (import "host" "raise" (func $raise (param i32 externref)))
  (import "host" "echo" (func $echo (type $echo)))

  (tag $boom (export "boom") (param i32 externref))
  (tag $carry (export "carry") (param (ref null $point)))
  (tag $strict_carry (export "strict_carry") (param (ref $point)))
  (tag $numbers (export "numbers") (param i32 i64))

  (table $functions (export "functions") 4 funcref)
  (table $slots (export "slots") 4 8 (ref null $point))
  (table $required (export "required") 1 (ref $point) (struct.new_default $point))
  (table $anything (export "anything") 2 anyref)
  (global $current (export "current") (mut (ref null $point)) (ref.null $point))
  (global $origin (export "origin") (mut (ref $point)) (struct.new_default $point))
  (global $fixed_point (export "fixed_point") (ref null $point) (ref.null $point))
  (global $count (export "count") (mut i32) (i32.const 0))
  (global $fixed (export "fixed") i32 (i32.const 3))

  (func $inc (export "inc") (type $unary) (i32.add (local.get 0) (i32.const 1)))
  (func $double (export "double") (type $unary) (i32.mul (local.get 0) (i32.const 2)))
  (func $triple (export "triple") (type $held) (i32.mul (local.get 0) (i32.const 3)))

  (func (export "point") (param i32) (result (ref $point))
    (struct.new $point
      (local.get 0) (i64.const 2) (f64.const 3.5) (i32.const -1) (i32.const -1)
      (ref.null $point) (ref.null extern) (i32.const 9)))

  (func (export "point3") (param i32) (result (ref $point3))
    (struct.new $point3
      (local.get 0) (i64.const 2) (f64.const 3.5) (i32.const -1) (i32.const -1)
      (ref.null $point) (ref.null extern) (i32.const 9) (i32.const 3)))

  (func (export "other") (result (ref $other))
    (struct.new $other (i32.const 1)))

  (func (export "pair") (param (ref $point)) (result (ref $pair))
    (struct.new $pair (local.get 0)))

  (func (export "holder") (result (ref $holder))
    (struct.new_default $holder))

  (func (export "call_held") (param (ref $holder) i32) (result i32)
    (call_ref $held (local.get 1) (ref.as_non_null (struct.get $holder 0 (local.get 0)))))

  (func $same (export "same") (param (ref null $point)) (result (ref null $point))
    (local.get 0))

  (func (export "x") (param (ref $point)) (result i32)
    (struct.get $point 0 (local.get 0)))

  (func (export "next") (param (ref $point)) (result (ref null $point))
    (struct.get $point 5 (local.get 0)))

  (func (export "label") (param (ref $point)) (result externref)
    (struct.get $point 6 (local.get 0)))

  (func (export "make_bytes") (param i32) (result (ref $bytes))
    (array.new $bytes (i32.const 1) (local.get 0)))

  (func (export "sum_bytes") (param (ref $bytes)) (result i32)
    (local $index i32)
    (local $sum i32)
    (block $done
      (loop $next
        (br_if $done (i32.ge_u (local.get $index) (array.len (local.get 0))))
        (local.set $sum (i32.add (local.get $sum) (array.get_u $bytes (local.get 0) (local.get $index))))
        (local.set $index (i32.add (local.get $index) (i32.const 1)))
        (br $next)))
    (local.get $sum))

  (func (export "make_chars") (param i32) (result (ref $chars))
    (array.new $chars (i32.const -1) (local.get 0)))

  (func (export "char_at") (param (ref $chars) i32) (result i32)
    (array.get_u $chars (local.get 0) (local.get 1)))

  (func (export "make_longs") (param i32) (result (ref $longs))
    (array.new $longs (i64.const 7) (local.get 0)))

  (func (export "make_points") (param i32) (result (ref $points))
    (array.new_default $points (local.get 0)))

  (func (export "make_strict") (param (ref $point) i32) (result (ref $strict))
    (array.new $strict (local.get 0) (local.get 1)))

  (func (export "make_point3s") (param i32) (result (ref $point3s))
    (array.new_default $point3s (local.get 0)))

  (func (export "make_anys") (param i32) (result (ref $anys))
    (array.new_default $anys (local.get 0)))

  (func (export "point_at") (param (ref $points) i32) (result (ref null $point))
    (array.get $points (local.get 0) (local.get 1)))

  (func (export "frozen") (result (ref $frozen))
    (array.new_fixed $frozen 2 (i32.const 1) (i32.const 2)))

  (func (export "throw") (param i32 externref)
    (throw $boom (local.get 0) (local.get 1)))

  (func (export "throw_carry") (param (ref null $point))
    (throw $carry (local.get 0)))

  (func (export "throw_numbers") (param i32 i64)
    (throw $numbers (local.get 0) (local.get 1)))

  (func (export "rethrow") (param exnref)
    (throw_ref (local.get 0)))

  (func (export "catch_host") (param i32 externref) (result i32 externref)
    (block $caught (result i32 externref)
      (try_table (catch $boom $caught)
        (call $raise (local.get 0) (local.get 1)))
      (unreachable)))

  (func (export "swap") (param externref (ref null $point)) (result (ref null $point) externref)
    (local.get 1) (local.get 0))

  (func (export "tail") (param (ref null $point)) (result (ref null $point))
    (return_call $same (local.get 0)))

  (func (export "tail_host") (param (ref null $point)) (result (ref null $point))
    (return_call $echo (local.get 0)))

  (func (export "call_slot") (param i32 i32) (result i32)
    (call_indirect $functions (type $unary) (local.get 1) (local.get 0)))

  (func (export "slot") (param i32) (result (ref null $point))
    (table.get $slots (local.get 0)))

  (func (export "current_x") (result i32)
    (struct.get $point 0 (ref.as_non_null (global.get $current))))

  (func (export "read_count") (result i32)
    (global.get $count))

  (func (export "i31_double") (param i31ref) (result i31ref)
    (ref.i31 (i32.mul (i31.get_s (local.get 0)) (i32.const 2))))
)
