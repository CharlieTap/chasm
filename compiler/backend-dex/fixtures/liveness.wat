(module
  (type $pair (struct (field (mut i32)) (field (mut i32))))
  (type $numbers (array (mut i32)))

  (func (export "aggregates") (param $pair (ref $pair)) (param $numbers (ref $numbers)) (param $index i32) (param $value i32) (result i32)
    (struct.set $pair 1 (local.get $pair) (local.get $value))
    (array.set $numbers (local.get $numbers) (local.get $index) (local.get $value))
    (ref.eq (local.get $pair) (local.get $numbers)))

  (func (export "overwritten") (param $first i32) (param $second i32) (result i32)
    (local.set $first (i32.const 7))
    (i32.add (local.get $first) (local.get $second)))

  (func (export "loop") (param $count i32) (param $kept i32) (result i32)
    (loop $again
      (local.set $count (i32.sub (local.get $count) (i32.const 1)))
      (br_if $again (local.get $count)))
    (local.get $kept)))
