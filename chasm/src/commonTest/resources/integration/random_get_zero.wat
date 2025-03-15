(module
  (type (;0;) (func (param i32 i32)))
  (type (;1;) (func (param i32) (result i32)))
  (type (;2;) (func (param i32 i32) (result i32)))
  (type (;3;) (func (param i32 i32 i32 i32 i32) (result i32)))
  (type (;4;) (func))
  (type (;5;) (func (param i32)))
  (type (;6;) (func (param i32 i32 i32 i32) (result i32)))
  (type (;7;) (func (param i32 i32 i32 i32)))
  (type (;8;) (func (param i32 i32 i32) (result i32)))
  (type (;9;) (func (param i32 i32 i32)))
  (import "wasi_snapshot_preview1" "fd_write" (func (;0;) (type 6)))
  (import "wasi_snapshot_preview1" "proc_exit" (func (;1;) (type 5)))
  (import "wasi_snapshot_preview1" "random_get" (func (;2;) (type 2)))
  (func (;3;) (type 0) (param i32 i32)
    local.get 0
    local.get 1
    i32.store
  )
  (func (;4;) (type 0) (param i32 i32)
    local.get 0
    local.get 1
    i32.store
  )
  (func (;5;) (type 1) (param i32) (result i32)
    local.get 0
    i32.const 20
    i32.sub
    i32.load offset=16
    i32.const 1
    i32.shr_u
  )
  (func (;6;) (type 3) (param i32 i32 i32 i32 i32) (result i32)
    (local i32 i32 i32 i32 i32 i32)
    local.get 0
    local.get 1
    i32.const 1
    i32.shl
    i32.add
    local.set 5
    local.get 2
    local.get 3
    i32.const 1
    i32.shl
    i32.add
    local.set 6
    i32.const 0
    i32.const 2
    i32.lt_s
    drop
    local.get 4
    i32.const 4
    i32.ge_u
    if (result i32) ;; label = @1
      local.get 5
      i32.const 7
      i32.and
      local.get 6
      i32.const 7
      i32.and
      i32.or
      i32.eqz
    else
      i32.const 0
    end
    if ;; label = @1
      block ;; label = @2
        loop ;; label = @3
          local.get 5
          i64.load
          local.get 6
          i64.load
          i64.ne
          if ;; label = @4
            br 2 (;@2;)
          end
          local.get 5
          i32.const 8
          i32.add
          local.set 5
          local.get 6
          i32.const 8
          i32.add
          local.set 6
          local.get 4
          i32.const 4
          i32.sub
          local.set 4
          local.get 4
          i32.const 4
          i32.ge_u
          br_if 0 (;@3;)
        end
      end
    end
    block ;; label = @1
      loop ;; label = @2
        local.get 4
        local.tee 7
        i32.const 1
        i32.sub
        local.set 4
        local.get 7
        local.set 8
        local.get 8
        if ;; label = @3
          local.get 5
          i32.load16_u
          local.set 9
          local.get 6
          i32.load16_u
          local.set 10
          local.get 9
          local.get 10
          i32.ne
          if ;; label = @4
            local.get 9
            local.get 10
            i32.sub
            return
          end
          local.get 5
          i32.const 2
          i32.add
          local.set 5
          local.get 6
          i32.const 2
          i32.add
          local.set 6
          br 1 (;@2;)
        end
      end
    end
    i32.const 0
  )
  (func (;7;) (type 2) (param i32 i32) (result i32)
    (local i32)
    local.get 0
    local.get 1
    i32.eq
    if ;; label = @1
      i32.const 1
      return
    end
    local.get 0
    i32.const 0
    i32.eq
    if (result i32) ;; label = @1
      i32.const 1
    else
      local.get 1
      i32.const 0
      i32.eq
    end
    if ;; label = @1
      i32.const 0
      return
    end
    local.get 0
    call 5
    local.set 2
    local.get 2
    local.get 1
    call 5
    i32.ne
    if ;; label = @1
      i32.const 0
      return
    end
    local.get 0
    i32.const 0
    local.get 1
    i32.const 0
    local.get 2
    call 6
    i32.eqz
  )
  (func (;8;) (type 2) (param i32 i32) (result i32)
    local.get 0
    local.get 1
    call 7
    i32.eqz
  )
  (func (;9;) (type 3) (param i32 i32 i32 i32 i32) (result i32)
    (local i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32)
    local.get 0
    local.get 1
    i32.const 1
    i32.shl
    i32.add
    local.set 5
    local.get 2
    local.set 6
    block ;; label = @1
      loop ;; label = @2
        local.get 0
        local.get 5
        i32.lt_u
        local.set 7
        local.get 7
        if ;; label = @3
          local.get 0
          i32.load16_u
          local.set 8
          local.get 8
          i32.const 128
          i32.lt_u
          if ;; label = @4
            local.get 6
            local.get 8
            i32.store8
            local.get 6
            i32.const 1
            i32.add
            local.set 6
            local.get 3
            local.get 8
            i32.eqz
            i32.and
            if ;; label = @5
              local.get 6
              local.get 2
              i32.sub
              return
            end
          else
            local.get 8
            i32.const 2048
            i32.lt_u
            if ;; label = @5
              local.get 8
              i32.const 6
              i32.shr_u
              i32.const 192
              i32.or
              local.set 9
              local.get 8
              i32.const 63
              i32.and
              i32.const 128
              i32.or
              local.set 10
              local.get 6
              local.get 10
              i32.const 8
              i32.shl
              local.get 9
              i32.or
              i32.store16
              local.get 6
              i32.const 2
              i32.add
              local.set 6
            else
              local.get 8
              i32.const 63488
              i32.and
              i32.const 55296
              i32.eq
              if ;; label = @6
                local.get 8
                i32.const 56320
                i32.lt_u
                if (result i32) ;; label = @7
                  local.get 0
                  i32.const 2
                  i32.add
                  local.get 5
                  i32.lt_u
                else
                  i32.const 0
                end
                if ;; label = @7
                  local.get 0
                  i32.load16_u offset=2
                  local.set 11
                  local.get 11
                  i32.const 64512
                  i32.and
                  i32.const 56320
                  i32.eq
                  if ;; label = @8
                    i32.const 65536
                    local.get 8
                    i32.const 1023
                    i32.and
                    i32.const 10
                    i32.shl
                    i32.add
                    local.get 11
                    i32.const 1023
                    i32.and
                    i32.or
                    local.set 8
                    local.get 8
                    i32.const 18
                    i32.shr_u
                    i32.const 240
                    i32.or
                    local.set 12
                    local.get 8
                    i32.const 12
                    i32.shr_u
                    i32.const 63
                    i32.and
                    i32.const 128
                    i32.or
                    local.set 13
                    local.get 8
                    i32.const 6
                    i32.shr_u
                    i32.const 63
                    i32.and
                    i32.const 128
                    i32.or
                    local.set 14
                    local.get 8
                    i32.const 63
                    i32.and
                    i32.const 128
                    i32.or
                    local.set 15
                    local.get 6
                    local.get 15
                    i32.const 24
                    i32.shl
                    local.get 14
                    i32.const 16
                    i32.shl
                    i32.or
                    local.get 13
                    i32.const 8
                    i32.shl
                    i32.or
                    local.get 12
                    i32.or
                    i32.store
                    local.get 6
                    i32.const 4
                    i32.add
                    local.set 6
                    local.get 0
                    i32.const 4
                    i32.add
                    local.set 0
                    br 6 (;@2;)
                  end
                end
                local.get 4
                i32.const 0
                i32.ne
                if ;; label = @7
                  local.get 4
                  i32.const 2
                  i32.eq
                  if ;; label = @8
                    i32.const 32
                    i32.const 96
                    i32.const 742
                    i32.const 49
                    call 13
                    unreachable
                  end
                  i32.const 65533
                  local.set 8
                end
              end
              local.get 8
              i32.const 12
              i32.shr_u
              i32.const 224
              i32.or
              local.set 16
              local.get 8
              i32.const 6
              i32.shr_u
              i32.const 63
              i32.and
              i32.const 128
              i32.or
              local.set 17
              local.get 8
              i32.const 63
              i32.and
              i32.const 128
              i32.or
              local.set 18
              local.get 6
              local.get 17
              i32.const 8
              i32.shl
              local.get 16
              i32.or
              i32.store16
              local.get 6
              local.get 18
              i32.store8 offset=2
              local.get 6
              i32.const 3
              i32.add
              local.set 6
            end
          end
          local.get 0
          i32.const 2
          i32.add
          local.set 0
          br 1 (;@2;)
        end
      end
    end
    local.get 3
    if ;; label = @1
      local.get 6
      local.tee 19
      i32.const 1
      i32.add
      local.set 6
      local.get 19
      i32.const 0
      i32.store8
    end
    local.get 6
    local.get 2
    i32.sub
  )
  (func (;10;) (type 3) (param i32 i32 i32 i32 i32) (result i32)
    block ;; label = @1
      block ;; label = @2
        block ;; label = @3
          block ;; label = @4
            global.get 7
            i32.const 3
            i32.sub
            br_table 1 (;@3;) 2 (;@2;) 3 (;@1;) 0 (;@4;)
          end
          unreachable
        end
        i32.const 0
        local.set 3
      end
      i32.const 0
      local.set 4
    end
    local.get 0
    local.get 1
    local.get 2
    local.get 3
    local.get 4
    call 9
  )
  (func (;11;) (type 1) (param i32) (result i32)
    local.get 0
    i32.const 100000
    i32.lt_u
    if ;; label = @1
      local.get 0
      i32.const 100
      i32.lt_u
      if ;; label = @2
        i32.const 1
        local.get 0
        i32.const 10
        i32.ge_u
        i32.add
        return
      else
        i32.const 3
        local.get 0
        i32.const 10000
        i32.ge_u
        i32.add
        local.get 0
        i32.const 1000
        i32.ge_u
        i32.add
        return
      end
      unreachable
    else
      local.get 0
      i32.const 10000000
      i32.lt_u
      if ;; label = @2
        i32.const 6
        local.get 0
        i32.const 1000000
        i32.ge_u
        i32.add
        return
      else
        i32.const 8
        local.get 0
        i32.const 1000000000
        i32.ge_u
        i32.add
        local.get 0
        i32.const 100000000
        i32.ge_u
        i32.add
        return
      end
      unreachable
    end
    unreachable
  )
  (func (;12;) (type 0) (param i32 i32)
    local.get 0
    local.get 1
    i32.store offset=4
  )
  (func (;13;) (type 7) (param i32 i32 i32 i32)
    (local i32 i32 i32 i32 i32 i32)
    i32.const 0
    i32.const 12
    call 4
    i32.const 12
    local.set 4
    local.get 4
    i64.const 9071471065260641
    i64.store
    local.get 4
    i32.const 7
    i32.add
    local.set 4
    local.get 0
    i32.const 0
    call 8
    if ;; label = @1
      local.get 4
      local.get 0
      local.get 0
      call 5
      local.get 4
      i32.const 0
      i32.const 3
      global.set 7
      i32.const 0
      call 10
      i32.add
      local.set 4
    end
    local.get 4
    i32.const 544106784
    i32.store
    local.get 4
    i32.const 4
    i32.add
    local.set 4
    local.get 1
    i32.const 0
    call 8
    if ;; label = @1
      local.get 4
      local.get 1
      local.get 1
      call 5
      local.get 4
      i32.const 0
      i32.const 3
      global.set 7
      i32.const 0
      call 10
      i32.add
      local.set 4
    end
    local.get 4
    local.tee 5
    i32.const 1
    i32.add
    local.set 4
    local.get 5
    i32.const 40
    i32.store8
    local.get 2
    call 11
    local.set 6
    local.get 4
    local.get 6
    i32.add
    local.set 4
    block ;; label = @1
      loop ;; label = @2
        local.get 2
        i32.const 10
        i32.div_u
        local.set 7
        local.get 4
        i32.const 1
        i32.sub
        local.tee 4
        i32.const 48
        local.get 2
        i32.const 10
        i32.rem_u
        i32.add
        i32.store8
        local.get 7
        local.set 2
        local.get 2
        br_if 0 (;@2;)
      end
    end
    local.get 4
    local.get 6
    i32.add
    local.set 4
    local.get 4
    local.tee 8
    i32.const 1
    i32.add
    local.set 4
    local.get 8
    i32.const 58
    i32.store8
    local.get 3
    call 11
    local.set 6
    local.get 4
    local.get 6
    i32.add
    local.set 4
    block ;; label = @1
      loop ;; label = @2
        local.get 3
        i32.const 10
        i32.div_u
        local.set 9
        local.get 4
        i32.const 1
        i32.sub
        local.tee 4
        i32.const 48
        local.get 3
        i32.const 10
        i32.rem_u
        i32.add
        i32.store8
        local.get 9
        local.set 3
        local.get 3
        br_if 0 (;@2;)
      end
    end
    local.get 4
    local.get 6
    i32.add
    local.set 4
    local.get 4
    i32.const 2601
    i32.store16
    local.get 4
    i32.const 2
    i32.add
    local.set 4
    i32.const 0
    local.get 4
    i32.const 12
    i32.sub
    call 12
    i32.const 2
    i32.const 0
    i32.const 1
    i32.const 8
    call 0
    drop
    i32.const 255
    call 1
  )
  (func (;14;) (type 0) (param i32 i32)
    local.get 0
    local.get 1
    i32.store
  )
  (func (;15;) (type 0) (param i32 i32)
    local.get 0
    local.get 1
    i32.store offset=4
  )
  (func (;16;) (type 0) (param i32 i32)
    local.get 0
    local.get 1
    i32.store offset=8
  )
  (func (;17;) (type 0) (param i32 i32)
    (local i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32)
    local.get 1
    i32.load
    local.set 2
    i32.const 1
    drop
    local.get 2
    i32.const 1
    i32.and
    i32.eqz
    if ;; label = @1
      i32.const 0
      i32.const 144
      i32.const 268
      i32.const 14
      call 13
      unreachable
    end
    local.get 2
    i32.const 3
    i32.const -1
    i32.xor
    i32.and
    local.set 3
    i32.const 1
    drop
    local.get 3
    i32.const 12
    i32.ge_u
    i32.eqz
    if ;; label = @1
      i32.const 0
      i32.const 144
      i32.const 270
      i32.const 14
      call 13
      unreachable
    end
    local.get 3
    i32.const 256
    i32.lt_u
    if ;; label = @1
      i32.const 0
      local.set 4
      local.get 3
      i32.const 4
      i32.shr_u
      local.set 5
    else
      local.get 3
      local.tee 6
      i32.const 1073741820
      local.tee 7
      local.get 6
      local.get 7
      i32.lt_u
      select
      local.set 8
      i32.const 31
      local.get 8
      i32.clz
      i32.sub
      local.set 4
      local.get 8
      local.get 4
      i32.const 4
      i32.sub
      i32.shr_u
      i32.const 1
      i32.const 4
      i32.shl
      i32.xor
      local.set 5
      local.get 4
      i32.const 8
      i32.const 1
      i32.sub
      i32.sub
      local.set 4
    end
    i32.const 1
    drop
    local.get 4
    i32.const 23
    i32.lt_u
    if (result i32) ;; label = @1
      local.get 5
      i32.const 16
      i32.lt_u
    else
      i32.const 0
    end
    i32.eqz
    if ;; label = @1
      i32.const 0
      i32.const 144
      i32.const 284
      i32.const 14
      call 13
      unreachable
    end
    local.get 1
    i32.load offset=4
    local.set 9
    local.get 1
    i32.load offset=8
    local.set 10
    local.get 9
    if ;; label = @1
      local.get 9
      local.get 10
      call 16
    end
    local.get 10
    if ;; label = @1
      local.get 10
      local.get 9
      call 15
    end
    local.get 1
    block (result i32) ;; label = @1
      local.get 0
      local.set 11
      local.get 4
      local.set 12
      local.get 5
      local.set 13
      local.get 11
      local.get 12
      i32.const 4
      i32.shl
      local.get 13
      i32.add
      i32.const 2
      i32.shl
      i32.add
      i32.load offset=96
    end
    i32.eq
    if ;; label = @1
      block ;; label = @2
        local.get 0
        local.set 14
        local.get 4
        local.set 15
        local.get 5
        local.set 16
        local.get 10
        local.set 17
        local.get 14
        local.get 15
        i32.const 4
        i32.shl
        local.get 16
        i32.add
        i32.const 2
        i32.shl
        i32.add
        local.get 17
        i32.store offset=96
      end
      local.get 10
      i32.eqz
      if ;; label = @2
        block (result i32) ;; label = @3
          local.get 0
          local.set 18
          local.get 4
          local.set 19
          local.get 18
          local.get 19
          i32.const 2
          i32.shl
          i32.add
          i32.load offset=4
        end
        local.set 20
        block ;; label = @3
          local.get 0
          local.set 21
          local.get 4
          local.set 22
          local.get 20
          i32.const 1
          local.get 5
          i32.shl
          i32.const -1
          i32.xor
          i32.and
          local.tee 20
          local.set 23
          local.get 21
          local.get 22
          i32.const 2
          i32.shl
          i32.add
          local.get 23
          i32.store offset=4
        end
        local.get 20
        i32.eqz
        if ;; label = @3
          local.get 0
          local.get 0
          i32.load
          i32.const 1
          local.get 4
          i32.shl
          i32.const -1
          i32.xor
          i32.and
          call 3
        end
      end
    end
  )
  (func (;18;) (type 0) (param i32 i32)
    (local i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32)
    i32.const 1
    drop
    local.get 1
    i32.eqz
    if ;; label = @1
      i32.const 0
      i32.const 144
      i32.const 201
      i32.const 14
      call 13
      unreachable
    end
    local.get 1
    i32.load
    local.set 2
    i32.const 1
    drop
    local.get 2
    i32.const 1
    i32.and
    i32.eqz
    if ;; label = @1
      i32.const 0
      i32.const 144
      i32.const 203
      i32.const 14
      call 13
      unreachable
    end
    block (result i32) ;; label = @1
      local.get 1
      local.set 3
      local.get 3
      i32.const 4
      i32.add
      local.get 3
      i32.load
      i32.const 3
      i32.const -1
      i32.xor
      i32.and
      i32.add
    end
    local.set 4
    local.get 4
    i32.load
    local.set 5
    local.get 5
    i32.const 1
    i32.and
    if ;; label = @1
      local.get 0
      local.get 4
      call 17
      local.get 1
      local.get 2
      i32.const 4
      i32.add
      local.get 5
      i32.const 3
      i32.const -1
      i32.xor
      i32.and
      i32.add
      local.tee 2
      call 14
      block (result i32) ;; label = @2
        local.get 1
        local.set 6
        local.get 6
        i32.const 4
        i32.add
        local.get 6
        i32.load
        i32.const 3
        i32.const -1
        i32.xor
        i32.and
        i32.add
      end
      local.set 4
      local.get 4
      i32.load
      local.set 5
    end
    local.get 2
    i32.const 2
    i32.and
    if ;; label = @1
      block (result i32) ;; label = @2
        local.get 1
        local.set 7
        local.get 7
        i32.const 4
        i32.sub
        i32.load
      end
      local.set 8
      local.get 8
      i32.load
      local.set 9
      i32.const 1
      drop
      local.get 9
      i32.const 1
      i32.and
      i32.eqz
      if ;; label = @2
        i32.const 0
        i32.const 144
        i32.const 221
        i32.const 16
        call 13
        unreachable
      end
      local.get 0
      local.get 8
      call 17
      local.get 8
      local.set 1
      local.get 1
      local.get 9
      i32.const 4
      i32.add
      local.get 2
      i32.const 3
      i32.const -1
      i32.xor
      i32.and
      i32.add
      local.tee 2
      call 14
    end
    local.get 4
    local.get 5
    i32.const 2
    i32.or
    call 14
    local.get 2
    i32.const 3
    i32.const -1
    i32.xor
    i32.and
    local.set 10
    i32.const 1
    drop
    local.get 10
    i32.const 12
    i32.ge_u
    i32.eqz
    if ;; label = @1
      i32.const 0
      i32.const 144
      i32.const 233
      i32.const 14
      call 13
      unreachable
    end
    i32.const 1
    drop
    local.get 1
    i32.const 4
    i32.add
    local.get 10
    i32.add
    local.get 4
    i32.eq
    i32.eqz
    if ;; label = @1
      i32.const 0
      i32.const 144
      i32.const 234
      i32.const 14
      call 13
      unreachable
    end
    local.get 4
    i32.const 4
    i32.sub
    local.get 1
    i32.store
    local.get 10
    i32.const 256
    i32.lt_u
    if ;; label = @1
      i32.const 0
      local.set 11
      local.get 10
      i32.const 4
      i32.shr_u
      local.set 12
    else
      local.get 10
      local.tee 13
      i32.const 1073741820
      local.tee 14
      local.get 13
      local.get 14
      i32.lt_u
      select
      local.set 15
      i32.const 31
      local.get 15
      i32.clz
      i32.sub
      local.set 11
      local.get 15
      local.get 11
      i32.const 4
      i32.sub
      i32.shr_u
      i32.const 1
      i32.const 4
      i32.shl
      i32.xor
      local.set 12
      local.get 11
      i32.const 8
      i32.const 1
      i32.sub
      i32.sub
      local.set 11
    end
    i32.const 1
    drop
    local.get 11
    i32.const 23
    i32.lt_u
    if (result i32) ;; label = @1
      local.get 12
      i32.const 16
      i32.lt_u
    else
      i32.const 0
    end
    i32.eqz
    if ;; label = @1
      i32.const 0
      i32.const 144
      i32.const 251
      i32.const 14
      call 13
      unreachable
    end
    block (result i32) ;; label = @1
      local.get 0
      local.set 16
      local.get 11
      local.set 17
      local.get 12
      local.set 18
      local.get 16
      local.get 17
      i32.const 4
      i32.shl
      local.get 18
      i32.add
      i32.const 2
      i32.shl
      i32.add
      i32.load offset=96
    end
    local.set 19
    local.get 1
    i32.const 0
    call 15
    local.get 1
    local.get 19
    call 16
    local.get 19
    if ;; label = @1
      local.get 19
      local.get 1
      call 15
    end
    block ;; label = @1
      local.get 0
      local.set 20
      local.get 11
      local.set 21
      local.get 12
      local.set 22
      local.get 1
      local.set 23
      local.get 20
      local.get 21
      i32.const 4
      i32.shl
      local.get 22
      i32.add
      i32.const 2
      i32.shl
      i32.add
      local.get 23
      i32.store offset=96
    end
    local.get 0
    local.get 0
    i32.load
    i32.const 1
    local.get 11
    i32.shl
    i32.or
    call 3
    block ;; label = @1
      local.get 0
      local.set 26
      local.get 11
      local.set 27
      block (result i32) ;; label = @2
        local.get 0
        local.set 24
        local.get 11
        local.set 25
        local.get 24
        local.get 25
        i32.const 2
        i32.shl
        i32.add
        i32.load offset=4
      end
      i32.const 1
      local.get 12
      i32.shl
      i32.or
      local.set 28
      local.get 26
      local.get 27
      i32.const 2
      i32.shl
      i32.add
      local.get 28
      i32.store offset=4
    end
  )
  (func (;19;) (type 8) (param i32 i32 i32) (result i32)
    (local i32 i32 i32 i32 i32 i32 i32 i32)
    i32.const 1
    drop
    local.get 1
    local.get 2
    i32.le_u
    i32.eqz
    if ;; label = @1
      i32.const 0
      i32.const 144
      i32.const 377
      i32.const 14
      call 13
      unreachable
    end
    local.get 1
    i32.const 4
    i32.add
    i32.const 15
    i32.add
    i32.const 15
    i32.const -1
    i32.xor
    i32.and
    i32.const 4
    i32.sub
    local.set 1
    local.get 2
    i32.const 15
    i32.const -1
    i32.xor
    i32.and
    local.set 2
    block (result i32) ;; label = @1
      local.get 0
      local.set 3
      local.get 3
      i32.load offset=1568
    end
    local.set 4
    i32.const 0
    local.set 5
    local.get 4
    if ;; label = @1
      i32.const 1
      drop
      local.get 1
      local.get 4
      i32.const 4
      i32.add
      i32.ge_u
      i32.eqz
      if ;; label = @2
        i32.const 0
        i32.const 144
        i32.const 384
        i32.const 16
        call 13
        unreachable
      end
      local.get 1
      i32.const 16
      i32.sub
      local.get 4
      i32.eq
      if ;; label = @2
        local.get 1
        i32.const 16
        i32.sub
        local.set 1
        local.get 4
        i32.load
        local.set 5
      else
        nop
      end
    else
      i32.const 1
      drop
      local.get 1
      local.get 0
      i32.const 1572
      i32.add
      i32.ge_u
      i32.eqz
      if ;; label = @2
        i32.const 0
        i32.const 144
        i32.const 397
        i32.const 5
        call 13
        unreachable
      end
    end
    local.get 2
    local.get 1
    i32.sub
    local.set 6
    local.get 6
    i32.const 4
    i32.const 12
    i32.add
    i32.const 4
    i32.add
    i32.lt_u
    if ;; label = @1
      i32.const 0
      return
    end
    local.get 6
    i32.const 2
    i32.const 4
    i32.mul
    i32.sub
    local.set 7
    local.get 1
    local.set 8
    local.get 8
    local.get 7
    i32.const 1
    i32.or
    local.get 5
    i32.const 2
    i32.and
    i32.or
    call 14
    local.get 8
    i32.const 0
    call 15
    local.get 8
    i32.const 0
    call 16
    local.get 1
    i32.const 4
    i32.add
    local.get 7
    i32.add
    local.set 4
    local.get 4
    i32.const 0
    i32.const 2
    i32.or
    call 14
    block ;; label = @1
      local.get 0
      local.set 9
      local.get 4
      local.set 10
      local.get 9
      local.get 10
      i32.store offset=1568
    end
    local.get 0
    local.get 8
    call 18
    i32.const 1
  )
  (func (;20;) (type 4)
    (local i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32)
    i32.const 0
    drop
    global.get 11
    i32.const 15
    i32.add
    i32.const 15
    i32.const -1
    i32.xor
    i32.and
    local.set 0
    memory.size
    local.set 1
    local.get 0
    i32.const 1572
    i32.add
    i32.const 65535
    i32.add
    i32.const 65535
    i32.const -1
    i32.xor
    i32.and
    i32.const 16
    i32.shr_u
    local.set 2
    local.get 2
    local.get 1
    i32.gt_s
    if (result i32) ;; label = @1
      local.get 2
      local.get 1
      i32.sub
      memory.grow
      i32.const 0
      i32.lt_s
    else
      i32.const 0
    end
    if ;; label = @1
      unreachable
    end
    local.get 0
    local.set 3
    local.get 3
    i32.const 0
    call 3
    block ;; label = @1
      local.get 3
      local.set 4
      i32.const 0
      local.set 5
      local.get 4
      local.get 5
      i32.store offset=1568
    end
    i32.const 0
    local.set 6
    block ;; label = @1
      loop ;; label = @2
        local.get 6
        i32.const 23
        i32.lt_u
        local.set 7
        local.get 7
        if ;; label = @3
          block ;; label = @4
            block ;; label = @5
              local.get 3
              local.set 8
              local.get 6
              local.set 9
              i32.const 0
              local.set 10
              local.get 8
              local.get 9
              i32.const 2
              i32.shl
              i32.add
              local.get 10
              i32.store offset=4
            end
            i32.const 0
            local.set 11
            block ;; label = @5
              loop ;; label = @6
                local.get 11
                i32.const 16
                i32.lt_u
                local.set 12
                local.get 12
                if ;; label = @7
                  block ;; label = @8
                    block ;; label = @9
                      local.get 3
                      local.set 13
                      local.get 6
                      local.set 14
                      local.get 11
                      local.set 15
                      i32.const 0
                      local.set 16
                      local.get 13
                      local.get 14
                      i32.const 4
                      i32.shl
                      local.get 15
                      i32.add
                      i32.const 2
                      i32.shl
                      i32.add
                      local.get 16
                      i32.store offset=96
                    end
                  end
                  local.get 11
                  i32.const 1
                  i32.add
                  local.set 11
                  br 1 (;@6;)
                end
              end
            end
          end
          local.get 6
          i32.const 1
          i32.add
          local.set 6
          br 1 (;@2;)
        end
      end
    end
    local.get 0
    i32.const 1572
    i32.add
    local.set 17
    i32.const 0
    drop
    local.get 3
    local.get 17
    memory.size
    i32.const 16
    i32.shl
    call 19
    drop
    local.get 3
    global.set 1
  )
  (func (;21;) (type 1) (param i32) (result i32)
    local.get 0
    i32.const 12
    i32.le_u
    if (result i32) ;; label = @1
      i32.const 12
    else
      local.get 0
      i32.const 4
      i32.add
      i32.const 15
      i32.add
      i32.const 15
      i32.const -1
      i32.xor
      i32.and
      i32.const 4
      i32.sub
    end
  )
  (func (;22;) (type 1) (param i32) (result i32)
    local.get 0
    i32.const 1073741820
    i32.gt_u
    if ;; label = @1
      i32.const 208
      i32.const 144
      i32.const 458
      i32.const 29
      call 13
      unreachable
    end
    local.get 0
    call 21
  )
  (func (;23;) (type 2) (param i32 i32) (result i32)
    (local i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32)
    local.get 1
    i32.const 256
    i32.lt_u
    if ;; label = @1
      i32.const 0
      local.set 2
      local.get 1
      i32.const 4
      i32.shr_u
      local.set 3
    else
      local.get 1
      i32.const 536870910
      i32.lt_u
      if (result i32) ;; label = @2
        local.get 1
        i32.const 1
        i32.const 27
        local.get 1
        i32.clz
        i32.sub
        i32.shl
        i32.add
        i32.const 1
        i32.sub
      else
        local.get 1
      end
      local.set 4
      i32.const 31
      local.get 4
      i32.clz
      i32.sub
      local.set 2
      local.get 4
      local.get 2
      i32.const 4
      i32.sub
      i32.shr_u
      i32.const 1
      i32.const 4
      i32.shl
      i32.xor
      local.set 3
      local.get 2
      i32.const 8
      i32.const 1
      i32.sub
      i32.sub
      local.set 2
    end
    i32.const 1
    drop
    local.get 2
    i32.const 23
    i32.lt_u
    if (result i32) ;; label = @1
      local.get 3
      i32.const 16
      i32.lt_u
    else
      i32.const 0
    end
    i32.eqz
    if ;; label = @1
      i32.const 0
      i32.const 144
      i32.const 330
      i32.const 14
      call 13
      unreachable
    end
    block (result i32) ;; label = @1
      local.get 0
      local.set 5
      local.get 2
      local.set 6
      local.get 5
      local.get 6
      i32.const 2
      i32.shl
      i32.add
      i32.load offset=4
    end
    i32.const 0
    i32.const -1
    i32.xor
    local.get 3
    i32.shl
    i32.and
    local.set 7
    i32.const 0
    local.set 8
    local.get 7
    i32.eqz
    if ;; label = @1
      local.get 0
      i32.load
      i32.const 0
      i32.const -1
      i32.xor
      local.get 2
      i32.const 1
      i32.add
      i32.shl
      i32.and
      local.set 9
      local.get 9
      i32.eqz
      if ;; label = @2
        i32.const 0
        local.set 8
      else
        local.get 9
        i32.ctz
        local.set 2
        block (result i32) ;; label = @3
          local.get 0
          local.set 10
          local.get 2
          local.set 11
          local.get 10
          local.get 11
          i32.const 2
          i32.shl
          i32.add
          i32.load offset=4
        end
        local.set 7
        i32.const 1
        drop
        local.get 7
        i32.eqz
        if ;; label = @3
          i32.const 0
          i32.const 144
          i32.const 343
          i32.const 18
          call 13
          unreachable
        end
        block (result i32) ;; label = @3
          local.get 0
          local.set 12
          local.get 2
          local.set 13
          local.get 7
          i32.ctz
          local.set 14
          local.get 12
          local.get 13
          i32.const 4
          i32.shl
          local.get 14
          i32.add
          i32.const 2
          i32.shl
          i32.add
          i32.load offset=96
        end
        local.set 8
      end
    else
      block (result i32) ;; label = @2
        local.get 0
        local.set 15
        local.get 2
        local.set 16
        local.get 7
        i32.ctz
        local.set 17
        local.get 15
        local.get 16
        i32.const 4
        i32.shl
        local.get 17
        i32.add
        i32.const 2
        i32.shl
        i32.add
        i32.load offset=96
      end
      local.set 8
    end
    local.get 8
  )
  (func (;24;) (type 0) (param i32 i32)
    (local i32 i32 i32 i32 i32 i32 i32)
    i32.const 0
    drop
    local.get 1
    i32.const 536870910
    i32.lt_u
    if ;; label = @1
      local.get 1
      i32.const 1
      i32.const 27
      local.get 1
      i32.clz
      i32.sub
      i32.shl
      i32.const 1
      i32.sub
      i32.add
      local.set 1
    end
    memory.size
    local.set 2
    local.get 1
    i32.const 4
    local.get 2
    i32.const 16
    i32.shl
    i32.const 4
    i32.sub
    block (result i32) ;; label = @1
      local.get 0
      local.set 3
      local.get 3
      i32.load offset=1568
    end
    i32.ne
    i32.shl
    i32.add
    local.set 1
    local.get 1
    i32.const 65535
    i32.add
    i32.const 65535
    i32.const -1
    i32.xor
    i32.and
    i32.const 16
    i32.shr_u
    local.set 4
    local.get 2
    local.tee 5
    local.get 4
    local.tee 6
    local.get 5
    local.get 6
    i32.gt_s
    select
    local.set 7
    local.get 7
    memory.grow
    i32.const 0
    i32.lt_s
    if ;; label = @1
      local.get 4
      memory.grow
      i32.const 0
      i32.lt_s
      if ;; label = @2
        unreachable
      end
    end
    memory.size
    local.set 8
    local.get 0
    local.get 2
    i32.const 16
    i32.shl
    local.get 8
    i32.const 16
    i32.shl
    call 19
    drop
  )
  (func (;25;) (type 9) (param i32 i32 i32)
    (local i32 i32 i32 i32 i32)
    local.get 1
    i32.load
    local.set 3
    i32.const 1
    drop
    local.get 2
    i32.const 4
    i32.add
    i32.const 15
    i32.and
    i32.eqz
    i32.eqz
    if ;; label = @1
      i32.const 0
      i32.const 144
      i32.const 357
      i32.const 14
      call 13
      unreachable
    end
    local.get 3
    i32.const 3
    i32.const -1
    i32.xor
    i32.and
    local.get 2
    i32.sub
    local.set 4
    local.get 4
    i32.const 4
    i32.const 12
    i32.add
    i32.ge_u
    if ;; label = @1
      local.get 1
      local.get 2
      local.get 3
      i32.const 2
      i32.and
      i32.or
      call 14
      local.get 1
      i32.const 4
      i32.add
      local.get 2
      i32.add
      local.set 5
      local.get 5
      local.get 4
      i32.const 4
      i32.sub
      i32.const 1
      i32.or
      call 14
      local.get 0
      local.get 5
      call 18
    else
      local.get 1
      local.get 3
      i32.const 1
      i32.const -1
      i32.xor
      i32.and
      call 14
      block (result i32) ;; label = @2
        local.get 1
        local.set 7
        local.get 7
        i32.const 4
        i32.add
        local.get 7
        i32.load
        i32.const 3
        i32.const -1
        i32.xor
        i32.and
        i32.add
      end
      block (result i32) ;; label = @2
        local.get 1
        local.set 6
        local.get 6
        i32.const 4
        i32.add
        local.get 6
        i32.load
        i32.const 3
        i32.const -1
        i32.xor
        i32.and
        i32.add
      end
      i32.load
      i32.const 2
      i32.const -1
      i32.xor
      i32.and
      call 14
    end
  )
  (func (;26;) (type 2) (param i32 i32) (result i32)
    (local i32 i32)
    local.get 1
    call 22
    local.set 2
    local.get 0
    local.get 2
    call 23
    local.set 3
    local.get 3
    i32.eqz
    if ;; label = @1
      local.get 0
      local.get 2
      call 24
      local.get 0
      local.get 2
      call 23
      local.set 3
      i32.const 1
      drop
      local.get 3
      i32.eqz
      if ;; label = @2
        i32.const 0
        i32.const 144
        i32.const 496
        i32.const 16
        call 13
        unreachable
      end
    end
    i32.const 1
    drop
    local.get 3
    i32.load
    i32.const 3
    i32.const -1
    i32.xor
    i32.and
    local.get 2
    i32.ge_u
    i32.eqz
    if ;; label = @1
      i32.const 0
      i32.const 144
      i32.const 498
      i32.const 14
      call 13
      unreachable
    end
    local.get 0
    local.get 3
    call 17
    local.get 0
    local.get 3
    local.get 2
    call 25
    i32.const 0
    drop
    local.get 3
  )
  (func (;27;) (type 1) (param i32) (result i32)
    global.get 1
    i32.eqz
    if ;; label = @1
      call 20
    end
    global.get 1
    local.get 0
    call 26
    i32.const 4
    i32.add
  )
  (func (;28;) (type 1) (param i32) (result i32)
    (local i32)
    local.get 0
    i32.const 4
    i32.sub
    local.set 1
    local.get 0
    i32.const 0
    i32.ne
    if (result i32) ;; label = @1
      local.get 0
      i32.const 15
      i32.and
      i32.eqz
    else
      i32.const 0
    end
    if (result i32) ;; label = @1
      local.get 1
      i32.load
      i32.const 1
      i32.and
      i32.eqz
    else
      i32.const 0
    end
    i32.eqz
    if ;; label = @1
      i32.const 0
      i32.const 144
      i32.const 559
      i32.const 3
      call 13
      unreachable
    end
    local.get 1
  )
  (func (;29;) (type 0) (param i32 i32)
    i32.const 0
    drop
    local.get 1
    local.get 1
    i32.load
    i32.const 1
    i32.or
    call 14
    local.get 0
    local.get 1
    call 18
  )
  (func (;30;) (type 5) (param i32)
    local.get 0
    global.get 11
    i32.lt_u
    if ;; label = @1
      return
    end
    global.get 1
    i32.eqz
    if ;; label = @1
      call 20
    end
    global.get 1
    local.get 0
    call 28
    call 29
  )
  (func (;31;) (type 4)
    global.get 0
    call 27
    global.set 8
    i32.const 0
    global.get 8
    global.get 0
    call 2
    i32.const 65535
    i32.and
    i32.eq
    i32.eqz
    if ;; label = @1
      i32.const 0
      i32.const 272
      i32.const 6
      i32.const 1
      call 13
      unreachable
    end
    global.get 8
    call 30
  )
  (func (;32;) (type 4)
    global.get 12
    if ;; label = @1
      return
    end
    i32.const 1
    global.set 12
    call 31
  )
  (table (;0;) 1 1 funcref)
  (memory (;0;) 1)
  (global (;0;) i32 i32.const 0)
  (global (;1;) (mut i32) i32.const 0)
  (global (;2;) i32 i32.const 0)
  (global (;3;) i32 i32.const 0)
  (global (;4;) i32 i32.const 1)
  (global (;5;) i32 i32.const 2)
  (global (;6;) i32 i32.const 0)
  (global (;7;) (mut i32) i32.const 0)
  (global (;8;) (mut i32) i32.const 0)
  (global (;9;) i32 i32.const 348)
  (global (;10;) (mut i32) i32.const 33116)
  (global (;11;) i32 i32.const 33116)
  (global (;12;) (mut i32) i32.const 0)
  (export "memory" (memory 0))
  (export "_start" (func 32))
  (elem (;0;) (i32.const 1) func)
  (data (;0;) (i32.const 12) "<\00\00\00\00\00\00\00\00\00\00\00\01\00\00\00$\00\00\00U\00n\00p\00a\00i\00r\00e\00d\00 \00s\00u\00r\00r\00o\00g\00a\00t\00e\00\00\00\00\00\00\00\00\00")
  (data (;1;) (i32.const 76) ",\00\00\00\00\00\00\00\00\00\00\00\01\00\00\00\1c\00\00\00~\00l\00i\00b\00/\00s\00t\00r\00i\00n\00g\00.\00t\00s\00")
  (data (;2;) (i32.const 124) "<\00\00\00\00\00\00\00\00\00\00\00\01\00\00\00\1e\00\00\00~\00l\00i\00b\00/\00r\00t\00/\00t\00l\00s\00f\00.\00t\00s\00\00\00\00\00\00\00\00\00\00\00\00\00\00\00")
  (data (;3;) (i32.const 188) "<\00\00\00\00\00\00\00\00\00\00\00\01\00\00\00(\00\00\00A\00l\00l\00o\00c\00a\00t\00i\00o\00n\00 \00t\00o\00o\00 \00l\00a\00r\00g\00e\00\00\00\00\00")
  (data (;4;) (i32.const 252) "\5c\00\00\00\00\00\00\00\00\00\00\00\01\00\00\00F\00\00\00t\00e\00s\00t\00s\00u\00i\00t\00e\00/\00r\00a\00n\00d\00o\00m\00_\00g\00e\00t\00-\00z\00e\00r\00o\00-\00l\00e\00n\00g\00t\00h\00.\00t\00s\00\00\00\00\00\00\00")
)
