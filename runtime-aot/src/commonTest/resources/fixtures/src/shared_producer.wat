(module
  (type $point (sub (struct (field i32) (field i32))))
  (type $point3 (sub $point (struct (field i32) (field i32) (field i32))))

  (func (export "make_point") (param $x i32) (param $y i32) (result (ref $point))
    (struct.new $point (local.get $x) (local.get $y)))

  (func (export "make_point3") (param $x i32) (param $y i32) (param $z i32) (result (ref $point))
    (struct.new $point3 (local.get $x) (local.get $y) (local.get $z))))
