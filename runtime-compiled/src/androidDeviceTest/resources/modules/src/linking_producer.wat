(module $producer
  (type $point (struct (field $x (mut i32)) (field $y (mut i32))))
  (type $shape (sub (struct (field $sides (mut i32)))))
  (type $square (sub $shape (struct (field $sides (mut i32)) (field $edge (mut i32)))))
  (type $sealed (struct (field i32)))

  (func (export "point") (param i32 i32) (result anyref)
    (struct.new $point (local.get 0) (local.get 1)))

  (func (export "square") (param i32 i32) (result anyref)
    (struct.new $square (local.get 0) (local.get 1)))

  (func (export "sealed") (param i32) (result anyref)
    (struct.new $sealed (local.get 0)))

  (func $boom (export "boom") (result i32)
    (unreachable)))
