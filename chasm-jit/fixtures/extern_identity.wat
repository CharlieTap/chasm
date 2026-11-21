(module
  (type $box (struct (field i32)))
  (type $all (struct
    (field (mut i8)) (field (mut i16)) (field (mut i32)) (field (mut i64))
    (field (mut f32)) (field (mut f64)) (field (mut (ref null $all))) (field (mut externref))))
  (global $saved (mut externref) (ref.null extern))
  (table $things 4 externref)

  (func (export "identity") (param externref) (result externref)
    (local.get 0))

  (func (export "through_any") (param externref) (result externref)
    (extern.convert_any (any.convert_extern (local.get 0))))

  (func (export "save") (param externref)
    (global.set $saved (local.get 0)))

  (func (export "saved") (result externref)
    (global.get $saved))

  (func (export "put") (param externref)
    (table.set $things (i32.const 1) (local.get 0)))

  (func (export "take") (result externref)
    (table.get $things (i32.const 1)))

  (func (export "casts") (param externref) (result i32)
    (local $value anyref)
    (local.set $value (any.convert_extern (local.get 0)))
    (i32.or
      (i32.or
        (i32.or
          (i32.or
            (ref.test (ref any) (local.get $value))
            (i32.shl (ref.test (ref eq) (local.get $value)) (i32.const 1)))
          (i32.shl (ref.test (ref struct) (local.get $value)) (i32.const 2)))
        (i32.shl (ref.test (ref array) (local.get $value)) (i32.const 3)))
      (i32.shl (ref.test (ref i31) (local.get $value)) (i32.const 4))))

  (func (export "make_box") (result (ref $box))
    (struct.new $box (i32.const 5)))

  (func (export "takes_box") (param (ref $box)) (result i32)
    (struct.get $box 0 (local.get 0)))

  (func (export "takes_nullable_box") (param (ref null $box)) (result i32)
    (ref.is_null (local.get 0)))

  (func (export "make_all") (param i32 i32 i32 i64 f32 f64 externref) (result (ref $all))
    (local $all (ref $all))
    (local.set $all
      (struct.new $all
        (local.get 0) (local.get 1) (local.get 2) (local.get 3)
        (local.get 4) (local.get 5) (ref.null $all) (local.get 6)))
    (struct.set $all 6 (local.get $all) (local.get $all))
    (local.get $all))

  (func (export "read_i8") (param (ref $all)) (result i32) (struct.get_s $all 0 (local.get 0)))
  (func (export "read_i16") (param (ref $all)) (result i32) (struct.get_u $all 1 (local.get 0)))
  (func (export "read_i32") (param (ref $all)) (result i32) (struct.get $all 2 (local.get 0)))
  (func (export "read_i64") (param (ref $all)) (result i64) (struct.get $all 3 (local.get 0)))
  (func (export "read_f32") (param (ref $all)) (result f32) (struct.get $all 4 (local.get 0)))
  (func (export "read_f64") (param (ref $all)) (result f64) (struct.get $all 5 (local.get 0)))
  (func (export "read_self") (param (ref $all)) (result (ref null $all)) (struct.get $all 6 (local.get 0)))
  (func (export "read_extern") (param (ref $all)) (result externref) (struct.get $all 7 (local.get 0)))

  (func (export "i31_unsigned") (param i31ref) (result i32)
    (i31.get_u (local.get 0)))

  (func (export "same_i31") (param i31ref) (result i32)
    (ref.eq (local.get 0) (ref.i31 (i32.const -1))))

  (func (export "takes_eq") (param eqref) (result i32)
    (ref.is_null (local.get 0)))
)
