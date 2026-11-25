(module
  (type $bytes (array (mut i8)))
  (type $chain (struct (field (ref null $chain)) (field (ref null $bytes))))
  (import "host" "allocated" (func $allocated (result i64)))
  (global $head (mut (ref null $chain)) (ref.null $chain))

  (func (export "grow") (param $mebibytes i32)
    (local $count i32)
    (loop $more
      (global.set $head (struct.new $chain (global.get $head) (array.new_default $bytes (i32.const 1048576))))
      (local.set $count (i32.add (local.get $count) (i32.const 1)))
      (br_if $more (i32.lt_u (local.get $count) (local.get $mebibytes)))))

  (func (export "release")
    (global.set $head (ref.null $chain)))

  (func (export "huge")
    (drop (array.new_default $bytes (i32.const -1))))

  (func (export "allocated") (result i64)
    (call $allocated))
)
