(module
   (func $int_function (export "int_function") (result i32)
     i32.const 0
   )

   (func $long_function (export "long_function") (result i64)
     i64.const 0
   )

   (func $float_function (export "float_function") (result f32)
     f32.const 0.0
   )

   (func $double_function (export "double_function") (result f64)
     f64.const 0.0
   )

   (func $string_function (export "string_function") (result i32 i32)
     i32.const 1
     i32.const 10
   )

   (func $unit_function (export "unit_function")
     nop
   )

   (func $multiple_param_function (export "multiple_param_function") (param i32 f64)
     nop
   )

   (func $multiple_return_function (export "multiple_return_function") (result i32 i64)
     i32.const 0
     i64.const 0
   )

  (global $mutable_global (export "mutable_global") (mut i32) (i32.const 117))
  (global $immutable_global (export "immutable_global") i32 (i32.const 117))
  (memory $memory (export "memory") 2)
  (table $table (export "table") 2 funcref)
  (tag $tag (export "tag") (param i64))
)
