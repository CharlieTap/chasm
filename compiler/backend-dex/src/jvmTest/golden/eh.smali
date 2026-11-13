.class public final Lchasm/jit/Code0;
.super Ljava/lang/Object;
.source "module.wasm"


# direct methods
.method public static f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V
    .registers 28

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0xf

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_10

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :cond_10
    move v13, v8

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v18, v0

    move/from16 v20, v15

    .line 2
    move/from16 v20, v17

    .line 3
    const/4 v8, 0x3

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v6

    long-to-int v11, v6

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v12

    move/from16 v0, v20

    int-to-long v0, v0

    add-int/lit16 v10, v11, 0x2

    aput-wide v0, v12, v10

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x0

    aget-wide v4, v12, v9

    long-to-int v8, v4

    const/4 v9, 0x2

    invoke-static {v14, v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_40

    return-void

    :goto_40
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    return-void
.end method

.method public static f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 38

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x12

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

    move-wide/from16 v29, v0

    long-to-int v8, v0

    move/from16 v18, v8

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->saveStack(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)J

    move-result-wide v0

    move-wide/from16 v27, v0

    move/from16 v20, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v21, v0

    move-wide/from16 v23, v0

    move-wide/from16 v25, v0

    .line 2
    if-eqz v20, :cond_5b

    .line 3
    const-wide/16 v23, 0x7

    add-int/lit8 v19, v18, 0x0

    aput-wide v23, v17, v19

    .line 4
    const/4 v8, 0x3

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v6

    long-to-int v11, v6

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v12

    move-wide/from16 v0, v23

    add-int/lit16 v10, v11, 0x2

    aput-wide v0, v12, v10

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x0

    aget-wide v4, v12, v9

    long-to-int v8, v4

    const/4 v9, 0x2

    invoke-static {v14, v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_71

    .line 5
    :cond_5b
    move-wide/from16 v6, v29

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    const/4 v8, 0x1

    return v8

    .line 6
    move-wide/from16 v0, v23

    long-to-int v8, v0

    add-int/lit8 v20, v8, 0x64

    .line 7
    move-wide/from16 v6, v29

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move/from16 v8, v20

    return v8

    const/4 v8, 0x0

    return v8

    :goto_71
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    move-wide/from16 v6, v29

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    const/4 v8, 0x0

    return v8
.end method

.method public static f10([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 33

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

    move-object/from16 v25, v16

    move-object/from16 v26, v14

    move/from16 v27, v13

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v18, v0

    move-wide/from16 v20, v0

    move-wide/from16 v22, v0

    const/4 v8, 0x0

    move-object/from16 v24, v8

    .line 2
    move/from16 v28, v17

    invoke-static/range {v25 .. v28}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_33

    .line 3
    const/4 v8, 0x0

    return v8

    const/4 v8, 0x0

    return v8

    :cond_33
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const/4 v8, 0x0

    return v8
.end method

.method public static f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 38

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x12

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

    move-wide/from16 v29, v0

    long-to-int v8, v0

    move/from16 v18, v8

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->saveStack(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)J

    move-result-wide v0

    move-wide/from16 v27, v0

    move/from16 v20, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v21, v0

    move-wide/from16 v23, v0

    move-wide/from16 v25, v0

    .line 2
    :try_start_31
    move/from16 v8, v20

    const/4 v9, 0x5

    if-le v8, v9, :cond_61

    .line 3
    move/from16 v0, v20

    int-to-long v0, v0

    move-wide/from16 v23, v0

    add-int/lit8 v19, v18, 0x0

    aput-wide v0, v17, v19

    .line 4
    const/4 v8, 0x3

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v6

    long-to-int v11, v6

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v12

    move-wide/from16 v0, v23

    add-int/lit16 v10, v11, 0x2

    aput-wide v0, v12, v10

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x0

    aget-wide v4, v12, v9

    long-to-int v8, v4

    const/4 v9, 0x2

    invoke-static {v14, v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_8b

    .line 5
    :cond_61
    move/from16 v0, v20

    int-to-long v0, v0

    move-wide/from16 v23, v0
    :try_end_66
    .catch Ljava/lang/Throwable; {:try_start_31 .. :try_end_66} :catch_80

    .line 6
    move-wide/from16 v6, v29

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move-wide/from16 v0, v23

    long-to-int v8, v0

    return v8

    .line 7
    :goto_6f
    move-wide/from16 v0, v23

    long-to-int v8, v0

    const/16 v9, 0x3e8

    add-int v20, v8, v9

    .line 8
    move-wide/from16 v6, v29

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move/from16 v8, v20

    return v8

    const/4 v8, 0x0

    return v8

    :catch_80
    move-exception v12

    invoke-static {v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->wasmException(Ljava/lang/Throwable;)Ljava/lang/Throwable;

    move-wide/from16 v6, v27

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_8b

    :goto_8b
    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->pendingTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v8

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x0

    aget-wide v4, v12, v9

    long-to-int v9, v4

    if-ne v8, v9, :cond_b0

    const/4 v9, 0x1

    new-array v12, v9, [J

    const/4 v9, 0x1

    invoke-static {v14, v12, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->takeException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)J

    move-result-wide v2

    const/4 v10, 0x0

    aget-wide v0, v12, v10

    move-wide/from16 v23, v0

    add-int/lit8 v19, v18, 0x0

    aput-wide v0, v17, v19

    goto :goto_6f

    :cond_b0
    goto/16 :goto_b2

    :goto_b2
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    move-wide/from16 v6, v29

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

    move-wide/from16 v32, v0

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

    move-wide/from16 v30, v0

    move/from16 v8, p3

    int-to-long v0, v8

    move-wide/from16 v20, v0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v22, v0

    move-wide/from16 v24, v0

    move-wide/from16 v26, v0

    move-wide/from16 v28, v0

    const/4 v8, 0x0

    move-object/from16 v34, v8

    .line 2
    :try_start_47
    move-wide/from16 v0, v20

    long-to-int v8, v0

    const/4 v9, 0x1

    if-ne v8, v9, :cond_72

    .line 3
    const-wide/16 v26, 0xb

    add-int/lit8 v19, v18, 0x1

    aput-wide v26, v17, v19

    .line 4
    const/4 v8, 0x4

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v6

    long-to-int v11, v6

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v12

    move-wide/from16 v0, v26

    add-int/lit16 v10, v11, 0x3

    aput-wide v0, v12, v10

    move-object/from16 v12, v35

    const/4 v9, 0x0

    aget-wide v4, v12, v9

    long-to-int v8, v4

    const/4 v9, 0x3

    invoke-static {v14, v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_11f

    .line 5
    :cond_72
    move-wide/from16 v0, v20

    long-to-int v8, v0

    const/4 v9, 0x2

    if-ne v8, v9, :cond_a9

    .line 6
    const-wide/16 v26, 0x16

    add-int/lit8 v19, v18, 0x1

    aput-wide v26, v17, v19

    .line 7
    const-wide/16 v28, 0x3

    add-int/lit8 v19, v18, 0x2

    aput-wide v28, v17, v19

    .line 8
    const/4 v8, 0x5

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v6

    long-to-int v11, v6

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v12

    move-wide/from16 v0, v26

    add-int/lit16 v10, v11, 0x3

    aput-wide v0, v12, v10

    move-wide/from16 v0, v28

    add-int/lit16 v10, v11, 0x4

    aput-wide v0, v12, v10

    move-object/from16 v12, v35

    const/4 v9, 0x1

    aget-wide v4, v12, v9

    long-to-int v8, v4

    const/4 v9, 0x3

    invoke-static {v14, v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_11f

    .line 9
    :cond_a9
    move-wide/from16 v0, v20

    long-to-int v8, v0

    const/4 v9, 0x3

    if-ne v8, v9, :cond_be

    .line 10
    const/16 v39, 0x21

    invoke-static/range {v36 .. v39}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_11f

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    .line 11
    goto/16 :goto_be

    .line 12
    :cond_be
    :goto_be
    move-wide/from16 v0, v20

    long-to-int v8, v0

    const/4 v9, 0x4

    if-ne v8, v9, :cond_d8

    .line 13
    const/4 v8, 0x0

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v6

    move-object/from16 v12, v35

    const/4 v9, 0x2

    aget-wide v4, v12, v9

    long-to-int v8, v4

    const/4 v9, 0x0

    invoke-static {v14, v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_11f
    :try_end_d8
    .catch Ljava/lang/Throwable; {:try_start_47 .. :try_end_d8} :catch_10e

    .line 14
    :cond_d8
    move-wide/from16 v6, v32

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    const-wide/16 v0, -0x1

    return-wide v0

    .line 15
    :goto_e0
    move-wide/from16 v24, v26

    .line 16
    move-wide/from16 v26, v24

    .line 17
    move-wide/from16 v6, v32

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move-wide/from16 v0, v26

    return-wide v0

    .line 18
    :goto_ec
    move-wide/from16 v24, v28

    .line 19
    move-wide/from16 v28, v24

    .line 20
    add-long v20, v26, v28

    .line 21
    move-wide/from16 v6, v32

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

    move-wide/from16 v6, v30

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_14c

    :catch_10e
    move-exception v12

    invoke-static {v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->wasmException(Ljava/lang/Throwable;)Ljava/lang/Throwable;

    move-object/from16 v36, v16

    move-object/from16 v37, v14

    move/from16 v38, v13

    move-wide/from16 v6, v30

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_11f

    :cond_11f
    :goto_11f
    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->pendingTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v8

    move-object/from16 v12, v35

    const/4 v9, 0x1

    aget-wide v4, v12, v9

    long-to-int v9, v4

    if-ne v8, v9, :cond_14a

    const/4 v9, 0x2

    new-array v12, v9, [J

    const/4 v9, 0x2

    invoke-static {v14, v12, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->takeException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)J

    move-result-wide v2

    const/4 v10, 0x0

    aget-wide v0, v12, v10

    move-wide/from16 v26, v0

    add-int/lit8 v19, v18, 0x1

    aput-wide v0, v17, v19

    const/4 v10, 0x1

    aget-wide v0, v12, v10

    move-wide/from16 v28, v0

    add-int/lit8 v19, v18, 0x2

    aput-wide v0, v17, v19

    goto :goto_ec

    :cond_14a
    goto/16 :goto_14c

    :goto_14c
    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->pendingTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v8

    move-object/from16 v12, v35

    const/4 v9, 0x0

    aget-wide v4, v12, v9

    long-to-int v9, v4

    if-ne v8, v9, :cond_177

    const/4 v9, 0x1

    new-array v12, v9, [J

    const/4 v9, 0x1

    invoke-static {v14, v12, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->takeException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)J

    move-result-wide v2

    const/4 v10, 0x0

    aget-wide v0, v12, v10

    move-wide/from16 v26, v0

    add-int/lit8 v19, v18, 0x1

    aput-wide v0, v17, v19

    const-wide/16 v0, 0x0

    move-wide/from16 v28, v0

    add-int/lit8 v19, v18, 0x2

    aput-wide v0, v17, v19

    goto/16 :goto_e0

    :cond_177
    goto/16 :goto_179

    :goto_179
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    move-wide/from16 v6, v32

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    const-wide/16 v0, 0x0

    return-wide v0
.end method

.method public static f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 33

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

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->saveStack(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)J

    move-result-wide v0

    move-wide/from16 v24, v0

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v18, v0

    move-wide/from16 v20, v0

    move-wide/from16 v22, v0

    .line 2
    :try_start_23
    if-eqz v17, :cond_3c

    .line 3
    const/4 v8, 0x0

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v6

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x2

    aget-wide v4, v12, v9

    long-to-int v8, v4

    const/4 v9, 0x0

    invoke-static {v14, v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_57

    .line 4
    :cond_3c
    move/from16 v8, v17

    const/4 v9, 0x2

    if-ne v8, v9, :cond_46

    .line 5
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
    :try_end_46
    .catch Ljava/lang/Throwable; {:try_start_23 .. :try_end_46} :catch_4c

    .line 6
    :cond_46
    const/4 v8, 0x0

    return v8

    .line 7
    :goto_48
    const/4 v8, 0x1

    return v8

    const/4 v8, 0x0

    return v8

    :catch_4c
    move-exception v12

    invoke-static {v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->wasmException(Ljava/lang/Throwable;)Ljava/lang/Throwable;

    move-wide/from16 v6, v24

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_57

    :goto_57
    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->pendingTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v8

    const/4 v9, 0x1

    new-array v12, v9, [J

    const/4 v9, 0x0

    invoke-static {v14, v12, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->takeException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)J

    move-result-wide v2

    goto :goto_48

    goto/16 :goto_66

    :goto_66
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const/4 v8, 0x0

    return v8
.end method

.method public static f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V
    .registers 33

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v18, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17

    add-int/lit16 v8, v13, 0x11

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_1d

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :cond_1d
    move v13, v8

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    move-object/from16 v25, v12

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v19, v0

    move-wide/from16 v21, v0

    move-wide/from16 v23, v0

    .line 2
    move-object/from16 v27, v14

    move-object/from16 v2, v25

    move-object/from16 v28, v2

    const/4 v8, 0x1

    move/from16 v29, v8

    invoke-static/range {v27 .. v29}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->memoryGrow(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;I)J

    move-result-wide v21

    move-object/from16 v12, v25

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v18, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17

    .line 3
    const v8, 0x10004

    add-int/lit8 v11, v17, -0x4

    if-gt v8, v11, :cond_7e

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

    const/4 v9, 0x2

    aget-wide v4, v12, v9

    long-to-int v8, v4

    const/4 v9, 0x0

    invoke-static {v14, v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_7a

    return-void

    :goto_7a
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    return-void

    .line 3
    :cond_7e
    move-object/from16 v12, v25

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
    .registers 35

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v18, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17

    add-int/lit16 v8, v13, 0x11

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_1e

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    :cond_1e
    move v13, v8

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    move-object/from16 v28, v12

    move-object/from16 v29, v16

    move-object/from16 v30, v14

    move/from16 v31, v13

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->saveStack(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)J

    move-result-wide v0

    move-wide/from16 v25, v0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v19, v15

    move-wide/from16 v20, v0

    move-wide/from16 v22, v0

    move/from16 v24, v15

    const/4 v8, 0x0

    move-object/from16 v27, v8

    .line 2
    :try_start_40
    invoke-static/range {v29 .. v31}, Lchasm/jit/Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_a0

    move-object/from16 v12, v28

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v18, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17
    :try_end_51
    .catch Ljava/lang/Throwable; {:try_start_40 .. :try_end_51} :catch_8f

    .line 3
    const/4 v8, -0x1

    return v8

    .line 4
    :goto_53
    move-object/from16 v2, v28

    move-object/from16 v30, v2

    invoke-static/range {v30 .. v30}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->memorySize(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;)J

    move-result-wide v22

    move-object/from16 v30, v14

    move/from16 v31, v13

    .line 5
    const v8, 0x10004

    add-int/lit8 v11, v17, -0x4

    if-gt v8, v11, :cond_c9

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

    move/from16 v24, v9

    .line 6
    move-wide/from16 v0, v22

    long-to-int v8, v0

    add-int v19, v8, v24

    .line 7
    move/from16 v8, v19

    return v8

    const/4 v8, 0x0

    return v8

    :catch_8f
    move-exception v12

    invoke-static {v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->wasmException(Ljava/lang/Throwable;)Ljava/lang/Throwable;

    move-object/from16 v29, v16

    move-object/from16 v30, v14

    move/from16 v31, v13

    move-wide/from16 v6, v25

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_a0

    :cond_a0
    :goto_a0
    move-object/from16 v12, v28

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v18, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->pendingTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v8

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x2

    aget-wide v4, v12, v9

    long-to-int v9, v4

    if-ne v8, v9, :cond_c2

    const/4 v9, 0x1

    new-array v12, v9, [J

    const/4 v9, 0x0

    invoke-static {v14, v12, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->takeException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)J

    move-result-wide v2

    goto :goto_53

    :cond_c2
    goto/16 :goto_c4

    :goto_c4
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const/4 v8, 0x0

    return v8

    .line 5
    :cond_c9
    move-object/from16 v12, v28

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
    .registers 43

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x13

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_11

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    :cond_11
    move v13, v8

    const/4 v8, 0x3

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openRoots(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v0

    move-wide/from16 v32, v0

    long-to-int v8, v0

    move/from16 v18, v8

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v35, v12

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->saveStack(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)J

    move-result-wide v0

    move-wide/from16 v30, v0

    move/from16 v20, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v21, v0

    move/from16 v23, v15

    const-wide/16 v2, 0x501

    move-wide/from16 v24, v2

    move-wide/from16 v26, v0

    move-wide/from16 v28, v0

    const/4 v8, 0x0

    move-object/from16 v34, v8

    .line 2
    :try_start_41
    move/from16 v0, v20

    int-to-long v0, v0

    move-wide/from16 v26, v0

    add-int/lit8 v19, v18, 0x1

    aput-wide v0, v17, v19

    .line 3
    const/4 v8, 0x5

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v6

    long-to-int v11, v6

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v12

    move-wide/from16 v0, v26

    add-int/lit16 v10, v11, 0x4

    aput-wide v0, v12, v10

    move-object/from16 v12, v35

    const/4 v9, 0x0

    aget-wide v4, v12, v9

    long-to-int v8, v4

    const/4 v9, 0x4

    invoke-static {v14, v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_b8
    :try_end_69
    .catch Ljava/lang/Throwable; {:try_start_41 .. :try_end_69} :catch_ad

    .line 4
    :goto_69
    move-wide/from16 v24, v28

    add-int/lit8 v19, v18, 0x0

    aput-wide v24, v17, v19

    .line 6
    :goto_6f
    const/high16 v10, -0x80000000

    add-int v8, v23, v10

    const v9, -0x7ffff830

    if-ge v8, v9, :cond_a2

    .line 7
    move/from16 v0, v23

    int-to-long v0, v0

    move-wide/from16 v26, v0

    add-int/lit8 v19, v18, 0x1

    aput-wide v0, v17, v19

    .line 8
    move-object/from16 v12, v35

    const/4 v9, 0x3

    aget-wide v4, v12, v9

    long-to-int v11, v4

    move-object/from16 v12, v34

    if-nez v12, :cond_90

    const/4 v8, 0x1

    new-array v12, v8, [J

    move-object/from16 v34, v12

    :cond_90
    move-wide/from16 v0, v26

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    invoke-static {v14, v11, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structNew(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J)J

    move-result-wide v0

    move-wide/from16 v26, v0

    add-int/lit8 v19, v18, 0x1

    aput-wide v0, v17, v19

    .line 9
    add-int/lit8 v23, v23, 0x1

    .line 10
    goto :goto_6f

    .line 11
    :cond_a2
    move-wide/from16 v26, v24

    .line 12
    move-wide/from16 v0, v26

    invoke-static {v14, v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwRef(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_e2

    const/4 v8, 0x0

    return v8

    :catch_ad
    move-exception v12

    invoke-static {v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->wasmException(Ljava/lang/Throwable;)Ljava/lang/Throwable;

    move-wide/from16 v6, v30

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_b8

    :goto_b8
    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->pendingTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v8

    move-object/from16 v12, v35

    const/4 v9, 0x0

    aget-wide v4, v12, v9

    long-to-int v9, v4

    if-ne v8, v9, :cond_e0

    const/4 v9, 0x1

    new-array v12, v9, [J

    const/4 v9, 0x1

    invoke-static {v14, v12, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->takeException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)J

    move-result-wide v2

    const/4 v10, 0x0

    aget-wide v0, v12, v10

    move-wide/from16 v26, v0

    add-int/lit8 v19, v18, 0x1

    aput-wide v0, v17, v19

    move-wide/from16 v28, v2

    add-int/lit8 v19, v18, 0x2

    aput-wide v2, v17, v19

    goto :goto_69

    :cond_e0
    goto/16 :goto_e2

    :goto_e2
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    move-wide/from16 v6, v32

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    const/4 v8, 0x0

    return v8
.end method

.method public static f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 40

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x12

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

    move-wide/from16 v29, v0

    long-to-int v8, v0

    move/from16 v18, v8

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    move-object/from16 v32, v16

    move-object/from16 v33, v14

    move/from16 v34, v13

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->saveStack(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)J

    move-result-wide v0

    move-wide/from16 v27, v0

    move/from16 v20, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v21, v0

    move-wide/from16 v23, v0

    move-wide/from16 v25, v0

    const/4 v8, 0x0

    move-object/from16 v31, v8

    .line 2
    :try_start_3a
    move/from16 v35, v20

    invoke-static/range {v32 .. v35}, Lchasm/jit/Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v9

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_74

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    int-to-long v0, v9

    move-wide/from16 v23, v0
    :try_end_4b
    .catch Ljava/lang/Throwable; {:try_start_3a .. :try_end_4b} :catch_63

    .line 3
    move-wide/from16 v6, v29

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move-wide/from16 v0, v23

    long-to-int v8, v0

    return v8

    .line 4
    :goto_54
    move-wide/from16 v0, v23

    long-to-int v8, v0

    add-int/lit8 v20, v8, 0x5

    .line 5
    move-wide/from16 v6, v29

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move/from16 v8, v20

    return v8

    const/4 v8, 0x0

    return v8

    :catch_63
    move-exception v12

    invoke-static {v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->wasmException(Ljava/lang/Throwable;)Ljava/lang/Throwable;

    move-object/from16 v32, v16

    move-object/from16 v33, v14

    move/from16 v34, v13

    move-wide/from16 v6, v27

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_74

    :cond_74
    :goto_74
    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->pendingTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v8

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x0

    aget-wide v4, v12, v9

    long-to-int v9, v4

    if-ne v8, v9, :cond_99

    const/4 v9, 0x1

    new-array v12, v9, [J

    const/4 v9, 0x1

    invoke-static {v14, v12, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->takeException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)J

    move-result-wide v2

    const/4 v10, 0x0

    aget-wide v0, v12, v10

    move-wide/from16 v23, v0

    add-int/lit8 v19, v18, 0x0

    aput-wide v0, v17, v19

    goto :goto_54

    :cond_99
    goto/16 :goto_9b

    :goto_9b
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    move-wide/from16 v6, v29

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    const/4 v8, 0x0

    return v8
.end method

.method public static f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V
    .registers 27

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0xf

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_10

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :cond_10
    move v13, v8

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v17, v0

    move-wide/from16 v19, v0

    .line 2
    const-wide/16 v0, 0x501

    move-wide/from16 v19, v0

    .line 3
    move-wide/from16 v0, v19

    invoke-static {v14, v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwRef(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_24

    return-void

    :goto_24
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

    const/4 v2, 0x1

    aput-object v4, p1, v2

    return-void
.end method

.method private static bindValues0(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V
    .registers 8

    const/4 v2, 0x0

    aget-object v5, p1, v2

    check-cast v5, [J

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->tagAddress(I)I

    move-result v3

    int-to-long v0, v3

    const/4 v2, 0x0

    aput-wide v0, v5, v2

    const/4 v2, 0x1

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->tagAddress(I)I

    move-result v3

    int-to-long v0, v3

    const/4 v2, 0x1

    aput-wide v0, v5, v2

    const/4 v2, 0x2

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->tagAddress(I)I

    move-result v3

    int-to-long v0, v3

    const/4 v2, 0x2

    aput-wide v0, v5, v2

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->runtimeType(I)I

    move-result v3

    int-to-long v0, v3

    const/4 v2, 0x3

    aput-wide v0, v5, v2

    return-void
.end method


# virtual methods
.method public accept(Ljava/lang/Object;)V
    .registers 5

    check-cast p1, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;

    const/4 v0, 0x2

    new-array v1, v0, [Ljava/lang/Object;

    const/4 v0, 0x4

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
