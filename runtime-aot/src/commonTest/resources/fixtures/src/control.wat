(module
  (func (export "sum") (param $n i32) (result i64)
    (local $i i32) (local $acc i64)
    (block $done
      (loop $next
        (br_if $done (i32.ge_s (local.get $i) (local.get $n)))
        (local.set $acc (i64.add (local.get $acc) (i64.extend_i32_s (local.get $i))))
        (local.set $i (i32.add (local.get $i) (i32.const 1)))
        (br $next)))
    (local.get $acc))

  (func (export "classify") (param i32) (result i32)
    (block $c (block $b (block $a
      (br_table $a $b $c (local.get 0)))
      (return (i32.const 10)))
      (return (i32.const 20)))
    (i32.const 30))

  (func (export "max") (param i32 i32) (result i32)
    (if (result i32) (i32.gt_s (local.get 0) (local.get 1))
      (then (local.get 0))
      (else (local.get 1))))

  (func (export "swap_loop") (param i32 i32 i32) (result i32)
    (local $t i32)
    (block $done
      (loop $next
        (br_if $done (i32.eqz (local.get 2)))
        (local.set $t (local.get 0))
        (local.set 0 (local.get 1))
        (local.set 1 (local.get $t))
        (local.set 2 (i32.sub (local.get 2) (i32.const 1)))
        (br $next)))
    (i32.sub (local.get 0) (local.get 1)))

  (func (export "spin") (loop (br 0)))
)
