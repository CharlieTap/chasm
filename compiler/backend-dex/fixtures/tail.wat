(module
  (type $countdown (func (param i64 i64) (result i64)))
  (table 2 funcref)
  (elem (i32.const 0) $even_i $odd_i)

  ;; Self tail call: a loop of n iterations through return_call.
  (func $sum (export "sum") (param $n i64) (param $acc i64) (result i64)
    (if (result i64) (i64.eqz (local.get $n))
      (then (local.get $acc))
      (else (return_call $sum (i64.sub (local.get $n) (i64.const 1)) (i64.add (local.get $acc) (local.get $n))))))

  ;; Mutual tail calls between two functions with different parameter counts.
  (func $ping (export "ping") (param $n i64) (result i64)
    (if (result i64) (i64.eqz (local.get $n)) (then (i64.const 1))
      (else (return_call $pong (i64.sub (local.get $n) (i64.const 1)) (i64.const 7) (i32.const 3)))))
  (func $pong (param $n i64) (param i64) (param i32) (result i64)
    (if (result i64) (i64.eqz (local.get $n)) (then (i64.const 2))
      (else (return_call $ping (i64.sub (local.get $n) (i64.const 1))))))

  ;; Tail calls through the table.
  (func $even_i (param $n i64) (param $acc i64) (result i64)
    (if (result i64) (i64.eqz (local.get $n)) (then (local.get $acc))
      (else (return_call_indirect (type $countdown) (i64.sub (local.get $n) (i64.const 1)) (i64.add (local.get $acc) (i64.const 2)) (i32.const 1)))))
  (func $odd_i (param $n i64) (param $acc i64) (result i64)
    (if (result i64) (i64.eqz (local.get $n)) (then (local.get $acc))
      (else (return_call_indirect (type $countdown) (i64.sub (local.get $n) (i64.const 1)) (local.get $acc) (i32.const 0)))))
  (func (export "indirect") (param i64) (result i64) (call $even_i (local.get 0) (i64.const 0)))

  ;; Tail calls through function references, returning two values.
  (type $pair (func (param i32 i32) (result i32 i32)))
  (func $swap (type $pair) (local.get 1) (local.get 0))
  (func $loop_pair (param $n i32) (param $x i32) (result i32 i32)
    (if (result i32 i32) (i32.eqz (local.get $n))
      (then (return_call_ref $pair (local.get $x) (i32.const 9) (ref.func $swap)))
      (else (return_call $loop_pair (i32.sub (local.get $n) (i32.const 1)) (i32.add (local.get $x) (i32.const 1))))))
  (elem declare func $swap)
  (func (export "pair") (param i32) (result i32)
    (call $loop_pair (local.get 0) (i32.const 0))
    (i32.sub))

  ;; A direct caller of a function that tail-calls another.
  (func (export "direct") (param i64) (result i64)
    (i64.add (call $ping (local.get 0)) (i64.const 100)))
)
