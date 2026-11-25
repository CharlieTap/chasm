.method public static f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;Ljava/lang/Object;)I
    .registers 33

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/16 v9, 0x9

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    move-object/from16 v24, v12

    move-object/from16 v18, p3

    move-object/from16 v19, p4

    move-object/from16 v21, v18

    move-object/from16 v12, v21

    if-eqz v12, :cond_25

    instance-of v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    if-eqz v8, :cond_25

    move-object v11, v12

    check-cast v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;->rtt:Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    move-object/from16 v10, v24

    if-eq v11, v10, :cond_28

    :cond_25
    const/4 v11, 0x0

    goto/16 :goto_29

    :cond_28
    const/4 v11, 0x1

    :goto_29
    move/from16 v20, v11

    move-object/from16 v23, v19

    move-object/from16 v12, v23

    if-eqz v12, :cond_3e

    instance-of v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    if-eqz v8, :cond_3e

    move-object v11, v12

    check-cast v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;->rtt:Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    move-object/from16 v10, v24

    if-eq v11, v10, :cond_41

    :cond_3e
    const/4 v11, 0x0

    goto/16 :goto_42

    :cond_41
    const/4 v11, 0x1

    :goto_42
    move/from16 v22, v11

    add-int v17, v20, v22

    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8
.end method
