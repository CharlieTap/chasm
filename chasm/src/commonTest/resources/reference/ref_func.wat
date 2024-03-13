(module
  (table 1 1 funcref)

  (func $reference)

  (elem (i32.const 0) $reference)

  (func (export "ref_func") (result funcref)
    ref.func $reference
  )
)
