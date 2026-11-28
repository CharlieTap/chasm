.method public static f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)J
    .registers 27

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move-object/from16 v19, p3

    move-object/from16 v12, v19

    if-eqz v12, :cond_26

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R2__I_II_RR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;->i1:I

    iget v9, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;->i2:I

    int-to-long v0, v8

    const-wide v2, 0xffffffffL

    and-long/2addr v0, v2

    int-to-long v2, v9

    const/16 v15, 0x20

    shl-long/2addr v2, v15

    or-long/2addr v0, v2

    move-wide/from16 v17, v0

    move-wide/from16 v0, v17

    return-wide v0

    const-wide/16 v0, 0x0

    return-wide v0

    :cond_26
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0
.end method
