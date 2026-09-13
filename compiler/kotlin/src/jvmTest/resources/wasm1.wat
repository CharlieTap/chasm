(module
  (type $binary (func (param i32 i32) (result i32)))
  (import "env" "host" (func $host (param i32) (result i32)))
  (memory (export "memory") 1 3)
  (table 2 funcref)
  (global $counter (mut i32) (i32.const 0))
  (func $add (type $binary) local.get 0 local.get 1 i32.add)
  (func $sub (type $binary) local.get 0 local.get 1 i32.sub)
  (elem (i32.const 0) $add $sub)
  (func $start
    i32.const 9 global.set $counter
    i32.const 0 i32.const 1234 i32.store)
  (start $start)
  (func (export "started") (result i32)
    i32.const 0 i32.load global.get $counter i32.add)
  (func (export "sum") (param $n i32) (result i32) (local $sum i32)
    block $done
      loop $again
        local.get $n i32.eqz br_if $done
        local.get $sum local.get $n i32.add local.set $sum
        local.get $n i32.const 1 i32.sub local.set $n
        br $again
      end
    end
    local.get $sum)
  (func $factorial (export "factorial") (param i32) (result i32)
    local.get 0 i32.const 1 i32.le_u
    if (result i32)
      i32.const 1
    else
      local.get 0 local.get 0 i32.const 1 i32.sub call $factorial i32.mul
    end)
  (func (export "indirect") (param i32 i32 i32) (result i32)
    local.get 0 local.get 1 local.get 2 call_indirect (type $binary))
  (func (export "host") (param i32) (result i32)
    local.get 0 call $host i32.const 3 i32.mul)
  (func (export "swap") (param i32 i32) (result i32) (local i32)
    local.get 0 local.set 2
    local.get 1 local.set 0
    local.get 2 local.set 1
    local.get 0 local.get 1 i32.sub)
  (func (export "table_branch") (param i32) (result i32)
    block $default
      block $one
        block $zero
          local.get 0 br_table $zero $one $default
        end
        i32.const 10 return
      end
      i32.const 20 return
    end
    i32.const 30)
  (func (export "memory16") (param i32) (result i32)
    i32.const 4 local.get 0 i32.store16 offset=1
    i32.const 4 i32.load16_s offset=1)
  (func (export "grow") (param i32) (result i32)
    local.get 0 memory.grow)
  (func (export "size") (result i32) memory.size)
  (func (export "load") (param i32) (result i32) local.get 0 i32.load)
  (func (export "div") (param i32 i32) (result i32) local.get 0 local.get 1 i32.div_s)
  (func (export "counter") (param i32) (result i32)
    local.get 0 global.set $counter global.get $counter)
  (func (export "select") (param i32 i32 i32) (result i32)
    local.get 0 local.get 1 local.get 2 select)
  (func (export "i64") (param i64 i64) (result i64)
    local.get 0 local.get 1 i64.mul i64.const 7 i64.rotl)
  (func (export "nearest") (param f64) (result i64)
    local.get 0 f64.nearest i64.reinterpret_f64)
  (func (export "trunc") (param f64) (result i32)
    local.get 0 i32.trunc_f64_s)
  (func (export "float") (param f32 f32) (result f32)
    local.get 0 local.get 1 f32.min)
)
