(module
  (memory i64 1)

  (data (i64.const 1) "\2A\00\00\00")

  (func (export "read") (result i32)
    i64.const 1
    i32.load
  )
)
