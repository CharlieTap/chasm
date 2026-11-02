(module $importer
  (import "owner" "boom" (func $boom))

  (func (export "not_executed")
    nop)

  (start $boom)
)
