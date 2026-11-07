(module $deep_trace_fixture
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
)
