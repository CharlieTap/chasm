(module $consumer
  (type $point (struct (field $x (mut i32)) (field $y (mut i32))))
  (type $flags (struct (field (mut i32)) (field i32)))
  (type $shape (sub (struct (field $sides (mut i32)))))
  (type $square (sub $shape (struct (field $sides (mut i32)) (field $edge (mut i32)))))
  (type $sealed (struct (field i32)))

  (import "producer" "boom" (func $boom (result i32)))

  (func (export "is_point") (param anyref) (result i32)
    (ref.test (ref $point) (local.get 0)))

  (func (export "point_y") (param anyref) (result i32)
    (struct.get $point $y (ref.cast (ref $point) (local.get 0))))

  (func (export "shape_sides") (param anyref) (result i32)
    (struct.get $shape $sides (ref.cast (ref $shape) (local.get 0))))

  (func (export "flags_first") (param anyref) (result i32)
    (struct.get $flags 0 (ref.cast (ref $flags) (local.get 0))))

  (func (export "is_sealed") (param anyref) (result i32)
    (ref.test (ref $sealed) (local.get 0)))

  (func $run (export "run") (result i32)
    (call $boom)))
