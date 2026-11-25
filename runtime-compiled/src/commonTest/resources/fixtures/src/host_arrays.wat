(module
  (type $point (sub (struct (field (mut i32)))))
  (type $point3 (sub $point (struct (field (mut i32)) (field (mut i32)))))
  (type $points (array (mut (ref null $point))))
  (type $point3s (array (mut (ref null $point3))))
  (type $strict (array (mut (ref $point))))
  (type $anys (array (mut anyref)))
  (type $strict_anys (array (mut (ref any))))

  (func (export "point") (param i32) (result anyref)
    (struct.new $point (local.get 0)))

  (func (export "point3") (param i32) (result anyref)
    (struct.new $point3 (local.get 0) (local.get 0)))

  (func (export "make_points") (param i32) (result anyref)
    (array.new_default $points (local.get 0)))

  (func (export "make_point3s") (param i32) (result anyref)
    (array.new_default $point3s (local.get 0)))

  (func (export "make_strict") (param i32) (result anyref)
    (array.new $strict (struct.new $point (i32.const 0)) (local.get 0)))

  (func (export "make_anys") (param i32) (result anyref)
    (array.new_default $anys (local.get 0)))

  (func (export "make_strict_anys") (param i32) (result anyref)
    (array.new $strict_anys (struct.new $point (i32.const 0)) (local.get 0)))

  (func (export "any_length") (param anyref) (result i32)
    (array.len (ref.cast (ref $anys) (local.get 0)))))
