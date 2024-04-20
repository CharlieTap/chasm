(module
  (func (export "new") (param $i i32) (result (ref i31))
    (ref.i31 (local.get $i))
  )

  (func (export "get_u") (param $i i32) (result i32)
    (i31.get_u (ref.i31 (local.get $i)))
  )
  (func (export "get_s") (param $i i32) (result i32)
    (i31.get_s (ref.i31 (local.get $i)))
  )

  (func (export "get_u-null") (result i32)
    (i31.get_u (ref.null i31))
  )
  (func (export "get_s-null") (result i32)
    (i31.get_u (ref.null i31))
  )

  (global $i (ref i31) (ref.i31 (i32.const 2)))
  (global $m (mut (ref i31)) (ref.i31 (i32.const 3)))

  (func (export "get_globals") (result i32 i32)
    (i31.get_u (global.get $i))
    (i31.get_u (global.get $m))
  )

  (func (export "set_global") (param i32)
    (global.set $m (ref.i31 (local.get 0)))
  )
)
