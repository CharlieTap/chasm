(module
  (memory 1)

  (data (i32.const 0) "\75") ;;75 hex is 117

  (func $memory_load_store (result i32)
    (local $tmp i32)
    (i32.const 0)
    (i32.load)

    (i32.const 1)
    (i32.add)

    local.set $tmp
    (i32.const 4)
    local.get $tmp

    (i32.store)

    (i32.const 4)
    (i32.load)
  )
  (export "memory_load_store" (func $memory_load_store))
)
