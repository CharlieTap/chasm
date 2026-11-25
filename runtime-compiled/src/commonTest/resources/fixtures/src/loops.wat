(module
  (func (export "loops") (param $n i32) (result i32)
    (local $i i32)
    (local $sum i32)
    (local.set $sum (i32.add (local.get $n) (i32.const 1)))
    (local.set $sum (i32.mul (local.get $sum) (i32.const 3)))
    (local.set $sum (i32.xor (local.get $sum) (i32.const 5)))
    (block $done
      (loop $again
        (br_if $done (i32.ge_s (local.get $i) (local.get $n)))
        (local.set $sum (i32.add (local.get $sum) (local.get $i)))
        (local.set $sum (i32.rotl (local.get $sum) (i32.const 3)))
        (local.set $i (i32.add (local.get $i) (i32.const 1)))
        (br $again)))
    (local.set $sum (i32.sub (local.get $sum) (i32.const 7)))
    (local.set $sum (i32.mul (local.get $sum) (i32.const 11)))
    (local.get $sum)))
