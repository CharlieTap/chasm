(module
  (memory 1 3)
  (data $d "hello, wasm")
  (global $counter (mut i32) (i32.const 7))
  (global $wide (mut i64) (i64.const -5))

  (func (export "store_load") (param $addr i32) (param $v i64) (result i64)
    (i64.store offset=4 (local.get $addr) (local.get $v))
    (i64.add
      (i64.load offset=4 (local.get $addr))
      (i64.extend_i32_u (i32.load16_u offset=5 (local.get $addr)))))

  (func (export "narrow") (param $addr i32) (param $v i32) (result i32)
    (i32.store8 (local.get $addr) (local.get $v))
    (i32.add (i32.load8_s (local.get $addr)) (i32.load8_u (local.get $addr))))

  (func (export "floats") (param $addr i32) (param $f f32) (param $d f64) (result f64)
    (f32.store (local.get $addr) (local.get $f))
    (f64.store offset=8 (local.get $addr) (local.get $d))
    (f64.add (f64.promote_f32 (f32.load (local.get $addr))) (f64.load offset=8 (local.get $addr))))

  (func (export "grow") (param $pages i32) (result i32)
    (drop (memory.grow (local.get $pages)))
    (memory.size))

  (func (export "bulk") (param $dst i32) (param $len i32) (result i32)
    (memory.fill (local.get $dst) (i32.const 0x41) (local.get $len))
    (memory.copy (i32.add (local.get $dst) (i32.const 3)) (local.get $dst) (local.get $len))
    (memory.init $d (local.get $dst) (i32.const 0) (i32.const 5))
    (i32.load (i32.add (local.get $dst) (i32.const 2))))

  (func (export "drop_data") (data.drop $d))

  (func (export "count") (param $n i32) (result i32)
    (global.set $counter (i32.add (global.get $counter) (local.get $n)))
    (global.set $wide (i64.mul (global.get $wide) (i64.const 3)))
    (i32.add (global.get $counter) (i32.wrap_i64 (global.get $wide))))
)
