(module
  (func (export "increment") (param i32) (result i32)
    local.get 0
    i32.const 1
    i32.add
  )
)
