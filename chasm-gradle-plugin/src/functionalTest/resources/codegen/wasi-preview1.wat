(module
  (import "wasi_snapshot_preview1" "fd_write"
    (func $fd_write (param i32 i32 i32 i32) (result i32)))
  (import "wasi_snapshot_preview1" "proc_exit"
    (func $proc_exit (param i32)))

  (memory (export "memory") 1)
  (data (i32.const 0) "\10\00\00\00\05\00\00\00")
  (data (i32.const 16) "hello")

  (func (export "write") (result i32)
    i32.const 1
    i32.const 0
    i32.const 1
    i32.const 8
    call $fd_write)

  (func (export "terminate")
    i32.const 7
    call $proc_exit))
