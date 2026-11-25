(module
  (table (export "table") 2 funcref)
  (global (export "refs") (mut externref) (ref.null extern))
  (global (export "count") (mut i32) (i32.const 0)))
