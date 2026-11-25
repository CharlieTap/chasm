(module
  (type $point (struct (field $x (mut i32)) (field $y (mut i64)) (field $r (mut anyref))))
  (type $base (sub (struct (field $a (mut i32)))))
  (type $mid (sub $base (struct (field $a (mut i32)) (field $b (mut i32)))))
  (type $leaf (sub $mid (struct (field $a (mut i32)) (field $b (mut i32)) (field $c (mut i32)))))
  (type $packed (struct (field $p (mut i8)) (field $q (mut i16))))
  (type $spilling (struct
    (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32))
    (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32))
    (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32))
    (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32))
    (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32))))
  (type $sealed (struct (field i32)))
  (type $bytes (array (mut i8)))

  (func (export "struct_new") (param i32 i64 anyref) (result anyref)
    (struct.new $point (local.get 0) (local.get 1) (local.get 2)))

  (func (export "get_i64") (param (ref $point)) (result i64)
    (struct.get $point $y (local.get 0)))

  (func (export "get_inherited") (param (ref $leaf)) (result i32)
    (struct.get $leaf $a (local.get 0)))

  (func (export "set_packed") (param (ref $packed) i32)
    (struct.set $packed $p (local.get 0) (local.get 1)))

  (func (export "get_spilled") (param (ref $spilling)) (result i32)
    (struct.get $spilling 24 (local.get 0)))

  (func (export "cast_final") (param anyref) (result anyref)
    (ref.cast (ref $sealed) (local.get 0)))

  (func (export "test_depth") (param anyref) (result i32)
    (ref.test (ref $leaf) (local.get 0)))

  (func (export "array_new") (param i32) (result anyref)
    (array.new_default $bytes (local.get 0)))

  (func (export "test_twice") (param anyref anyref) (result i32)
    (i32.add (ref.test (ref $sealed) (local.get 0)) (ref.test (ref $sealed) (local.get 1))))
)
