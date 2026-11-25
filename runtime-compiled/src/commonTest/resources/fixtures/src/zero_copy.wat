(module
  (type $bytes (array (mut i8)))
  (table $items (export "items") 2 externref)

  (func (export "make_bytes") (param $length i32) (result (ref $bytes))
    (array.new_default $bytes (local.get $length)))

  (func (export "set_byte") (param $bytes (ref $bytes)) (param $index i32) (param $value i32)
    (array.set $bytes (local.get $bytes) (local.get $index) (local.get $value)))

  (func (export "get_byte") (param $bytes (ref $bytes)) (param $index i32) (result i32)
    (array.get_u $bytes (local.get $bytes) (local.get $index)))

  (func (export "set_item") (param $index i32) (param $value externref)
    (table.set $items (local.get $index) (local.get $value))))
