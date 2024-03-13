(module
  (memory $mem 1)

  (data (i32.const 117) "\75")

  (func $memory_init (result i32)
    i32.const 117
    i32.load8_u
  )
  (export "memory_init" (func $memory_init))
)
