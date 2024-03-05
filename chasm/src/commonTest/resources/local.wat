(module
  (func $local (export "local") (param $x i32) (result i32)
    (local $y i32)
    local.get $x
    i32.const 2
    i32.mul
    local.tee $y
    i32.const 5
    i32.add
    local.set $y
    local.get $y
  )
)
