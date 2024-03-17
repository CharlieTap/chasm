(module
  (type $t (func (result i32)))

  (func $n (param $r (ref null $t)) (result i32)
    (call_ref $t (ref.as_non_null (local.get $r)))
  )

  (elem func $f)
  (func $f (result i32) (i32.const 7))

  (func (export "ref_as_non_null") (result i32) (call $n (ref.func $f)))
)
