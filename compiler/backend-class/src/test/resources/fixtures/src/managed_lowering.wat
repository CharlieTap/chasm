(module
  (type $bytes (array (mut i8)))
  (type $shape (sub (struct (field i32) (field (mut (ref null $bytes))))))
  (type $circle (sub final $shape (struct (field i32) (field (mut (ref null $bytes))) (field f64))))
  (type $wide (struct (field i32) (field i32) (field i32) (field i32) (field i32) (field i32) (field i32)
    (field i32) (field i32) (field i32) (field i32) (field i32) (field i32) (field i32) (field i32)
    (field i32) (field i32) (field i32) (field i32) (field i32) (field i32) (field i32) (field (mut i64))
    (field (mut anyref))))
  (type $record (struct (field i32) (field anyref) (field anyref) (field anyref) (field anyref)))

  (func $new_circle (export "new_circle") (param $radius f64) (result (ref $circle))
    (struct.new $circle (i32.const 1) (ref.null $bytes) (local.get $radius)))

  (func $get_shape (export "get_shape") (param $shape (ref null $shape)) (result i32)
    (struct.get $shape 0 (local.get $shape)))

  (func $get_inherited (export "get_inherited") (param $circle (ref null $circle)) (result i32)
    (struct.get $circle 0 (local.get $circle)))

  (func $radius (export "radius") (param $circle (ref null $circle)) (result f64)
    (struct.get $circle 2 (local.get $circle)))

  (func $is_circle (export "is_circle") (param $value anyref) (result i32)
    (ref.test (ref $circle) (local.get $value)))

  (func $is_shape (export "is_shape") (param $value anyref) (result i32)
    (ref.test (ref $shape) (local.get $value)))

  (func $byte_at (export "byte_at") (param $bytes (ref null $bytes)) (param $index i32) (result i32)
    (array.get_u $bytes (local.get $bytes) (local.get $index)))

  (func $spilled (export "spilled") (param $wide (ref null $wide)) (param $value i64) (result i64)
    (struct.set $wide 22 (local.get $wide) (local.get $value))
    (struct.get $wide 22 (local.get $wide)))

  (func $mixed (export "mixed") (param $count i32) (param $shape (ref null $shape)) (result i32)
    (block $done (result i32)
      (drop (br_if $done (i32.add (local.get $count) (local.get $count)) (local.get $count)))
      (struct.get $shape 0 (block (result (ref null $shape)) (local.get $shape)))))

  (func $carries (export "carries") (param $shape (ref null $shape)) (param $count i32) (result i32)
    (local $total i32)
    (loop $again
      (local.set $total (i32.add (local.get $total) (struct.get $shape 0 (local.get $shape))))
      (local.set $count (i32.sub (local.get $count) (i32.const 1)))
      (br_if $again (local.get $count)))
    (local.get $total))

  (func $parameter_homes (export "parameter_homes") (param $bytes (ref null $bytes)) (result i32)
    (array.len (local.get $bytes)))

  (func $new_record (export "new_record") (result (ref $record))
    (struct.new $record (i32.const 1) (ref.null any) (ref.null any) (ref.null any) (ref.null any))))
