(module
  (tag $boom (param i32))

  (func $start
    i32.const 7
    throw $boom)

  (start $start)
)
