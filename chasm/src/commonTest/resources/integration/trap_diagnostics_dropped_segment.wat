(module $dropped_segment_fixture
  (memory 1)
  (data $four "\01\02\03\04")

  (func (export "init_after_drop")
    data.drop $four
    i32.const 65536
    i32.const 0
    i32.const 1
    memory.init $four)

  (func (export "init_before_drop")
    i32.const 65536
    i32.const 0
    i32.const 1
    memory.init $four)
)
