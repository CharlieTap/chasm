(module
  (func $dummyFunction)

  (table 4 4 funcref)

  (elem (i32.const 0) $dummyFunction)
  (elem (i32.const 1) $dummyFunction)

  (func (export "table_copy") (result funcref)
    i32.const 2
    i32.const 0
    i32.const 2
    table.copy
    i32.const 3
    table.get
  )
)
