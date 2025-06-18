(module
  (type $type (func (param i32) (result i32)))

  (func $fibonacci (type $type) (param i32) (result i32)
    local.get 0
    i32.const 1
    i32.le_s
    if
      local.get 0
      return
    end

    local.get 0
    i32.const 1
    i32.sub
    ref.func $fibonacci
    return_call_ref $type
  )

  (export "fibonacci" (func $fibonacci))
)
