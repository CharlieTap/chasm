(module $memory_fixture
  (memory 1)
  (data $one "\01")

  (func (export "load_constant_offset") (result i32)
    i32.const 65534
    i32.load offset=4)

  (func (export "store_constant_offset")
    i32.const 65534
    i32.const 1
    i32.store offset=4)

  (func (export "init_source_out_of_bounds")
    i32.const 0
    i32.const 1
    i32.const 1
    memory.init $one)

  (func (export "init_destination_out_of_bounds")
    i32.const 65536
    i32.const 0
    i32.const 1
    memory.init $one)
)
