(module
  (type $exception-tag-type (func))
  (tag $exception-tag (type $exception-tag-type))

  (func (export "test_exception_reference") (result i32)
    block (result exnref)
      try_table (result exnref) (catch_all_ref 0)
        throw $exception-tag
      end
    end
    ref.test (ref exn)
  )

  (func (export "cast_exception_reference") (result i32)
    block (result exnref)
      try_table (result exnref) (catch_all_ref 0)
        throw $exception-tag
      end
    end
    ref.cast (ref exn)
    ref.is_null
    i32.eqz
  )
)
