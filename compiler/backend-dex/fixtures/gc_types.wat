(module
  (type $shape (sub (struct (field $id i32))))
  (type $circle (sub final $shape (struct (field $id i32) (field $radius f64))))
  (rec
    (type $tree (struct (field $left (ref null $tree)) (field $leaf (ref null $leaf))))
    (type $leaf (struct (field $value (mut i64)) (field $packed (mut i8)) (field $wide (mut i16)))))
  (type $holder (struct (field $any anyref) (field $eq eqref) (field $i31 i31ref) (field $func funcref) (field $shape (ref null $shape))))
  (type $shapes (sub (array (ref null $shape))))
  (type $circles (sub final $shapes (array (ref null $circle))))
  (type $bytes (array (mut i8)))
  (type $same_shape (sub (struct (field i32))))
  (func (export "keep") (param (ref null $holder) (ref null $tree) (ref null $circles) (ref null $bytes) (ref null $same_shape)))
)
