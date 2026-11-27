(module
  (type $first (struct (field i32)))
  (type $second (struct (field i32)))
  (type $takes_first (func (param (ref null $first)) (result i32)))
  (type $takes_second (func (param (ref null $second)) (result i32)))
  (func (type $takes_first) (i32.const 1))
  (func (type $takes_second) (i32.const 2)))
