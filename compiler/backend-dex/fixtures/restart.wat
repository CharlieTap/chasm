(module
  (type $box (struct (field i32)))
  (type $bytes (array (mut i8)))

  (func $churn
    (local $count i32)
    (local.set $count (i32.const 64))
    (loop $again
      (drop (array.new_default $bytes (i32.const 4096)))
      (br_if $again (local.tee $count (i32.sub (local.get $count) (i32.const 1)))))
    (drop (struct.new $box (i32.const 99))))

  (func $swap (param $n i32) (param $a (ref $box)) (param $b (ref $box)) (param $spare (ref null $box)) (result i32)
    (call $churn)
    (if (result i32) (i32.eqz (local.get $n))
      (then (i32.add (i32.mul (struct.get $box 0 (local.get $a)) (i32.const 10)) (struct.get $box 0 (local.get $b))))
      (else (return_call $swap (i32.sub (local.get $n) (i32.const 1)) (local.get $b) (local.get $a) (ref.null $box)))))

  (func (export "swap") (param $n i32) (result i32)
    (call $swap (local.get $n) (struct.new $box (i32.const 1)) (struct.new $box (i32.const 2)) (ref.null $box)))
)
