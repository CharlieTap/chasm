(module
  (rec
    (type $kinds (struct
      (field $i32 (mut i32))
      (field $i64 (mut i64))
      (field $f32 (mut f32))
      (field $f64 (mut f64))
      (field $i8 (mut i8))
      (field $i16 (mut i16))
      (field $ref (mut externref))
      (field $fixed i32)
      (field $self (mut (ref null $kinds))))))
  (type $base (sub (struct (field $a (mut i32)))))
  (type $derived (sub final $base (struct (field $a (mut i32)) (field $b (mut f32)) (field $c (mut externref)))))
  (type $empty (struct))
  (type $boxes (array (mut (ref null $kinds))))

  (func (export "kinds") (result (ref $kinds)) (struct.new_default $kinds))
  (func (export "derived") (result (ref $derived)) (struct.new_default $derived))
  (func (export "empty") (result (ref $empty)) (struct.new $empty))
  (func (export "boxes") (result (ref $boxes)) (array.new_default $boxes (i32.const 1)))

  (func (export "read_f32") (param (ref $kinds)) (result f32) (struct.get $kinds $f32 (local.get 0)))
  (func (export "read_i8_s") (param (ref $kinds)) (result i32) (struct.get_s $kinds $i8 (local.get 0)))
  (func (export "read_i16_u") (param (ref $kinds)) (result i32) (struct.get_u $kinds $i16 (local.get 0)))
  (func (export "read_inherited") (param (ref $derived)) (result i32) (struct.get $derived $a (local.get 0))))
