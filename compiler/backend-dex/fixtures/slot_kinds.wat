(module
  (tag $wide (param i64))

  (func (export "merge") (param i32 f32) (result i32)
    (drop (f32.neg (local.get 1)))
    (if (result i32) (local.get 0)
      (then (i32.const 7))
      (else (i32.add (local.get 0) (i32.const 1)))))

  (func (export "select") (param i32 f32) (result f32)
    (select (local.get 1) (f32.const 1) (local.get 0)))

  (func (export "copies") (param i32 f64) (result f64 i32)
    (local i32 f64)
    (local.set 2 (local.get 0))
    (local.set 3 (local.get 1))
    (local.get 3)
    (local.get 2))

  (func (export "conditional") (param i32 i64 f32) (result i64)
    (block $done (result i64)
      (f32.neg (local.get 2))
      (br_if $done (i64.add (local.get 1) (i64.const 1)) (local.get 0))
      (drop)
      (drop)
      (i64.const 9)))

  (func (export "conflict") (param i32 f64 i64) (result f64)
    (local.set 1 (if (result f64) (i32.eqz (i32.wrap_i64 (local.get 2)))
      (then (f64.reinterpret_i64 (i64.or (local.get 2) (i64.const 1))))
      (else (local.get 1))))
    (local.get 1))

  (func $restart (export "restart") (param i32 f32) (result f32)
    (if (result f32) (local.get 0)
      (then (return_call $restart (i32.const 0) (f32.add (local.get 1) (f32.const 1))))
      (else (local.get 1))))

  (func (export "payload") (param i32) (result i64)
    (block $caught (result i64)
      (try_table (catch $wide $caught)
        (drop (f32.neg (f32.convert_i32_s (local.get 0))))
        (if (local.get 0) (then (throw $wide (i64.const 3)))))
      (i64.const 0))))
