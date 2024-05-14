(module
  (global $global_i32 (export "global_i32") (mut i32) (i32.const 666))
  (global $global_i64 (export "global_i64") (mut i64) (i64.const 666))
  (global $global_f32 (export "global_f32") (mut f32) (f32.const 666.6))
  (global $global_f64 (export "global_f64") (mut f64) (f64.const 666.6))

  (table (export "table") 10 20 funcref)

  (memory (export "memory") 1 2)

  (func $print (export "print"))
  (func $print_i32 (export "print_i32") (param i32))
  (func $print_i64 (export "print_i64") (param i64))
  (func $print_f32 (export "print_f32") (param f32))
  (func $print_f64 (export "print_f64") (param f64))
  (func $print_i32_f32 (export "print_i32_f32") (param i32 f32))
  (func $print_f64_f64 (export "print_f64_f64") (param f64 f64))
)

