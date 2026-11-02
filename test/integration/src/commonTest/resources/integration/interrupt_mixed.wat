(module
  (import "env" "interrupt" (func $interrupt))
  (import "owner" "spin" (func $spin))

  (func (export "interrupt_then_owner_spin")
    (call $interrupt)
    (call $spin)))
