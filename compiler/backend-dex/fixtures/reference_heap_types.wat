(module
  (func (export "extern_identity") (param i32 externref) (result externref)
    (local.get 1))
  (func (export "func_identity") (param i32 funcref) (result funcref)
    (local.get 1)))
