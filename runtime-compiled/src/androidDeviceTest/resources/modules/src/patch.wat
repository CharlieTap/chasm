(module
  (import "host" "add" (func $add (param i32 i32) (result i32)))

  (type $point (struct (field $x (mut i32)) (field $y (mut i32))))
  (type $wide (struct
    (field $big (mut i64))
    (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32))
    (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32))
    (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32))
    (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32)) (field (mut i32))))
  (type $shape (sub (struct (field $sides (mut i32)))))
  (type $square (sub $shape (struct (field $sides (mut i32)) (field $edge (mut i32)))))
  (type $sealed (struct (field i32)))
  (type $bytes (array (mut i8)))
  (type $points (array (mut (ref null $point))))
  (type $unary (func (param i32) (result i32)))

  (memory 1)
  (data (i32.const 0) "\05\00\00\00")
  (data $passive "\01\02\03\04")

  (global $saved (mut (ref null $point)) (ref.null $point))

  (table $functions 2 funcref)
  (elem (table $functions) (i32.const 0) func $double $triple)

  (func $double (type $unary) (i32.mul (local.get 0) (i32.const 2)))
  (func $triple (type $unary) (i32.mul (local.get 0) (i32.const 3)))

  (func (export "struct_inline") (result i32)
    (local $point (ref null $point))
    (local.set $point (struct.new $point (i32.const 3) (i32.const 4)))
    (struct.set $point $x (local.get $point) (i32.const 10))
    (i32.add (struct.get $point $x (local.get $point)) (struct.get $point $y (local.get $point))))

  (func (export "struct_wide") (result i64)
    (local $wide (ref null $wide))
    (local.set $wide (struct.new_default $wide))
    (struct.set $wide $big (local.get $wide) (i64.const 0x100000000))
    (struct.set $wide 24 (local.get $wide) (i32.const 7))
    (i64.add (struct.get $wide $big (local.get $wide)) (i64.extend_i32_u (struct.get $wide 24 (local.get $wide)))))

  (func (export "subtype") (result i32)
    (local $shape (ref null $shape))
    (local.set $shape (struct.new $square (i32.const 4) (i32.const 5)))
    (i32.add
      (struct.get $shape $sides (local.get $shape))
      (struct.get $square $edge (ref.cast (ref $square) (local.get $shape)))))

  (func (export "array_bytes") (result i32)
    (local $source (ref null $bytes))
    (local $target (ref null $bytes))
    (local.set $source (array.new $bytes (i32.const 1) (i32.const 4)))
    (array.set $bytes (local.get $source) (i32.const 2) (i32.const 200))
    (local.set $target (array.new_default $bytes (i32.const 4)))
    (array.copy $bytes $bytes (local.get $target) (i32.const 0) (local.get $source) (i32.const 1) (i32.const 3))
    (i32.add (array.len (local.get $target)) (array.get_u $bytes (local.get $target) (i32.const 1))))

  (func (export "array_references") (result i32)
    (local $source (ref null $points))
    (local $target (ref null $points))
    (local.set $source (array.new $points (ref.null $point) (i32.const 3)))
    (array.set $points (local.get $source) (i32.const 1) (struct.new $point (i32.const 6) (i32.const 7)))
    (local.set $target (array.new_default $points (i32.const 3)))
    (array.copy $points $points (local.get $target) (i32.const 0) (local.get $source) (i32.const 0) (i32.const 3))
    (i32.add
      (array.len (local.get $target))
      (struct.get $point $y (array.get $points (local.get $target) (i32.const 1)))))

  (func (export "casts") (result i32)
    (local $square anyref)
    (local $sealed anyref)
    (local.set $square (struct.new $square (i32.const 4) (i32.const 2)))
    (local.set $sealed (struct.new $sealed (i32.const 9)))
    (i32.add
      (i32.add
        (ref.test (ref $shape) (local.get $square))
        (i32.shl (ref.test (ref $sealed) (local.get $square)) (i32.const 1)))
      (i32.add
        (i32.shl (ref.test (ref $sealed) (local.get $sealed)) (i32.const 2))
        (i32.add
          (struct.get $sealed 0 (ref.cast (ref $sealed) (local.get $sealed)))
          (struct.get $shape $sides (ref.cast (ref $shape) (local.get $square)))))))

  (func (export "branch_casts") (result i32)
    (local $square anyref)
    (local.set $square (struct.new $square (i32.const 4) (i32.const 2)))
    (block $not_sealed (result anyref)
      (block $is_sealed (result (ref $sealed))
        (br_on_cast $is_sealed anyref (ref $sealed) (local.get $square))
        (br $not_sealed))
      (drop)
      (return (i32.const -1)))
    (drop)
    (block $is_shape (result (ref $shape))
      (br_on_cast $is_shape anyref (ref $shape) (local.get $square))
      (drop)
      (return (i32.const -2)))
    (struct.get $shape $sides))

  (func (export "data") (result i32)
    (local $bytes (ref null $bytes))
    (memory.init $passive (i32.const 16) (i32.const 0) (i32.const 4))
    (local.set $bytes (array.new_data $bytes $passive (i32.const 1) (i32.const 2)))
    (i32.add
      (i32.add (i32.load (i32.const 0)) (i32.load8_u (i32.const 19)))
      (array.get_u $bytes (local.get $bytes) (i32.const 1))))

  (func (export "reference_global") (result i32)
    (global.set $saved (struct.new $point (i32.const 8) (i32.const 1)))
    (struct.get $point $x (global.get $saved)))

  (func (export "call_indirect") (result i32)
    (i32.add
      (call_indirect $functions (type $unary) (i32.const 5) (i32.const 0))
      (call_indirect $functions (type $unary) (i32.const 5) (i32.const 1))))

  (func (export "host") (result i32)
    (call $add (i32.const 20) (i32.const 22)))

  (func (export "trap") (result i32)
    (unreachable))
)
