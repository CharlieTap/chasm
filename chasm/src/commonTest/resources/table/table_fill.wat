(module
  (func $fillFunction)

  (table 3 3 funcref)

  (elem (i32.const 0) $fillFunction)

  (func (export "table_fill") (result funcref)
    i32.const 0
    ref.func $fillFunction
    i32.const 3
    table.fill
    i32.const 2
    table.get
  )
)
