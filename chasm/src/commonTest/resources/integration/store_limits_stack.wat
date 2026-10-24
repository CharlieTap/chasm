(module
  (import "env" "host" (func $host (param i64 i64 i64 i64 i64 i64 i64 i64)))

  ;; Recurses without end.
  (func $runaway (export "runaway")
    (call $runaway))

  ;; Recurses $depth calls deep and returns $depth.
  (func $recurse (export "recurse") (param $depth i32) (result i32)
    (if (result i32) (i32.eqz (local.get $depth))
      (then (i32.const 0))
      (else
        (i32.add
          (call $recurse (i32.sub (local.get $depth) (i32.const 1)))
          (i32.const 1)))))

  ;; Takes 64 parameters, so its frame needs more than 64 slots.
  (func (export "wide")
    (param i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64)
    (param i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64)
    (param i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64)
    (param i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64 i64)
    (result i64)
    (local.get 63))

  (export "host" (func $host)))
