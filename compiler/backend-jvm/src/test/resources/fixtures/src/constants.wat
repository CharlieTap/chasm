(module
  (type $numbers (array i32))
  (global $plain i32 (i32.const 7))
  (global $built (export "numbers") (ref $numbers) (array.new_fixed $numbers 3 (i32.const 1) (i32.const 2) (global.get $plain))))
