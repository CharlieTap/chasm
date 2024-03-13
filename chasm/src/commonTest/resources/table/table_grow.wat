(module

  (table 1 117 funcref)

  (func (export "table_grow") (result i32)
    ref.null func
    i32.const 116
    table.grow

    drop

    table.size
  )
)
