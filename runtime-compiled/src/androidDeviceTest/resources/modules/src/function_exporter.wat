(module
  (type $sig (func (param i32) (result i32)))
  (func $double (export "double") (type $sig)
    (i32.mul (local.get 0) (i32.const 2)))
  (elem declare func $double)
  (func (export "double_ref") (result funcref)
    (ref.func $double))
)
