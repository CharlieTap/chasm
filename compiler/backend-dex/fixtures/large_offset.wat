(module
  (memory 1)
  (func (export "store") (param $address i32)
    (i32.store offset=2147483648 (local.get $address) (i32.const 1))
    (i32.store offset=2147483648 (local.get $address) (i32.const 2))))
