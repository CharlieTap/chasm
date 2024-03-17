(module
  (type $factorialType (func (param i64 i64) (result i64)))

  (func $return_call_ref (export "return_call_ref") (param $n i64) (result i64)
    (return_call $factorial (local.get $n) (i64.const 1))
  )

  (func $factorial (param $n i64) (param $acc i64) (result i64)
    (if (i64.eqz (local.get $n))
      (then
        (return (local.get $acc))
      )
    )
    (return_call_ref $factorialType
      (i64.sub (local.get $n) (i64.const 1))
      (i64.mul (local.get $n) (local.get $acc))
      (ref.func $factorial)
    )
  )
  (elem declare func $factorial)
)
