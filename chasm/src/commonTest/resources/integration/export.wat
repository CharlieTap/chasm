(module
  (func $exported_function (export "exported_function"))
  (global $exported_global (export "exported_global") (mut i32) (i32.const 117))
  (memory $exported_memory (export "exported_memory") 2)
  (table $exported_table (export "exported_table") 2 funcref)
  (tag $exported_tag (export "exported_tag") (param i64))
)
