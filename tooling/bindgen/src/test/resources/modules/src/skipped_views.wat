(module
  (type $inner (struct (field i32)))
  (type $broken (struct (field (ref $inner)) (field v128)))
  (type $kept (struct (field i64)))
  (type $orphan (struct (field f32)))

  (func (export "uses_broken") (param (ref $broken))
    (unreachable))
  (func (export "uses_funcref") (param (ref $orphan) funcref)
    (unreachable))
  (func (export "uses_kept") (param (ref $kept)) (result i64)
    (struct.get $kept 0 (local.get 0))))
