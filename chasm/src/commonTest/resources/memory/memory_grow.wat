(module
  (memory $mem 1)

  (func $memory_grow (param $pages i32) (result i32)
    local.get $pages
    memory.grow

    drop

    memory.size
  )
  (export "memory_grow" (func $memory_grow))
)
