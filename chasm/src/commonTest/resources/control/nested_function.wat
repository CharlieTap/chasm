(module
  (func $inner_function (param $x i32) (result i32)
    (if (result i32)
      (i32.lt_s (local.get $x) (i32.const 10))
      (then (i32.const 1))
      (else (i32.const 0))
    )
  )

  (func $nested_function (param $x i32) (result i32)
    (call $inner_function (local.get $x))
  )
  (export "nested_function" (func $nested_function))
)
