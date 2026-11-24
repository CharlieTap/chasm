(module
  (type $point (struct (field i32)))
  (type $callback (func (param i32) (result i32)))
  (type $points (array (ref null $point)))
)
