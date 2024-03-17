(module
  (type $t (func (result i32)))

  (func $n (param $r (ref null $t)) (result i32)
    (block $l
      (return (call_ref $t (br_on_null $l (local.get $r))))
    )
    (i32.const -1)
  )
  (func (export "br_on_null") (result i32) (call $n (ref.null $t)))
)
