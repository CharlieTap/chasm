(module
  (type $bytes (array (mut i8)))
  (type $node (struct (field (ref null $node)) (field (ref $bytes))))
  (memory (export "memory") 1 10)
  (table (export "table") 1 funcref)
  (global $kept (mut (ref null $bytes)) (ref.null $bytes))
  (global $head (mut (ref null $node)) (ref.null $node))

  ;; Allocates $count arrays of $length bytes, keeping only the latest reachable.
  (func (export "churn") (param $count i32) (param $length i32)
    (loop $continue
      (global.set $kept (array.new_default $bytes (local.get $length)))
      (local.set $count (i32.sub (local.get $count) (i32.const 1)))
      (br_if $continue (local.get $count))))

  ;; Allocates $count arrays of $length bytes, keeping all of them reachable.
  (func (export "retain") (param $count i32) (param $length i32)
    (loop $continue
      (global.set $head
        (struct.new $node
          (global.get $head)
          (array.new_default $bytes (local.get $length))))
      (local.set $count (i32.sub (local.get $count) (i32.const 1)))
      (br_if $continue (local.get $count))))

  (func (export "release")
    (global.set $head (ref.null $node)))

  (func (export "memory-grow") (param $pages i32) (result i32)
    (memory.grow (local.get $pages)))

  (func (export "table-grow") (param $elements i32) (result i32)
    (table.grow (ref.null func) (local.get $elements))))
