(module
  (import "host" "make" (func $make (result externref)))
  (import "host" "echo" (func $echo (param externref) (result externref)))
  (global $kept (export "kept") (mut externref) (ref.null extern))
  (func $start
    (global.set $kept (call $echo (call $make))))
  (start $start))
