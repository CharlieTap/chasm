(module
  (type $sig (func (param i32) (result i32)))
  (type $other (func (param i64) (result i64)))
  (type $base (sub (func (param i32) (result i32))))
  (type $derived (sub $base (func (param i32) (result i32))))
  (import "a" "double" (func $double (type $sig)))
  (import "host" "triple" (func $triple (type $sig)))
  (table $local 3 funcref)
  (elem $segment func $double $triple $quad)
  (elem declare func $boom $five)
  (global $saved (mut funcref) (ref.null func))
  (table $held 2 funcref)

  (func $five (type $derived)
    (i32.add (local.get 0) (i32.const 5)))

  (func $quad (type $sig)
    (i32.mul (local.get 0) (i32.const 4)))

  (func $boom (type $sig)
    (unreachable))

  (func (export "double_ref") (result funcref) (ref.func $double))
  (func (export "triple_ref") (result funcref) (ref.func $triple))
  (func (export "quad_ref") (result funcref) (ref.func $quad))

  (func (export "call_ref") (param funcref i32) (result i32)
    (call_ref $sig (local.get 1) (ref.cast (ref $sig) (local.get 0))))

  (func (export "init")
    (table.init $local $segment (i32.const 0) (i32.const 0) (i32.const 3)))

  (func (export "call_indirect") (param i32 i32) (result i32)
    (call_indirect $local (type $sig) (local.get 1) (local.get 0)))

  (func (export "is_sig") (param funcref) (result i32)
    (ref.test (ref $sig) (local.get 0)))

  (func (export "is_other") (param funcref) (result i32)
    (ref.test (ref $other) (local.get 0)))

  (func (export "is_func") (param funcref) (result i32)
    (ref.test (ref func) (local.get 0)))

  (func (export "call_boom") (param i32) (result i32)
    (call_ref $sig (local.get 0) (ref.func $boom)))

  (func (export "five_ref") (result funcref) (ref.func $five))

  (func (export "is_base") (param funcref) (result i32)
    (ref.test (ref $base) (local.get 0)))

  (func (export "is_derived") (param funcref) (result i32)
    (ref.test (ref $derived) (local.get 0)))

  (func (export "save") (param funcref)
    (global.set $saved (local.get 0)))

  (func (export "saved") (result funcref)
    (global.get $saved))

  (func (export "hold") (param funcref)
    (table.set $held (i32.const 1) (local.get 0)))

  (func (export "held") (result funcref)
    (table.get $held (i32.const 1)))

  (func (export "extern_identity") (param externref) (result externref)
    (local.get 0))
)
