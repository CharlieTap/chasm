(module
  (import "env" "someString" (func $someString (result externref)))
  (import "env" "len" (func $len (param externref) (result i32)))

  (func $main (export "main") (result i32)
    call $someString
    call $len
  )
)
