(module
  (func (export "local_set") (result i32)
    (local $x i32)
    i32.const 117
    local.set $x
    local.get $x
  )
)
