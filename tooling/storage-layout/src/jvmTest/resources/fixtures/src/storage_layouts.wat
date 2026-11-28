(module
  (type $wide (struct (field i32) (field i64)))
  (type $packed (struct (field i8) (field i16) (field i32)))
  (type $spilling (struct
    (field i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32)))
  (type $leaf (struct (field i32)))
  (type $bytes (array (mut i8)))
  (type $holder (struct (field (ref null $leaf)) (field (ref null $bytes)) (field anyref)))
  (type $vectors (struct (field v128)))
  (type $function (func))
  (type $chars (array (mut i16)))
  (type $ints (array i32))
  (type $longs (array i64))
  (type $floats (array f32))
  (type $doubles (array f64))
  (type $refs (array (ref null $leaf)))
  (type $spill_refs (struct
    (field anyref anyref anyref anyref anyref anyref anyref anyref anyref anyref anyref anyref)
    (field anyref anyref anyref anyref anyref anyref anyref anyref anyref anyref anyref anyref)))
  (type $straddle (struct
    (field i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32)
    (field i64)))
  (rec
    (type $base (sub (struct (field i32) (field (ref null $leaf)))))
    (type $middle (sub $base (struct (field i32) (field (ref null $leaf)) (field (ref null $tip)))))
    (type $tip (sub final $middle (struct (field i32) (field (ref null $leaf)) (field (ref null $tip)) (field i32)))))
)
