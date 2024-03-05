(module
  (export "reference" (func $reference))

  (func $reference (result i32)
    (local $result i32)

    ref.null func
    ref.is_null
    (if (then
      (local.set $result (i32.add (local.get $result) (i32.const 5)))
    ))

    local.get $result
  )
)
