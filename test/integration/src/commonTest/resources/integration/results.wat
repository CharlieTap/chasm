(module
  (import "env" "observe" (func $observe))

  (tag $boom (param i32))

  (func (export "trap")
    unreachable)

  (func (export "throw") (param i32)
    local.get 0
    throw $boom)

  (func (export "catch") (param exnref) (result i32)
    block $handler (result i32)
      try_table (catch $boom $handler)
        call $observe
        local.get 0
        throw_ref
      end
      unreachable
    end)
)
