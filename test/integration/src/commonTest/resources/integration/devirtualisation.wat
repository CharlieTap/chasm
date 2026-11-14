(module
  (type $binary (func (param i32 i32) (result i32)))
  (type $unary (func (param i32) (result i32)))
  (type $bytes (array (mut i8)))
  (tag $raised (param i32))

  (table $dispatch 6 funcref)
  (elem (table $dispatch) (i32.const 0) func $add $sub $mul $negate)

  (table $effects 2 funcref)
  (elem (table $effects) (i32.const 0) func $raise $allocate)

  (func $add (type $binary) (i32.add (local.get 0) (local.get 1)))
  (func $sub (type $binary) (i32.sub (local.get 0) (local.get 1)))
  (func $mul (type $binary) (i32.mul (local.get 0) (local.get 1)))
  (func $negate (type $unary) (i32.sub (i32.const 0) (local.get 0)))

  (func $raise (type $binary)
    (throw $raised (i32.add (local.get 0) (local.get 1))))

  (func $allocate (type $binary)
    (drop (array.new_default $bytes (i32.const 4096)))
    (i32.add (local.get 0) (local.get 1)))

  (func (export "dispatch") (param $selector i32) (param $a i32) (param $b i32) (result i32)
    (call_indirect $dispatch (type $binary) (local.get $a) (local.get $b) (local.get $selector)))

  (func (export "dispatch-constant") (result i32)
    (call_indirect $dispatch (type $binary) (i32.const 6) (i32.const 7) (i32.const 2)))

  (func (export "reuse") (param $selector i32) (param $a i32) (result i32)
    (local.set $selector
      (call_indirect $dispatch (type $binary)
        (i32.add (local.get $a) (i32.const 1))
        (local.get $selector)
        (i32.and (local.get $selector) (i32.const 1))))
    (local.get $selector))

  (func (export "caught") (param $selector i32) (param $a i32) (param $b i32) (result i32)
    (block $handler (result i32)
      (try_table (catch $raised $handler)
        (return (call_indirect $effects (type $binary) (local.get $a) (local.get $b) (local.get $selector))))
      (unreachable)))

  (func (export "retained") (param $selector i32) (param $iterations i32) (result i32)
    (local $kept (ref null $bytes))
    (local $sum i32)
    (local.set $kept (array.new $bytes (i32.const 7) (i32.const 4)))
    (block $done
      (loop $again
        (br_if $done (i32.eqz (local.get $iterations)))
        (local.set $sum (i32.add (local.get $sum)
          (call_indirect $effects (type $binary) (i32.const 1) (i32.const 2) (local.get $selector))))
        (local.set $iterations (i32.sub (local.get $iterations) (i32.const 1)))
        (br $again)))
    (i32.add (local.get $sum) (array.get_u $bytes (local.get $kept) (i32.const 3)))))
