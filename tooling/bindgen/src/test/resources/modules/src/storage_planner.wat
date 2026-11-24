(module
  (type $bytes (array (mut i8)))
  (rec
    (type $base (sub (struct (field (mut i32)))))
    (type $middle (sub $base (struct (field (mut i32)) (field (mut (ref null $leaf))))))
    (type $leaf (sub final $middle (struct (field (mut i32)) (field (mut (ref null $leaf))) (field (mut i64))))))
  (type $holder (struct (field (mut (ref null $base))) (field (mut (ref null $bytes))) (field (mut anyref)) (field (mut f64))))

  (func (export "hold") (param (ref $leaf)) (result (ref $holder))
    (struct.new $holder (local.get 0) (ref.null $bytes) (ref.null any) (f64.const 0))))
