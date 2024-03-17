(module
  (type $factorial_impl_type (func (param i64 i64) (result i64)))
  (table $func_table 1 1 funcref)

  (func $return_call_indirect (export "return_call_indirect") (param $n i64) (result i64)
    (local.get $n)
    (i64.const 1)
    (return_call_indirect (type $factorial_impl_type) (i32.const 0))
  )

  (func $factorial_impl (param $n i64) (param $acc i64) (result i64)
    (if (i64.eqz (local.get $n))
      (then
        (return (local.get $acc))
      )
    )
    (i64.sub (local.get $n) (i64.const 1))
    (i64.mul (local.get $n) (local.get $acc))
    (return_call_indirect (type $factorial_impl_type) (i32.const 0))
  )
  (elem (i32.const 0) $factorial_impl)
)
