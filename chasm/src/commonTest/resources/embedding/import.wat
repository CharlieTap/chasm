(module
  (import "env" "memory" (memory 1))
  (import "env" "println" (func $println (param i32)))

  (data (i32.const 0) "Hello World!\00")

  (func $import
    (call $println (i32.const 0))
  )

  (export "import" (func $import))
)
