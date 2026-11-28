.method public static f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I
    .registers 26

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move-object/from16 v18, p3

    move-object/from16 v12, v18

    if-eqz v12, :cond_17

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R2__I_IR_IR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v17, v8

    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    :cond_17
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method
