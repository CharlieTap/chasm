(module
  (memory 1 1 shared)

  (func (export "load") (param i32) (result i32)
    local.get 0
    i32.atomic.load)
)
