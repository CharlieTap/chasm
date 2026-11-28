(module
  (rec
    (type $header (sub (struct (field (mut i32)) (field (mut anyref)) (field (mut anyref)) (field (mut anyref)))))
    (type $child (sub $header (struct
      (field (mut i32)) (field (mut anyref)) (field (mut anyref)) (field (mut anyref)) (field (mut i64)))))
    (type $grandchild (sub $child (struct
      (field (mut i32)) (field (mut anyref)) (field (mut anyref)) (field (mut anyref)) (field (mut i64)) (field (mut i32))))))

  (func (export "make_header") (param $tag i32) (result (ref $header))
    (struct.new $header (local.get $tag) (ref.null any) (ref.null any) (ref.null any)))

  (func (export "make_child") (param $tag i32) (param $value i64) (param $other anyref) (result (ref $child))
    (struct.new $child (local.get $tag) (local.get $other) (ref.null any) (local.get $other) (local.get $value)))

  (func (export "make_grandchild") (param $tag i32) (param $value i64) (param $extra i32) (result (ref $grandchild))
    (struct.new $grandchild (local.get $tag) (ref.null any) (ref.null any) (ref.null any) (local.get $value) (local.get $extra))))
