(module
  (import "env" "imported_function" (func $function (param i32)))
  (import "env" "imported_global" (global $global i32))
  (import "env" "imported_memory" (memory $memory 1))
  (import "env" "imported_table" (table $table 1 funcref))
  (import "env" "imported_tag" (tag $tag (param i32)))

  (func $exported_function_name (export "exported_function") (param $a i32)(param $b i32))
  (global $exported_global_name (export "exported_global") (mut i32) (i32.const 117))
  (memory $exported_memory_name (export "exported_memory") 2)
  (table $exported_table_name (export "exported_table") 2 funcref)
  (tag $exported_tag_name (export "exported_tag") (param i64))
)
