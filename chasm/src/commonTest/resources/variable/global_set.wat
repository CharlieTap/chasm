(module
  (global $global (mut i32) (i32.const 0))

  (func (export "global_set") (result i32)
    i32.const 117
    global.set $global
    global.get $global
  )
)
