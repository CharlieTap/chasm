(module
  (type $void (func))
  (table 1 funcref)
  (elem (i32.const 0) $indirect)

  (func $spin (export "spin")
    (loop $continue
      (br $continue)))

  (func $count (export "count") (param $n i32) (result i32)
    (loop $continue
      (local.set $n (i32.sub (local.get $n) (i32.const 1)))
      (br_if $continue (local.get $n)))
    (local.get $n))

  (func $recurse (export "recurse")
    (call $recurse))

  (func $ping (export "ping")
    (return_call $pong))

  (func $pong
    (return_call $ping))

  (func $indirect (export "indirect")
    (call_indirect (type $void) (i32.const 0))))
