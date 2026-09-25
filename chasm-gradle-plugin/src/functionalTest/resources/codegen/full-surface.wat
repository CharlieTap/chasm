(module
  (memory (export "memory") 1)

  (global (export "mutable_i32") (mut i32) (i32.const 1))
  (global (export "immutable_i64") i64 (i64.const 2))
  (global (export "mutable_f32") (mut f32) (f32.const 3))
  (global (export "immutable_f64") f64 (f64.const 4))

  (func (export "unit"))
  (func (export "numeric")
    (param i32 i64 f32 f64)
    (result i32 i64 f32 f64)
    local.get 0
    local.get 1
    local.get 2
    local.get 3)

  (func (export "alloc") (param i32) (result i32)
    i32.const 16)
  (func (export "free") (param i32))

  (func (export "pointer_string_param") (param i32 i32))
  (func (export "length_prefixed_string_param") (param i32))
  (func (export "null_terminated_string_param") (param i32))
  (func (export "packed_string_param") (param i64))
  (func (export "free_string_param") (param i32 i32))

  (func (export "pointer_string_return") (result i32 i32)
    i32.const 0
    i32.const 0)
  (func (export "length_prefixed_string_return") (result i32)
    i32.const 0)
  (func (export "null_terminated_string_return") (result i32)
    i32.const 0)
  (func (export "packed_string_return") (result i64)
    i64.const 0)
)
