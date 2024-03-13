(module
  (func $loops_and_labels (result i32)
    (local $i i32)
    (local.set $i (i32.const 1))
    (loop $label
      (local.get $i)
      (i32.const 10)
      (i32.lt_s)
      (if
        (then
          (local.set $i (i32.add (local.get $i) (i32.const 1)))
          (br $label)
        )
      )
    )
    (local.get $i)
  )
  (export "loops_and_labels" (func $loops_and_labels))
)
