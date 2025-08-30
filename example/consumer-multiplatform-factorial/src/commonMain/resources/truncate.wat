(module
  (memory (export "memory") 1)

  (global $heap (mut i32) (i32.const 0))

  (func (export "malloc") (param $len i32) (result i32)
    (local $old i32)
    (local.set $old (global.get $heap))
    (global.set $heap (i32.add (global.get $heap) (local.get $len)))
    (local.get $old)
  )

  (func (export "free") (param $ptr i32)
    (nop)
  )

  (func $truncate (export "truncate") (param $ptr i32) (param $len i32) (result i32 i32)
    (local $i i32)
    (local $b i32)

    (if (i32.eqz (local.get $len))
      (then (return (local.get $ptr) (i32.const 0)))
    )

    (local.set $i (i32.sub (local.get $len) (i32.const 1)))

    (block $break
      (loop $scan
        (local.set $b
          (i32.load8_u (i32.add (local.get $ptr) (local.get $i)))
        )
        (br_if $break
          (i32.ne
            (i32.and (local.get $b) (i32.const 192))
            (i32.const 128)
          )
        )
        (br_if $break (i32.eqz (local.get $i)))
        (local.set $i (i32.sub (local.get $i) (i32.const 1)))
        (br $scan)
      )
    )

    (return (local.get $ptr) (local.get $i))
  )

  (func $calculate_len_null_terminated (param $ptr i32) (result i32)
    (local $len i32)
    (local $b i32)

    (local.set $len (i32.const 0))
    (block $done
      (loop $count
        (local.set $b (i32.load8_u (i32.add (local.get $ptr) (local.get $len))))
        (br_if $done (i32.eqz (local.get $b)))
        (local.set $len (i32.add (local.get $len) (i32.const 1)))
        (br $count)
      )
    )
    (local.get $len)
  )

  (func (export "truncate_null_terminated") (param $ptr i32) (result i32 i32)
    (call $truncate
      (local.get $ptr)
      (call $calculate_len_null_terminated (local.get $ptr))
    )
  )

  (func (export "truncate_len_prefixed") (param $ptr i32) (result i32 i32)
    (local $len i32)
    (local $str_ptr i32)

    (local.set $len (i32.load (local.get $ptr)))
    (local.set $str_ptr (i32.add (local.get $ptr) (i32.const 4)))

    (call $truncate
      (local.get $str_ptr)
      (local.get $len)
    )
  )

  (func (export "truncate_packed") (param $packed i64) (result i32 i32)
    (local $ptr i32)
    (local $len i32)

    (local.set $ptr (i32.wrap_i64 (i64.shr_u (local.get $packed) (i64.const 32))))
    (local.set $len (i32.wrap_i64 (local.get $packed)))

    (call $truncate
      (local.get $ptr)
      (local.get $len)
    )
  )
)
