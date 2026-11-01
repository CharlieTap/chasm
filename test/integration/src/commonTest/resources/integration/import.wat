(module
  (import "env" "function" (func $function (param i32)))
  (import "env" "global" (global $global i32))
  (import "env" "memory" (memory $memory 1))
  (import "env" "table" (table $table 1 funcref))
  (import "env" "tag" (tag $tag (param i32)))

  (data (i32.const 0) "Hello World!\00")

  (func $import
    (call $function (i32.const 0))
  )

  (export "import" (func $import))
)
