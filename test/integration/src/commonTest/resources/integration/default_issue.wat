(module
  (type $struct (struct (field i32)))
  (type $array (array (ref null $struct)))
  (func (export "create_array") (param $length i32) (result (ref $array))
    (array.new_default $array
      (local.get $length)
    )
  )
)
