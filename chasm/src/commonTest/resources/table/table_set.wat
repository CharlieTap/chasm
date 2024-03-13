(module
  (type $no_args_no_return (func))

  (func $initial (type $no_args_no_return))
  (func $successor (result i32)
    (i32.const 117)
  )

  (table 2 2 funcref)
  (elem (i32.const 0) $initial)
  (elem (i32.const 1) $successor)

  (func (export "table_set") (result funcref)
     i32.const 0
     ref.func $successor
     table.set
     i32.const 0
     table.get
  )
)
