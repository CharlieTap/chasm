(module
  (import "bench" "noop" (func $noop))
  (import "bench" "consume4" (func $consume4 (param i32 i32 i32 i32)))
  (import "bench" "roundtrip2" (func $roundtrip2 (param i32 i32) (result i32)))

  (func $wasm-noop)
  (func $wasm-identity1 (param i32) (result i32)
    local.get 0
  )
  (func $wasm-roundtrip2 (param i32 i32) (result i32)
    local.get 0
    local.get 1
    i32.add
  )
  (func $wasm-sum3 (param i32 i32 i32) (result i32)
    local.get 0
    local.get 1
    i32.add
    local.get 2
    i32.add
  )
  (func $wasm-sum4 (param i32 i32 i32 i32) (result i32)
    local.get 0
    local.get 1
    i32.add
    local.get 2
    i32.add
    local.get 3
    i32.add
  )

  (func (export "baseline_noop") (param $count i32) (result i32)
    (local $index i32)
    (block $done
      (loop $loop
        local.get $index
        local.get $count
        i32.ge_u
        br_if $done

        local.get $index
        i32.const 1
        i32.add
        local.set $index
        br $loop
      )
    )
    local.get $index
  )

  (func (export "host_noop") (param $count i32) (result i32)
    (local $index i32)
    (block $done
      (loop $loop
        local.get $index
        local.get $count
        i32.ge_u
        br_if $done

        call $noop

        local.get $index
        i32.const 1
        i32.add
        local.set $index
        br $loop
      )
    )
    local.get $index
  )

  (func (export "wasm_noop") (param $count i32) (result i32)
    (local $index i32)
    (block $done
      (loop $loop
        local.get $index
        local.get $count
        i32.ge_u
        br_if $done

        call $wasm-noop

        local.get $index
        i32.const 1
        i32.add
        local.set $index
        br $loop
      )
    )
    local.get $index
  )

  (func (export "baseline_consume4") (param $count i32) (result i32)
    (local $index i32)
    (local $sink i32)
    (block $done
      (loop $loop
        local.get $index
        local.get $count
        i32.ge_u
        br_if $done

        local.get $sink
        local.get $index
        i32.add
        i32.const 17
        i32.add
        i32.const 31
        i32.add
        i32.const 47
        i32.add
        local.set $sink

        local.get $index
        i32.const 1
        i32.add
        local.set $index
        br $loop
      )
    )
    local.get $sink
  )

  (func (export "wasm_identity1") (param $count i32) (result i32)
    (local $index i32)
    (local $result i32)
    (block $done
      (loop $loop
        local.get $index
        local.get $count
        i32.ge_u
        br_if $done

        local.get $index
        call $wasm-identity1
        local.set $result

        local.get $index
        i32.const 1
        i32.add
        local.set $index
        br $loop
      )
    )
    local.get $result
  )

  (func (export "wasm_sum3") (param $count i32) (result i32)
    (local $index i32)
    (local $accumulator i32)
    (block $done
      (loop $loop
        local.get $index
        local.get $count
        i32.ge_u
        br_if $done

        local.get $index
        local.get $accumulator
        local.get $count
        call $wasm-sum3
        local.set $accumulator

        local.get $index
        i32.const 1
        i32.add
        local.set $index
        br $loop
      )
    )
    local.get $accumulator
  )

  (func (export "wasm_sum4") (param $count i32) (result i32)
    (local $index i32)
    (local $accumulator i32)
    (block $done
      (loop $loop
        local.get $index
        local.get $count
        i32.ge_u
        br_if $done

        local.get $index
        local.get $accumulator
        local.get $count
        local.get $index
        call $wasm-sum4
        local.set $accumulator

        local.get $index
        i32.const 1
        i32.add
        local.set $index
        br $loop
      )
    )
    local.get $accumulator
  )

  (func (export "host_consume4") (param $count i32) (result i32)
    (local $index i32)
    (block $done
      (loop $loop
        local.get $index
        local.get $count
        i32.ge_u
        br_if $done

        local.get $index
        i32.const 17
        i32.const 31
        i32.const 47
        call $consume4

        local.get $index
        i32.const 1
        i32.add
        local.set $index
        br $loop
      )
    )
    local.get $index
  )

  (func (export "baseline_roundtrip2") (param $count i32) (result i32)
    (local $index i32)
    (local $accumulator i32)
    (block $done
      (loop $loop
        local.get $index
        local.get $count
        i32.ge_u
        br_if $done

        local.get $index
        local.get $accumulator
        i32.add
        local.set $accumulator

        local.get $index
        i32.const 1
        i32.add
        local.set $index
        br $loop
      )
    )
    local.get $accumulator
  )

  (func (export "host_roundtrip2") (param $count i32) (result i32)
    (local $index i32)
    (local $accumulator i32)
    (block $done
      (loop $loop
        local.get $index
        local.get $count
        i32.ge_u
        br_if $done

        local.get $index
        local.get $accumulator
        call $roundtrip2
        local.set $accumulator

        local.get $index
        i32.const 1
        i32.add
        local.set $index
        br $loop
      )
    )
    local.get $accumulator
  )

  (func (export "wasm_roundtrip2") (param $count i32) (result i32)
    (local $index i32)
    (local $accumulator i32)
    (block $done
      (loop $loop
        local.get $index
        local.get $count
        i32.ge_u
        br_if $done

        local.get $index
        local.get $accumulator
        call $wasm-roundtrip2
        local.set $accumulator

        local.get $index
        i32.const 1
        i32.add
        local.set $index
        br $loop
      )
    )
    local.get $accumulator
  )
)
