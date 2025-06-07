(module
  (global $mutable_global (export "mutable_global") (mut i32) (i32.const 117))
  (global $immutable_global (export "immutable_global") i32 (i32.const 117))
  (memory $memory (export "memory") 2)
  (table $table (export "table") 2 funcref)
  (tag $tag (export "tag") (param i64))

  (data (i32.const 1) "Hello WASM")

   (func $int_function (export "int_function") (result i32)
     i32.const 1
   )

   (func $long_function (export "long_function") (result i64)
     i64.const 2
   )

   (func $float_function (export "float_function") (result f32)
     f32.const 3.1
   )

   (func $double_function (export "double_function") (result f64)
     f64.const 4.2
   )

   (func $string_function (export "string_function") (result i32 i32)
     i32.const 1
     i32.const 10
   )

   (func $unit_function (export "unit_function")
     i32.const 316
     global.set $mutable_global
   )

   (func $multiple_param_function (export "multiple_param_function") (param i32 f64) (result f64)
     local.get 0
     f64.convert_i32_s
     local.get 1
     f64.mul
   )

   (func $multiple_return_function (export "multiple_return_function") (result i32 i64)
     i32.const 117
     i64.const 118
   )
)
