.method public static f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;I)V
    .registers 31

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move-object/from16 v17, p3

    move/from16 v18, p4

    move-object/from16 v12, v17

    if-eqz v12, :cond_18

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_IR;

    move/from16 v8, v18

    and-int/lit16 v8, v8, 0xff

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I;->i0:I

    return-void

    return-void

    :cond_18
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    return-void
.end method
