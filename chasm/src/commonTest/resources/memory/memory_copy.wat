(module
  (memory $mem 2)

  (data (i32.const 0) "\00\94\35\77") ;; 2 000 000 000

  (func $memory_copy (param $src_offset i32) (param $dest_offset i32) (param $size i32) (result i32)
    local.get $dest_offset
    local.get $src_offset
    local.get $size
    memory.copy
    local.get $dest_offset
    i32.load
  )

  (export "memory_copy" (func $memory_copy))
)
