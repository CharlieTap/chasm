(module
  (func (export "select") (param $a i32) (param $b i32) (param $condition i32) (result i32)
    (local.get $a)
    (local.get $b)
    (local.get $condition)
    (select)
  )
)
