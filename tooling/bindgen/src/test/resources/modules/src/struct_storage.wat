(module
  (type $bytes (array (mut i8)))
  (type $base (sub (struct (field (mut i32)))))
  (type $derived (sub final $base (struct
    (field (mut i32))
    (field (mut (ref null $base)))
    (field (mut (ref null $bytes))))))
  (type $wide (struct
    (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32))
    (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32))
    (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32))
    (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32))
    (field (mut i64)) (field (mut i8)) (field (mut f64)) (field (mut (ref null $base)))))

  (func (export "make_derived") (param i32) (result (ref $derived))
    (struct.new $derived (local.get 0) (ref.null $base) (ref.null $bytes)))
  (func (export "make_wide") (result (ref $wide))
    (struct.new_default $wide)))
