(module
  (type $ii (func (param i32) (result i32)))

  (func $f (type $ii) (i32.mul (local.get 0) (local.get 0)))
  (func $g (type $ii) (i32.sub (i32.const 0) (local.get 0)))

  (elem declare func $f $g)

  (func (export "call_ref") (param $x i32) (result i32)
    (local $rf (ref null $ii))
    (local $rg (ref null $ii))
    (local.set $rf (ref.func $f))
    (local.set $rg (ref.func $g))
    (call_ref $ii (call_ref $ii (local.get $x) (local.get $rf)) (local.get $rg))
  )
)
