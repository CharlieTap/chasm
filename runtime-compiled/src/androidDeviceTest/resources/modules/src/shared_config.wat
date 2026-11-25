(module
  (memory (export "memory") 1)
  (table (export "table") 1 funcref)

  (func (export "spin")
    (loop $continue
      (br $continue)))

  (func (export "memory-grow") (param $pages i32) (result i32)
    (memory.grow (local.get $pages)))

  (func (export "table-grow") (param $elements i32) (result i32)
    (table.grow (ref.null func) (local.get $elements))))
