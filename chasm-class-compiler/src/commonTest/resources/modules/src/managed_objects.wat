(module
  (type $point (struct (field (mut i32)) (field (mut i32))))
  (type $wide (struct
    (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32))
    (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32))
    (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32))
    (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32))
    (field (mut i64)) (field (mut i8)) (field (mut f64)) (field (mut anyref))))
  (global $kept (mut (ref null $point)) (ref.null $point))

  (func (export "make_point") (param $x i32) (param $y i32) (result (ref $point))
    (struct.new $point (local.get $x) (local.get $y)))

  (func (export "keep") (param $point (ref null $point))
    (global.set $kept (local.get $point)))

  (func (export "kept") (result (ref null $point))
    (global.get $kept))

  (func (export "same") (param $left eqref) (param $right eqref) (result i32)
    (ref.eq (local.get $left) (local.get $right)))

  (func (export "x") (param $point (ref $point)) (result i32)
    (struct.get $point 0 (local.get $point)))

  (func (export "echo") (param $value externref) (result externref)
    (local.get $value))

  (func (export "make_wide") (param $long i64) (param $byte i32) (param $double f64) (result (ref $wide))
    (local $wide (ref null $wide))
    (local.set $wide (struct.new_default $wide))
    (struct.set $wide 22 (local.get $wide) (local.get $long))
    (struct.set $wide 23 (local.get $wide) (local.get $byte))
    (struct.set $wide 24 (local.get $wide) (local.get $double))
    (ref.as_non_null (local.get $wide)))

  (func (export "make_default_wide") (result (ref $wide))
    (struct.new_default $wide))

  (func (export "wide_long") (param $wide (ref $wide)) (result i64)
    (struct.get $wide 22 (local.get $wide)))

  (func (export "wide_byte_signed") (param $wide (ref $wide)) (result i32)
    (struct.get_s $wide 23 (local.get $wide)))

  (func (export "wide_byte_unsigned") (param $wide (ref $wide)) (result i32)
    (struct.get_u $wide 23 (local.get $wide)))

  (func (export "wide_double") (param $wide (ref $wide)) (result f64)
    (struct.get $wide 24 (local.get $wide))))
