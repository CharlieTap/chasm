(module
  (type $binary (func (param i32 i32) (result i32)))
  (import "host" "add" (func $add (type $binary)))
  (import "host" "recurse" (func $recurse (param i32) (result i32)))
  (import "other" "double" (func $double (param i32) (result i32)))
  (elem declare func $add)

  (func (export "call_host") (param i32 i32) (result i32)
    (call $add (local.get 0) (local.get 1)))

  (func (export "call_host_nested") (param i32) (result i32)
    (call $add (call $add (local.get 0) (i32.const 1)) (call $double (local.get 0))))

  (func (export "tail_host") (param i32 i32) (result i32)
    (return_call $add (local.get 0) (local.get 1)))

  (func (export "tail_other") (param i32) (result i32)
    (return_call $double (local.get 0)))

  (func (export "call_ref_host") (param i32 i32) (result i32)
    (call_ref $binary (local.get 0) (local.get 1) (ref.func $add)))

  (func (export "countdown") (param i32) (result i32)
    (if (result i32) (i32.eqz (local.get 0))
      (then (i32.const 0))
      (else
        (i32.add
          (call $recurse (i32.sub (local.get 0) (i32.const 1)))
          (local.get 0))))))
