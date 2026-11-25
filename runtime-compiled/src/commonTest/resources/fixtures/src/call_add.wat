(module
  (import "add" "add" (func $add (param i32 i32) (result i32)))
  (func (export "call_add") (param i32 i32) (result i32)
    local.get 0
    local.get 1
    call $add)
  (func (export "tail_add") (param i32 i32) (result i32)
    local.get 0
    local.get 1
    return_call $add)
)
