(module
  (type $arr8 (array i8))
  (type $arr8_mut (array (mut i8)))

  (global $g_arr8 (ref $arr8) (array.new $arr8 (i32.const 10) (i32.const 12)))
  (global $g_arr8_mut (mut (ref $arr8_mut)) (array.new_default $arr8_mut (i32.const 12)))

  (func (export "array_get_nth") (param $1 i32) (result i32)
    (array.get_u $arr8_mut (global.get $g_arr8_mut) (local.get $1))
  )

  (func (export "array_fill-null")
    (array.fill $arr8_mut (ref.null $arr8_mut) (i32.const 0) (i32.const 0) (i32.const 0))
  )

  (func (export "array_fill") (param $1 i32) (param $2 i32) (param $3 i32)
    (array.fill $arr8_mut (global.get $g_arr8_mut) (local.get $1) (local.get $2) (local.get $3))
  )
)
