(module
  (tag $failed (export "failed") (param i32))
  (func $start (throw $failed (i32.const 7)))
  (start $start))
