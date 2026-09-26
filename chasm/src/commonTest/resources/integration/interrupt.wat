(module
  (import "env" "interrupt" (func $interrupt))
  (import "env" "callback" (func $callback))

  (func $spin (export "spin")
    (loop $continue
      (br $continue)))

  (func $interrupt_then_spin (export "interrupt_then_spin")
    (call $interrupt)
    (loop $continue
      (br $continue)))

  (func $callback_then_spin (export "callback_then_spin")
    (call $callback)
    (loop $continue
      (br $continue)))

  (func $count (export "count") (param $n i32) (result i32)
    (loop $continue
      (local.set $n (i32.sub (local.get $n) (i32.const 1)))
      (br_if $continue (local.get $n)))
    (local.get $n)))
