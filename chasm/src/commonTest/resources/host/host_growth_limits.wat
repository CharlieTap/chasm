(module
  (import "env" "grow" (func $grow))
  (memory (export "memory") 1 10)
  (table (export "table") 2 externref)
  (func (export "run")
    call $grow)
)
