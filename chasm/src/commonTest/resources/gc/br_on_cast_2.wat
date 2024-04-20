(module
  (type $t0 (sub (struct)))
  (type $t1 (sub $t0 (struct (field i32))))
  (type $t1' (sub $t0 (struct (field i32))))
  (type $t2 (sub $t1 (struct (field i32 i32))))
  (type $t2' (sub $t1' (struct (field i32 i32))))
  (type $t3 (sub $t0 (struct (field i32 i32))))
  (type $t0' (sub $t0 (struct)))
  (type $t4 (sub $t0' (struct (field i32 i32))))

  (table 20 structref)

  (func $init
    (table.set (i32.const 0) (struct.new_default $t0))
    (table.set (i32.const 10) (struct.new_default $t0'))
    (table.set (i32.const 1) (struct.new_default $t1))
    (table.set (i32.const 11) (struct.new_default $t1'))
    (table.set (i32.const 2) (struct.new_default $t2))
    (table.set (i32.const 12) (struct.new_default $t2'))
    (table.set (i32.const 3) (struct.new_default $t3))
    (table.set (i32.const 4) (struct.new_default $t4))
  )

  (func (export "test-sub")
    (call $init)
    (block $l (result structref)
      ;; must succeed
      (drop (block (result structref) (br_on_cast 0 structref (ref $t0) (ref.null struct))))
      (drop (block (result structref) (br_on_cast 0 structref (ref $t0) (table.get (i32.const 0)))))
      (drop (block (result structref) (br_on_cast 0 structref (ref $t0) (table.get (i32.const 1)))))
      (drop (block (result structref) (br_on_cast 0 structref (ref $t0) (table.get (i32.const 2)))))
      (drop (block (result structref) (br_on_cast 0 structref (ref $t0) (table.get (i32.const 3)))))
      (drop (block (result structref) (br_on_cast 0 structref (ref $t0) (table.get (i32.const 4)))))

      (drop (block (result structref) (br_on_cast 0 structref (ref $t1) (ref.null struct))))
      (drop (block (result structref) (br_on_cast 0 structref (ref $t1) (table.get (i32.const 1)))))
      (drop (block (result structref) (br_on_cast 0 structref (ref $t1) (table.get (i32.const 2)))))

      (drop (block (result structref) (br_on_cast 0 structref (ref $t2) (ref.null struct))))
      (drop (block (result structref) (br_on_cast 0 structref (ref $t2) (table.get (i32.const 2)))))

      (drop (block (result structref) (br_on_cast 0 structref (ref $t3) (ref.null struct))))
      (drop (block (result structref) (br_on_cast 0 structref (ref $t3) (table.get (i32.const 3)))))

      (drop (block (result structref) (br_on_cast 0 structref (ref $t4) (ref.null struct))))
      (drop (block (result structref) (br_on_cast 0 structref (ref $t4) (table.get (i32.const 4)))))

      ;; must not succeed
      (br_on_cast $l anyref (ref $t1) (table.get (i32.const 0)))
      (br_on_cast $l anyref (ref $t1) (table.get (i32.const 3)))
      (br_on_cast $l anyref (ref $t1) (table.get (i32.const 4)))

      (br_on_cast $l anyref (ref $t2) (table.get (i32.const 0)))
      (br_on_cast $l anyref (ref $t2) (table.get (i32.const 1)))
      (br_on_cast $l anyref (ref $t2) (table.get (i32.const 3)))
      (br_on_cast $l anyref (ref $t2) (table.get (i32.const 4)))

      (br_on_cast $l anyref (ref $t3) (table.get (i32.const 0)))
      (br_on_cast $l anyref (ref $t3) (table.get (i32.const 1)))
      (br_on_cast $l anyref (ref $t3) (table.get (i32.const 2)))
      (br_on_cast $l anyref (ref $t3) (table.get (i32.const 4)))

      (br_on_cast $l anyref (ref $t4) (table.get (i32.const 0)))
      (br_on_cast $l anyref (ref $t4) (table.get (i32.const 1)))
      (br_on_cast $l anyref (ref $t4) (table.get (i32.const 2)))
      (br_on_cast $l anyref (ref $t4) (table.get (i32.const 3)))

      (return)
    )
    (unreachable)
  )

  (func (export "test-canon")
    (call $init)
    (block $l
      (drop (block (result structref) (br_on_cast 0 structref (ref $t0') (table.get (i32.const 0)))))
      (drop (block (result structref) (br_on_cast 0 structref (ref $t0') (table.get (i32.const 1)))))
      (drop (block (result structref) (br_on_cast 0 structref (ref $t0') (table.get (i32.const 2)))))
      (drop (block (result structref) (br_on_cast 0 structref (ref $t0') (table.get (i32.const 3)))))
      (drop (block (result structref) (br_on_cast 0 structref (ref $t0') (table.get (i32.const 4)))))

      (drop (block (result structref) (br_on_cast 0 structref (ref $t0) (table.get (i32.const 10)))))
      (drop (block (result structref) (br_on_cast 0 structref (ref $t0) (table.get (i32.const 11)))))
      (drop (block (result structref) (br_on_cast 0 structref (ref $t0) (table.get (i32.const 12)))))

      (drop (block (result structref) (br_on_cast 0 structref (ref $t1') (table.get (i32.const 1)))))
      (drop (block (result structref) (br_on_cast 0 structref (ref $t1') (table.get (i32.const 2)))))

      (drop (block (result structref) (br_on_cast 0 structref (ref $t1) (table.get (i32.const 11)))))
      (drop (block (result structref) (br_on_cast 0 structref (ref $t1) (table.get (i32.const 12)))))

      (drop (block (result structref) (br_on_cast 0 structref (ref $t2') (table.get (i32.const 2)))))

      (drop (block (result structref) (br_on_cast 0 structref (ref $t2) (table.get (i32.const 12)))))

      (return)
    )
    (unreachable)
  )
)
