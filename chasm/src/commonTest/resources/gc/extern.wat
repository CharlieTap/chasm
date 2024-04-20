(module
  (type $ft (func))
  (type $st (struct))
  (type $at (array i8))

  (table 10 anyref)

  (elem declare func $f)
  (func $f)

  (func (export "init") (param $x externref)
    (table.set (i32.const 0) (ref.null any))
    (table.set (i32.const 1) (ref.i31 (i32.const 7)))
    (table.set (i32.const 2) (struct.new_default $st))
    (table.set (i32.const 3) (array.new_default $at (i32.const 0)))
    (table.set (i32.const 4) (any.convert_extern (local.get $x)))
  )

  (func (export "internalize") (param externref) (result anyref)
    (any.convert_extern (local.get 0))
  )
  (func (export "externalize") (param anyref) (result externref)
    (extern.convert_any (local.get 0))
  )

  (func (export "externalize-i") (param i32) (result externref)
    (extern.convert_any (table.get (local.get 0)))
  )
  (func (export "externalize-ii") (param i32) (result anyref)
    (any.convert_extern (extern.convert_any (table.get (local.get 0))))
  )
)
