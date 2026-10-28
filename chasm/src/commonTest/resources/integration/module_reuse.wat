;; A memory and a table that grow, for instantiating one decoded module more than once.
(module
  (memory (export "memory") 1)
  (table (export "table") 1 funcref)
  (func (export "grow")
    (drop (memory.grow (i32.const 1)))
    (drop (table.grow (ref.null func) (i32.const 1))))
  (func (export "memory_size") (result i32)
    (memory.size))
  (func (export "table_size") (result i32)
    (table.size)))
