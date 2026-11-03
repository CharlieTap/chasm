(module
  (type $node (struct (field $value i32) (field $next (ref null $node))))
  (type $ints (array (mut i32)))
  (type $binary (func (param i32 i32) (result i32)))
  (tag $e (param i32))

  (func $add (type $binary) (i32.add (local.get 0) (local.get 1)))
  (elem declare func $add)

  ;; Builds an n-node list, then sums it: allocation and field reads.
  (func (export "list") (param $n i32) (result i32)
    (local $head (ref null $node)) (local $i i32) (local $sum i32)
    (block $built (loop $build
      (br_if $built (i32.ge_u (local.get $i) (local.get $n)))
      (local.set $head (struct.new $node (local.get $i) (local.get $head)))
      (local.set $i (i32.add (local.get $i) (i32.const 1)))
      (br $build)))
    (block $done (loop $walk
      (br_if $done (ref.is_null (local.get $head)))
      (local.set $sum (i32.add (local.get $sum) (struct.get $node $value (ref.as_non_null (local.get $head)))))
      (local.set $head (struct.get $node $next (ref.as_non_null (local.get $head))))
      (br $walk)))
    (local.get $sum))

  ;; Array element reads and writes.
  (func (export "array") (param $n i32) (result i32)
    (local $a (ref $ints)) (local $i i32) (local $sum i32)
    (local.set $a (array.new_default $ints (i32.const 1024)))
    (block $done (loop $next
      (br_if $done (i32.ge_u (local.get $i) (local.get $n)))
      (array.set $ints (local.get $a) (i32.and (local.get $i) (i32.const 1023)) (local.get $i))
      (local.set $sum (i32.add (local.get $sum) (array.get $ints (local.get $a) (i32.and (i32.mul (local.get $i) (i32.const 7)) (i32.const 1023)))))
      (local.set $i (i32.add (local.get $i) (i32.const 1)))
      (br $next)))
    (local.get $sum))

  ;; i31 boxing and unboxing.
  (func (export "i31") (param $n i32) (result i32)
    (local $i i32) (local $sum i32)
    (block $done (loop $next
      (br_if $done (i32.ge_u (local.get $i) (local.get $n)))
      (local.set $sum (i32.add (local.get $sum) (i31.get_s (ref.i31 (local.get $i)))))
      (local.set $i (i32.add (local.get $i) (i32.const 1)))
      (br $next)))
    (local.get $sum))

  ;; Calls through a function reference.
  (func (export "call_ref") (param $n i32) (result i32)
    (local $i i32) (local $sum i32) (local $f (ref $binary))
    (local.set $f (ref.func $add))
    (block $done (loop $next
      (br_if $done (i32.ge_u (local.get $i) (local.get $n)))
      (local.set $sum (call_ref $binary (local.get $sum) (local.get $i) (local.get $f)))
      (local.set $i (i32.add (local.get $i) (i32.const 1)))
      (br $next)))
    (local.get $sum))

  ;; Throw and catch in a loop.
  (func $thrower (param i32) (throw $e (local.get 0)))
  (func (export "throw") (param $n i32) (result i32)
    (local $i i32) (local $sum i32)
    (block $done (loop $next
      (br_if $done (i32.ge_u (local.get $i) (local.get $n)))
      (block $caught (result i32)
        (try_table (catch $e $caught) (call $thrower (local.get $i)))
        (unreachable))
      (local.set $sum (i32.add (local.get $sum)))
      (local.set $i (i32.add (local.get $i) (i32.const 1)))
      (br $next)))
    (local.get $sum))

  ;; Mutual tail calls.
  (func $ping (param $n i32) (param $acc i32) (result i32)
    (if (result i32) (i32.eqz (local.get $n)) (then (local.get $acc))
      (else (return_call $pong (i32.sub (local.get $n) (i32.const 1)) (i32.add (local.get $acc) (i32.const 1))))))
  (func $pong (param $n i32) (param $acc i32) (result i32)
    (if (result i32) (i32.eqz (local.get $n)) (then (local.get $acc))
      (else (return_call $ping (i32.sub (local.get $n) (i32.const 1)) (i32.add (local.get $acc) (i32.const 2))))))
  (func (export "tail") (param $n i32) (result i32) (call $ping (local.get $n) (i32.const 0)))
)
