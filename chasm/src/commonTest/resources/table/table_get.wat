(module
  (type $funcType (func (result i32)))

  (table 1 1 funcref)

  (func $indirectFunction (type $funcType) (result i32)
    (i32.const 117)
  )

  (elem (i32.const 0) $indirectFunction)

  (func (export "table_get") (result funcref)
    i32.const 0
    table.get
  )
)
