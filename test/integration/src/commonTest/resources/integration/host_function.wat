(module
  (import "env" "host_function" (func $host_function (result i32)))
  (func $call_host_function (result i32) call $host_function)
  (export "call_host_function" (func $call_host_function))
)
