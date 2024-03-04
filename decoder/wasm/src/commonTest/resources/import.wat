(module
  (import "env" "externalFunction" (func $externalFunction (result i32)))
  (import "env" "externalTable" (table $externalTable 1 2 funcref))
  (import "env" "externalMemory" (memory $externalMemory 1 2))
  (import "env" "externalGlobal" (global $externalGlobal i32))
)
