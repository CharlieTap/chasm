(module
  (import "host" "table" (table 2 funcref))
  (import "host" "refs" (global (mut externref)))
  (export "table" (table 0))
  (export "refs" (global 0)))
