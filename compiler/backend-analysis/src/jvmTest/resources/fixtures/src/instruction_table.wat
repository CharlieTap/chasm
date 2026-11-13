(module
  (memory 1)
  (memory $second 1)
  (table $references 1 externref)

  (func (export "accesses") (param $address i32) (param $value i32) (result i32)
    (i32.store offset=8 (local.get $address) (local.get $value))
    (i32.store $second (i32.const 16) (local.get $value))
    (i32.load16_u offset=2 (local.get $address)))

  (func (export "jumps") (param $count i32) (result i32)
    (local $total i32)
    (block $done
      (loop $again
        (br_if $done (i32.eqz (local.get $count)))
        (local.set $total (i32.add (local.get $total) (local.get $count)))
        (local.set $count (i32.sub (local.get $count) (i32.const 1)))
        (br $again)))
    (local.get $total))

  (func (export "copies") (param $first i64) (param $second f32) (result i64)
    (local $copy i64)
    (local $other f32)
    (local.set $copy (local.get $first))
    (local.set $other (local.get $second))
    (i64.add (local.get $copy) (i64.trunc_f32_s (local.get $other))))

  (func (export "helpers") (result i32)
    (drop (table.get $references (i32.const 0)))
    (memory.size))

  (func $self (export "self") (param $count i32) (result i32)
    (if (result i32) (local.get $count)
      (then (return_call $self (i32.sub (local.get $count) (i32.const 1))))
      (else (i32.const 0))))

  (func $guarded (export "guarded") (param $count i32) (result i32)
    (block $caught
      (try_table (catch_all $caught)
        (return_call $guarded (local.get $count))))
    (i32.const 0)))
