(module
  (func $fib_rec (param $n i32) (param $a i32) (param $b i32) (result i32)
    (if (i32.eqz (local.get $n))
      (then (return (local.get $a)))
    )
    (return_call $fib_rec
      (i32.sub (local.get $n) (i32.const 1))
      (local.get $b)
      (i32.add (local.get $a) (local.get $b))
    )
  )

  (func $fib (param $n i32) (result i32)
    (call $fib_rec (local.get $n) (i32.const 0) (i32.const 1))
  )

  (export "fibonacci" (func $fib))
)
