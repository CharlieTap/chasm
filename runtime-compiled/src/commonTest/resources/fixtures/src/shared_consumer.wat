(module
  (type $point (sub (struct (field i32) (field i32))))
  (type $point3 (sub $point (struct (field i32) (field i32) (field i32))))
  (import "producer" "make_point" (func $make (param i32 i32) (result (ref $point))))

  (func $sum (export "sum") (param $point (ref $point)) (result i32)
    (i32.add (struct.get $point 0 (local.get $point)) (struct.get $point 1 (local.get $point))))

  (func (export "make_point") (param $x i32) (param $y i32) (result (ref $point))
    (struct.new $point (local.get $x) (local.get $y)))

  (func (export "third") (param $value anyref) (result i32)
    (struct.get $point3 2 (ref.cast (ref $point3) (local.get $value))))

  (func (export "is_point3") (param $value anyref) (result i32)
    (ref.test (ref $point3) (local.get $value)))

  (func (export "sum_imported") (result i32)
    (call $sum (call $make (i32.const 2) (i32.const 3)))))
