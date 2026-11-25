.method public static f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)Ljava/lang/Object;
    .registers 26

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move-object/from16 v17, p3

    move-object/from16 v18, v17

    move-object/from16 v12, v18

    if-eqz v12, :cond_1f

    instance-of v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    if-eqz v8, :cond_1f

    move-object v11, v12

    check-cast v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;->rtt:Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    const/16 v9, 0x9

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    if-eq v11, v10, :cond_24

    :cond_1f
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->castFailed()Ljava/lang/Void;

    const/4 v12, 0x0

    return-object v12

    :cond_24
    move-object/from16 v17, v12

    move-object/from16 v12, v17

    return-object v12

    const/4 v12, 0x0

    return-object v12
.end method
