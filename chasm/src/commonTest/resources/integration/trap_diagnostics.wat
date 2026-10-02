(module $trap_fixture
  (type $nullary_i32 (func (result i32)))
  (type $inner (struct (field i32)))
  (type $outer (struct (field (ref null $inner))))
  (import "env" "host_fail" (func $host_fail))
  (memory 1)
  (table 1 funcref)
  (elem (i32.const 0) $divide)

  (func $leaf
    unreachable)

  (func $middle
    call $leaf)

  (func $nested_unreachable (export "nested_unreachable")
    call $middle)

  (func $divide (param i32 i32) (result i32)
    local.get 0
    local.get 1
    i32.div_s)

  (func $divide_by_zero (export "divide_by_zero") (result i32)
    (local i32)
    i32.const 10
    i32.const 0
    call $divide
    local.set 0
    local.get 0)

  (func $load (param i32) (result i32)
    local.get 0
    i32.load offset=4)

  (func $load_out_of_bounds (export "load_out_of_bounds") (param i32) (result i32)
    local.get 0
    call $load)

  (func $store_out_of_bounds (export "store_out_of_bounds") (param i32)
    local.get 0
    i32.const 7
    i32.store16 offset=2)

  (func $load_immediate (export "load_immediate") (result i64)
    i32.const 70000
    i64.load)

  (func $fill_out_of_bounds (export "fill_out_of_bounds") (param i32 i32)
    local.get 0
    i32.const 0
    local.get 1
    memory.fill)

  (func $copy_out_of_bounds (export "copy_out_of_bounds") (param i32 i32 i32)
    local.get 0
    local.get 1
    local.get 2
    memory.copy)

  (func $call_host (export "call_host")
    call $host_fail)

  (func $indirect_mismatch (export "indirect_mismatch") (result i32)
    i32.const 0
    call_indirect (type $nullary_i32))

  (func $recurse (param i32)
    local.get 0
    i32.eqz
    if
      unreachable
    end
    local.get 0
    i32.const 1
    i32.sub
    call $recurse)

  (func (export "deep") (param i32)
    local.get 0
    call $recurse)

  (func $tail_target
    unreachable)

  (func $tail_caller
    return_call $tail_target)

  (func $tail (export "tail")
    call $tail_caller)

  (func $chained_struct_get (export "chained_struct_get") (result i32)
    ref.null $inner
    struct.new $outer
    struct.get $outer 0
    struct.get $inner 0)
)
