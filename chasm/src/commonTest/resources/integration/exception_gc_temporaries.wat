(module
  (type $box (struct (field i32)))
  (type $array (array (mut i32)))
  (tag $error (param (ref $box)))
  (func $throw (param (ref $box))
    local.get 0 throw $error)
  (func (export "handler-temporaries") (result i32)
    (local $second i32)
    block (result (ref $box))
      try_table (result (ref $box)) (catch $error 0)
        i32.const 321 struct.new $box call $throw unreachable
      end
    end
    drop
    i32.const 123 struct.new $box
    i32.const 456 struct.new $box
    ;; A fresh large allocation forces collection with both references live.
    i32.const 0 i32.const 65536 array.new $array drop
    struct.get $box 0 local.set $second
    struct.get $box 0 local.get $second i32.add)
  (func $ordinary (result i32) i32.const 0)
  (func (export "call-temporaries") (result i32)
    (local $second i32)
    call $ordinary drop
    i32.const 123 struct.new $box
    i32.const 456 struct.new $box
    i32.const 0 i32.const 131072 array.new $array drop
    struct.get $box 0 local.set $second
    struct.get $box 0 local.get $second i32.add)
)
