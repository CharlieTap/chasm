(module
  (type $box (struct (field i32)))
  (import "env" "host" (func $host))

  ;; One copy before the call and one after it, which no collection follows.
  (func (export "trailing") (param $first (ref $box)) (param $second (ref $box)) (result i32)
    (local $box (ref null $box))
    (local.set $box (local.get $first))
    (call $host)
    (local.set $box (local.get $second))
    (struct.get $box 0 (ref.as_non_null (local.get $box))))

  ;; The copy follows the call in the loop's body, so only the back edge leads to the next collection.
  (func (export "loop") (param $first (ref $box)) (param $count i32) (result i32)
    (local $box (ref null $box))
    (loop $again
      (call $host)
      (local.set $box (local.get $first))
      (br_if $again (local.tee $count (i32.sub (local.get $count) (i32.const 1)))))
    (struct.get $box 0 (ref.as_non_null (local.get $box))))

  (func (export "between") (param $first (ref $box)) (result i32)
    (local $box (ref null $box))
    (call $host)
    (local.set $box (local.get $first))
    (call $host)
    (struct.get $box 0 (ref.as_non_null (local.get $box))))

  ;; Nothing that runs after the parameters are stored collects.
  (func (export "reads") (param $first (ref $box)) (result i32)
    (struct.get $box 0 (local.get $first))))
