(module
  (memory (export "memory") 1)

  (func $recurse (export "recurse") (param $depth i32) (result i32)
    (if (result i32) (i32.eqz (local.get $depth))
      (then (i32.const 0))
      (else
        (i32.add
          (call $recurse (i32.sub (local.get $depth) (i32.const 1)))
          (i32.const 1)))))

  (func (export "memory-grow") (param $pages i32) (result i32)
    (memory.grow (local.get $pages))))
