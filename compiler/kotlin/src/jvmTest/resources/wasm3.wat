(module
  (type $box (struct (field i32)))
  (type $array (array (mut i32)))
  (type $unary (func (param i32) (result i32)))
  (import "env" "sample" (func $sample))
  (tag $error (param (ref $box)))
  (table 1 (ref null $box))
  (elem declare func $increment)
  (func $increment (type $unary) (param i32) (result i32)
    local.get 0 i32.const 1 i32.add)
  (func (export "call_ref") (param i32) (result i32)
    local.get 0 ref.func $increment call_ref $unary)
  (func (export "tail") (param i32) (result i32)
    local.get 0 return_call $increment)
  (func $throw (param (ref $box))
    local.get 0 throw $error)
  (func $rethrow (param (ref $box))
    block (result exnref)
      try_table (result exnref) (catch_all_ref 0)
        local.get 0 call $throw unreachable
      end
    end
    throw_ref)
  (func (export "exception") (result i32)
    block (result (ref $box))
      try_table (result (ref $box)) (catch $error 0)
        i32.const 321 struct.new $box call $rethrow unreachable
      end
    end
    ;; Preserve the caught reference while an allocating helper runs.
    i32.const 0 i32.const 4096 array.new $array drop
    struct.get $box 0)
  (func (export "handler_temporaries") (result i32)
    (local $second i32)
    block (result (ref $box))
      try_table (result (ref $box)) (catch $error 0)
        i32.const 321 struct.new $box call $rethrow unreachable
      end
    end
    drop
    ;; Both new references must be rooted above the handler's payload area.
    i32.const 123 struct.new $box
    i32.const 456 struct.new $box
    i32.const 0 i32.const 65536 array.new $array drop
    struct.get $box 0 local.set $second
    struct.get $box 0 local.get $second i32.add)
  (func $ordinary (result i32) i32.const 0)
  (func (export "call_temporaries") (result i32)
    (local $second i32)
    call $ordinary drop
    i32.const 123 struct.new $box
    i32.const 456 struct.new $box
    i32.const 0 i32.const 131072 array.new $array drop
    struct.get $box 0 local.set $second
    struct.get $box 0 local.get $second i32.add)
  (func (export "stress") (result i32)
    (local $a (ref null $box)) (local $b (ref null $box))
    (local $n i32) (local $sum i32)
    i32.const 111 struct.new $box local.set $a
    i32.const 222 struct.new $box local.set $b
    i32.const 0 local.get $a table.set 0
    i32.const 512 local.set $n
    loop $again
      call $sample
      ;; The selected reference is a promoted raw value live over array.new.
      local.get $a ref.as_non_null
      local.get $b ref.as_non_null
      local.get $n i32.const 1 i32.and
      select (result (ref $box))
      i32.const 0 i32.const 4096 array.new $array drop
      struct.get $box 0
      local.get $sum i32.add local.set $sum
      local.get $n i32.const 1 i32.sub local.tee $n br_if $again
    end
    i32.const 0 table.get 0 ref.as_non_null struct.get $box 0
    local.get $sum i32.add)
  (func (export "cast") (param $null i32) (result i32)
    block $empty
      block $present (result (ref $box))
        local.get $null
        if (result (ref null $box))
          ref.null $box
        else
          i32.const 99 struct.new $box
        end
        br_on_non_null $present
        br $empty
      end
      struct.get $box 0 return
    end
    i32.const -1)
)
