(module $start_trap
  (func $boom unreachable)
  (func $start (call $boom))
  (start $start))
