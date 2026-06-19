(module
  (global $mutable_global (export "mutable_global") (mut i32) (i32.const 117))
  (global $immutable_global (export "immutable_global") i64 (i64.const 118))
  (memory $memory (export "memory") 2)
  (table $table (export "table") 2 funcref)

  (data (i32.const 1) "pointer and length")
  (data (i32.const 36) "\0F\00\00\00length prefixed")
  (data (i32.const 80) "null terminated\00")
  (data (i32.const 120) "packed pointer and length")

  (func $int_function (export "int_function") (result i32)
    i32.const 1
  )

  (func $long_function (export "long_function") (result i64)
    i64.const 2
  )

  (func $float_function (export "float_function") (result f32)
    f32.const 3.25
  )

  (func $double_function (export "double_function") (result f64)
    f64.const 4.5
  )

  (func $identity_i32 (export "identity_i32") (param $value i32) (result i32)
    local.get $value
  )

  (func $identity_i64 (export "identity_i64") (param $value i64) (result i64)
    local.get $value
  )

  (func $identity_f32 (export "identity_f32") (param $value f32) (result f32)
    local.get $value
  )

  (func $identity_f64 (export "identity_f64") (param $value f64) (result f64)
    local.get $value
  )

  (func $mixed_numeric_function (export "mixed_numeric_function")
    (param $int_value i32)
    (param $long_value i64)
    (param $float_value f32)
    (param $double_value f64)
    (result f64)
    local.get $int_value
    f64.convert_i32_s
    local.get $long_value
    f64.convert_i64_s
    f64.add
    local.get $float_value
    f64.promote_f32
    f64.add
    local.get $double_value
    f64.add
  )

  (func $pointer_and_length_string (export "pointer_and_length_string") (result i32 i32)
    i32.const 1
    i32.const 18
  )

  (func $length_prefixed_string (export "length_prefixed_string") (result i32)
    i32.const 36
  )

  (func $null_terminated_string (export "null_terminated_string") (result i32)
    i32.const 80
  )

  (func $packed_i64_string (export "packed_i64_string") (result i64)
    i64.const 0x0000007800000019
  )

  (func $unit_function (export "unit_function")
    i32.const 316
    global.set $mutable_global
  )

  (func $interop_multiple_return (export "interop_multiple_return") (result i32 i64 f32 f64)
    i32.const 117
    i64.const 118
    f32.const 119.5
    f64.const 120.25
  )
)
