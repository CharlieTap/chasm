(module
  (func $function)
  (table $table 1 funcref)
  (memory $memory 1)
  (global $global (mut i32) (i32.const 0))

  (export "function" (func $function))
  (export "table" (table $table))
  (export "memory" (memory $memory))
  (export "global" (global $global))
)
