(module
  (func (export "local_tee") (result i32)
    (local $x i32)
    i32.const 117
    local.tee $x
    local.get $x
    i32.add
  )
)
