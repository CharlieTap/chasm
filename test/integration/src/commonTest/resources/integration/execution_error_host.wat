(module
  (import "env" "nested" (func $nested (result i32)))
  (import "env" "escape" (func $escape))

  (memory (export "memory") 1)

  (tag $boom (param i32))

  (func (export "call_nested") (result i32)
    call $nested)

  (func (export "call_escape")
    call $escape)

  (func (export "throw") (param i32)
    local.get 0
    throw $boom)

  (func (export "noop"))

  (func (export "catch") (param exnref) (result i32)
    block $handler (result i32)
      try_table (catch $boom $handler)
        local.get 0
        throw_ref
      end
      unreachable
    end)
)
