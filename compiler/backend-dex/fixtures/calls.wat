(module
  (type $binary (func (param i32 i32) (result i32)))
  (table 4 funcref)
  (elem (i32.const 0) $add $sub $mul)

  (func $add (type $binary) (i32.add (local.get 0) (local.get 1)))
  (func $sub (type $binary) (i32.sub (local.get 0) (local.get 1)))
  (func $mul (type $binary) (i32.mul (local.get 0) (local.get 1)))

  (func $fib (export "fib") (param $n i32) (result i64)
    (if (result i64) (i32.lt_s (local.get $n) (i32.const 2))
      (then (i64.extend_i32_s (local.get $n)))
      (else (i64.add
        (call $fib (i32.sub (local.get $n) (i32.const 1)))
        (call $fib (i32.sub (local.get $n) (i32.const 2)))))))

  (func $even (export "even") (param $n i32) (result i32)
    (if (result i32) (i32.eqz (local.get $n)) (then (i32.const 1)) (else (call $odd (i32.sub (local.get $n) (i32.const 1))))))
  (func $odd (param $n i32) (result i32)
    (if (result i32) (i32.eqz (local.get $n)) (then (i32.const 0)) (else (call $even (i32.sub (local.get $n) (i32.const 1))))))

  (func (export "dispatch") (param $op i32) (param $a i32) (param $b i32) (result i32)
    (call_indirect (type $binary) (local.get $a) (local.get $b) (local.get $op)))

  (func $pair (param i32) (result i32 i32) (local.get 0) (i32.mul (local.get 0) (i32.const 2)))
  (func (export "pair_sum") (param i32) (result i32)
    (call $pair (local.get 0))
    (i32.add))

  (func $runaway (export "runaway") (call $runaway))

  (func (export "swap_args") (param i32 i32) (result i32)
    (call $sub (local.get 1) (local.get 0)))

  (func (export "table_ops") (param i32) (result i32)
    (table.set (i32.const 3) (table.get (local.get 0)))
    (drop (table.grow (ref.null func) (i32.const 2)))
    (i32.add (table.size) (call_indirect (type $binary) (i32.const 10) (i32.const 3) (i32.const 3))))
)
