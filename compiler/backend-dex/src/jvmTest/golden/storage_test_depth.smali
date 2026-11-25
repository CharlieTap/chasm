.method public static f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I
    .registers 27

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move-object/from16 v18, p3

    move-object/from16 v19, v18

    move-object/from16 v12, v19

    if-eqz v12, :cond_2a

    instance-of v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    if-eqz v8, :cond_2a

    move-object v11, v12

    check-cast v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;->rtt:Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    const/4 v9, 0x6

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    if-eq v11, v10, :cond_2d

    iget v8, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;->depth:I

    const/4 v9, 0x2

    if-lt v8, v9, :cond_2a

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;->supertypes:[Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    const/4 v9, 0x2

    aget-object v11, v11, v9

    if-eq v11, v10, :cond_2d

    :cond_2a
    const/4 v11, 0x0

    goto/16 :goto_2e

    :cond_2d
    const/4 v11, 0x1

    :goto_2e
    move/from16 v17, v11

    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8
.end method
