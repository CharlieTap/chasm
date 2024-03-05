(module
  (export "parametric" (func $parametric))

  (func $parametric (param $a i32) (param $b i32) (param $condition i32) (result i32)
    (i32.add (local.get $a) (local.get $b))
    (drop)

    (local.get $a)
    (local.get $b)
    (local.get $condition)
    (select)
  )
)
