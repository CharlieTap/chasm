(module
  (func (export "splat") (param i32) (result v128)
    local.get 0
    i32x4.splat)
)
