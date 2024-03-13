(module
  (global $global (mut i32) (i32.const 117))

  (func (export "global_get") (result i32)
    global.get $global
  )
)
