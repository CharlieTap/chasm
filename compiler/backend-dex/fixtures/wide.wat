;; Wide arithmetic: two-result 128-bit add and subtract, and widening multiplies.
(module
  (func (export "add128") (param i64 i64) (result i64 i64)
    local.get 0 local.get 1 local.get 1 local.get 0
    i64.add128)
  (func (export "sub128") (param i64 i64) (result i64 i64)
    local.get 0 local.get 1 local.get 1 local.get 0
    i64.sub128)
  (func (export "mul_wide_s") (param i64 i64) (result i64 i64)
    local.get 0 local.get 1
    i64.mul_wide_s)
  (func (export "mul_wide_u") (param i64 i64) (result i64 i64)
    local.get 0 local.get 1
    i64.mul_wide_u)
  ;; Results written back over an operand's slot.
  (func (export "chain") (param i64 i64) (result i64)
    (local i64 i64)
    local.get 0 local.get 1 i64.mul_wide_u
    local.set 1 local.set 0
    local.get 0 local.get 1 i64.const 5 i64.const -1
    i64.add128
    i64.xor))
