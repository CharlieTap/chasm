(module
  (import "adapter" "call" (func $adapter (param i32) (result i32)))
  (func (export "call") (param i32) (result i32)
    local.get 0
    call $adapter))
