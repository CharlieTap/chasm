(module $start_fixture
  (func $boom
    unreachable)

  (func $start
    call $boom)

  (start $start)
)
