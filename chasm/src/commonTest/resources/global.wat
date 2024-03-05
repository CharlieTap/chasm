(module
  (global $global1 (mut i32) (i32.const 7))
  (global $global2 (mut i32) (i32.const 6))

  (func $global (export "global") (result i32)
    global.get $global1
    (i32.const 2)
    i32.mul
    global.set $global1
    global.get $global1
    global.get $global2
    i32.add
  )
)
