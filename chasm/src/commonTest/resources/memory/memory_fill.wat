(module
  (memory $mem 1)

  (func $memory_fill (param $offset i32) (param $value i32) (param $length i32) (result i32)

    local.get $offset
    local.get $value
    local.get $length

    memory.fill

    local.get $offset
    i32.load8_u
  )

  (export "memory_fill" (func $memory_fill))
)
