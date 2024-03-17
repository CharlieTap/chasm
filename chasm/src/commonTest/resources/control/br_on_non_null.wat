(module
  (type $t (func (result i32)))

  (func $n (param $r (ref null $t)) (result i32)
    (call_ref $t
      (block $l (result (ref $t))
        (br_on_non_null $l (local.get $r))
        (return (i32.const -1))
      )
    )
  )

  (func (export "br_on_non_null") (result i32) (call $n (ref.null $t)))
)
