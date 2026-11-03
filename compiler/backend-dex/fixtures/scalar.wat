(module
  (func (export "add") (param i32 i32) (result i32)
    local.get 0
    local.get 1
    i32.add)

  (func (export "mix") (param i64 f64) (result f64)
    local.get 0
    f64.convert_i64_s
    local.get 1
    f64.mul
    f64.sqrt)

  (func (export "div_u") (param i32 i32) (result i32)
    local.get 0
    local.get 1
    i32.div_u)

  (func (export "lt_u") (param i64 i64) (result i32)
    local.get 0
    local.get 1
    i64.lt_u)

  (func (export "pick") (param i32 f32 f32) (result f32)
    local.get 1
    local.get 2
    local.get 0
    select)

  (func (export "trunc") (param f32) (result i32)
    local.get 0
    i32.trunc_f32_s)

  (func (export "trap")
    unreachable)
)
