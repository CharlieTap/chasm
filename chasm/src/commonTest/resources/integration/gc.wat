(module
  (type $array (array (mut i32)))
  (type $struct (struct
    (field i32)
    (field (ref null $array))))

  (global $struct-root (mut (ref null $struct)) (ref.null $struct))
  (table $array-roots 1 (ref null $array))
  (tag $exception-root (param (ref $struct)))

  ;; Compile the exported wrapper before discovering the GC instructions in
  ;; its internal callee.
  (func (export "allocate")
    call $allocate)

  (func $allocate
    ;; Unreachable array and struct allocations.
    i32.const 1
    i32.const 2
    array.new $array
    drop

    i32.const 2
    ref.null $array
    struct.new $struct
    drop

    i32.const 3
    i32.const 4
    array.new $array
    drop

    i32.const 4
    ref.null $array
    struct.new $struct
    drop

    ;; A struct rooted by a global, containing an array reference.
    i32.const 5
    i32.const 6
    i32.const 2
    array.new $array
    struct.new $struct
    global.set $struct-root

    ;; An array rooted directly by a table.
    i32.const 0
    i32.const 7
    i32.const 3
    array.new $array
    table.set $array-roots

    ;; A struct and its nested array rooted by a caught exception.
    block (result (ref $struct))
      try_table (result (ref $struct)) (catch $exception-root 0)
        i32.const 8
        i32.const 9
        i32.const 2
        array.new $array
        struct.new $struct
        throw $exception-root
      end
    end
    drop)

  (func (export "rooted-struct-value") (result i32)
    global.get $struct-root
    ref.as_non_null
    struct.get $struct 0)

  (func (export "nested-array-length") (result i32)
    global.get $struct-root
    ref.as_non_null
    struct.get $struct 1
    ref.as_non_null
    array.len)

  (func (export "table-array-length") (result i32)
    i32.const 0
    table.get $array-roots
    ref.as_non_null
    array.len))
