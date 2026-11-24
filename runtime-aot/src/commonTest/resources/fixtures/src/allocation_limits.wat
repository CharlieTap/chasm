(module
  (type $bytes (array (mut i8)))
  (type $longs (array (mut i64)))
  (type $box (struct (field i32)))
  (global $kept (mut (ref null $box)) (ref.null $box))

  (func (export "bytes") (param $length i32) (result i32)
    (array.len (array.new_default $bytes (local.get $length))))

  (func (export "longs") (param $length i32) (result i32)
    (array.len (array.new_default $longs (local.get $length))))

  (func (export "keep") (param $value i32)
    (global.set $kept (struct.new $box (local.get $value))))

  (func (export "kept") (result i32)
    (struct.get $box 0 (global.get $kept))))
