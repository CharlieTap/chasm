(module $mixed
  (import "owner" "boom" (func $boom))

  (func $call_boom
    call $boom)

  (func $call_owner (export "call_owner")
    call $call_boom)
)
