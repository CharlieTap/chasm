(module
  (func (export "nested_return") (param $x i32) (result i32)
    (if (i32.lt_s (local.get $x) (i32.const 5))
      (return (i32.const 1))
    )
    (i32.const 0)
  )
)
