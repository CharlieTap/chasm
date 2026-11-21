(module
  (global $count (export "count") (mut i32) (i32.const 0))
  (func (export "bump")
    (global.set $count (i32.add (global.get $count) (i32.const 1)))))
