(module
  (import "env" "interrupt" (func $interrupt))
  (import "env" "callback" (func $callback))
  (import "env" "keep_running" (func $keep_running (result i32)))
  (export "callback" (func $callback))

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

  (func $spin_while_host_allows (export "spin_while_host_allows")
    (loop $continue
      (br_if $continue (call $keep_running))))

  (func $count (export "count") (param $n i32) (result i32)
    (loop $continue
      (local.set $n (i32.sub (local.get $n) (i32.const 1)))
      (br_if $continue (local.get $n)))
    (local.get $n)))
