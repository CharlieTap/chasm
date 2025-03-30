(module
  (func $blockadd (param $lhs i32) (param $rhs i32) (result i32)
    (block (result i32)
      local.get $lhs
      local.get $rhs
      i32.add
    )
  )
  (export "blockadd" (func $blockadd))
)
