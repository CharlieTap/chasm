(module
  (type (;0;) (func (param i32 i32) (result i32)))
  (type (;1;) (func))
  (type (;2;) (func (param i32 i32)))
  (type (;3;) (func (param i32 i32 i32)))
  (type (;4;) (func (param i32 i32 i32 i32)))
  (type (;5;) (func (param i32 i32 i32) (result i32)))
  (type (;6;) (func (param i32 i32 i32 i32 i32) (result i32)))
  (type (;7;) (func (param i32 i32 i32 i32 i32 i32) (result i32)))
  (type (;8;) (func (result i64)))
  (type (;9;) (func (result f32)))
  (import "env" "clock_ms" (func (;0;) (type 8)))
  (func (;1;) (type 0) (param i32 i32) (result i32)
    (local i32 i32 i32 i32)
    local.get 0
    i32.load16_u
    local.tee 3
    i32.const 128
    i32.and
    if ;; label = @1
      local.get 3
      i32.const 127
      i32.and
      return
    end
    local.get 3
    i32.const 3
    i32.shr_u
    i32.const 15
    i32.and
    local.tee 2
    i32.const 4
    i32.shl
    local.get 2
    i32.or
    local.set 4
    block ;; label = @1
      block ;; label = @2
        block ;; label = @3
          local.get 3
          local.tee 2
          i32.const 7
          i32.and
          br_table 0 (;@3;) 1 (;@2;) 2 (;@1;)
        end
        local.get 1
        i32.load offset=24
        local.get 1
        i32.const 20
        i32.add
        i32.load
        local.get 1
        i32.load16_s
        local.get 1
        i32.load16_s offset=2
        local.get 4
        i32.const 34
        local.get 4
        i32.const 34
        i32.gt_u
        select
        local.get 1
        i32.load16_u offset=56
        call 7
        local.set 2
        local.get 1
        i32.load16_u offset=62
        br_if 1 (;@1;)
        local.get 1
        local.get 2
        i32.store16 offset=62
        br 1 (;@1;)
      end
      local.get 1
      i32.load16_u offset=56
      local.set 2
      local.get 1
      i32.const 40
      i32.add
      local.tee 5
      i32.load
      local.get 5
      i32.load offset=12
      local.get 5
      i32.load offset=4
      local.get 5
      i32.load offset=8
      local.get 4
      call 5
      local.get 2
      call 12
      local.set 2
      local.get 1
      i32.load16_u offset=60
      br_if 0 (;@1;)
      local.get 1
      local.get 2
      i32.store16 offset=60
    end
    local.get 1
    local.get 2
    local.get 1
    i32.load16_u offset=56
    call 10
    i32.store16 offset=56
    local.get 0
    local.get 2
    i32.const 127
    i32.and
    local.tee 0
    local.get 3
    i32.const 65280
    i32.and
    i32.or
    i32.const 128
    i32.or
    i32.store16
    local.get 0
  )
  (func (;2;) (type 0) (param i32 i32) (result i32)
    (local i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32)
    local.get 0
    i32.load offset=36
    local.set 4
    block (result i32) ;; label = @1
      local.get 0
      i32.load16_s offset=4
      local.tee 11
      i32.const 1
      i32.lt_s
      if ;; label = @2
        local.get 1
        local.set 9
        i32.const 0
        br 1 (;@1;)
      end
      local.get 1
      local.set 9
      loop ;; label = @2
        local.get 4
        local.set 3
        block ;; label = @3
          block ;; label = @4
            local.get 9
            i32.const 16
            i32.shl
            i32.const 16
            i32.shr_s
            i32.const -1
            i32.gt_s
            if ;; label = @5
              loop ;; label = @6
                local.get 3
                i32.load offset=4
                i32.load16_u offset=2
                local.get 9
                i32.const 65535
                i32.and
                i32.eq
                br_if 3 (;@3;)
                local.get 3
                i32.load
                local.tee 3
                br_if 0 (;@6;)
              end
              br 1 (;@4;)
            end
            loop ;; label = @5
              local.get 3
              i32.load offset=4
              i32.load8_u
              local.get 6
              i32.const 255
              i32.and
              i32.xor
              i32.eqz
              br_if 2 (;@3;)
              local.get 3
              i32.load
              local.tee 3
              br_if 0 (;@5;)
            end
          end
          i32.const 0
          local.set 3
        end
        block ;; label = @3
          local.get 4
          i32.eqz
          if ;; label = @4
            i32.const 0
            local.set 4
            br 1 (;@3;)
          end
          i32.const 0
          local.set 5
          local.get 4
          local.set 2
          loop ;; label = @4
            local.get 2
            local.tee 4
            i32.load
            local.set 2
            local.get 4
            local.get 5
            i32.store
            local.get 4
            local.set 5
            local.get 2
            br_if 0 (;@4;)
          end
        end
        block ;; label = @3
          local.get 3
          i32.eqz
          if ;; label = @4
            local.get 14
            i32.const 1
            i32.add
            local.set 14
            local.get 4
            i32.load
            i32.load offset=4
            i32.load8_u offset=1
            i32.const 1
            i32.and
            local.get 13
            i32.add
            local.set 13
            br 1 (;@3;)
          end
          local.get 15
          i32.const 1
          i32.add
          local.set 15
          local.get 3
          i32.load offset=4
          i32.load16_u
          local.tee 2
          i32.const 9
          i32.shr_u
          local.get 2
          i32.and
          i32.const 1
          i32.and
          local.get 13
          i32.add
          local.set 13
          local.get 3
          i32.load
          local.tee 2
          i32.eqz
          br_if 0 (;@3;)
          local.get 3
          local.get 2
          i32.load
          i32.store
          local.get 2
          local.get 4
          i32.load
          i32.store
          local.get 4
          local.get 2
          i32.store
        end
        local.get 9
        i32.const -1
        i32.xor
        i32.const 32768
        i32.and
        i32.const 15
        i32.shr_u
        local.get 9
        i32.add
        local.set 9
        local.get 6
        i32.const 16
        i32.shl
        i32.const 65536
        i32.add
        i32.const 16
        i32.shr_s
        local.tee 6
        local.get 11
        i32.lt_s
        br_if 0 (;@2;)
      end
      local.get 11
      i32.const -1
      i32.add
      i32.const 255
      i32.and
    end
    local.set 16
    block ;; label = @1
      block ;; label = @2
        local.get 1
        i32.const 1
        i32.lt_s
        br_if 0 (;@2;)
        i32.const 1
        local.set 10
        loop ;; label = @3
          local.get 10
          i32.const 1
          i32.lt_s
          br_if 2 (;@1;)
          i32.const 0
          local.set 12
          i32.const 0
          local.set 6
          local.get 4
          local.set 2
          i32.const 0
          local.set 4
          loop ;; label = @4
            local.get 12
            local.tee 17
            i32.const 1
            i32.add
            local.set 12
            i32.const 0
            local.set 3
            local.get 2
            local.set 5
            block (result i32) ;; label = @5
              block ;; label = @6
                loop ;; label = @7
                  local.get 5
                  i32.load
                  local.tee 5
                  i32.eqz
                  br_if 1 (;@6;)
                  local.get 10
                  local.get 3
                  i32.const 1
                  i32.add
                  local.tee 3
                  i32.ne
                  br_if 0 (;@7;)
                end
                local.get 10
                br 1 (;@5;)
              end
              local.get 3
              i32.const 1
              i32.add
            end
            local.set 8
            local.get 2
            local.set 3
            local.get 5
            local.set 2
            local.get 10
            local.set 7
            loop ;; label = @5
              block ;; label = @6
                block ;; label = @7
                  block (result i32) ;; label = @8
                    block ;; label = @9
                      block (result i32) ;; label = @10
                        block ;; label = @11
                          local.get 8
                          i32.const 0
                          i32.gt_s
                          br_if 0 (;@11;)
                          local.get 2
                          i32.eqz
                          br_if 4 (;@7;)
                          local.get 7
                          i32.const 1
                          i32.lt_s
                          br_if 4 (;@7;)
                          local.get 8
                          br_if 0 (;@11;)
                          local.get 7
                          i32.const -1
                          i32.add
                          local.set 7
                          i32.const 0
                          local.set 8
                          local.get 2
                          i32.load
                          br 1 (;@10;)
                        end
                        local.get 2
                        i32.eqz
                        br_if 1 (;@9;)
                        local.get 7
                        i32.eqz
                        br_if 1 (;@9;)
                        local.get 2
                        i32.load offset=4
                        local.set 1
                        local.get 3
                        i32.load offset=4
                        local.get 0
                        call 1
                        i32.const 65535
                        i32.and
                        local.get 1
                        local.get 0
                        call 1
                        i32.const 65535
                        i32.and
                        i32.le_u
                        br_if 1 (;@9;)
                        local.get 7
                        i32.const -1
                        i32.add
                        local.set 7
                        local.get 2
                        i32.load
                      end
                      local.set 1
                      local.get 3
                      local.set 11
                      local.get 2
                      br 1 (;@8;)
                    end
                    local.get 8
                    i32.const -1
                    i32.add
                    local.set 8
                    local.get 3
                    i32.load
                    local.set 11
                    local.get 2
                    local.set 1
                    local.get 3
                  end
                  local.set 5
                  local.get 6
                  i32.eqz
                  if ;; label = @8
                    local.get 5
                    local.set 4
                    br 2 (;@6;)
                  end
                  local.get 6
                  local.get 5
                  i32.store
                  br 1 (;@6;)
                end
                local.get 2
                br_if 2 (;@4;)
                local.get 6
                i32.const 0
                i32.store
                local.get 10
                i32.const 1
                i32.shl
                local.set 10
                local.get 17
                br_if 3 (;@3;)
                br 4 (;@2;)
              end
              local.get 11
              local.set 3
              local.get 1
              local.set 2
              local.get 5
              local.set 6
              br 0 (;@5;)
            end
            unreachable
          end
          unreachable
        end
        unreachable
      end
      local.get 15
      i32.const 2
      i32.shl
      local.get 14
      i32.sub
      local.get 13
      i32.add
      local.set 12
      local.get 4
      i32.load
      local.tee 1
      i32.load offset=4
      local.set 6
      local.get 1
      local.get 1
      i32.load
      local.tee 0
      i32.load offset=4
      i32.store offset=4
      local.get 1
      local.get 0
      i32.load
      i32.store
      local.get 0
      local.get 6
      i32.store offset=4
      local.get 0
      i32.const 0
      i32.store
      block ;; label = @2
        block ;; label = @3
          block ;; label = @4
            local.get 9
            i32.const 16
            i32.shl
            i32.const 16
            i32.shr_s
            i32.const -1
            i32.le_s
            if ;; label = @5
              local.get 4
              i32.eqz
              br_if 1 (;@4;)
              local.get 4
              local.set 2
              loop ;; label = @6
                local.get 16
                local.get 2
                i32.load offset=4
                i32.load8_u
                i32.eq
                br_if 3 (;@3;)
                local.get 2
                i32.load
                local.tee 2
                br_if 0 (;@6;)
              end
              br 1 (;@4;)
            end
            local.get 4
            i32.eqz
            br_if 0 (;@4;)
            local.get 9
            i32.const 65535
            i32.and
            local.set 1
            local.get 4
            local.set 2
            loop ;; label = @5
              local.get 2
              i32.load offset=4
              i32.load16_u offset=2
              local.get 1
              i32.eq
              br_if 2 (;@3;)
              local.get 2
              i32.load
              local.tee 2
              br_if 0 (;@5;)
            end
          end
          local.get 4
          i32.load
          local.tee 2
          i32.eqz
          br_if 1 (;@2;)
        end
        loop ;; label = @3
          local.get 4
          i32.load offset=4
          i32.load16_s
          local.get 12
          i32.const 65535
          i32.and
          call 12
          local.set 12
          local.get 2
          i32.load
          local.tee 2
          br_if 0 (;@3;)
        end
        local.get 0
        i32.load offset=4
        local.set 6
      end
      local.get 0
      local.get 4
      i32.load
      local.tee 1
      i32.load offset=4
      i32.store offset=4
      local.get 0
      local.get 1
      i32.load
      i32.store
      local.get 1
      local.get 6
      i32.store offset=4
      local.get 1
      local.get 0
      i32.store
      i32.const 1
      local.set 0
      loop ;; label = @2
        local.get 0
        i32.const 1
        i32.ge_s
        if ;; label = @3
          i32.const 0
          local.set 10
          i32.const 0
          local.set 6
          local.get 4
          local.set 2
          i32.const 0
          local.set 4
          loop ;; label = @4
            local.get 10
            local.tee 9
            i32.const 1
            i32.add
            local.set 10
            i32.const 0
            local.set 3
            local.get 2
            local.set 5
            block (result i32) ;; label = @5
              block ;; label = @6
                loop ;; label = @7
                  local.get 5
                  i32.load
                  local.tee 5
                  i32.eqz
                  br_if 1 (;@6;)
                  local.get 0
                  local.get 3
                  i32.const 1
                  i32.add
                  local.tee 3
                  i32.ne
                  br_if 0 (;@7;)
                end
                local.get 0
                br 1 (;@5;)
              end
              local.get 3
              i32.const 1
              i32.add
            end
            local.set 8
            local.get 2
            local.set 3
            local.get 5
            local.set 2
            local.get 0
            local.set 7
            loop ;; label = @5
              block ;; label = @6
                block ;; label = @7
                  block (result i32) ;; label = @8
                    block ;; label = @9
                      block (result i32) ;; label = @10
                        block ;; label = @11
                          local.get 8
                          i32.const 0
                          i32.gt_s
                          br_if 0 (;@11;)
                          local.get 2
                          i32.eqz
                          br_if 4 (;@7;)
                          local.get 7
                          i32.const 1
                          i32.lt_s
                          br_if 4 (;@7;)
                          local.get 8
                          br_if 0 (;@11;)
                          local.get 7
                          i32.const -1
                          i32.add
                          local.set 7
                          i32.const 0
                          local.set 8
                          local.get 2
                          i32.load
                          br 1 (;@10;)
                        end
                        local.get 2
                        i32.eqz
                        br_if 1 (;@9;)
                        local.get 7
                        i32.eqz
                        br_if 1 (;@9;)
                        local.get 3
                        i32.load offset=4
                        local.tee 1
                        local.get 1
                        i32.load8_u offset=1
                        i32.store8
                        local.get 2
                        i32.load offset=4
                        local.tee 5
                        local.get 5
                        i32.load8_u offset=1
                        i32.store8
                        local.get 1
                        i32.load16_s offset=2
                        local.get 5
                        i32.load16_s offset=2
                        i32.le_s
                        br_if 1 (;@9;)
                        local.get 7
                        i32.const -1
                        i32.add
                        local.set 7
                        local.get 2
                        i32.load
                      end
                      local.set 11
                      local.get 2
                      local.set 5
                      local.get 3
                      br 1 (;@8;)
                    end
                    local.get 8
                    i32.const -1
                    i32.add
                    local.set 8
                    local.get 2
                    local.set 11
                    local.get 3
                    local.set 5
                    local.get 3
                    i32.load
                  end
                  local.set 1
                  local.get 6
                  i32.eqz
                  if ;; label = @8
                    local.get 5
                    local.set 4
                    br 2 (;@6;)
                  end
                  local.get 6
                  local.get 5
                  i32.store
                  br 1 (;@6;)
                end
                local.get 2
                br_if 2 (;@4;)
                local.get 6
                i32.const 0
                i32.store
                local.get 0
                i32.const 1
                i32.shl
                local.set 0
                local.get 9
                br_if 4 (;@2;)
                local.get 4
                i32.load
                local.tee 2
                if ;; label = @7
                  loop ;; label = @8
                    local.get 4
                    i32.load offset=4
                    i32.load16_s
                    local.get 12
                    i32.const 65535
                    i32.and
                    call 12
                    local.set 12
                    local.get 2
                    i32.load
                    local.tee 2
                    br_if 0 (;@8;)
                  end
                end
                local.get 12
                i32.const 65535
                i32.and
                return
              end
              local.get 1
              local.set 3
              local.get 11
              local.set 2
              local.get 5
              local.set 6
              br 0 (;@5;)
            end
            unreachable
          end
          unreachable
        end
      end
      loop ;; label = @2
        br 0 (;@2;)
      end
      unreachable
    end
    loop ;; label = @1
      br 0 (;@1;)
    end
    unreachable
  )
  (func (;3;) (type 5) (param i32 i32 i32) (result i32)
    (local i32 i32 i32 i32 i32 i32 i32 i32 i32)
    local.get 1
    i32.const 0
    i32.store
    local.get 1
    local.get 1
    local.get 0
    i32.const 20
    i32.div_u
    i32.const -2
    i32.add
    local.tee 6
    i32.const 3
    i32.shl
    i32.add
    local.tee 7
    i32.store offset=4
    local.get 7
    i32.const 32896
    i32.store align=2
    local.get 7
    local.get 6
    i32.const 2
    i32.shl
    i32.add
    local.set 9
    local.get 1
    i32.const 8
    i32.add
    local.set 3
    local.get 7
    i32.const 4
    i32.add
    local.set 5
    block ;; label = @1
      local.get 0
      i32.const 100
      i32.lt_u
      br_if 0 (;@1;)
      local.get 9
      local.get 5
      i32.const 4
      i32.add
      local.tee 0
      i32.le_u
      if ;; label = @2
        br 1 (;@1;)
      end
      local.get 1
      local.get 3
      i32.store
      local.get 1
      i32.const 0
      i32.store offset=8
      local.get 5
      i32.const 2147483647
      i32.store align=2
      local.get 1
      i32.const 12
      i32.add
      local.get 5
      i32.store
      local.get 3
      local.set 4
      local.get 0
      local.set 5
      local.get 1
      i32.const 16
      i32.add
      local.set 3
    end
    local.get 6
    if ;; label = @1
      i32.const 0
      local.set 0
      loop ;; label = @2
        block ;; label = @3
          local.get 3
          i32.const 8
          i32.add
          local.tee 8
          local.get 7
          i32.ge_u
          br_if 0 (;@3;)
          local.get 5
          i32.const 4
          i32.add
          local.tee 10
          local.get 9
          i32.ge_u
          br_if 0 (;@3;)
          local.get 3
          local.get 4
          i32.store
          local.get 3
          local.get 5
          i32.store offset=4
          local.get 5
          i32.const 32767
          i32.store16 offset=2
          local.get 5
          local.get 0
          local.get 2
          i32.xor
          i32.const 3
          i32.shl
          i32.const 120
          i32.and
          local.get 0
          i32.const 7
          i32.and
          i32.or
          local.tee 4
          i32.const 8
          i32.shl
          local.get 4
          i32.or
          i32.store16
          local.get 1
          local.get 3
          i32.store
          local.get 3
          local.set 4
          local.get 10
          local.set 5
          local.get 8
          local.set 3
        end
        local.get 6
        local.get 0
        i32.const 1
        i32.add
        local.tee 0
        i32.ne
        br_if 0 (;@2;)
      end
    end
    local.get 4
    i32.load
    local.tee 3
    if ;; label = @1
      local.get 6
      i32.const 5
      i32.div_u
      local.set 8
      i32.const 1
      local.set 0
      loop ;; label = @2
        local.get 0
        i32.const 1
        i32.add
        local.set 5
        local.get 4
        i32.load offset=4
        local.get 0
        local.get 8
        i32.ge_u
        if (result i32) ;; label = @3
          local.get 5
          i32.const 8
          i32.shl
          i32.const 1792
          i32.and
          local.get 0
          local.get 2
          i32.xor
          i32.const 16383
          i32.and
          i32.or
        else
          local.get 0
        end
        i32.store16 offset=2
        local.get 5
        local.set 0
        local.get 3
        local.tee 4
        i32.load
        local.tee 3
        br_if 0 (;@2;)
      end
    end
    i32.const 1
    local.set 6
    loop ;; label = @1
      local.get 6
      i32.const 1
      i32.ge_s
      if ;; label = @2
        i32.const 0
        local.set 9
        i32.const 0
        local.set 5
        local.get 1
        local.set 0
        i32.const 0
        local.set 1
        loop ;; label = @3
          local.get 9
          local.tee 11
          i32.const 1
          i32.add
          local.set 9
          i32.const 0
          local.set 3
          local.get 0
          local.set 4
          block (result i32) ;; label = @4
            block ;; label = @5
              loop ;; label = @6
                local.get 4
                i32.load
                local.tee 4
                i32.eqz
                br_if 1 (;@5;)
                local.get 6
                local.get 3
                i32.const 1
                i32.add
                local.tee 3
                i32.ne
                br_if 0 (;@6;)
              end
              local.get 6
              br 1 (;@4;)
            end
            local.get 3
            i32.const 1
            i32.add
          end
          local.set 7
          local.get 0
          local.set 3
          local.get 4
          local.set 0
          local.get 6
          local.set 2
          loop ;; label = @4
            block ;; label = @5
              block ;; label = @6
                block (result i32) ;; label = @7
                  block ;; label = @8
                    block (result i32) ;; label = @9
                      block ;; label = @10
                        local.get 7
                        i32.const 0
                        i32.gt_s
                        br_if 0 (;@10;)
                        local.get 0
                        i32.eqz
                        br_if 4 (;@6;)
                        local.get 2
                        i32.const 1
                        i32.lt_s
                        br_if 4 (;@6;)
                        local.get 7
                        br_if 0 (;@10;)
                        local.get 0
                        i32.load
                        local.set 8
                        i32.const 0
                        local.set 7
                        local.get 2
                        i32.const -1
                        i32.add
                        br 1 (;@9;)
                      end
                      local.get 0
                      i32.eqz
                      br_if 1 (;@8;)
                      local.get 2
                      i32.eqz
                      br_if 1 (;@8;)
                      local.get 3
                      i32.load offset=4
                      local.tee 4
                      local.get 4
                      i32.load8_u offset=1
                      i32.store8
                      local.get 0
                      i32.load offset=4
                      local.tee 8
                      local.get 8
                      i32.load8_u offset=1
                      i32.store8
                      local.get 4
                      i32.load16_s offset=2
                      local.get 8
                      i32.load16_s offset=2
                      i32.le_s
                      br_if 1 (;@8;)
                      local.get 0
                      i32.load
                      local.set 8
                      local.get 2
                      i32.const -1
                      i32.add
                    end
                    local.set 2
                    local.get 3
                    local.set 10
                    local.get 0
                    br 1 (;@7;)
                  end
                  local.get 7
                  i32.const -1
                  i32.add
                  local.set 7
                  local.get 3
                  i32.load
                  local.set 10
                  local.get 0
                  local.set 8
                  local.get 3
                end
                local.set 4
                local.get 5
                i32.eqz
                if ;; label = @7
                  local.get 4
                  local.set 1
                  br 2 (;@5;)
                end
                local.get 5
                local.get 4
                i32.store
                br 1 (;@5;)
              end
              local.get 0
              br_if 2 (;@3;)
              local.get 5
              i32.const 0
              i32.store
              local.get 6
              i32.const 1
              i32.shl
              local.set 6
              local.get 11
              br_if 4 (;@1;)
              local.get 1
              return
            end
            local.get 10
            local.set 3
            local.get 8
            local.set 0
            local.get 4
            local.set 5
            br 0 (;@4;)
          end
          unreachable
        end
        unreachable
      end
    end
    loop ;; label = @1
      br 0 (;@1;)
    end
    unreachable
  )
  (func (;4;) (type 9) (result f32)
    (local i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i64 f32 f64)
    global.get 0
    i32.const 80
    i32.sub
    local.tee 0
    global.set 0
    local.get 0
    i32.const 0
    i32.store offset=76
    local.get 0
    i32.const 66
    i32.add
    local.tee 13
    i32.const 1
    i32.store8
    local.get 0
    i32.const 800
    i32.load
    i32.load
    i32.store16
    local.get 0
    i32.const 804
    i32.load
    i32.load
    i32.store16 offset=2
    local.get 0
    i32.const 808
    i32.load
    i32.load
    i32.store16 offset=4
    local.get 0
    i32.const 812
    i32.load
    i32.load
    i32.store offset=28
    local.get 0
    i32.const 816
    i32.load
    i32.load
    local.tee 1
    i32.const 7
    local.get 1
    select
    local.tee 1
    i32.store offset=32
    block ;; label = @1
      local.get 0
      i32.load16_u offset=4
      local.tee 5
      local.get 0
      i32.load16_u offset=2
      local.tee 4
      local.get 0
      i32.load16_u
      local.tee 2
      i32.or
      i32.or
      if ;; label = @2
        local.get 2
        i32.const 65535
        i32.and
        i32.const 1
        i32.ne
        br_if 1 (;@1;)
        i32.const 13333
        local.set 3
        local.get 4
        local.get 5
        i32.or
        br_if 1 (;@1;)
      end
      local.get 0
      i32.const 102
      i32.store16 offset=4
      local.get 0
      local.get 3
      i32.store16 offset=2
      local.get 0
      local.get 3
      i32.store16
      local.get 3
      local.set 2
    end
    local.get 0
    i32.const 0
    i32.store16 offset=64
    local.get 0
    i32.const 832
    i32.store offset=8
    local.get 0
    i32.const 2000
    local.get 1
    i32.const 1
    i32.and
    local.tee 5
    local.get 1
    i32.const 2
    i32.and
    local.tee 4
    i32.const 1
    i32.shr_u
    i32.add
    local.get 1
    i32.const 4
    i32.and
    local.tee 3
    i32.const 2
    i32.shr_u
    i32.add
    i32.div_u
    local.tee 6
    i32.store offset=24
    local.get 5
    if ;; label = @1
      local.get 0
      i32.const 832
      i32.store offset=12
      i32.const 1
      local.set 7
    end
    local.get 4
    if ;; label = @1
      local.get 0
      i32.const 16
      i32.add
      local.get 6
      local.get 7
      i32.mul
      i32.const 832
      i32.add
      i32.store
      local.get 7
      i32.const 1
      i32.add
      local.set 7
    end
    local.get 3
    if ;; label = @1
      local.get 0
      i32.const 20
      i32.add
      local.get 6
      local.get 7
      i32.mul
      i32.const 832
      i32.add
      i32.store
    end
    local.get 5
    if (result i32) ;; label = @1
      local.get 0
      local.get 6
      local.get 0
      i32.load offset=12
      local.get 2
      i32.const 16
      i32.shl
      i32.const 16
      i32.shr_s
      call 3
      i32.store offset=36
      local.get 0
      i32.load offset=32
      local.tee 1
      i32.const 2
      i32.and
    else
      local.get 4
    end
    if (result i32) ;; label = @1
      local.get 0
      i32.load offset=24
      local.get 0
      i32.load offset=16
      local.get 0
      i32.load16_s
      local.get 0
      i32.load16_u offset=2
      i32.const 16
      i32.shl
      i32.or
      local.get 0
      i32.const 40
      i32.add
      call 6
      local.get 0
      i32.load offset=32
    else
      local.get 1
    end
    i32.const 4
    i32.and
    if ;; label = @1
      local.get 0
      i32.load offset=24
      local.get 0
      i32.load16_s
      local.get 0
      i32.load offset=20
      call 9
    end
    local.get 0
    i32.load offset=28
    i32.eqz
    if ;; label = @1
      local.get 0
      i32.const 1
      i32.store offset=28
      i32.const 1
      local.set 1
      loop ;; label = @2
        local.get 0
        local.get 1
        i32.const 10
        i32.mul
        i32.store offset=28
        call 14
        local.get 0
        i64.const 0
        i64.store offset=56
        block ;; label = @3
          local.get 0
          i32.load offset=28
          local.tee 1
          i32.eqz
          br_if 0 (;@3;)
          local.get 0
          local.get 0
          i32.const 1
          call 2
          local.get 0
          i32.load16_u offset=56
          call 10
          i32.store16 offset=56
          local.get 0
          local.get 0
          i32.const -1
          call 2
          local.get 0
          i32.load16_u offset=56
          call 10
          local.tee 3
          i32.store16 offset=58
          local.get 0
          local.get 3
          i32.store16 offset=56
          local.get 1
          i32.const 1
          i32.eq
          br_if 0 (;@3;)
          local.get 1
          i32.const -1
          i32.add
          local.set 1
          loop ;; label = @4
            local.get 0
            local.get 0
            i32.const 1
            call 2
            local.get 0
            i32.load16_u offset=56
            call 10
            i32.store16 offset=56
            local.get 0
            local.get 0
            i32.const -1
            call 2
            local.get 0
            i32.load16_u offset=56
            call 10
            i32.store16 offset=56
            local.get 1
            i32.const -1
            i32.add
            local.tee 1
            br_if 0 (;@4;)
          end
        end
        call 15
        i32.const 2856
        i64.load
        i32.const 2848
        i64.load
        i64.sub
        f64.convert_i64_u
        f64.const 0x1.f4p+9 (;=1000;)
        f64.div
        local.tee 16
        f64.const 0x1p+0 (;=1;)
        f64.lt
        i32.const 1
        i32.xor
        i32.eqz
        if ;; label = @3
          local.get 0
          i32.load offset=28
          local.set 1
          br 1 (;@2;)
        end
      end
      local.get 0
      local.get 0
      i32.load offset=28
      i32.const 10
      block (result i32) ;; label = @2
        local.get 16
        f64.const 0x1p+32 (;=4294967296;)
        f64.lt
        local.get 16
        f64.const 0x0p+0 (;=0;)
        f64.ge
        i32.and
        if ;; label = @3
          local.get 16
          i32.trunc_f64_u
          br 1 (;@2;)
        end
        i32.const 0
      end
      local.tee 3
      i32.const 1
      local.get 3
      select
      i32.div_u
      i32.const 1
      i32.add
      i32.mul
      i32.store offset=28
    end
    call 14
    local.get 0
    i64.const 0
    i64.store offset=56
    block ;; label = @1
      local.get 0
      i32.load offset=28
      local.tee 1
      i32.eqz
      br_if 0 (;@1;)
      local.get 0
      local.get 0
      i32.const 1
      call 2
      local.get 0
      i32.load16_u offset=56
      call 10
      i32.store16 offset=56
      local.get 0
      local.get 0
      i32.const -1
      call 2
      local.get 0
      i32.load16_u offset=56
      call 10
      local.tee 3
      i32.store16 offset=58
      local.get 0
      local.get 3
      i32.store16 offset=56
      local.get 1
      i32.const 1
      i32.eq
      br_if 0 (;@1;)
      local.get 1
      i32.const -1
      i32.add
      local.set 1
      loop ;; label = @2
        local.get 0
        local.get 0
        i32.const 1
        call 2
        local.get 0
        i32.load16_u offset=56
        call 10
        i32.store16 offset=56
        local.get 0
        local.get 0
        i32.const -1
        call 2
        local.get 0
        i32.load16_u offset=56
        call 10
        i32.store16 offset=56
        local.get 1
        i32.const -1
        i32.add
        local.tee 1
        br_if 0 (;@2;)
      end
    end
    call 15
    i32.const 0
    local.set 2
    i32.const 2856
    i64.load
    i32.const 2848
    i64.load
    i64.sub
    local.set 14
    local.get 0
    i32.load16_s
    i32.const 0
    call 12
    local.set 3
    local.get 0
    i32.load16_s offset=2
    local.get 3
    call 12
    local.set 3
    local.get 0
    i32.load16_s offset=4
    local.get 3
    call 12
    local.set 1
    i32.const 65535
    local.set 3
    block ;; label = @1
      block ;; label = @2
        block ;; label = @3
          local.get 0
          i32.load16_s offset=24
          local.get 1
          call 12
          local.tee 1
          i32.const 31492
          i32.le_s
          if ;; label = @4
            local.get 1
            i32.const 6386
            i32.eq
            br_if 1 (;@3;)
            local.get 1
            i32.const 20143
            i32.ne
            br_if 3 (;@1;)
            i32.const 2
            local.set 2
            br 2 (;@2;)
          end
          local.get 1
          i32.const 59893
          i32.ne
          if ;; label = @4
            local.get 1
            i32.const 35330
            i32.eq
            br_if 2 (;@2;)
            local.get 1
            i32.const 31493
            i32.ne
            br_if 3 (;@1;)
            i32.const 1
            local.set 2
            br 2 (;@2;)
          end
          i32.const 3
          local.set 2
          br 1 (;@2;)
        end
        i32.const 4
        local.set 2
      end
      i32.const 0
      local.set 3
      i32.const 824
      i32.load
      local.tee 8
      i32.eqz
      br_if 0 (;@1;)
      local.get 0
      i32.load offset=32
      local.tee 4
      i32.const 4
      i32.and
      local.set 9
      local.get 4
      i32.const 2
      i32.and
      local.set 12
      local.get 2
      i32.const 1
      i32.shl
      local.tee 1
      i32.const 532
      i32.add
      local.set 10
      local.get 1
      i32.const 522
      i32.add
      local.set 7
      local.get 0
      i32.load16_u offset=62
      local.set 11
      local.get 0
      i32.load16_u offset=60
      local.set 6
      block ;; label = @2
        local.get 4
        i32.const 1
        i32.and
        if ;; label = @3
          i32.const 2
          i32.const 1
          local.get 0
          i32.load16_u offset=58
          local.get 1
          i32.const 512
          i32.add
          i32.load16_u
          i32.ne
          local.tee 4
          select
          local.set 5
          i32.const 0
          local.set 2
          loop ;; label = @4
            local.get 4
            local.set 1
            local.get 12
            if ;; label = @5
              local.get 4
              local.get 5
              local.get 6
              local.get 7
              i32.load16_u
              i32.eq
              select
              local.set 1
            end
            local.get 9
            if ;; label = @5
              local.get 1
              local.get 11
              local.get 10
              i32.load16_u
              i32.ne
              i32.add
              local.set 1
            end
            local.get 1
            local.get 3
            i32.add
            local.set 3
            local.get 8
            local.get 2
            i32.const 1
            i32.add
            local.tee 2
            i32.const 65535
            i32.and
            i32.gt_u
            br_if 0 (;@4;)
          end
          br 1 (;@2;)
        end
        local.get 12
        i32.eqz
        if ;; label = @3
          i32.const 0
          local.set 2
          loop ;; label = @4
            i32.const 0
            local.set 1
            local.get 9
            if ;; label = @5
              local.get 11
              local.get 10
              i32.load16_u
              i32.ne
              local.set 1
            end
            local.get 1
            local.get 3
            i32.add
            local.set 3
            local.get 8
            local.get 2
            i32.const 1
            i32.add
            local.tee 2
            i32.const 65535
            i32.and
            i32.gt_u
            br_if 0 (;@4;)
          end
          br 1 (;@2;)
        end
        i32.const 2
        i32.const 1
        local.get 6
        local.get 7
        i32.load16_u
        i32.ne
        local.tee 4
        select
        local.set 5
        i32.const 0
        local.set 2
        loop ;; label = @3
          local.get 4
          local.set 1
          local.get 9
          if ;; label = @4
            local.get 4
            local.get 5
            local.get 11
            local.get 10
            i32.load16_u
            i32.eq
            select
            local.set 1
          end
          local.get 1
          local.get 3
          i32.add
          local.set 3
          local.get 8
          local.get 2
          i32.const 1
          i32.add
          local.tee 2
          i32.const 65535
          i32.and
          i32.gt_u
          br_if 0 (;@3;)
        end
      end
      local.get 0
      local.get 1
      i32.store16 offset=64
    end
    local.get 14
    f64.convert_i64_u
    f64.const 0x1.f4p+9 (;=1000;)
    f64.div
    local.set 16
    local.get 0
    i32.load offset=32
    local.tee 6
    i32.const 1
    i32.and
    if ;; label = @1
      i32.const 824
      i32.load
      local.set 2
      i32.const 0
      local.set 1
      loop ;; label = @2
        local.get 1
        i32.const 65535
        i32.and
        local.set 4
        local.get 1
        i32.const 1
        i32.add
        local.set 1
        local.get 2
        local.get 4
        i32.gt_u
        br_if 0 (;@2;)
      end
    end
    local.get 6
    i32.const 2
    i32.and
    if ;; label = @1
      i32.const 0
      local.set 1
      i32.const 824
      i32.load
      local.set 2
      loop ;; label = @2
        local.get 1
        i32.const 65535
        i32.and
        local.set 4
        local.get 1
        i32.const 1
        i32.add
        local.set 1
        local.get 2
        local.get 4
        i32.gt_u
        br_if 0 (;@2;)
      end
    end
    i32.const 0
    local.set 1
    i32.const 824
    i32.load
    local.set 5
    local.get 6
    i32.const 4
    i32.and
    if ;; label = @1
      loop ;; label = @2
        local.get 1
        i32.const 65535
        i32.and
        local.set 4
        local.get 1
        i32.const 1
        i32.add
        local.set 1
        local.get 5
        local.get 4
        i32.gt_u
        br_if 0 (;@2;)
      end
    end
    i32.const -1
    i32.const 0
    local.get 16
    f64.const 0x1.4p+3 (;=10;)
    f64.lt
    select
    local.set 2
    i32.const 0
    local.set 1
    loop ;; label = @1
      local.get 1
      i32.const 65535
      i32.and
      local.set 4
      local.get 1
      i32.const 1
      i32.add
      local.set 1
      local.get 5
      local.get 4
      i32.gt_u
      br_if 0 (;@1;)
    end
    local.get 13
    i32.const 0
    i32.store8
    local.get 3
    i32.const 65535
    i32.and
    local.get 2
    i32.const 65535
    i32.and
    i32.eq
    if ;; label = @1
      local.get 0
      i32.load offset=28
      i32.const 824
      i32.load
      i32.mul
      f64.convert_i32_u
      local.get 14
      f64.convert_i64_u
      f64.const 0x1.f4p+9 (;=1000;)
      f64.div
      f64.div
      f32.demote_f64
      local.set 15
    end
    local.get 0
    i32.const 80
    i32.add
    global.set 0
    local.get 15
  )
  (func (;5;) (type 6) (param i32 i32 i32 i32 i32) (result i32)
    (local i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32)
    block ;; label = @1
      local.get 0
      if ;; label = @2
        local.get 4
        i32.const -4096
        i32.or
        local.set 16
        local.get 0
        i32.const 1
        i32.shl
        local.set 10
        local.get 2
        local.set 7
        loop ;; label = @3
          local.get 0
          local.set 5
          local.get 7
          local.set 6
          loop ;; label = @4
            local.get 6
            local.get 6
            i32.load16_u
            local.get 4
            i32.add
            i32.store16
            local.get 6
            i32.const 2
            i32.add
            local.set 6
            local.get 5
            i32.const -1
            i32.add
            local.tee 5
            br_if 0 (;@4;)
          end
          local.get 7
          local.get 10
          i32.add
          local.set 7
          local.get 11
          i32.const 1
          i32.add
          local.tee 11
          local.get 0
          i32.ne
          br_if 0 (;@3;)
        end
        local.get 0
        i32.const 1
        i32.shl
        local.set 10
        local.get 0
        i32.const 2
        i32.shl
        local.set 12
        local.get 2
        local.set 11
        local.get 1
        local.set 8
        loop ;; label = @3
          local.get 0
          local.set 7
          local.get 11
          local.set 6
          local.get 8
          local.set 5
          loop ;; label = @4
            local.get 5
            local.get 6
            i32.load16_s
            local.get 4
            i32.mul
            i32.store
            local.get 6
            i32.const 2
            i32.add
            local.set 6
            local.get 5
            i32.const 4
            i32.add
            local.set 5
            local.get 7
            i32.const -1
            i32.add
            local.tee 7
            br_if 0 (;@4;)
          end
          local.get 10
          local.get 11
          i32.add
          local.set 11
          local.get 8
          local.get 12
          i32.add
          local.set 8
          local.get 9
          i32.const 1
          i32.add
          local.tee 9
          local.get 0
          i32.ne
          br_if 0 (;@3;)
        end
        local.get 0
        i32.const 2
        i32.shl
        local.set 13
        local.get 1
        local.set 10
        i32.const 0
        local.set 12
        i32.const 0
        local.set 8
        i32.const 0
        local.set 7
        i32.const 0
        local.set 9
        loop ;; label = @3
          local.get 0
          local.set 11
          local.get 10
          local.set 5
          loop ;; label = @4
            i32.const 0
            local.get 5
            i32.load
            local.tee 6
            local.get 9
            i32.add
            local.tee 9
            local.get 9
            local.get 16
            i32.gt_s
            local.tee 14
            select
            local.set 9
            i32.const 10
            local.get 6
            local.get 7
            i32.gt_s
            local.get 14
            select
            local.get 8
            i32.add
            local.set 8
            local.get 5
            i32.const 4
            i32.add
            local.set 5
            local.get 6
            local.set 7
            local.get 11
            i32.const -1
            i32.add
            local.tee 11
            br_if 0 (;@4;)
          end
          local.get 10
          local.get 13
          i32.add
          local.set 10
          local.get 12
          i32.const 1
          i32.add
          local.tee 12
          local.get 0
          i32.ne
          br_if 0 (;@3;)
        end
        local.get 0
        i32.const 1
        i32.shl
        local.set 10
        local.get 8
        i32.const 16
        i32.shl
        i32.const 16
        i32.shr_s
        i32.const 0
        call 12
        local.set 14
        local.get 2
        local.set 8
        i32.const 0
        local.set 9
        loop ;; label = @3
          local.get 1
          local.get 9
          i32.const 2
          i32.shl
          i32.add
          local.tee 12
          i32.const 0
          i32.store
          local.get 3
          local.set 6
          local.get 0
          local.set 11
          local.get 8
          local.set 5
          i32.const 0
          local.set 7
          loop ;; label = @4
            local.get 6
            i32.load16_s
            local.get 5
            i32.load16_s
            i32.mul
            local.get 7
            i32.add
            local.set 7
            local.get 6
            i32.const 2
            i32.add
            local.set 6
            local.get 5
            i32.const 2
            i32.add
            local.set 5
            local.get 11
            i32.const -1
            i32.add
            local.tee 11
            br_if 0 (;@4;)
          end
          local.get 12
          local.get 7
          i32.store
          local.get 8
          local.get 10
          i32.add
          local.set 8
          local.get 9
          i32.const 1
          i32.add
          local.tee 9
          local.get 0
          i32.ne
          br_if 0 (;@3;)
        end
        local.get 0
        i32.const 2
        i32.shl
        local.set 13
        local.get 1
        local.set 10
        i32.const 0
        local.set 12
        i32.const 0
        local.set 9
        i32.const 0
        local.set 7
        i32.const 0
        local.set 8
        loop ;; label = @3
          local.get 0
          local.set 11
          local.get 10
          local.set 5
          loop ;; label = @4
            i32.const 0
            local.get 5
            i32.load
            local.tee 6
            local.get 8
            i32.add
            local.tee 8
            local.get 8
            local.get 16
            i32.gt_s
            local.tee 15
            select
            local.set 8
            i32.const 10
            local.get 6
            local.get 7
            i32.gt_s
            local.get 15
            select
            local.get 9
            i32.add
            local.set 9
            local.get 5
            i32.const 4
            i32.add
            local.set 5
            local.get 6
            local.set 7
            local.get 11
            i32.const -1
            i32.add
            local.tee 11
            br_if 0 (;@4;)
          end
          local.get 10
          local.get 13
          i32.add
          local.set 10
          local.get 12
          i32.const 1
          i32.add
          local.tee 12
          local.get 0
          i32.ne
          br_if 0 (;@3;)
        end
        local.get 0
        i32.const 1
        i32.shl
        local.set 13
        i32.const 0
        local.set 10
        local.get 9
        i32.const 16
        i32.shl
        i32.const 16
        i32.shr_s
        local.get 14
        call 12
        local.set 14
        local.get 2
        local.set 12
        loop ;; label = @3
          local.get 0
          local.get 10
          i32.mul
          local.set 15
          local.get 3
          local.set 9
          i32.const 0
          local.set 8
          loop ;; label = @4
            i32.const 0
            local.set 7
            local.get 1
            local.get 8
            local.get 15
            i32.add
            i32.const 2
            i32.shl
            i32.add
            local.tee 17
            i32.const 0
            i32.store
            local.get 0
            local.set 11
            local.get 12
            local.set 6
            local.get 9
            local.set 5
            loop ;; label = @5
              local.get 5
              i32.load16_s
              local.get 6
              i32.load16_s
              i32.mul
              local.get 7
              i32.add
              local.set 7
              local.get 6
              i32.const 2
              i32.add
              local.set 6
              local.get 5
              local.get 13
              i32.add
              local.set 5
              local.get 11
              i32.const -1
              i32.add
              local.tee 11
              br_if 0 (;@5;)
            end
            local.get 17
            local.get 7
            i32.store
            local.get 9
            i32.const 2
            i32.add
            local.set 9
            local.get 8
            i32.const 1
            i32.add
            local.tee 8
            local.get 0
            i32.ne
            br_if 0 (;@4;)
          end
          local.get 12
          local.get 13
          i32.add
          local.set 12
          local.get 10
          i32.const 1
          i32.add
          local.tee 10
          local.get 0
          i32.ne
          br_if 0 (;@3;)
        end
        local.get 0
        i32.const 2
        i32.shl
        local.set 13
        local.get 1
        local.set 10
        i32.const 0
        local.set 12
        i32.const 0
        local.set 8
        i32.const 0
        local.set 7
        i32.const 0
        local.set 9
        loop ;; label = @3
          local.get 0
          local.set 11
          local.get 10
          local.set 5
          loop ;; label = @4
            i32.const 0
            local.get 5
            i32.load
            local.tee 6
            local.get 9
            i32.add
            local.tee 9
            local.get 9
            local.get 16
            i32.gt_s
            local.tee 15
            select
            local.set 9
            i32.const 10
            local.get 6
            local.get 7
            i32.gt_s
            local.get 15
            select
            local.get 8
            i32.add
            local.set 8
            local.get 5
            i32.const 4
            i32.add
            local.set 5
            local.get 6
            local.set 7
            local.get 11
            i32.const -1
            i32.add
            local.tee 11
            br_if 0 (;@4;)
          end
          local.get 10
          local.get 13
          i32.add
          local.set 10
          local.get 12
          i32.const 1
          i32.add
          local.tee 12
          local.get 0
          i32.ne
          br_if 0 (;@3;)
        end
        local.get 0
        i32.const 1
        i32.shl
        local.set 13
        i32.const 0
        local.set 12
        local.get 8
        i32.const 16
        i32.shl
        i32.const 16
        i32.shr_s
        local.get 14
        call 12
        local.set 14
        local.get 2
        local.set 9
        loop ;; label = @3
          local.get 0
          local.get 12
          i32.mul
          local.set 15
          local.get 3
          local.set 8
          i32.const 0
          local.set 10
          loop ;; label = @4
            i32.const 0
            local.set 7
            local.get 1
            local.get 10
            local.get 15
            i32.add
            i32.const 2
            i32.shl
            i32.add
            local.tee 17
            i32.const 0
            i32.store
            local.get 0
            local.set 11
            local.get 9
            local.set 6
            local.get 8
            local.set 5
            loop ;; label = @5
              local.get 7
              local.get 5
              i32.load16_u
              local.get 6
              i32.load16_u
              i32.mul
              local.tee 7
              i32.const 2
              i32.shr_u
              i32.const 15
              i32.and
              local.get 7
              i32.const 5
              i32.shr_u
              i32.const 127
              i32.and
              i32.mul
              i32.add
              local.set 7
              local.get 6
              i32.const 2
              i32.add
              local.set 6
              local.get 5
              local.get 13
              i32.add
              local.set 5
              local.get 11
              i32.const -1
              i32.add
              local.tee 11
              br_if 0 (;@5;)
            end
            local.get 17
            local.get 7
            i32.store
            local.get 8
            i32.const 2
            i32.add
            local.set 8
            local.get 10
            i32.const 1
            i32.add
            local.tee 10
            local.get 0
            i32.ne
            br_if 0 (;@4;)
          end
          local.get 9
          local.get 13
          i32.add
          local.set 9
          local.get 12
          i32.const 1
          i32.add
          local.tee 12
          local.get 0
          i32.ne
          br_if 0 (;@3;)
        end
        local.get 0
        i32.const 2
        i32.shl
        local.set 6
        i32.const 0
        local.set 10
        i32.const 0
        local.set 8
        i32.const 0
        local.set 7
        i32.const 0
        local.set 9
        loop ;; label = @3
          local.get 0
          local.set 11
          local.get 1
          local.set 5
          loop ;; label = @4
            i32.const 0
            local.get 5
            i32.load
            local.tee 3
            local.get 9
            i32.add
            local.tee 9
            local.get 9
            local.get 16
            i32.gt_s
            local.tee 12
            select
            local.set 9
            i32.const 10
            local.get 3
            local.get 7
            i32.gt_s
            local.get 12
            select
            local.get 8
            i32.add
            local.set 8
            local.get 5
            i32.const 4
            i32.add
            local.set 5
            local.get 3
            local.set 7
            local.get 11
            i32.const -1
            i32.add
            local.tee 11
            br_if 0 (;@4;)
          end
          local.get 1
          local.get 6
          i32.add
          local.set 1
          local.get 10
          i32.const 1
          i32.add
          local.tee 10
          local.get 0
          i32.ne
          br_if 0 (;@3;)
        end
        local.get 0
        i32.const 1
        i32.shl
        local.set 1
        i32.const 0
        local.set 7
        local.get 8
        i32.const 16
        i32.shl
        i32.const 16
        i32.shr_s
        local.get 14
        call 12
        local.set 8
        loop ;; label = @3
          local.get 0
          local.set 5
          local.get 2
          local.set 6
          loop ;; label = @4
            local.get 6
            local.get 6
            i32.load16_u
            local.get 4
            i32.sub
            i32.store16
            local.get 6
            i32.const 2
            i32.add
            local.set 6
            local.get 5
            i32.const -1
            i32.add
            local.tee 5
            br_if 0 (;@4;)
          end
          local.get 1
          local.get 2
          i32.add
          local.set 2
          local.get 7
          i32.const 1
          i32.add
          local.tee 7
          local.get 0
          i32.ne
          br_if 0 (;@3;)
        end
        br 1 (;@1;)
      end
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      call 12
      call 12
      call 12
      call 12
      local.set 8
    end
    local.get 8
    i32.const 16
    i32.shl
    i32.const 16
    i32.shr_s
  )
  (func (;6;) (type 4) (param i32 i32 i32 i32)
    (local i32 i32 i32 i32 i32 i32 i32 i32 i32 i32 i32)
    block ;; label = @1
      local.get 0
      i32.eqz
      if ;; label = @2
        i32.const -1
        local.set 4
        br 1 (;@1;)
      end
      i32.const -1
      local.set 5
      i32.const 8
      local.set 6
      loop ;; label = @2
        local.get 6
        local.get 5
        i32.const 2
        i32.add
        i32.mul
        local.get 6
        i32.const 8
        i32.add
        local.set 6
        local.get 5
        i32.const 1
        i32.add
        local.tee 4
        local.set 5
        local.get 0
        i32.lt_u
        br_if 0 (;@2;)
      end
    end
    local.get 1
    i32.const 3
    i32.add
    i32.const -4
    i32.and
    local.tee 7
    local.get 4
    local.get 4
    i32.mul
    local.tee 9
    i32.const 1
    i32.shl
    i32.add
    local.set 10
    local.get 4
    if ;; label = @1
      local.get 2
      i32.const 1
      local.get 2
      select
      local.set 0
      local.get 4
      i32.const 1
      i32.shl
      local.set 11
      local.get 9
      i32.const 1
      i32.shl
      local.set 12
      local.get 7
      local.set 2
      i32.const 1
      local.set 1
      loop ;; label = @2
        local.get 1
        i32.const 1
        i32.shl
        local.set 8
        local.get 2
        local.set 5
        i32.const 0
        local.set 6
        loop ;; label = @3
          local.get 5
          local.get 12
          i32.add
          local.get 1
          local.get 6
          i32.add
          local.tee 13
          local.get 0
          local.get 13
          i32.mul
          i32.const 65536
          i32.rem_s
          local.tee 0
          i32.add
          i32.store16
          local.get 5
          local.get 0
          local.get 8
          i32.add
          i32.const 255
          i32.and
          i32.store16
          local.get 8
          i32.const 2
          i32.add
          local.set 8
          local.get 5
          i32.const 2
          i32.add
          local.set 5
          local.get 4
          local.get 6
          i32.const 1
          i32.add
          local.tee 6
          i32.ne
          br_if 0 (;@3;)
        end
        local.get 1
        local.get 6
        i32.add
        local.set 1
        local.get 2
        local.get 11
        i32.add
        local.set 2
        local.get 14
        i32.const 1
        i32.add
        local.tee 14
        local.get 4
        i32.ne
        br_if 0 (;@2;)
      end
    end
    local.get 3
    local.get 10
    i32.store offset=8
    local.get 3
    local.get 7
    i32.store offset=4
    local.get 3
    local.get 4
    i32.store
    local.get 3
    local.get 10
    local.get 9
    i32.const 1
    i32.shl
    i32.add
    i32.const 3
    i32.add
    i32.const -4
    i32.and
    i32.store offset=12
  )
  (func (;7;) (type 7) (param i32 i32 i32 i32 i32 i32) (result i32)
    (local i32 i32 i32 i32)
    global.get 0
    i32.const 80
    i32.sub
    local.tee 6
    global.set 0
    local.get 6
    i32.const 32
    i32.add
    i64.const 0
    i64.store
    local.get 6
    i32.const 40
    i32.add
    i64.const 0
    i64.store
    local.get 6
    i32.const -64
    i32.sub
    i64.const 0
    i64.store
    local.get 6
    i32.const 72
    i32.add
    i64.const 0
    i64.store
    local.get 6
    i64.const 0
    i64.store offset=48
    local.get 6
    i64.const 0
    i64.store offset=56
    local.get 6
    i64.const 0
    i64.store offset=16
    local.get 6
    i64.const 0
    i64.store offset=24
    local.get 6
    local.get 1
    i32.store offset=12
    local.get 1
    i32.load8_u
    if ;; label = @1
      loop ;; label = @2
        local.get 6
        i32.const 48
        i32.add
        local.get 6
        i32.const 12
        i32.add
        local.get 6
        i32.const 16
        i32.add
        call 8
        i32.const 2
        i32.shl
        i32.add
        local.tee 7
        local.get 7
        i32.load
        i32.const 1
        i32.add
        i32.store
        local.get 6
        i32.load offset=12
        i32.load8_u
        br_if 0 (;@2;)
      end
    end
    local.get 6
    local.get 1
    i32.store offset=12
    local.get 0
    local.get 1
    i32.add
    local.set 8
    local.get 0
    i32.const 1
    i32.ge_s
    if ;; label = @1
      local.get 1
      local.set 7
      loop ;; label = @2
        local.get 7
        i32.load8_u
        local.tee 9
        i32.const 44
        i32.ne
        if ;; label = @3
          local.get 7
          local.get 2
          local.get 9
          i32.xor
          i32.store8
        end
        local.get 6
        local.get 4
        local.get 7
        i32.add
        local.tee 7
        i32.store offset=12
        local.get 7
        local.get 8
        i32.lt_u
        br_if 0 (;@2;)
      end
    end
    local.get 6
    local.get 1
    i32.store offset=12
    local.get 1
    i32.load8_u
    if ;; label = @1
      loop ;; label = @2
        local.get 6
        i32.const 48
        i32.add
        local.get 6
        i32.const 12
        i32.add
        local.get 6
        i32.const 16
        i32.add
        call 8
        i32.const 2
        i32.shl
        i32.add
        local.tee 2
        local.get 2
        i32.load
        i32.const 1
        i32.add
        i32.store
        local.get 6
        i32.load offset=12
        i32.load8_u
        br_if 0 (;@2;)
      end
    end
    local.get 6
    local.get 1
    i32.store offset=12
    local.get 0
    i32.const 1
    i32.ge_s
    if ;; label = @1
      loop ;; label = @2
        local.get 1
        i32.load8_u
        local.tee 0
        i32.const 44
        i32.ne
        if ;; label = @3
          local.get 1
          local.get 0
          local.get 3
          i32.xor
          i32.store8
        end
        local.get 6
        local.get 1
        local.get 4
        i32.add
        local.tee 1
        i32.store offset=12
        local.get 1
        local.get 8
        i32.lt_u
        br_if 0 (;@2;)
      end
    end
    local.get 6
    i32.load offset=48
    local.get 5
    call 11
    local.set 0
    local.get 6
    i32.load offset=16
    local.get 0
    call 11
    local.set 0
    local.get 6
    i32.load offset=52
    local.get 0
    call 11
    local.set 0
    local.get 6
    i32.load offset=20
    local.get 0
    call 11
    local.set 0
    local.get 6
    i32.load offset=56
    local.get 0
    call 11
    local.set 0
    local.get 6
    i32.load offset=24
    local.get 0
    call 11
    local.set 0
    local.get 6
    i32.load offset=60
    local.get 0
    call 11
    local.set 0
    local.get 6
    i32.load offset=28
    local.get 0
    call 11
    local.set 0
    local.get 6
    i32.load offset=64
    local.get 0
    call 11
    local.set 0
    local.get 6
    i32.load offset=32
    local.get 0
    call 11
    local.set 0
    local.get 6
    i32.load offset=68
    local.get 0
    call 11
    local.set 0
    local.get 6
    i32.load offset=36
    local.get 0
    call 11
    local.set 0
    local.get 6
    i32.load offset=72
    local.get 0
    call 11
    local.set 0
    local.get 6
    i32.load offset=40
    local.get 0
    call 11
    local.set 0
    local.get 6
    i32.load offset=76
    local.get 0
    call 11
    local.set 0
    local.get 6
    i32.load offset=44
    local.get 0
    call 11
    local.get 6
    i32.const 80
    i32.add
    global.set 0
  )
  (func (;8;) (type 0) (param i32 i32) (result i32)
    (local i32 i32 i32 i32 i32 i32 i32 i32)
    local.get 0
    i32.load
    local.tee 4
    i32.load8_u
    local.tee 3
    i32.eqz
    if ;; label = @1
      local.get 0
      local.get 4
      i32.store
      i32.const 0
      return
    end
    local.get 4
    i32.const 1
    i32.add
    local.set 5
    local.get 1
    i32.const 16
    i32.add
    local.set 6
    local.get 1
    i32.const 20
    i32.add
    local.set 7
    local.get 1
    i32.const 12
    i32.add
    local.set 4
    local.get 1
    i32.const 4
    i32.add
    local.set 8
    block ;; label = @1
      loop ;; label = @2
        local.get 3
        i32.const 44
        i32.eq
        br_if 1 (;@1;)
        block ;; label = @3
          block ;; label = @4
            block ;; label = @5
              block ;; label = @6
                block ;; label = @7
                  block ;; label = @8
                    block ;; label = @9
                      block ;; label = @10
                        block ;; label = @11
                          block ;; label = @12
                            local.get 2
                            br_table 0 (;@12;) 9 (;@3;) 1 (;@11;) 4 (;@8;) 2 (;@10;) 3 (;@9;) 5 (;@7;) 6 (;@6;) 9 (;@3;)
                          end
                          i32.const 4
                          local.set 2
                          block ;; label = @12
                            local.get 3
                            i32.const -48
                            i32.add
                            i32.const 255
                            i32.and
                            i32.const 10
                            i32.lt_u
                            br_if 0 (;@12;)
                            i32.const 2
                            local.set 2
                            block ;; label = @13
                              block ;; label = @14
                                local.get 3
                                i32.const -43
                                i32.add
                                br_table 2 (;@12;) 1 (;@13;) 2 (;@12;) 0 (;@14;) 1 (;@13;)
                              end
                              i32.const 5
                              local.set 2
                              br 1 (;@12;)
                            end
                            i32.const 1
                            local.set 2
                            local.get 8
                            local.get 8
                            i32.load
                            i32.const 1
                            i32.add
                            i32.store
                          end
                          local.get 1
                          local.get 1
                          i32.load
                          i32.const 1
                          i32.add
                          i32.store
                          br 8 (;@3;)
                        end
                        local.get 3
                        i32.const -48
                        i32.add
                        i32.const 255
                        i32.and
                        i32.const 9
                        i32.gt_u
                        br_if 6 (;@4;)
                        local.get 1
                        local.get 1
                        i32.load offset=8
                        i32.const 1
                        i32.add
                        i32.store offset=8
                        i32.const 4
                        local.set 2
                        br 7 (;@3;)
                      end
                      local.get 3
                      i32.const 46
                      i32.eq
                      if ;; label = @10
                        local.get 6
                        local.get 6
                        i32.load
                        i32.const 1
                        i32.add
                        i32.store
                        i32.const 5
                        local.set 2
                        br 7 (;@3;)
                      end
                      i32.const 4
                      local.set 2
                      local.get 3
                      i32.const -48
                      i32.add
                      i32.const 255
                      i32.and
                      i32.const 9
                      i32.le_u
                      br_if 6 (;@3;)
                      local.get 6
                      local.set 4
                      br 4 (;@5;)
                    end
                    local.get 3
                    i32.const 32
                    i32.or
                    i32.const 101
                    i32.eq
                    if ;; label = @9
                      local.get 7
                      local.get 7
                      i32.load
                      i32.const 1
                      i32.add
                      i32.store
                      i32.const 3
                      local.set 2
                      br 6 (;@3;)
                    end
                    i32.const 5
                    local.set 2
                    local.get 3
                    i32.const -48
                    i32.add
                    i32.const 255
                    i32.and
                    i32.const 9
                    i32.le_u
                    br_if 5 (;@3;)
                    local.get 7
                    local.set 4
                    br 3 (;@5;)
                  end
                  block ;; label = @8
                    local.get 3
                    i32.const -43
                    i32.add
                    br_table 0 (;@8;) 3 (;@5;) 0 (;@8;) 3 (;@5;)
                  end
                  local.get 4
                  local.get 4
                  i32.load
                  i32.const 1
                  i32.add
                  i32.store
                  i32.const 6
                  local.set 2
                  br 4 (;@3;)
                end
                local.get 1
                local.get 1
                i32.load offset=24
                i32.const 1
                i32.add
                i32.store offset=24
                i32.const 1
                i32.const 7
                local.get 3
                i32.const -48
                i32.add
                i32.const 255
                i32.and
                i32.const 9
                i32.gt_u
                select
                local.set 2
                br 3 (;@3;)
              end
              i32.const 7
              local.set 2
              local.get 3
              i32.const -48
              i32.add
              i32.const 255
              i32.and
              i32.const 10
              i32.lt_u
              br_if 2 (;@3;)
              local.get 8
              local.set 4
            end
            local.get 4
            local.get 4
            i32.load
            i32.const 1
            i32.add
            i32.store
            local.get 0
            local.get 5
            i32.store
            i32.const 1
            return
          end
          local.get 1
          local.get 1
          i32.load offset=8
          i32.const 1
          i32.add
          i32.store offset=8
          i32.const 5
          i32.const 1
          local.get 3
          i32.const 46
          i32.eq
          select
          local.set 2
        end
        local.get 5
        i32.const 1
        i32.add
        local.set 9
        local.get 2
        i32.const 1
        i32.ne
        if ;; label = @3
          local.get 5
          i32.load8_u
          local.set 3
          local.get 9
          local.set 5
          local.get 3
          br_if 1 (;@2;)
        end
      end
      local.get 9
      i32.const -1
      i32.add
      local.set 5
    end
    local.get 0
    local.get 5
    i32.store
    local.get 2
  )
  (func (;9;) (type 3) (param i32 i32 i32)
    (local i32 i32 i32 i32 i32 i32)
    local.get 0
    i32.const -1
    i32.add
    local.tee 8
    i32.const 2
    i32.ge_u
    if ;; label = @1
      loop ;; label = @2
        local.get 4
        if ;; label = @3
          local.get 2
          local.get 3
          i32.add
          local.set 6
          local.get 4
          local.set 7
          loop ;; label = @4
            local.get 6
            local.get 5
            i32.load8_u
            i32.store8
            local.get 5
            i32.const 1
            i32.add
            local.set 5
            local.get 6
            i32.const 1
            i32.add
            local.set 6
            local.get 7
            i32.const -1
            i32.add
            local.tee 7
            br_if 0 (;@4;)
          end
          local.get 2
          local.get 3
          i32.add
          local.get 4
          i32.add
          i32.const 44
          i32.store8
          local.get 3
          local.get 4
          i32.add
          i32.const 1
          i32.add
          local.set 3
        end
        local.get 1
        i32.const 1
        i32.add
        local.tee 1
        i32.const 7
        i32.and
        i32.const 2
        i32.shl
        local.tee 4
        i32.const 736
        i32.add
        i32.load
        local.get 1
        i32.const 1
        i32.shr_u
        i32.const 12
        i32.and
        i32.add
        i32.load
        local.set 5
        local.get 3
        local.get 4
        i32.const 768
        i32.add
        i32.load
        local.tee 4
        i32.add
        i32.const 1
        i32.add
        local.get 8
        i32.lt_u
        br_if 0 (;@2;)
      end
    end
    local.get 3
    local.get 0
    i32.lt_u
    if ;; label = @1
      local.get 2
      local.get 3
      i32.add
      local.get 0
      local.get 3
      i32.sub
      call 13
    end
  )
  (func (;10;) (type 0) (param i32 i32) (result i32)
    (local i32 i32)
    local.get 1
    i32.const 1
    i32.shr_u
    local.tee 2
    i32.const -24575
    i32.xor
    local.get 2
    local.get 0
    local.get 1
    i32.xor
    i32.const 1
    i32.and
    select
    local.tee 1
    i32.const 1
    i32.shr_u
    i32.const 32767
    i32.and
    local.tee 2
    i32.const -24575
    i32.xor
    local.get 2
    local.get 1
    local.get 0
    i32.const 255
    i32.and
    local.tee 1
    i32.const 1
    i32.shr_u
    i32.xor
    i32.const 1
    i32.and
    select
    local.tee 2
    i32.const 1
    i32.shr_u
    i32.const 32767
    i32.and
    local.tee 3
    i32.const -24575
    i32.xor
    local.get 3
    local.get 1
    i32.const 2
    i32.shr_u
    local.get 2
    i32.xor
    i32.const 1
    i32.and
    select
    local.tee 2
    i32.const 1
    i32.shr_u
    i32.const 32767
    i32.and
    local.tee 3
    i32.const -24575
    i32.xor
    local.get 3
    local.get 1
    i32.const 3
    i32.shr_u
    local.get 2
    i32.xor
    i32.const 1
    i32.and
    select
    local.tee 2
    i32.const 1
    i32.shr_u
    i32.const 32767
    i32.and
    local.tee 3
    i32.const -24575
    i32.xor
    local.get 3
    local.get 1
    i32.const 4
    i32.shr_u
    local.get 2
    i32.xor
    i32.const 1
    i32.and
    select
    local.tee 2
    i32.const 1
    i32.shr_u
    i32.const 32767
    i32.and
    local.tee 3
    i32.const -24575
    i32.xor
    local.get 3
    local.get 1
    i32.const 5
    i32.shr_u
    local.get 2
    i32.xor
    i32.const 1
    i32.and
    select
    local.tee 2
    i32.const 1
    i32.shr_u
    i32.const 32767
    i32.and
    local.tee 3
    i32.const -24575
    i32.xor
    local.get 3
    local.get 1
    i32.const 6
    i32.shr_u
    local.get 2
    i32.xor
    i32.const 1
    i32.and
    select
    local.tee 2
    i32.const 1
    i32.shr_u
    i32.const 32767
    i32.and
    local.tee 3
    local.get 3
    i32.const -24575
    i32.xor
    local.get 2
    i32.const 1
    i32.and
    local.get 1
    i32.const 7
    i32.shr_u
    i32.eq
    select
    local.tee 1
    i32.const 1
    i32.shr_u
    i32.const 32767
    i32.and
    local.tee 2
    i32.const -24575
    i32.xor
    local.get 2
    local.get 1
    local.get 0
    i32.const 8
    i32.shr_u
    i32.xor
    i32.const 1
    i32.and
    select
    local.tee 1
    i32.const 1
    i32.shr_u
    i32.const 32767
    i32.and
    local.tee 2
    i32.const -24575
    i32.xor
    local.get 2
    local.get 1
    local.get 0
    i32.const 9
    i32.shr_u
    i32.xor
    i32.const 1
    i32.and
    select
    local.tee 1
    i32.const 1
    i32.shr_u
    i32.const 32767
    i32.and
    local.tee 2
    i32.const -24575
    i32.xor
    local.get 2
    local.get 1
    local.get 0
    i32.const 10
    i32.shr_u
    i32.xor
    i32.const 1
    i32.and
    select
    local.tee 1
    i32.const 1
    i32.shr_u
    i32.const 32767
    i32.and
    local.tee 2
    i32.const -24575
    i32.xor
    local.get 2
    local.get 1
    local.get 0
    i32.const 11
    i32.shr_u
    i32.xor
    i32.const 1
    i32.and
    select
    local.tee 1
    i32.const 1
    i32.shr_u
    i32.const 32767
    i32.and
    local.tee 2
    i32.const -24575
    i32.xor
    local.get 2
    local.get 1
    local.get 0
    i32.const 12
    i32.shr_u
    i32.xor
    i32.const 1
    i32.and
    select
    local.tee 1
    i32.const 1
    i32.shr_u
    i32.const 32767
    i32.and
    local.tee 2
    i32.const -24575
    i32.xor
    local.get 2
    local.get 1
    local.get 0
    i32.const 13
    i32.shr_u
    i32.xor
    i32.const 1
    i32.and
    select
    local.tee 1
    i32.const 1
    i32.shr_u
    i32.const 32767
    i32.and
    local.tee 2
    i32.const -24575
    i32.xor
    local.get 2
    local.get 1
    local.get 0
    i32.const 14
    i32.shr_u
    i32.xor
    i32.const 1
    i32.and
    select
    local.tee 1
    i32.const 1
    i32.shr_u
    i32.const 32767
    i32.and
    local.tee 2
    local.get 2
    i32.const 40961
    i32.xor
    local.get 1
    i32.const 1
    i32.and
    local.get 0
    i32.const 15
    i32.shr_u
    i32.eq
    select
  )
  (func (;11;) (type 0) (param i32 i32) (result i32)
    local.get 0
    i32.const 16
    i32.shr_u
    local.get 0
    i32.const 65535
    i32.and
    local.get 1
    call 10
    call 10
  )
  (func (;12;) (type 0) (param i32 i32) (result i32)
    local.get 0
    i32.const 65535
    i32.and
    local.get 1
    call 10
  )
  (func (;13;) (type 2) (param i32 i32)
    (local i32 i32)
    block ;; label = @1
      local.get 1
      i32.eqz
      br_if 0 (;@1;)
      local.get 0
      local.get 1
      i32.add
      local.tee 2
      i32.const -1
      i32.add
      i32.const 0
      i32.store8
      local.get 0
      i32.const 0
      i32.store8
      local.get 1
      i32.const 3
      i32.lt_u
      br_if 0 (;@1;)
      local.get 2
      i32.const -2
      i32.add
      i32.const 0
      i32.store8
      local.get 0
      i32.const 0
      i32.store8 offset=1
      local.get 2
      i32.const -3
      i32.add
      i32.const 0
      i32.store8
      local.get 0
      i32.const 0
      i32.store8 offset=2
      local.get 1
      i32.const 7
      i32.lt_u
      br_if 0 (;@1;)
      local.get 2
      i32.const -4
      i32.add
      i32.const 0
      i32.store8
      local.get 0
      i32.const 0
      i32.store8 offset=3
      local.get 1
      i32.const 9
      i32.lt_u
      br_if 0 (;@1;)
      local.get 1
      i32.const 0
      local.get 0
      i32.sub
      i32.const 3
      i32.and
      local.tee 1
      i32.sub
      i32.const 2
      i32.shr_u
      local.tee 2
      i32.eqz
      br_if 0 (;@1;)
      i32.const 0
      local.get 2
      i32.sub
      local.set 2
      local.get 0
      local.get 1
      i32.add
      local.set 1
      loop ;; label = @2
        local.get 1
        i32.const 0
        i32.store
        local.get 1
        i32.const 4
        i32.add
        local.set 1
        local.get 2
        i32.const 1
        i32.add
        local.tee 0
        local.get 2
        i32.ge_u
        local.get 0
        local.set 2
        br_if 0 (;@2;)
      end
    end
  )
  (func (;14;) (type 1)
    i32.const 2848
    call 0
    i64.store
  )
  (func (;15;) (type 1)
    i32.const 2856
    call 0
    i64.store
  )
  (memory (;0;) 1)
  (global (;0;) (mut i32) i32.const 512)
  (export "memory" (memory 0))
  (export "run" (func 4))
  (data (;0;) (i32.const 512) "\b0\d4@3yj\14\e7\c1\e3R\be\99\11\08V\d7\1fG\07G^\bf9\a4\e5:\8e\84\8d\00\00`\02\00\00e\02\00\00j\02\00\00o\02\00\00t\02\00\00}\02\00\00\86\02\00\00\8f\02\00\00\98\02\00\00\a1\02\00\00\aa\02\00\00\b3\02\00\00\bc\02\00\00\c5\02\00\00\ce\02\00\00\d7\02\00\005012\001234\00-874\00+122\0035.54400\00.1234500\00-110.700\00+0.64400\005.500e+3\00-.123e-2\00-87e+832\00+0.6e-12\00T0.3e-1F\00-T.T++Tq\001T3.4e4z\0034.0e-T^\00 \02\00\00 \02\00\00 \02\00\000\02\00\000\02\00\00@\02\00\00@\02\00\00P\02\00\00\04\00\00\00\04\00\00\00\04\00\00\00\08\00\00\00\08\00\00\00\08\00\00\00\08\00\00\00\08\00\00\00\10\0b\00\00\14\0b\00\004\03\00\00\18\0b\00\00\1c\0b")
  (data (;1;) (i32.const 820) "f\00\00\00\01")
)
