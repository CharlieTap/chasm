.class public final Lchasm/jit/Code0;
.super Ljava/lang/Object;
.source "module.wasm"


# direct methods
.method public static f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V
    .registers 26

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v18, v15

    .line 2
    move/from16 v18, v17

    .line 3
    const/4 v8, 0x3

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v6

    long-to-int v11, v6

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v12

    move/from16 v0, v18

    int-to-long v0, v0

    add-int/lit16 v10, v11, 0x2

    aput-wide v0, v12, v10

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/16 v9, 0x9

    aget-wide v4, v12, v9

    long-to-int v8, v4

    const/4 v9, 0x2

    invoke-static {v14, v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_34

    return-void

    :goto_34
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    return-void
.end method

.method public static f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 35

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v8, 0x1

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openRoots(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v0

    move-wide/from16 v26, v0

    long-to-int v8, v0

    move/from16 v18, v8

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->saveStack(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)J

    move-result-wide v0

    move-wide/from16 v24, v0

    move/from16 v20, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v23, v15

    move-wide/from16 v21, v0

    .line 2
    if-eqz v20, :cond_4b

    .line 3
    const/16 v23, 0x7

    .line 4
    const/4 v8, 0x3

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v6

    long-to-int v11, v6

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v12

    move/from16 v0, v23

    int-to-long v0, v0

    add-int/lit16 v10, v11, 0x2

    aput-wide v0, v12, v10

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/16 v9, 0x9

    aget-wide v4, v12, v9

    long-to-int v8, v4

    const/4 v9, 0x2

    invoke-static {v14, v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_59

    .line 5
    :cond_4b
    move-wide/from16 v6, v26

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    const/4 v8, 0x1

    return v8

    .line 6
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 7
    const/4 v8, 0x0

    return v8

    :goto_59
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    move-wide/from16 v6, v26

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    const/4 v8, 0x0

    return v8
.end method

.method public static f10([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 26

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0xf

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_11

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    :cond_11
    move v13, v8

    move-object/from16 v18, v16

    move-object/from16 v19, v14

    move/from16 v20, v13

    move/from16 v17, p3

    .line 2
    move/from16 v21, v17

    invoke-static/range {v18 .. v21}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_27

    .line 3
    const/4 v8, 0x0

    return v8

    const/4 v8, 0x0

    return v8

    :cond_27
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const/4 v8, 0x0

    return v8
.end method

.method public static f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 35

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v8, 0x1

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openRoots(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v0

    move-wide/from16 v26, v0

    long-to-int v8, v0

    move/from16 v18, v8

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->saveStack(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)J

    move-result-wide v0

    move-wide/from16 v24, v0

    move/from16 v20, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v23, v15

    move-wide/from16 v21, v0

    .line 2
    :try_start_23
    move/from16 v8, v20

    const/4 v9, 0x5

    if-le v8, v9, :cond_4e

    .line 3
    move/from16 v23, v20

    .line 4
    const/4 v8, 0x3

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v6

    long-to-int v11, v6

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v12

    move/from16 v0, v23

    int-to-long v0, v0

    add-int/lit16 v10, v11, 0x2

    aput-wide v0, v12, v10

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/16 v9, 0x9

    aget-wide v4, v12, v9

    long-to-int v8, v4

    const/4 v9, 0x2

    invoke-static {v14, v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_74

    .line 5
    :cond_4e
    move/from16 v23, v20
    :try_end_50
    .catch Ljava/lang/Throwable; {:try_start_23 .. :try_end_50} :catch_69

    .line 6
    move-wide/from16 v6, v26

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move/from16 v8, v23

    return v8

    .line 7
    :goto_58
    move-wide/from16 v0, v21

    long-to-int v8, v0

    const/16 v9, 0x3e8

    add-int v20, v8, v9

    .line 8
    move-wide/from16 v6, v26

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move/from16 v8, v20

    return v8

    const/4 v8, 0x0

    return v8

    :catch_69
    move-exception v12

    invoke-static {v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->wasmException(Ljava/lang/Throwable;)Ljava/lang/Throwable;

    move-wide/from16 v6, v24

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_74

    :goto_74
    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->pendingTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v8

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/16 v9, 0x9

    aget-wide v4, v12, v9

    long-to-int v9, v4

    if-ne v8, v9, :cond_9a

    const/4 v9, 0x1

    new-array v12, v9, [J

    const/4 v9, 0x1

    invoke-static {v14, v12, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->takeException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)J

    move-result-wide v2

    const/4 v10, 0x0

    aget-wide v0, v12, v10

    move-wide/from16 v21, v0

    add-int/lit8 v19, v18, 0x0

    aput-wide v0, v17, v19

    goto :goto_58

    :cond_9a
    goto/16 :goto_9c

    :goto_9c
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    move-wide/from16 v6, v26

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    const/4 v8, 0x0

    return v8
.end method

.method public static f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)J
    .registers 44

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x13

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_12

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    :cond_12
    move v13, v8

    const/4 v8, 0x3

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openRoots(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v0

    move-wide/from16 v33, v0

    long-to-int v8, v0

    move/from16 v18, v8

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v35, v12

    move-object/from16 v36, v16

    move-object/from16 v37, v14

    move/from16 v38, v13

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->saveStack(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)J

    move-result-wide v0

    move-wide/from16 v31, v0

    move/from16 v22, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v23, v0

    move/from16 v27, v15

    move-wide/from16 v25, v0

    move/from16 v30, v15

    move-wide/from16 v28, v0

    .line 2
    :try_start_43
    move/from16 v8, v22

    const/4 v9, 0x1

    if-ne v8, v9, :cond_6b

    .line 3
    const/16 v27, 0xb

    .line 4
    const/4 v8, 0x4

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v6

    long-to-int v11, v6

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v12

    move/from16 v0, v27

    int-to-long v0, v0

    add-int/lit16 v10, v11, 0x3

    aput-wide v0, v12, v10

    move-object/from16 v12, v35

    const/16 v9, 0x9

    aget-wide v4, v12, v9

    long-to-int v8, v4

    const/4 v9, 0x3

    invoke-static {v14, v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_110

    .line 5
    :cond_6b
    move/from16 v8, v22

    const/4 v9, 0x2

    if-ne v8, v9, :cond_9f

    .line 6
    const-wide/16 v25, 0x16

    add-int/lit8 v19, v18, 0x1

    aput-wide v25, v17, v19

    .line 7
    const/16 v30, 0x3

    .line 8
    const/4 v8, 0x5

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v6

    long-to-int v11, v6

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v12

    move-wide/from16 v0, v25

    add-int/lit16 v10, v11, 0x3

    aput-wide v0, v12, v10

    move/from16 v0, v30

    int-to-long v0, v0

    add-int/lit16 v10, v11, 0x4

    aput-wide v0, v12, v10

    move-object/from16 v12, v35

    const/16 v9, 0xa

    aget-wide v4, v12, v9

    long-to-int v8, v4

    const/4 v9, 0x3

    invoke-static {v14, v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_110

    .line 9
    :cond_9f
    move/from16 v8, v22

    const/4 v9, 0x3

    if-ne v8, v9, :cond_af

    .line 10
    const/16 v39, 0x21

    invoke-static/range {v36 .. v39}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_110

    .line 11
    goto/16 :goto_af

    .line 12
    :cond_af
    :goto_af
    move/from16 v8, v22

    const/4 v9, 0x4

    if-ne v8, v9, :cond_c9

    .line 13
    const/4 v8, 0x0

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v6

    move-object/from16 v12, v35

    const/16 v9, 0xb

    aget-wide v4, v12, v9

    long-to-int v8, v4

    const/4 v9, 0x0

    invoke-static {v14, v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_110
    :try_end_c9
    .catch Ljava/lang/Throwable; {:try_start_43 .. :try_end_c9} :catch_ff

    .line 14
    :cond_c9
    move-wide/from16 v6, v33

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    const-wide/16 v0, -0x1

    return-wide v0

    .line 15
    :goto_d1
    move-wide/from16 v23, v25

    .line 16
    move-wide/from16 v25, v23

    .line 17
    move-wide/from16 v6, v33

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move-wide/from16 v0, v25

    return-wide v0

    .line 18
    :goto_dd
    move-wide/from16 v23, v28

    .line 19
    move-wide/from16 v28, v23

    .line 20
    add-long v20, v25, v28

    .line 21
    move-wide/from16 v6, v33

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move-wide/from16 v0, v20

    return-wide v0

    const-wide/16 v0, 0x0

    return-wide v0

    move-exception v12

    invoke-static {v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->wasmException(Ljava/lang/Throwable;)Ljava/lang/Throwable;

    move-object/from16 v36, v16

    move-object/from16 v37, v14

    move/from16 v38, v13

    move-wide/from16 v6, v31

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_13e

    :catch_ff
    move-exception v12

    invoke-static {v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->wasmException(Ljava/lang/Throwable;)Ljava/lang/Throwable;

    move-object/from16 v36, v16

    move-object/from16 v37, v14

    move/from16 v38, v13

    move-wide/from16 v6, v31

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_110

    :cond_110
    :goto_110
    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->pendingTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v8

    move-object/from16 v12, v35

    const/16 v9, 0xa

    aget-wide v4, v12, v9

    long-to-int v9, v4

    if-ne v8, v9, :cond_13c

    const/4 v9, 0x2

    new-array v12, v9, [J

    const/4 v9, 0x2

    invoke-static {v14, v12, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->takeException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)J

    move-result-wide v2

    const/4 v10, 0x0

    aget-wide v0, v12, v10

    move-wide/from16 v25, v0

    add-int/lit8 v19, v18, 0x1

    aput-wide v0, v17, v19

    const/4 v10, 0x1

    aget-wide v0, v12, v10

    move-wide/from16 v28, v0

    add-int/lit8 v19, v18, 0x2

    aput-wide v0, v17, v19

    goto :goto_dd

    :cond_13c
    goto/16 :goto_13e

    :goto_13e
    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->pendingTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v8

    move-object/from16 v12, v35

    const/16 v9, 0x9

    aget-wide v4, v12, v9

    long-to-int v9, v4

    if-ne v8, v9, :cond_168

    add-int/lit8 v8, v18, 0x2

    const/4 v9, 0x1

    invoke-static {v14, v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->clearRoots(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    const/4 v9, 0x1

    new-array v12, v9, [J

    const/4 v9, 0x1

    invoke-static {v14, v12, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->takeException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)J

    move-result-wide v2

    const/4 v10, 0x0

    aget-wide v0, v12, v10

    move-wide/from16 v25, v0

    add-int/lit8 v19, v18, 0x1

    aput-wide v0, v17, v19

    goto/16 :goto_d1

    :cond_168
    goto/16 :goto_16a

    :goto_16a
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    move-wide/from16 v6, v33

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    const-wide/16 v0, 0x0

    return-wide v0
.end method

.method public static f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 27

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->saveStack(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)J

    move-result-wide v0

    move-wide/from16 v18, v0

    move/from16 v17, p3

    .line 2
    :try_start_e
    if-eqz v17, :cond_28

    .line 3
    const/4 v8, 0x0

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v6

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/16 v9, 0xb

    aget-wide v4, v12, v9

    long-to-int v8, v4

    const/4 v9, 0x0

    invoke-static {v14, v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_43

    .line 4
    :cond_28
    move/from16 v8, v17

    const/4 v9, 0x2

    if-ne v8, v9, :cond_32

    .line 5
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
    :try_end_32
    .catch Ljava/lang/Throwable; {:try_start_e .. :try_end_32} :catch_38

    .line 6
    :cond_32
    const/4 v8, 0x0

    return v8

    .line 7
    :goto_34
    const/4 v8, 0x1

    return v8

    const/4 v8, 0x0

    return v8

    :catch_38
    move-exception v12

    invoke-static {v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->wasmException(Ljava/lang/Throwable;)Ljava/lang/Throwable;

    move-wide/from16 v6, v18

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_43

    :goto_43
    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->pendingTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v8

    const/4 v9, 0x1

    new-array v12, v9, [J

    const/4 v9, 0x0

    invoke-static {v14, v12, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->takeException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)J

    move-result-wide v2

    goto :goto_34

    goto/16 :goto_52

    :goto_52
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const/4 v8, 0x0

    return v8
.end method

.method public static f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V
    .registers 28

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x3

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v18, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17

    move-object/from16 v20, v12

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v19, v15

    .line 2
    move-object/from16 v22, v14

    move-object/from16 v2, v20

    move-object/from16 v23, v2

    const/16 v24, 0x1

    invoke-static/range {v22 .. v24}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->memoryGrow(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;I)J

    move-result-wide v0

    long-to-int v15, v0

    move/from16 v19, v15

    move-object/from16 v12, v20

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v18, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17

    .line 3
    const v8, 0x10004

    add-int/lit8 v11, v17, -0x4

    if-gt v8, v11, :cond_6d

    const/16 v9, 0x4d

    aput-byte v9, v18, v8

    ushr-int/lit8 v10, v9, 0x8

    add-int/lit16 v11, v8, 0x1

    aput-byte v10, v18, v11

    ushr-int/lit8 v10, v9, 0x10

    add-int/lit16 v11, v8, 0x2

    aput-byte v10, v18, v11

    ushr-int/lit8 v10, v9, 0x18

    add-int/lit16 v11, v8, 0x3

    aput-byte v10, v18, v11

    .line 4
    const/4 v8, 0x0

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v6

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/16 v9, 0xb

    aget-wide v4, v12, v9

    long-to-int v8, v4

    const/4 v9, 0x0

    invoke-static {v14, v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_69

    return-void

    :goto_69
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    return-void

    .line 3
    :cond_6d
    move-object/from16 v12, v20

    const/4 v10, 0x1

    const v9, 0x10004

    int-to-long v2, v9

    const-wide v4, 0xffffffffL

    and-long/2addr v2, v4

    const-wide/16 v4, 0x0

    add-long/2addr v2, v4

    const/4 v11, 0x4

    invoke-static {v12, v10, v2, v3, v11}, Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;->memoryAccessOutOfBounds(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;IJI)Ljava/lang/Void;

    return-void
.end method

.method public static f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
    .registers 31

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x3

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v18, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17

    move-object/from16 v24, v12

    add-int/lit16 v8, v13, 0x10

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_20

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    :cond_20
    move v13, v8

    move-object/from16 v25, v16

    move-object/from16 v26, v14

    move/from16 v27, v13

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->saveStack(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)J

    move-result-wide v0

    move-wide/from16 v22, v0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v19, v15

    move/from16 v20, v15

    move/from16 v21, v15

    .line 2
    :try_start_36
    invoke-static/range {v25 .. v27}, Lchasm/jit/Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_96

    move-object/from16 v12, v24

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v18, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17
    :try_end_47
    .catch Ljava/lang/Throwable; {:try_start_36 .. :try_end_47} :catch_85

    .line 3
    const/4 v8, -0x1

    return v8

    .line 4
    :goto_49
    move-object/from16 v2, v24

    move-object/from16 v26, v2

    invoke-static/range {v26 .. v26}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->memorySize(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;)J

    move-result-wide v0

    move-object/from16 v26, v14

    move/from16 v27, v13

    long-to-int v15, v0

    move/from16 v20, v15

    .line 5
    const v8, 0x10004

    add-int/lit8 v11, v17, -0x4

    if-gt v8, v11, :cond_c0

    aget-byte v9, v18, v8

    and-int/lit16 v9, v9, 0xff

    add-int/lit16 v11, v8, 0x1

    aget-byte v10, v18, v11

    and-int/lit16 v10, v10, 0xff

    shl-int/lit8 v10, v10, 0x8

    or-int/2addr v9, v10

    add-int/lit16 v11, v8, 0x2

    aget-byte v10, v18, v11

    and-int/lit16 v10, v10, 0xff

    shl-int/lit8 v10, v10, 0x10

    or-int/2addr v9, v10

    add-int/lit16 v11, v8, 0x3

    aget-byte v10, v18, v11

    shl-int/lit8 v10, v10, 0x18

    or-int/2addr v9, v10

    move/from16 v21, v9

    .line 6
    add-int v19, v20, v21

    .line 7
    move/from16 v8, v19

    return v8

    const/4 v8, 0x0

    return v8

    :catch_85
    move-exception v12

    invoke-static {v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->wasmException(Ljava/lang/Throwable;)Ljava/lang/Throwable;

    move-object/from16 v25, v16

    move-object/from16 v26, v14

    move/from16 v27, v13

    move-wide/from16 v6, v22

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_96

    :cond_96
    :goto_96
    move-object/from16 v12, v24

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v18, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->pendingTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v8

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/16 v9, 0xb

    aget-wide v4, v12, v9

    long-to-int v9, v4

    if-ne v8, v9, :cond_b9

    const/4 v9, 0x1

    new-array v12, v9, [J

    const/4 v9, 0x0

    invoke-static {v14, v12, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->takeException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)J

    move-result-wide v2

    goto :goto_49

    :cond_b9
    goto/16 :goto_bb

    :goto_bb
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const/4 v8, 0x0

    return v8

    .line 5
    :cond_c0
    move-object/from16 v12, v24

    const/4 v10, 0x0

    const v9, 0x10004

    int-to-long v2, v9

    const-wide v4, 0xffffffffL

    and-long/2addr v2, v4

    const-wide/16 v4, 0x0

    add-long/2addr v2, v4

    const/4 v11, 0x4

    invoke-static {v12, v10, v2, v3, v11}, Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;->memoryAccessOutOfBounds(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;IJI)Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 42

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v8, 0x3

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openRoots(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v0

    move-wide/from16 v31, v0

    long-to-int v8, v0

    move/from16 v18, v8

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v34, v12

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->saveStack(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)J

    move-result-wide v0

    move-wide/from16 v29, v0

    move/from16 v20, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v21, v15

    const-wide/16 v2, 0x501

    move-wide/from16 v22, v2

    move/from16 v26, v15

    move-wide/from16 v24, v0

    move-wide/from16 v27, v0

    const/4 v8, 0x0

    move-object/from16 v33, v8

    .line 2
    :try_start_35
    move/from16 v26, v20

    .line 3
    const/4 v8, 0x5

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v6

    long-to-int v11, v6

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v12

    move/from16 v0, v26

    int-to-long v0, v0

    add-int/lit16 v10, v11, 0x4

    aput-wide v0, v12, v10

    move-object/from16 v12, v34

    const/16 v9, 0x9

    aget-wide v4, v12, v9

    long-to-int v8, v4

    const/4 v9, 0x4

    invoke-static {v14, v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_9d
    :try_end_58
    .catch Ljava/lang/Throwable; {:try_start_35 .. :try_end_58} :catch_92

    .line 4
    :goto_58
    move-wide/from16 v22, v27

    add-int/lit8 v19, v18, 0x0

    aput-wide v22, v17, v19

    .line 6
    :goto_5e
    const/high16 v10, -0x80000000

    add-int v8, v21, v10

    const v9, -0x7ffff830

    if-ge v8, v9, :cond_87

    .line 7
    move/from16 v26, v21

    .line 8
    move-object/from16 v12, v34

    const/4 v9, 0x0

    aget-wide v4, v12, v9

    long-to-int v11, v4

    move-object/from16 v12, v33

    if-nez v12, :cond_78

    const/4 v8, 0x1

    new-array v12, v8, [J

    move-object/from16 v33, v12

    :cond_78
    move/from16 v0, v26

    int-to-long v0, v0

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    invoke-static {v14, v11, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structNew(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J)J

    move-result-wide v0

    move-wide/from16 v24, v0

    .line 9
    add-int/lit8 v21, v21, 0x1

    .line 10
    goto :goto_5e

    .line 11
    :cond_87
    move-wide/from16 v24, v22

    .line 12
    move-wide/from16 v0, v24

    invoke-static {v14, v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwRef(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_c8

    const/4 v8, 0x0

    return v8

    :catch_92
    move-exception v12

    invoke-static {v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->wasmException(Ljava/lang/Throwable;)Ljava/lang/Throwable;

    move-wide/from16 v6, v29

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_9d

    :goto_9d
    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->pendingTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v8

    move-object/from16 v12, v34

    const/16 v9, 0x9

    aget-wide v4, v12, v9

    long-to-int v9, v4

    if-ne v8, v9, :cond_c6

    const/4 v9, 0x1

    new-array v12, v9, [J

    const/4 v9, 0x1

    invoke-static {v14, v12, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->takeException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)J

    move-result-wide v2

    const/4 v10, 0x0

    aget-wide v0, v12, v10

    move-wide/from16 v24, v0

    add-int/lit8 v19, v18, 0x1

    aput-wide v0, v17, v19

    move-wide/from16 v27, v2

    add-int/lit8 v19, v18, 0x2

    aput-wide v2, v17, v19

    goto :goto_58

    :cond_c6
    goto/16 :goto_c8

    :goto_c8
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    move-wide/from16 v6, v31

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    const/4 v8, 0x0

    return v8
.end method

.method public static f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 36

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x11

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_11

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    :cond_11
    move v13, v8

    const/4 v8, 0x1

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openRoots(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v0

    move-wide/from16 v26, v0

    long-to-int v8, v0

    move/from16 v18, v8

    move-object/from16 v28, v16

    move-object/from16 v29, v14

    move/from16 v30, v13

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->saveStack(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)J

    move-result-wide v0

    move-wide/from16 v24, v0

    move/from16 v20, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v23, v15

    move-wide/from16 v21, v0

    .line 2
    :try_start_31
    move/from16 v31, v20

    invoke-static/range {v28 .. v31}, Lchasm/jit/Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v9

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_65

    move/from16 v23, v9
    :try_end_3d
    .catch Ljava/lang/Throwable; {:try_start_31 .. :try_end_3d} :catch_54

    .line 3
    move-wide/from16 v6, v26

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move/from16 v8, v23

    return v8

    .line 4
    :goto_45
    move-wide/from16 v0, v21

    long-to-int v8, v0

    add-int/lit8 v20, v8, 0x5

    .line 5
    move-wide/from16 v6, v26

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move/from16 v8, v20

    return v8

    const/4 v8, 0x0

    return v8

    :catch_54
    move-exception v12

    invoke-static {v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->wasmException(Ljava/lang/Throwable;)Ljava/lang/Throwable;

    move-object/from16 v28, v16

    move-object/from16 v29, v14

    move/from16 v30, v13

    move-wide/from16 v6, v24

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_65

    :cond_65
    :goto_65
    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->pendingTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v8

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/16 v9, 0x9

    aget-wide v4, v12, v9

    long-to-int v9, v4

    if-ne v8, v9, :cond_8b

    const/4 v9, 0x1

    new-array v12, v9, [J

    const/4 v9, 0x1

    invoke-static {v14, v12, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->takeException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)J

    move-result-wide v2

    const/4 v10, 0x0

    aget-wide v0, v12, v10

    move-wide/from16 v21, v0

    add-int/lit8 v19, v18, 0x0

    aput-wide v0, v17, v19

    goto :goto_45

    :cond_8b
    goto/16 :goto_8d

    :goto_8d
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    move-wide/from16 v6, v26

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    const/4 v8, 0x0

    return v8
.end method

.method public static f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V
    .registers 25

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v17, v0

    .line 2
    const-wide/16 v0, 0x501

    move-wide/from16 v17, v0

    .line 3
    move-wide/from16 v0, v17

    invoke-static {v14, v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwRef(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_17

    return-void

    :goto_17
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    return-void
.end method

.class public final Lchasm/jit/Entry0;
.super Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;


# direct methods
.method public constructor <init>([Ljava/lang/Object;I)V
    .registers 3

    invoke-direct {p0, p1, p2}, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;-><init>([Ljava/lang/Object;I)V

    return-void
.end method


# virtual methods
.method public call(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 13

    :try_start_0
    move-object v2, p0

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p3

    const/4 v3, 0x0

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v8, v2

    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V
    :try_end_d
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_d} :catch_e

    return-void

    :catch_e
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void
.end method

.method public invoke(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 13

    :try_start_0
    move-object v2, p0

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    invoke-static {v6}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->depth(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v7

    add-int/lit16 v3, p3, 0x0

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v8, v2

    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V
    :try_end_11
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_11} :catch_12

    return-void

    :catch_12
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void
.end method

.class public final Lchasm/jit/Entry1;
.super Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;


# direct methods
.method public constructor <init>([Ljava/lang/Object;I)V
    .registers 3

    invoke-direct {p0, p1, p2}, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;-><init>([Ljava/lang/Object;I)V

    return-void
.end method


# virtual methods
.method public call(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 13

    :try_start_0
    move-object v2, p0

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p3

    const/4 v3, 0x0

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v8, v2

    packed-switch v4, :pswitch_data_42

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_14

    :goto_14
    :pswitch_14
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_38

    :pswitch_1a
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_38

    :pswitch_20
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_38

    :pswitch_26
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_38

    :pswitch_2c
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_38

    :pswitch_32
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f10([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_38

    :goto_38
    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_3c
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_3c} :catch_3d

    return-void

    :catch_3d
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    nop

    :pswitch_data_42
    .packed-switch 0x0
        :pswitch_14
        :pswitch_1a
        :pswitch_20
        :pswitch_26
        :pswitch_2c
        :pswitch_32
    .end packed-switch
.end method

.method public invoke(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 13

    :try_start_0
    move-object v2, p0

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    invoke-static {v6}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->depth(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v7

    add-int/lit16 v3, p3, 0x0

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v8, v2

    packed-switch v4, :pswitch_data_48

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_18

    :goto_18
    :pswitch_18
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_3c

    :pswitch_1e
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_3c

    :pswitch_24
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_3c

    :pswitch_2a
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_3c

    :pswitch_30
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_3c

    :pswitch_36
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f10([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_3c

    :goto_3c
    int-to-long v0, v2

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_43
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_43} :catch_44

    return-void

    :catch_44
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_48
    .packed-switch 0x0
        :pswitch_18
        :pswitch_1e
        :pswitch_24
        :pswitch_2a
        :pswitch_30
        :pswitch_36
    .end packed-switch
.end method

.class public final Lchasm/jit/Entry2;
.super Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;


# direct methods
.method public constructor <init>([Ljava/lang/Object;I)V
    .registers 3

    invoke-direct {p0, p1, p2}, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;-><init>([Ljava/lang/Object;I)V

    return-void
.end method


# virtual methods
.method public call(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 13

    :try_start_0
    move-object v2, p0

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p3

    const/4 v3, 0x0

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v8, v2

    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)J

    move-result-wide v0

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_11
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_11} :catch_12

    return-void

    :catch_12
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void
.end method

.method public invoke(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 13

    :try_start_0
    move-object v2, p0

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    invoke-static {v6}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->depth(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v7

    add-int/lit16 v3, p3, 0x0

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v8, v2

    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)J

    move-result-wide v0

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_18
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_18} :catch_19

    return-void

    :catch_19
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void
.end method

.class public final Lchasm/jit/Entry3;
.super Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;


# direct methods
.method public constructor <init>([Ljava/lang/Object;I)V
    .registers 3

    invoke-direct {p0, p1, p2}, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;-><init>([Ljava/lang/Object;I)V

    return-void
.end method


# virtual methods
.method public call(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 12

    :try_start_0
    move-object v2, p0

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p3

    packed-switch v4, :pswitch_data_1e

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_f

    :goto_f
    :pswitch_f
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    goto/16 :goto_19

    :pswitch_14
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    goto/16 :goto_19
    :try_end_19
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_19} :catch_1a

    :goto_19
    return-void

    :catch_1a
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_1e
    .packed-switch 0x0
        :pswitch_f
        :pswitch_14
    .end packed-switch
.end method

.method public invoke(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 12

    :try_start_0
    move-object v2, p0

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    invoke-static {v6}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->depth(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v7

    packed-switch v4, :pswitch_data_22

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_12

    :goto_12
    :pswitch_12
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    goto/16 :goto_1c

    :pswitch_17
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    goto/16 :goto_1c
    :try_end_1c
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_1c} :catch_1d

    :goto_1c
    return-void

    :catch_1d
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    nop

    :pswitch_data_22
    .packed-switch 0x0
        :pswitch_12
        :pswitch_17
    .end packed-switch
.end method

.class public final Lchasm/jit/Entry4;
.super Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;


# direct methods
.method public constructor <init>([Ljava/lang/Object;I)V
    .registers 3

    invoke-direct {p0, p1, p2}, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;-><init>([Ljava/lang/Object;I)V

    return-void
.end method


# virtual methods
.method public call(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 12

    :try_start_0
    move-object v2, p0

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p3

    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_d
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_d} :catch_e

    return-void

    :catch_e
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void
.end method

.method public invoke(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 12

    :try_start_0
    move-object v2, p0

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    invoke-static {v6}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->depth(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v7

    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    int-to-long v0, v2

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_13
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_13} :catch_14

    return-void

    :catch_14
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void
.end method

.class public final Lchasm/jit/Install;
.super Ljava/lang/Object;

# interfaces
.implements Ljava/util/function/Consumer;


# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method private static bindObjects0(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V
    .registers 8

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->memory(I)Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    move-result-object v4

    const/4 v2, 0x3

    aput-object v4, p1, v2

    return-void
.end method

.method private static bindValues0(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V
    .registers 8

    const/4 v2, 0x0

    aget-object v5, p1, v2

    check-cast v5, [J

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->runtimeType(I)I

    move-result v3

    int-to-long v0, v3

    const/4 v2, 0x0

    aput-wide v0, v5, v2

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->tagAddress(I)I

    move-result v3

    int-to-long v0, v3

    const/16 v2, 0x9

    aput-wide v0, v5, v2

    const/4 v2, 0x1

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->tagAddress(I)I

    move-result v3

    int-to-long v0, v3

    const/16 v2, 0xa

    aput-wide v0, v5, v2

    const/4 v2, 0x2

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->tagAddress(I)I

    move-result v3

    int-to-long v0, v3

    const/16 v2, 0xb

    aput-wide v0, v5, v2

    return-void
.end method


# virtual methods
.method public accept(Ljava/lang/Object;)V
    .registers 5

    check-cast p1, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;

    const/4 v0, 0x4

    new-array v1, v0, [Ljava/lang/Object;

    const/16 v0, 0xc

    new-array v2, v0, [J

    const/4 v0, 0x0

    aput-object v2, v1, v0

    invoke-static {p1, v1}, Lchasm/jit/Install;->bindObjects0(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V

    invoke-static {p1, v1}, Lchasm/jit/Install;->bindValues0(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V

    invoke-static {p1, v1}, Lchasm/jit/InstallEntries0;->installEntries0(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V

    return-void
.end method

.class final Lchasm/jit/InstallEntries0;
.super Ljava/lang/Object;


# direct methods
.method static installEntries0(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V
    .registers 8

    invoke-static {p0, p1}, Lchasm/jit/InstallEntries0;->installEntries1(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V

    return-void
.end method

.method private static installEntries1(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V
    .registers 8

    new-instance v4, Lchasm/jit/Entry0;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry0;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x0

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry1;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry1;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x1

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry1;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry1;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x2

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x3

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry1;

    const/4 v3, 0x2

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry1;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x4

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry3;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry3;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x5

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry4;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry4;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x6

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry1;

    const/4 v3, 0x3

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry1;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x7

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry1;

    const/4 v3, 0x4

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry1;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x8

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry3;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry3;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x9

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry1;

    const/4 v3, 0x5

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry1;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xa

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    return-void
.end method
