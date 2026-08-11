;; Source for contrived.wasm. Regenerate the binary with:
;; wasm-tools parse contrived.wat -o contrived.wasm
(module
  (type $nullary_i32 (func (result i32)))
  (type $unary_i32 (func (param i32) (result i32)))
  (type $leaf (struct (field (mut i32))))
  (type $node (struct (field (mut (ref null $leaf)))))
  (type $i32_array (array (mut i32)))

  (memory $memory 1)
  (table $functions 4 funcref)
  (global $global (mut i32) (i32.const 0))

  (func $callee.no_locals (result i32)
    i32.const 1)

  (func $callee.locals (result i32) (local i32)
    i32.const 1
    local.set 0
    local.get 0)

  (func $callee.unary (param $value i32) (result i32)
    local.get $value)

  (elem (i32.const 0) $callee.no_locals $callee.locals $callee.unary)

  (func $numeric.i32.add.ii (param $p0 i32) (param $p1 i32) (result i32)
    i32.const 1
    i32.const 1
    i32.add)

  (func $numeric.i32.add.is (param $p0 i32) (param $p1 i32) (result i32)
    i32.const 1
    local.get $p1
    i32.add)

  (func $numeric.i32.add.si (param $p0 i32) (param $p1 i32) (result i32)
    local.get $p0
    i32.const 1
    i32.add)

  (func $numeric.i32.add.ss (param $p0 i32) (param $p1 i32) (result i32)
    local.get $p0
    local.get $p1
    i32.add)

  (func $parametric.select.iii (param $p0 i32) (param $p1 i32) (param $p2 i32) (result i32)
    i32.const 1
    i32.const 1
    i32.const 1
    select)

  (func $parametric.select.sii (param $p0 i32) (param $p1 i32) (param $p2 i32) (result i32)
    i32.const 1
    i32.const 1
    local.get $p2
    select)

  (func $parametric.select.iis (param $p0 i32) (param $p1 i32) (param $p2 i32) (result i32)
    i32.const 1
    local.get $p1
    i32.const 1
    select)

  (func $parametric.select.sis (param $p0 i32) (param $p1 i32) (param $p2 i32) (result i32)
    i32.const 1
    local.get $p1
    local.get $p2
    select)

  (func $parametric.select.isi (param $p0 i32) (param $p1 i32) (param $p2 i32) (result i32)
    local.get $p0
    i32.const 1
    i32.const 1
    select)

  (func $parametric.select.ssi (param $p0 i32) (param $p1 i32) (param $p2 i32) (result i32)
    local.get $p0
    i32.const 1
    local.get $p2
    select)

  (func $parametric.select.iss (param $p0 i32) (param $p1 i32) (param $p2 i32) (result i32)
    local.get $p0
    local.get $p1
    i32.const 1
    select)

  (func $parametric.select.sss (param $p0 i32) (param $p1 i32) (param $p2 i32) (result i32)
    local.get $p0
    local.get $p1
    local.get $p2
    select)

  (func $memory.i32.load.i (param $p0 i32) (result i32)
    i32.const 1
    i32.load)

  (func $memory.i32.load.s (param $p0 i32) (result i32)
    local.get $p0
    i32.load)

  (func $memory.i32.store.ii (param $p0 i32) (param $p1 i32)
    i32.const 1
    i32.const 1
    i32.store)

  (func $memory.i32.store.is (param $p0 i32) (param $p1 i32)
    local.get $p0
    i32.const 1
    i32.store)

  (func $memory.i32.store.si (param $p0 i32) (param $p1 i32)
    i32.const 1
    local.get $p1
    i32.store)

  (func $memory.i32.store.ss (param $p0 i32) (param $p1 i32)
    local.get $p0
    local.get $p1
    i32.store)

  (func $table.get.i (param $p0 i32) (result funcref)
    i32.const 1
    table.get $functions)

  (func $table.get.s (param $p0 i32) (result funcref)
    local.get $p0
    table.get $functions)

  (func $table.set.si (param $index i32) (param $value funcref)
    i32.const 0
    local.get $value
    table.set $functions)

  (func $table.set.ss (param $index i32) (param $value funcref)
    local.get $index
    local.get $value
    table.set $functions)

  (func $memory.copy.iii (param $p0 i32) (param $p1 i32) (param $p2 i32)
    i32.const 1
    i32.const 1
    i32.const 1
    memory.copy)

  (func $memory.copy.iis (param $p0 i32) (param $p1 i32) (param $p2 i32)
    local.get $p0
    i32.const 1
    i32.const 1
    memory.copy)

  (func $memory.copy.isi (param $p0 i32) (param $p1 i32) (param $p2 i32)
    i32.const 1
    local.get $p1
    i32.const 1
    memory.copy)

  (func $memory.copy.iss (param $p0 i32) (param $p1 i32) (param $p2 i32)
    local.get $p0
    local.get $p1
    i32.const 1
    memory.copy)

  (func $memory.copy.sii (param $p0 i32) (param $p1 i32) (param $p2 i32)
    i32.const 1
    i32.const 1
    local.get $p2
    memory.copy)

  (func $memory.copy.sis (param $p0 i32) (param $p1 i32) (param $p2 i32)
    local.get $p0
    i32.const 1
    local.get $p2
    memory.copy)

  (func $memory.copy.ssi (param $p0 i32) (param $p1 i32) (param $p2 i32)
    i32.const 1
    local.get $p1
    local.get $p2
    memory.copy)

  (func $memory.copy.sss (param $p0 i32) (param $p1 i32) (param $p2 i32)
    local.get $p0
    local.get $p1
    local.get $p2
    memory.copy)

  (func $table.copy.iii (param $p0 i32) (param $p1 i32) (param $p2 i32)
    i32.const 1
    i32.const 1
    i32.const 1
    table.copy $functions $functions)

  (func $table.copy.iis (param $p0 i32) (param $p1 i32) (param $p2 i32)
    local.get $p0
    i32.const 1
    i32.const 1
    table.copy $functions $functions)

  (func $table.copy.isi (param $p0 i32) (param $p1 i32) (param $p2 i32)
    i32.const 1
    local.get $p1
    i32.const 1
    table.copy $functions $functions)

  (func $table.copy.iss (param $p0 i32) (param $p1 i32) (param $p2 i32)
    local.get $p0
    local.get $p1
    i32.const 1
    table.copy $functions $functions)

  (func $table.copy.sii (param $p0 i32) (param $p1 i32) (param $p2 i32)
    i32.const 1
    i32.const 1
    local.get $p2
    table.copy $functions $functions)

  (func $table.copy.sis (param $p0 i32) (param $p1 i32) (param $p2 i32)
    local.get $p0
    i32.const 1
    local.get $p2
    table.copy $functions $functions)

  (func $table.copy.ssi (param $p0 i32) (param $p1 i32) (param $p2 i32)
    i32.const 1
    local.get $p1
    local.get $p2
    table.copy $functions $functions)

  (func $table.copy.sss (param $p0 i32) (param $p1 i32) (param $p2 i32)
    local.get $p0
    local.get $p1
    local.get $p2
    table.copy $functions $functions)

  (func $variable.global_get (result i32)
    global.get $global)

  (func $variable.global_set.i
    i32.const 1
    global.set $global)

  (func $variable.global_set.s (param $value i32)
    local.get $value
    global.set $global)


  ;; Direct producer destinations and compiler-state-only variable operations.
  (func $lowering.producer_to_local (param $left i32) (param $right i32) (local $result i32)
    local.get $left
    local.get $right
    i32.add
    local.set $result)

  (func $lowering.producer_to_local_tee
    (param $left i32)
    (param $right i32)
    (result i32)
    (local $result i32)
    local.get $left
    local.get $right
    i32.add
    local.tee $result
    i32.const 1
    i32.add)

  (func $lowering.local_get_alias (param $value i32) (result i32)
    local.get $value)

  (func $lowering.identity_local_write (param $value i32)
    local.get $value
    local.set $value)

  (func $lowering.preserve_live_local_alias (param $value i32) (result i32) (local $copy i32)
    local.get $value
    local.set $copy
    local.get $copy
    i32.const 2
    local.set $copy
    i32.const 1
    i32.add)

  (func $lowering.no_op_reinterpret (result i32)
    f32.const 1
    f32.neg
    i32.reinterpret_f32)

  ;; Structured control and taken-only branch work.
  (func $admin.jump_if.i
    block
      i32.const 1
      br_if 0
      nop
    end)

  (func $admin.jump_if.s (param $condition i32)
    block
      local.get $condition
      br_if 0
      nop
    end)

  (func $admin.jump_if_zero.i
    i32.const 1
    if
      nop
    end)

  (func $admin.jump_if_zero.s (param $condition i32)
    local.get $condition
    if
      nop
    end)

  (func $admin.jump_if_copy.i (result i32)
    block
      i32.const 20
      i32.const 22
      i32.add
      i32.const 1
      br_if 1
      drop
    end
    i32.const 0)

  (func $admin.jump_if_copy.s (param $condition i32) (result i32)
    block
      i32.const 20
      i32.const 22
      i32.add
      local.get $condition
      br_if 1
      drop
    end
    i32.const 0)

  (func $lowering.taken_only_branch_copy (param $condition i32) (result i32)
    block (result i32)
      i32.const 42
      local.get $condition
      br_if 0
      drop
      i32.const 7
    end)

  (func $control.unreachable
    unreachable
    i32.const 1
    drop)

  ;; Predecoded direct-call plans.
  (func $control.call.wasm.no_locals (result i32)
    call $callee.no_locals)

  (func $control.call.wasm.locals (result i32)
    call $callee.locals)

  (func $control.return_call.wasm (result i32)
    return_call $callee.no_locals)

  (func $control.call_indirect.i (result i32)
    i32.const 0
    call_indirect (type $nullary_i32))

  (func $control.call_indirect.s (param $target i32) (result i32)
    local.get $target
    call_indirect (type $nullary_i32))

  (func $control.call_ref (result i32)
    ref.func $callee.no_locals
    call_ref $nullary_i32)

  (func $control.return_call_indirect.i (result i32)
    i32.const 0
    return_call_indirect (type $nullary_i32))

  (func $control.return_call_indirect.s (param $target i32) (result i32)
    local.get $target
    return_call_indirect (type $nullary_i32))

  (func $control.return_call_ref (result i32)
    ref.func $callee.no_locals
    return_call_ref $nullary_i32)

  (func $lowering.indirect_target_staging
    (param $target i32)
    (param $argument i32)
    (result i32)
    local.get $argument
    local.get $target
    call_indirect (type $unary_i32))

  (func $lowering.tail_call_operands (param $value i32) (result i32)
    local.get $value
    return_call $callee.unary)

  ;; Aggregate access-chain fusions.
  (func $aggregate.ref_cast_struct_get (param $value anyref) (result i32)
    local.get $value
    ref.cast (ref $leaf)
    struct.get $leaf 0)

  (func $aggregate.struct_get_struct_get (param $value (ref $node)) (result i32)
    local.get $value
    struct.get $node 0
    struct.get $leaf 0)

  (func $aggregate.local_set_struct_get
    (param $destination (ref null $leaf))
    (param $value (ref $leaf))
    (result i32)
    local.get $value
    local.tee $destination
    struct.get $leaf 0)

  ;; Representative aggregate immediate/slot specializations.
  (func $aggregate.array.new.ii (result (ref $i32_array))
    i32.const 1
    i32.const 2
    array.new $i32_array)

  (func $aggregate.array.new.is (param $value i32) (result (ref $i32_array))
    local.get $value
    i32.const 2
    array.new $i32_array)

  (func $aggregate.array.new.si (param $size i32) (result (ref $i32_array))
    i32.const 1
    local.get $size
    array.new $i32_array)


  (func $aggregate.array.new.ss
    (param $value i32)
    (param $size i32)
    (result (ref $i32_array))
    local.get $value
    local.get $size
    array.new $i32_array)

  (func $aggregate.array.set.ii (param $array (ref $i32_array))
    local.get $array
    i32.const 0
    i32.const 1
    array.set $i32_array)

  (func $aggregate.array.set.is
    (param $array (ref $i32_array))
    (param $index i32)
    local.get $array
    local.get $index
    i32.const 1
    array.set $i32_array)

  (func $aggregate.array.set.si
    (param $array (ref $i32_array))
    (param $value i32)
    local.get $array
    i32.const 0
    local.get $value
    array.set $i32_array)

  (func $aggregate.array.set.ss
    (param $array (ref $i32_array))
    (param $index i32)
    (param $value i32)
    local.get $array
    local.get $index
    local.get $value
    array.set $i32_array)

  (func $aggregate.array.copy.iii
    (param $destination (ref $i32_array))
    (param $source (ref $i32_array))
    (param $destination_offset i32)
    (param $source_offset i32)
    (param $length i32)
    local.get $destination
    i32.const 1
    local.get $source
    i32.const 1
    i32.const 1
    array.copy $i32_array $i32_array)

  (func $aggregate.array.copy.iis
    (param $destination (ref $i32_array))
    (param $source (ref $i32_array))
    (param $destination_offset i32)
    (param $source_offset i32)
    (param $length i32)
    local.get $destination
    local.get $destination_offset
    local.get $source
    i32.const 1
    i32.const 1
    array.copy $i32_array $i32_array)

  (func $aggregate.array.copy.isi
    (param $destination (ref $i32_array))
    (param $source (ref $i32_array))
    (param $destination_offset i32)
    (param $source_offset i32)
    (param $length i32)
    local.get $destination
    i32.const 1
    local.get $source
    local.get $source_offset
    i32.const 1
    array.copy $i32_array $i32_array)

  (func $aggregate.array.copy.iss
    (param $destination (ref $i32_array))
    (param $source (ref $i32_array))
    (param $destination_offset i32)
    (param $source_offset i32)
    (param $length i32)
    local.get $destination
    local.get $destination_offset
    local.get $source
    local.get $source_offset
    i32.const 1
    array.copy $i32_array $i32_array)

  (func $aggregate.array.copy.sii
    (param $destination (ref $i32_array))
    (param $source (ref $i32_array))
    (param $destination_offset i32)
    (param $source_offset i32)
    (param $length i32)
    local.get $destination
    i32.const 1
    local.get $source
    i32.const 1
    local.get $length
    array.copy $i32_array $i32_array)

  (func $aggregate.array.copy.sis
    (param $destination (ref $i32_array))
    (param $source (ref $i32_array))
    (param $destination_offset i32)
    (param $source_offset i32)
    (param $length i32)
    local.get $destination
    local.get $destination_offset
    local.get $source
    i32.const 1
    local.get $length
    array.copy $i32_array $i32_array)

  (func $aggregate.array.copy.ssi
    (param $destination (ref $i32_array))
    (param $source (ref $i32_array))
    (param $destination_offset i32)
    (param $source_offset i32)
    (param $length i32)
    local.get $destination
    i32.const 1
    local.get $source
    local.get $source_offset
    local.get $length
    array.copy $i32_array $i32_array)

  (func $aggregate.array.copy.sss
    (param $destination (ref $i32_array))
    (param $source (ref $i32_array))
    (param $destination_offset i32)
    (param $source_offset i32)
    (param $length i32)
    local.get $destination
    local.get $destination_offset
    local.get $source
    local.get $source_offset
    local.get $length
    array.copy $i32_array $i32_array)


  (func $admin.jump_condition.i32.eqz.s.match (param $p0 i32)
    block
      local.get $p0
      i32.eqz
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i32.eqz.i.mismatch
      i32.const 1
    i32.eqz
    if
      nop
    end)

  (func $admin.jump_condition.i64.eqz.s.match (param $p0 i64)
    block
      local.get $p0
      i64.eqz
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i64.eqz.i.mismatch
      i64.const 1
    i64.eqz
    if
      nop
    end)

  (func $admin.jump_condition.i32.eq.ss.match (param $p0 i32) (param $p1 i32)
    block
      local.get $p0
      local.get $p1
      i32.eq
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i32.eq.ii.mismatch
      i32.const 1
      i32.const 1
    i32.eq
    if
      nop
    end)

  (func $admin.jump_condition.i64.eq.ss.match (param $p0 i64) (param $p1 i64)
    block
      local.get $p0
      local.get $p1
      i64.eq
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i64.eq.ii.mismatch
      i64.const 1
      i64.const 1
    i64.eq
    if
      nop
    end)

  (func $admin.jump_condition.i32.ne.ss.match (param $p0 i32) (param $p1 i32)
    block
      local.get $p0
      local.get $p1
      i32.ne
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i32.ne.ii.mismatch
      i32.const 1
      i32.const 1
    i32.ne
    if
      nop
    end)

  (func $admin.jump_condition.i64.ne.ss.match (param $p0 i64) (param $p1 i64)
    block
      local.get $p0
      local.get $p1
      i64.ne
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i64.ne.ii.mismatch
      i64.const 1
      i64.const 1
    i64.ne
    if
      nop
    end)

  (func $admin.jump_condition.i32.lt_s.ss.match (param $p0 i32) (param $p1 i32)
    block
      local.get $p0
      local.get $p1
      i32.lt_s
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i32.lt_s.ii.mismatch
      i32.const 1
      i32.const 1
    i32.lt_s
    if
      nop
    end)

  (func $admin.jump_condition.i64.lt_s.ss.match (param $p0 i64) (param $p1 i64)
    block
      local.get $p0
      local.get $p1
      i64.lt_s
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i64.lt_s.ii.mismatch
      i64.const 1
      i64.const 1
    i64.lt_s
    if
      nop
    end)

  (func $admin.jump_condition.i32.lt_u.ss.match (param $p0 i32) (param $p1 i32)
    block
      local.get $p0
      local.get $p1
      i32.lt_u
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i32.lt_u.ii.mismatch
      i32.const 1
      i32.const 1
    i32.lt_u
    if
      nop
    end)

  (func $admin.jump_condition.i64.lt_u.ss.match (param $p0 i64) (param $p1 i64)
    block
      local.get $p0
      local.get $p1
      i64.lt_u
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i64.lt_u.ii.mismatch
      i64.const 1
      i64.const 1
    i64.lt_u
    if
      nop
    end)

  (func $admin.jump_condition.i32.gt_s.ss.match (param $p0 i32) (param $p1 i32)
    block
      local.get $p0
      local.get $p1
      i32.gt_s
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i32.gt_s.ii.mismatch
      i32.const 1
      i32.const 1
    i32.gt_s
    if
      nop
    end)

  (func $admin.jump_condition.i64.gt_s.ss.match (param $p0 i64) (param $p1 i64)
    block
      local.get $p0
      local.get $p1
      i64.gt_s
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i64.gt_s.ii.mismatch
      i64.const 1
      i64.const 1
    i64.gt_s
    if
      nop
    end)

  (func $admin.jump_condition.i32.gt_u.ss.match (param $p0 i32) (param $p1 i32)
    block
      local.get $p0
      local.get $p1
      i32.gt_u
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i32.gt_u.ii.mismatch
      i32.const 1
      i32.const 1
    i32.gt_u
    if
      nop
    end)

  (func $admin.jump_condition.i64.gt_u.ss.match (param $p0 i64) (param $p1 i64)
    block
      local.get $p0
      local.get $p1
      i64.gt_u
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i64.gt_u.ii.mismatch
      i64.const 1
      i64.const 1
    i64.gt_u
    if
      nop
    end)

  (func $admin.jump_condition.i32.le_s.ss.match (param $p0 i32) (param $p1 i32)
    block
      local.get $p0
      local.get $p1
      i32.le_s
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i32.le_s.ii.mismatch
      i32.const 1
      i32.const 1
    i32.le_s
    if
      nop
    end)

  (func $admin.jump_condition.i64.le_s.ss.match (param $p0 i64) (param $p1 i64)
    block
      local.get $p0
      local.get $p1
      i64.le_s
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i64.le_s.ii.mismatch
      i64.const 1
      i64.const 1
    i64.le_s
    if
      nop
    end)

  (func $admin.jump_condition.i32.le_u.ss.match (param $p0 i32) (param $p1 i32)
    block
      local.get $p0
      local.get $p1
      i32.le_u
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i32.le_u.ii.mismatch
      i32.const 1
      i32.const 1
    i32.le_u
    if
      nop
    end)

  (func $admin.jump_condition.i64.le_u.ss.match (param $p0 i64) (param $p1 i64)
    block
      local.get $p0
      local.get $p1
      i64.le_u
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i64.le_u.ii.mismatch
      i64.const 1
      i64.const 1
    i64.le_u
    if
      nop
    end)

  (func $admin.jump_condition.i32.ge_s.ss.match (param $p0 i32) (param $p1 i32)
    block
      local.get $p0
      local.get $p1
      i32.ge_s
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i32.ge_s.ii.mismatch
      i32.const 1
      i32.const 1
    i32.ge_s
    if
      nop
    end)

  (func $admin.jump_condition.i64.ge_s.ss.match (param $p0 i64) (param $p1 i64)
    block
      local.get $p0
      local.get $p1
      i64.ge_s
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i64.ge_s.ii.mismatch
      i64.const 1
      i64.const 1
    i64.ge_s
    if
      nop
    end)

  (func $admin.jump_condition.i32.ge_u.ss.match (param $p0 i32) (param $p1 i32)
    block
      local.get $p0
      local.get $p1
      i32.ge_u
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i32.ge_u.ii.mismatch
      i32.const 1
      i32.const 1
    i32.ge_u
    if
      nop
    end)

  (func $admin.jump_condition.i64.ge_u.ss.match (param $p0 i64) (param $p1 i64)
    block
      local.get $p0
      local.get $p1
      i64.ge_u
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i64.ge_u.ii.mismatch
      i64.const 1
      i64.const 1
    i64.ge_u
    if
      nop
    end)

  (func $admin.jump_condition.f32.eq.ss.match (param $p0 f32) (param $p1 f32)
    block
      local.get $p0
      local.get $p1
      f32.eq
      br_if 0
      nop
    end)

  (func $admin.jump_condition.f32.eq.ii.mismatch
      f32.const 1
      f32.const 1
    f32.eq
    if
      nop
    end)

  (func $admin.jump_condition.f64.eq.ss.match (param $p0 f64) (param $p1 f64)
    block
      local.get $p0
      local.get $p1
      f64.eq
      br_if 0
      nop
    end)

  (func $admin.jump_condition.f64.eq.ii.mismatch
      f64.const 1
      f64.const 1
    f64.eq
    if
      nop
    end)

  (func $admin.jump_condition.f32.ne.ss.match (param $p0 f32) (param $p1 f32)
    block
      local.get $p0
      local.get $p1
      f32.ne
      br_if 0
      nop
    end)

  (func $admin.jump_condition.f32.ne.ii.mismatch
      f32.const 1
      f32.const 1
    f32.ne
    if
      nop
    end)

  (func $admin.jump_condition.f64.ne.ss.match (param $p0 f64) (param $p1 f64)
    block
      local.get $p0
      local.get $p1
      f64.ne
      br_if 0
      nop
    end)

  (func $admin.jump_condition.f64.ne.ii.mismatch
      f64.const 1
      f64.const 1
    f64.ne
    if
      nop
    end)

  (func $admin.jump_condition.f32.lt.ss.match (param $p0 f32) (param $p1 f32)
    block
      local.get $p0
      local.get $p1
      f32.lt
      br_if 0
      nop
    end)

  (func $admin.jump_condition.f32.lt.ii.mismatch
      f32.const 1
      f32.const 1
    f32.lt
    if
      nop
    end)

  (func $admin.jump_condition.f64.lt.ss.match (param $p0 f64) (param $p1 f64)
    block
      local.get $p0
      local.get $p1
      f64.lt
      br_if 0
      nop
    end)

  (func $admin.jump_condition.f64.lt.ii.mismatch
      f64.const 1
      f64.const 1
    f64.lt
    if
      nop
    end)

  (func $admin.jump_condition.f32.gt.ss.match (param $p0 f32) (param $p1 f32)
    block
      local.get $p0
      local.get $p1
      f32.gt
      br_if 0
      nop
    end)

  (func $admin.jump_condition.f32.gt.ii.mismatch
      f32.const 1
      f32.const 1
    f32.gt
    if
      nop
    end)

  (func $admin.jump_condition.f64.gt.ss.match (param $p0 f64) (param $p1 f64)
    block
      local.get $p0
      local.get $p1
      f64.gt
      br_if 0
      nop
    end)

  (func $admin.jump_condition.f64.gt.ii.mismatch
      f64.const 1
      f64.const 1
    f64.gt
    if
      nop
    end)

  (func $admin.jump_condition.f32.le.ss.match (param $p0 f32) (param $p1 f32)
    block
      local.get $p0
      local.get $p1
      f32.le
      br_if 0
      nop
    end)

  (func $admin.jump_condition.f32.le.ii.mismatch
      f32.const 1
      f32.const 1
    f32.le
    if
      nop
    end)

  (func $admin.jump_condition.f64.le.ss.match (param $p0 f64) (param $p1 f64)
    block
      local.get $p0
      local.get $p1
      f64.le
      br_if 0
      nop
    end)

  (func $admin.jump_condition.f64.le.ii.mismatch
      f64.const 1
      f64.const 1
    f64.le
    if
      nop
    end)

  (func $admin.jump_condition.f32.ge.ss.match (param $p0 f32) (param $p1 f32)
    block
      local.get $p0
      local.get $p1
      f32.ge
      br_if 0
      nop
    end)

  (func $admin.jump_condition.f32.ge.ii.mismatch
      f32.const 1
      f32.const 1
    f32.ge
    if
      nop
    end)

  (func $admin.jump_condition.f64.ge.ss.match (param $p0 f64) (param $p1 f64)
    block
      local.get $p0
      local.get $p1
      f64.ge
      br_if 0
      nop
    end)

  (func $admin.jump_condition.f64.ge.ii.mismatch
      f64.const 1
      f64.const 1
    f64.ge
    if
      nop
    end)

  (func $admin.jump_condition.i32.eq.is.match (param $p0 i32) (param $p1 i32)
    block
      i32.const 1
      local.get $p1
      i32.eq
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i32.eq.si.match (param $p0 i32) (param $p1 i32)
    block
      local.get $p0
      i32.const 1
      i32.eq
      br_if 0
      nop
    end)

  (func $admin.jump_condition.i32.eqz.i.match
    block
      i32.const 1
      i32.eqz
      br_if 0
      nop
    end)
)
