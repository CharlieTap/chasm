(module
  (type $point (struct (field $x (mut i32)) (field $tag (mut i8)) (field $next (mut (ref null $point))) (field $label (mut externref))))
  (import "host" "inspect" (func $inspect (param structref) (result i32)))
  (import "host" "make" (func $make (result structref)))
  (import "host" "raise" (func $raise (param i32)))
  (import "host" "label" (func $label (param externref) (result i32)))
  (tag $boom (param i32 externref))
  (global $saved (export "saved") (mut anyref) (ref.null any))
  (table $things (export "things") 2 anyref)

  (func (export "inspect") (param $x i32) (result i32)
    (local $point (ref null $point))
    (local.set $point
      (struct.new $point
        (local.get $x)
        (i32.const 200)
        (struct.new $point (i32.const 7) (i32.const 0) (ref.null $point) (ref.null extern))
        (ref.null extern)))
    (i32.add (call $inspect (local.get $point)) (struct.get $point $x (local.get $point))))

  (func (export "make") (result i32)
    (local $point (ref $point))
    (local.set $point (ref.cast (ref $point) (call $make)))
    (i32.add
      (struct.get $point $x (local.get $point))
      (call $label (struct.get $point $label (local.get $point)))))

  (func (export "catch_host") (param i32) (result i32)
    (block $caught (result i32 externref)
      (try_table (catch $boom $caught) (call $raise (local.get 0)))
      (return (i32.const -1)))
    (drop)
    (i32.const 1)
    (i32.add))

  (func (export "store") (param i32)
    (table.set $things (i32.const 1)
      (struct.new $point (local.get 0) (i32.const 0) (ref.null $point) (ref.null extern))))

  (func (export "saved_x") (result i32)
    (struct.get $point $x (ref.cast (ref $point) (global.get $saved))))
)
