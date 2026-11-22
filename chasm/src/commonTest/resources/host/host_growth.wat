(module
  (import "env" "grow" (func $grow))
  (memory (export "decoy") 1)
  (memory (export "target") 1 3)
  (table (export "table") 2 4 externref)
  (func (export "run")
    call $grow)
)
