.class public final Lchasm/jit/Code0;
.super Ljava/lang/Object;
.source "module.wasm"


# direct methods
.method public static f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I
    .registers 33

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    move/from16 v18, p4

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v19, v0

    move-wide/from16 v21, v0

    move-wide/from16 v23, v0

    .line 2
    add-int v17, v17, v18

    .line 3
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 41

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

    move/from16 v20, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v21, v0

    const-wide/16 v2, 0xe01

    move-wide/from16 v23, v2

    move/from16 v25, v15

    move/from16 v26, v15

    move-wide/from16 v27, v0

    move-wide/from16 v29, v0

    const/4 v8, 0x0

    move-object/from16 v33, v8

    .line 3
    :goto_2a
    const/high16 v10, -0x80000000

    add-int v8, v25, v10

    add-int v9, v20, v10

    if-ge v8, v9, :cond_66

    .line 4
    move/from16 v0, v25

    int-to-long v0, v0

    move-wide/from16 v27, v0

    add-int/lit8 v19, v18, 0x1

    aput-wide v0, v17, v19

    move-wide/from16 v29, v23

    add-int/lit8 v19, v18, 0x2

    aput-wide v29, v17, v19

    .line 5
    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x0

    aget-wide v4, v12, v9

    long-to-int v11, v4

    move-object/from16 v12, v33

    if-nez v12, :cond_53

    const/4 v8, 0x2

    new-array v12, v8, [J

    move-object/from16 v33, v12

    :cond_53
    move-wide/from16 v0, v27

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move-wide/from16 v0, v29

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    invoke-static {v14, v11, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structNew(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J)J

    move-result-wide v0

    move-wide/from16 v23, v0

    .line 6
    add-int/lit8 v25, v25, 0x1

    .line 7
    goto :goto_2a

    .line 9
    :cond_66
    :goto_66
    move-wide/from16 v27, v23

    .line 10
    move-wide/from16 v0, v27

    invoke-static {v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refIsNull(J)J

    move-result-wide v0

    move-wide/from16 v27, v0

    .line 11
    const-wide/16 v2, 0x0

    cmp-long v8, v27, v2

    if-nez v8, :cond_a2

    .line 12
    move-wide/from16 v29, v23

    .line 13
    move-wide/from16 v0, v29

    invoke-static {v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refAsNonNull(J)J

    move-result-wide v0

    move-wide/from16 v29, v0

    .line 14
    move-wide/from16 v0, v29

    const/4 v8, 0x0

    invoke-static {v14, v0, v1, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    move-wide/from16 v29, v0

    .line 15
    move-wide/from16 v2, v29

    long-to-int v9, v2

    add-int v26, v26, v9

    .line 16
    move-wide/from16 v27, v23

    .line 17
    move-wide/from16 v0, v27

    invoke-static {v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refAsNonNull(J)J

    move-result-wide v0

    move-wide/from16 v27, v0

    .line 18
    move-wide/from16 v0, v27

    const/4 v8, 0x1

    invoke-static {v14, v0, v1, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    move-wide/from16 v23, v0

    .line 19
    goto :goto_66

    .line 20
    :cond_a2
    move-wide/from16 v6, v31

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move/from16 v8, v26

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 41

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v18, v0

    move/from16 v20, v15

    move/from16 v21, v15

    move-wide/from16 v22, v0

    move-wide/from16 v24, v0

    move/from16 v26, v15

    move/from16 v27, v15

    move-wide/from16 v28, v0

    .line 2
    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x1

    aget-wide v4, v12, v9

    long-to-int v11, v4

    const/16 v8, 0x400

    const-wide/16 v2, 0x0

    invoke-static {v14, v11, v8, v2, v3}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->arrayNewDefault(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIJ)J

    move-result-wide v0

    move-wide/from16 v22, v0

    .line 4
    :goto_2e
    const/high16 v10, -0x80000000

    add-int v8, v20, v10

    add-int v9, v17, v10

    if-ge v8, v9, :cond_62

    .line 5
    const/16 v9, 0x3ff

    and-int v26, v20, v9

    .line 6
    move-object/from16 v31, v14

    move-wide/from16 v0, v22

    move-wide/from16 v32, v0

    move/from16 v8, v26

    move/from16 v34, v8

    move/from16 v0, v20

    int-to-long v0, v0

    move-wide/from16 v35, v0

    invoke-static/range {v31 .. v36}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->arraySet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JIJ)V

    .line 7
    mul-int/lit8 v27, v20, 0x7

    .line 8
    const/16 v9, 0x3ff

    and-int v27, v27, v9

    .line 9
    move-wide/from16 v0, v22

    move/from16 v10, v27

    invoke-static {v14, v0, v1, v10}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->arrayGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    long-to-int v15, v0

    move/from16 v26, v15

    .line 10
    add-int v21, v21, v26

    .line 11
    add-int/lit8 v20, v20, 0x1

    .line 12
    goto :goto_2e

    .line 13
    :cond_62
    move/from16 v8, v21

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 33

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v18, v0

    move/from16 v20, v15

    move/from16 v21, v15

    move-wide/from16 v22, v0

    move-wide/from16 v24, v0

    .line 3
    :goto_15
    const/high16 v10, -0x80000000

    add-int v8, v20, v10

    add-int v9, v17, v10

    if-ge v8, v9, :cond_35

    .line 4
    move/from16 v8, v20

    invoke-static {v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refI31(I)J

    move-result-wide v0

    move-wide/from16 v24, v0

    .line 5
    move-wide/from16 v0, v24

    invoke-static {v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->i31GetSigned(J)J

    move-result-wide v0

    move-wide/from16 v24, v0

    .line 6
    move-wide/from16 v2, v24

    long-to-int v9, v2

    add-int v21, v21, v9

    .line 7
    add-int/lit8 v20, v20, 0x1

    .line 8
    goto :goto_15

    .line 9
    :cond_35
    move/from16 v8, v21

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 42

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

    move-object/from16 v31, v16

    move-object/from16 v32, v14

    move/from16 v33, v13

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v18, v0

    move/from16 v20, v15

    move/from16 v21, v15

    move-wide/from16 v22, v0

    move-wide/from16 v24, v0

    move-wide/from16 v26, v0

    move-wide/from16 v28, v0

    const/4 v8, 0x0

    move-object/from16 v30, v8

    .line 2
    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x2

    aget-wide v0, v12, v9

    move-wide/from16 v22, v0

    .line 4
    :goto_38
    const/high16 v10, -0x80000000

    add-int v8, v20, v10

    add-int v9, v17, v10

    if-ge v8, v9, :cond_b4

    .line 5
    move-wide/from16 v4, v22

    invoke-static {v14, v4, v5}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->refTarget(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)Ljava/lang/Object;

    move-result-object v12

    instance-of v8, v12, Lchasm/jit/Sig0;

    if-eqz v8, :cond_78

    check-cast v12, Lchasm/jit/Sig0;

    move-object/from16 v31, v12

    move/from16 v34, v21

    move/from16 v35, v20

    move/from16 v33, v13

    invoke-interface/range {v31 .. v35}, Lchasm/jit/Sig0;->typed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v9

    move-object/from16 v31, v16

    move-object/from16 v32, v14

    move/from16 v33, v13

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_b9

    iget-object v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->pendingTailFunction:Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    if-eqz v8, :cond_74

    const/4 v8, 0x1

    new-array v12, v8, [J

    invoke-static {v14, v12, v13}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->completeTailCall(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_b9

    const/4 v10, 0x0

    aget-wide v0, v12, v10

    long-to-int v9, v0

    :cond_74
    move/from16 v21, v9

    goto/16 :goto_b1

    :cond_78
    move-object/from16 v12, v30

    if-nez v12, :cond_81

    const/4 v8, 0x2

    new-array v12, v8, [J

    move-object/from16 v30, v12

    :cond_81
    move/from16 v0, v21

    int-to-long v0, v0

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move/from16 v0, v20

    int-to-long v0, v0

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    move-object/from16 v36, v12

    move-object/from16 v32, v14

    const/4 v9, 0x1

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    move-object/from16 v33, v2

    move-wide/from16 v4, v22

    move-wide/from16 v34, v4

    move/from16 v37, v13

    invoke-static/range {v32 .. v37}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->callRef(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;J[JI)V

    move-object/from16 v32, v14

    move/from16 v33, v13

    move-object/from16 v12, v30

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_b9

    const/4 v10, 0x0

    aget-wide v0, v12, v10

    long-to-int v15, v0

    move/from16 v21, v15

    .line 6
    :goto_b1
    add-int/lit8 v20, v20, 0x1

    .line 7
    goto :goto_38

    .line 8
    :cond_b4
    move/from16 v8, v21

    return v8

    const/4 v8, 0x0

    return v8

    :cond_b9
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const/4 v8, 0x0

    return v8
.end method

.method public static f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V
    .registers 28

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

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

    const/4 v9, 0x3

    aget-wide v4, v12, v9

    long-to-int v8, v4

    const/4 v9, 0x2

    invoke-static {v14, v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->throwTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_35

    return-void

    :goto_35
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    return-void
.end method

.method public static f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 42

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

    const/4 v8, 0x1

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openRoots(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v0

    move-wide/from16 v31, v0

    long-to-int v8, v0

    move/from16 v18, v8

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    move-object/from16 v34, v16

    move-object/from16 v35, v14

    move/from16 v36, v13

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->saveStack(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)J

    move-result-wide v0

    move-wide/from16 v29, v0

    move/from16 v20, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v21, v0

    move/from16 v23, v15

    move/from16 v24, v15

    move-wide/from16 v25, v0

    move-wide/from16 v27, v0

    const/4 v8, 0x0

    move-object/from16 v33, v8

    .line 3
    :goto_3e
    const/high16 v10, -0x80000000

    add-int v8, v23, v10

    add-int v9, v20, v10

    if-ge v8, v9, :cond_60

    .line 4
    :try_start_46
    move/from16 v37, v23

    invoke-static/range {v34 .. v37}, Lchasm/jit/Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_7b

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17
    :try_end_53
    .catch Ljava/lang/Throwable; {:try_start_46 .. :try_end_53} :catch_6a

    .line 5
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 6
    :goto_58
    move-wide/from16 v0, v25

    long-to-int v8, v0

    add-int v24, v8, v24

    .line 7
    add-int/lit8 v23, v23, 0x1

    .line 8
    goto :goto_3e

    .line 9
    :cond_60
    move-wide/from16 v6, v31

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move/from16 v8, v24

    return v8

    const/4 v8, 0x0

    return v8

    :catch_6a
    move-exception v12

    invoke-static {v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->wasmException(Ljava/lang/Throwable;)Ljava/lang/Throwable;

    move-object/from16 v34, v16

    move-object/from16 v35, v14

    move/from16 v36, v13

    move-wide/from16 v6, v29

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    goto/16 :goto_7b

    :cond_7b
    :goto_7b
    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->pendingTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v8

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x3

    aget-wide v4, v12, v9

    long-to-int v9, v4

    if-ne v8, v9, :cond_a0

    const/4 v9, 0x1

    new-array v12, v9, [J

    const/4 v9, 0x1

    invoke-static {v14, v12, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;->takeException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)J

    move-result-wide v2

    const/4 v10, 0x0

    aget-wide v0, v12, v10

    move-wide/from16 v25, v0

    add-int/lit8 v19, v18, 0x0

    aput-wide v0, v17, v19

    goto :goto_58

    :cond_a0
    goto/16 :goto_a2

    :goto_a2
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    move-wide/from16 v6, v31

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    const/4 v8, 0x0

    return v8
.end method

.method public static f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I
    .registers 37

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

    move/from16 v17, p3

    move/from16 v18, p4

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v19, v0

    move/from16 v21, v15

    move/from16 v22, v15

    move-wide/from16 v23, v0

    .line 2
    if-nez v17, :cond_27

    .line 3
    move/from16 v21, v18

    .line 4
    goto/16 :goto_49

    .line 5
    :cond_27
    add-int/lit8 v21, v17, -0x1

    .line 6
    add-int/lit8 v22, v18, 0x1

    .line 7
    const/4 v8, 0x2

    new-array v12, v8, [J

    move/from16 v0, v21

    int-to-long v0, v0

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move/from16 v0, v22

    int-to-long v0, v0

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    const/4 v9, 0x1

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    const/4 v9, 0x2

    aget-object v3, v16, v9

    check-cast v3, Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    invoke-static {v14, v2, v3, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->tailCall(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;[J)V

    const/4 v8, 0x0

    return v8

    .line 8
    :goto_49
    move/from16 v8, v21

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I
    .registers 37

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

    move/from16 v17, p3

    move/from16 v18, p4

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v19, v0

    move/from16 v21, v15

    move/from16 v22, v15

    move-wide/from16 v23, v0

    .line 2
    if-nez v17, :cond_27

    .line 3
    move/from16 v21, v18

    .line 4
    goto/16 :goto_49

    .line 5
    :cond_27
    add-int/lit8 v21, v17, -0x1

    .line 6
    add-int/lit8 v22, v18, 0x2

    .line 7
    const/4 v8, 0x2

    new-array v12, v8, [J

    move/from16 v0, v21

    int-to-long v0, v0

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move/from16 v0, v22

    int-to-long v0, v0

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    const/4 v9, 0x1

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    const/4 v9, 0x3

    aget-object v3, v16, v9

    check-cast v3, Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    invoke-static {v14, v2, v3, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->tailCall(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;[J)V

    const/4 v8, 0x0

    return v8

    .line 8
    :goto_49
    move/from16 v8, v21

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 35

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

    move-object/from16 v26, v16

    move-object/from16 v27, v14

    move/from16 v28, v13

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v18, v0

    move/from16 v20, v15

    move-wide/from16 v21, v0

    move-wide/from16 v23, v0

    const/4 v8, 0x0

    move-object/from16 v25, v8

    .line 2
    move/from16 v29, v17

    const/16 v30, 0x0

    invoke-static/range {v26 .. v30}, Lchasm/jit/Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v9

    iget-object v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->pendingTailFunction:Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    if-eqz v8, :cond_42

    const/4 v8, 0x1

    new-array v12, v8, [J

    invoke-static {v14, v12, v13}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->completeTailCall(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_49

    const/4 v10, 0x0

    aget-wide v0, v12, v10

    long-to-int v9, v0

    :cond_42
    move/from16 v20, v9

    .line 3
    move/from16 v8, v20

    return v8

    const/4 v8, 0x0

    return v8

    :cond_49
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const/4 v8, 0x0

    return v8
.end method

.class public final Lchasm/jit/Entry0;
.super Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;

# interfaces
.implements Lchasm/jit/Sig0;


# direct methods
.method public constructor <init>([Ljava/lang/Object;I)V
    .registers 3

    invoke-direct {p0, p1, p2}, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;-><init>([Ljava/lang/Object;I)V

    return-void
.end method


# virtual methods
.method public call(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 14

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

    const/4 v3, 0x1

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v9, v2

    packed-switch v4, :pswitch_data_34

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_19

    :goto_19
    :pswitch_19
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v2

    goto/16 :goto_2b

    :pswitch_1f
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v2

    goto/16 :goto_2b

    :pswitch_25
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v2

    goto/16 :goto_2b

    :goto_2b
    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_2f
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_2f} :catch_30

    return-void

    :catch_30
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_34
    .packed-switch 0x0
        :pswitch_19
        :pswitch_1f
        :pswitch_25
    .end packed-switch
.end method

.method public invoke(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 14

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

    add-int/lit16 v3, p3, 0x1

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v9, v2

    packed-switch v4, :pswitch_data_3c

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_1e

    :goto_1e
    :pswitch_1e
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v2

    goto/16 :goto_30

    :pswitch_24
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v2

    goto/16 :goto_30

    :pswitch_2a
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v2

    goto/16 :goto_30

    :goto_30
    int-to-long v0, v2

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_37
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_37} :catch_38

    return-void

    :catch_38
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_3c
    .packed-switch 0x0
        :pswitch_1e
        :pswitch_24
        :pswitch_2a
    .end packed-switch
.end method

.method public typed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I
    .registers 7

    iget v1, p0, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v0, p0, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object p0, v0

    packed-switch v1, :pswitch_data_1c

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_d

    :goto_d
    :pswitch_d
    invoke-static/range {p0 .. p4}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v0

    return v0

    :pswitch_12
    invoke-static/range {p0 .. p4}, Lchasm/jit/Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v0

    return v0

    :pswitch_17
    invoke-static/range {p0 .. p4}, Lchasm/jit/Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v0

    return v0

    :pswitch_data_1c
    .packed-switch 0x0
        :pswitch_d
        :pswitch_12
        :pswitch_17
    .end packed-switch
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
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_38

    :pswitch_26
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_38

    :pswitch_2c
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_38

    :pswitch_32
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

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
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_3c

    :pswitch_2a
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_3c

    :pswitch_30
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_3c

    :pswitch_36
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

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

    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V
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

    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V
    :try_end_11
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_11} :catch_12

    return-void

    :catch_12
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

    invoke-virtual {p0}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->getInstance()Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    move-result-object v4

    const/4 v2, 0x1

    aput-object v4, p1, v2

    const/16 v2, 0x8

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->function(I)Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    move-result-object v4

    const/4 v2, 0x2

    aput-object v4, p1, v2

    const/4 v2, 0x7

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->function(I)Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

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

    const/4 v2, 0x1

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->runtimeType(I)I

    move-result v3

    int-to-long v0, v3

    const/4 v2, 0x1

    aput-wide v0, v5, v2

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->functionReference(I)J

    move-result-wide v0

    const/4 v2, 0x2

    aput-wide v0, v5, v2

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->tagAddress(I)I

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

    const/4 v0, 0x4

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

    new-instance v4, Lchasm/jit/Entry1;

    const/4 v3, 0x2

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry1;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x3

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry1;

    const/4 v3, 0x3

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry1;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x4

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x5

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry1;

    const/4 v3, 0x4

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry1;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x6

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry0;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry0;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x7

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry0;

    const/4 v3, 0x2

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry0;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x8

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry1;

    const/4 v3, 0x5

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry1;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x9

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    return-void
.end method

.class public interface abstract Lchasm/jit/Sig0;
.super Ljava/lang/Object;


# virtual methods
.method public abstract typed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I
.end method
