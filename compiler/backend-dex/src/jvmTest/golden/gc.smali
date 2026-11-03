.class public final Lchasm/jit/Code0;
.super Ljava/lang/Object;
.source "module.wasm"


# direct methods
.method public static f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I
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

    move/from16 v17, p3

    move/from16 v18, p4

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v19, v0

    .line 2
    add-int v17, v17, v18

    .line 3
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I
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

    move/from16 v17, p3

    move/from16 v18, p4

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v19, v0

    .line 2
    mul-int v17, v17, v18

    .line 3
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f10([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIII)I
    .registers 41

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

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v27, v12

    move-object/from16 v28, v16

    move-object/from16 v29, v14

    move/from16 v30, v13

    move/from16 v17, p3

    move/from16 v18, p4

    move/from16 v19, p5

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v20, v0

    const/4 v8, 0x0

    move-object/from16 v26, v8

    .line 2
    move/from16 v22, v18

    move/from16 v23, v19

    .line 3
    if-eqz v17, :cond_3c

    .line 4
    move-object/from16 v12, v27

    const/4 v9, 0x4

    aget-wide v0, v12, v9

    move-wide/from16 v24, v0

    .line 5
    goto/16 :goto_43

    .line 6
    :cond_3c
    move-object/from16 v12, v27

    const/4 v9, 0x5

    aget-wide v0, v12, v9

    move-wide/from16 v24, v0

    .line 7
    :goto_43
    move-wide/from16 v4, v24

    invoke-static {v14, v4, v5}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->refTarget(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)Ljava/lang/Object;

    move-result-object v12

    instance-of v8, v12, Lchasm/jit/Sig0;

    if-eqz v8, :cond_65

    check-cast v12, Lchasm/jit/Sig0;

    move-object/from16 v28, v12

    move/from16 v31, v22

    move/from16 v32, v23

    move/from16 v30, v13

    invoke-interface/range {v28 .. v32}, Lchasm/jit/Sig0;->typed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v9

    move-object/from16 v28, v16

    move-object/from16 v29, v14

    move/from16 v30, v13

    move/from16 v22, v9

    goto/16 :goto_a1

    :cond_65
    move-object/from16 v12, v26

    if-nez v12, :cond_6e

    const/4 v8, 0x2

    new-array v12, v8, [J

    move-object/from16 v26, v12

    :cond_6e
    move/from16 v0, v22

    int-to-long v0, v0

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move/from16 v0, v23

    int-to-long v0, v0

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    move-object/from16 v33, v12

    move-object/from16 v29, v14

    const/4 v9, 0x4

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    move-object/from16 v30, v2

    move-wide/from16 v4, v24

    move-wide/from16 v31, v4

    move/from16 v34, v13

    invoke-static/range {v29 .. v34}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->callRef(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;J[JI)V

    move-object/from16 v29, v14

    move/from16 v30, v13

    move-object/from16 v12, v26

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-eqz v8, :cond_9b

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->raiseUnwinding(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)V

    :cond_9b
    const/4 v10, 0x0

    aget-wide v0, v12, v10

    long-to-int v15, v0

    move/from16 v22, v15

    .line 8
    :goto_a1
    move/from16 v8, v22

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f11([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
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

    move-wide/from16 v29, v0

    long-to-int v8, v0

    move/from16 v18, v8

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    move-object/from16 v32, v16

    move-object/from16 v33, v14

    move/from16 v34, v13

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v20, v0

    move-wide/from16 v22, v0

    move-wide/from16 v27, v0

    const/4 v8, 0x0

    move-object/from16 v31, v8

    .line 2
    const-wide/32 v0, 0x40e01

    move-wide/from16 v27, v0

    add-int/lit8 v19, v18, 0x0

    aput-wide v0, v17, v19

    .line 3
    move-wide/from16 v4, v27

    invoke-static {v14, v4, v5}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->refTarget(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)Ljava/lang/Object;

    move-result-object v12

    instance-of v8, v12, Lchasm/jit/Sig0;

    if-eqz v8, :cond_61

    check-cast v12, Lchasm/jit/Sig0;

    move-object/from16 v32, v12

    const/16 v35, 0x1

    const/16 v36, 0x2

    move/from16 v34, v13

    invoke-interface/range {v32 .. v36}, Lchasm/jit/Sig0;->typed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v9

    move-object/from16 v32, v16

    move-object/from16 v33, v14

    move/from16 v34, v13

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    move/from16 v24, v9

    goto/16 :goto_9f

    :cond_61
    move-object/from16 v12, v31

    if-nez v12, :cond_6a

    const/4 v8, 0x2

    new-array v12, v8, [J

    move-object/from16 v31, v12

    :cond_6a
    const-wide/16 v0, 0x1

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    const-wide/16 v0, 0x2

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    move-object/from16 v37, v12

    move-object/from16 v33, v14

    const/4 v9, 0x4

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    move-object/from16 v34, v2

    move-wide/from16 v4, v27

    move-wide/from16 v35, v4

    move/from16 v38, v13

    invoke-static/range {v33 .. v38}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->callRef(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;J[JI)V

    move-object/from16 v33, v14

    move/from16 v34, v13

    move-object/from16 v12, v31

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-eqz v8, :cond_95

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->raiseUnwinding(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)V

    :cond_95
    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    const/4 v10, 0x0

    aget-wide v0, v12, v10

    long-to-int v15, v0

    move/from16 v24, v15

    .line 4
    :goto_9f
    move-wide/from16 v6, v29

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move/from16 v8, v24

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f12([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 29

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x10

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_11

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    :cond_11
    move v13, v8

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v18, v0

    move-wide/from16 v20, v0

    .line 2
    move/from16 v8, v17

    invoke-static {v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refI31(I)J

    move-result-wide v0

    move-wide/from16 v20, v0

    .line 3
    move-wide/from16 v0, v20

    move-wide/from16 v23, v0

    invoke-static/range {v23 .. v24}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->externConvertAny(J)J

    move-result-wide v20

    .line 4
    move-wide/from16 v0, v20

    move-wide/from16 v23, v0

    invoke-static/range {v23 .. v24}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->anyConvertExtern(J)J

    move-result-wide v20

    .line 5
    const/4 v9, 0x3

    aget-object v12, v16, v9

    move-wide/from16 v0, v20

    invoke-static {v14, v0, v1, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refCast(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JLjava/lang/Object;)J

    move-result-wide v0

    move-wide/from16 v20, v0

    .line 6
    move-wide/from16 v0, v20

    invoke-static {v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->i31GetSigned(J)J

    move-result-wide v0

    long-to-int v15, v0

    move/from16 v17, v15

    .line 7
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f13([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
    .registers 29

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x10

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_11

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    :cond_11
    move v13, v8

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v17, v0

    move-wide/from16 v19, v0

    move-wide/from16 v21, v0

    .line 2
    const/4 v8, 0x1

    invoke-static {v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refI31(I)J

    move-result-wide v0

    move-wide/from16 v21, v0

    .line 3
    const/4 v9, 0x2

    aget-object v12, v16, v9

    move-wide/from16 v0, v21

    invoke-static {v14, v0, v1, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refCast(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JLjava/lang/Object;)J

    move-result-wide v0

    move-wide/from16 v21, v0

    .line 4
    const/4 v8, 0x0

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIJ)J
    .registers 50

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x15

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_12

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    :cond_12
    move v13, v8

    const/4 v8, 0x1

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openRoots(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v0

    move-wide/from16 v34, v0

    long-to-int v8, v0

    move/from16 v18, v8

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    move/from16 v8, p3

    int-to-long v0, v8

    move-wide/from16 v20, v0

    move-wide/from16 v22, p4

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v24, v0

    move-wide/from16 v26, v0

    const/4 v8, 0x0

    move-object/from16 v36, v8

    .line 2
    move-wide/from16 v28, v20

    move-wide/from16 v30, v22

    .line 3
    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x0

    aget-wide v4, v12, v9

    long-to-int v11, v4

    move-object/from16 v12, v36

    if-nez v12, :cond_48

    const/4 v8, 0x2

    new-array v12, v8, [J

    move-object/from16 v36, v12

    :cond_48
    move-wide/from16 v0, v28

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move-wide/from16 v0, v30

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    invoke-static {v14, v11, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structNew(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J)J

    move-result-wide v0

    move-wide/from16 v26, v0

    .line 4
    move-wide/from16 v0, v26

    const/4 v8, 0x0

    invoke-static {v14, v0, v1, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    move-wide/from16 v30, v0

    .line 5
    move-wide/from16 v0, v30

    long-to-int v8, v0

    add-int/lit16 v8, v8, 0x1

    int-to-long v0, v8

    move-wide/from16 v30, v0

    .line 6
    move-object/from16 v38, v14

    move-wide/from16 v0, v26

    move-wide/from16 v39, v0

    const/4 v8, 0x0

    move/from16 v41, v8

    move-wide/from16 v0, v30

    move-wide/from16 v42, v0

    invoke-static/range {v38 .. v43}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structSet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JIJ)V

    .line 7
    move-wide/from16 v0, v26

    const/4 v8, 0x0

    invoke-static {v14, v0, v1, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    move-wide/from16 v28, v0

    .line 9
    move-wide/from16 v0, v26

    const/4 v8, 0x1

    invoke-static {v14, v0, v1, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    move-wide/from16 v30, v0

    .line 10
    add-long v20, v28, v30

    .line 11
    move-wide/from16 v6, v34

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move-wide/from16 v0, v20

    return-wide v0

    const-wide/16 v0, 0x0

    return-wide v0
.end method

.method public static f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 41

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

    move-wide/from16 v28, v0

    long-to-int v8, v0

    move/from16 v18, v8

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    move/from16 v20, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v21, v0

    move-wide/from16 v23, v0

    .line 2
    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x1

    aget-wide v4, v12, v9

    long-to-int v11, v4

    const/4 v9, 0x1

    aget-object v2, v16, v9

    check-cast v2, [J

    invoke-static {v14, v11, v2}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structNewDefault(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J)J

    move-result-wide v0

    move-wide/from16 v23, v0

    .line 3
    move-object/from16 v31, v14

    move-wide/from16 v0, v23

    move-wide/from16 v32, v0

    const/4 v8, 0x0

    move/from16 v34, v8

    move/from16 v0, v20

    int-to-long v0, v0

    move-wide/from16 v35, v0

    invoke-static/range {v31 .. v36}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structSet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JIJ)V

    .line 4
    move-object/from16 v31, v14

    move-wide/from16 v0, v23

    move-wide/from16 v32, v0

    const/4 v8, 0x1

    move/from16 v34, v8

    move/from16 v0, v20

    int-to-long v0, v0

    move-wide/from16 v35, v0

    invoke-static/range {v31 .. v36}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structSet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JIJ)V

    .line 5
    move-wide/from16 v0, v23

    const/4 v8, 0x0

    invoke-static {v14, v0, v1, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    long-to-int v8, v0

    int-to-byte v8, v8

    int-to-long v0, v8

    long-to-int v15, v0

    move/from16 v25, v15

    .line 6
    move-wide/from16 v0, v23

    const/4 v8, 0x0

    invoke-static {v14, v0, v1, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    long-to-int v8, v0

    and-int/lit16 v8, v8, 0xff

    int-to-long v0, v8

    long-to-int v15, v0

    move/from16 v26, v15

    .line 7
    add-int v25, v25, v26

    .line 8
    move-wide/from16 v0, v23

    const/4 v8, 0x1

    invoke-static {v14, v0, v1, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    long-to-int v8, v0

    int-to-short v8, v8

    int-to-long v0, v8

    long-to-int v15, v0

    move/from16 v26, v15

    .line 9
    move-wide/from16 v0, v23

    const/4 v8, 0x1

    invoke-static {v14, v0, v1, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    long-to-int v8, v0

    int-to-char v8, v8

    int-to-long v0, v8

    long-to-int v15, v0

    move/from16 v27, v15

    .line 10
    add-int v26, v26, v27

    .line 11
    add-int v20, v25, v26

    .line 12
    move-wide/from16 v6, v28

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move/from16 v8, v20

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 49

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x15

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

    move-wide/from16 v35, v0

    long-to-int v8, v0

    move/from16 v18, v8

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    move/from16 v20, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v21, v0

    move/from16 v23, v15

    move/from16 v24, v15

    move-wide/from16 v25, v0

    .line 2
    move-object/from16 v38, v14

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x2

    aget-wide v4, v12, v9

    long-to-int v8, v4

    move/from16 v39, v8

    move/from16 v8, v20

    move/from16 v40, v8

    const-wide/16 v0, 0x3

    move-wide/from16 v41, v0

    invoke-static/range {v38 .. v42}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->arrayNew(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIJ)J

    move-result-wide v25

    .line 3
    move/from16 v8, v20

    const/4 v9, 0x2

    invoke-static {v8, v9}, Lio/github/charlietap/chasm/runtime/compiled/CompiledNumerics;->i32DivU(II)I

    move-result v8

    move/from16 v32, v8

    .line 4
    move-object/from16 v38, v14

    move-wide/from16 v0, v25

    move-wide/from16 v39, v0

    const/4 v8, 0x0

    move/from16 v41, v8

    const-wide/16 v0, 0x7

    move-wide/from16 v42, v0

    move/from16 v8, v32

    move/from16 v44, v8

    invoke-static/range {v38 .. v44}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->arrayFill(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JIJI)V

    .line 5
    move-object/from16 v38, v14

    move-wide/from16 v0, v25

    move-wide/from16 v39, v0

    const/4 v8, 0x0

    move/from16 v41, v8

    const-wide/16 v0, 0x64

    move-wide/from16 v42, v0

    invoke-static/range {v38 .. v43}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->arraySet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JIJ)V

    .line 7
    :goto_73
    move-wide/from16 v0, v25

    invoke-static {v14, v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->arrayLen(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)J

    move-result-wide v0

    long-to-int v15, v0

    move/from16 v29, v15

    .line 8
    const/high16 v10, -0x80000000

    add-int v8, v23, v10

    add-int v9, v29, v10

    if-ge v8, v9, :cond_94

    .line 9
    move-wide/from16 v0, v25

    move/from16 v10, v23

    invoke-static {v14, v0, v1, v10}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->arrayGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    long-to-int v15, v0

    move/from16 v29, v15

    .line 10
    add-int v24, v24, v29

    .line 11
    add-int/lit8 v23, v23, 0x1

    .line 12
    goto :goto_73

    .line 13
    :cond_94
    move-wide/from16 v6, v35

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move/from16 v8, v24

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 50

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x15

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_11

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    :cond_11
    move v13, v8

    const/4 v8, 0x2

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

    move-object/from16 v36, v12

    move/from16 v20, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v21, v0

    move-wide/from16 v23, v0

    move-wide/from16 v25, v0

    const/4 v8, 0x0

    move-object/from16 v35, v8

    .line 2
    move/from16 v27, v20

    .line 3
    const/16 v28, 0x2

    .line 4
    const/16 v29, 0x3

    .line 5
    const/16 v30, 0x4

    .line 6
    move-object/from16 v12, v35

    if-nez v12, :cond_46

    const/4 v8, 0x4

    new-array v12, v8, [J

    move-object/from16 v35, v12

    :cond_46
    move/from16 v0, v27

    int-to-long v0, v0

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move/from16 v0, v28

    int-to-long v0, v0

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    move/from16 v0, v29

    int-to-long v0, v0

    const/4 v10, 0x2

    aput-wide v0, v12, v10

    move/from16 v0, v30

    int-to-long v0, v0

    const/4 v10, 0x3

    aput-wide v0, v12, v10

    move-object/from16 v40, v12

    move-object/from16 v38, v14

    move-object/from16 v12, v36

    const/4 v9, 0x2

    aget-wide v4, v12, v9

    long-to-int v8, v4

    move/from16 v39, v8

    const/4 v8, 0x4

    move/from16 v41, v8

    invoke-static/range {v38 .. v41}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->arrayNewFixed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[JI)J

    move-result-wide v0

    move-wide/from16 v23, v0

    add-int/lit8 v19, v18, 0x0

    aput-wide v0, v17, v19

    .line 7
    move-object/from16 v12, v36

    const/4 v9, 0x2

    aget-wide v4, v12, v9

    long-to-int v11, v4

    const/4 v8, 0x4

    const-wide/16 v2, 0x0

    invoke-static {v14, v11, v8, v2, v3}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->arrayNewDefault(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIJ)J

    move-result-wide v0

    move-wide/from16 v25, v0

    .line 8
    move-object/from16 v38, v14

    move-wide/from16 v0, v23

    move-wide/from16 v39, v0

    const/4 v8, 0x0

    move/from16 v41, v8

    move-wide/from16 v0, v25

    move-wide/from16 v42, v0

    const/4 v8, 0x1

    move/from16 v44, v8

    const/4 v8, 0x3

    move/from16 v45, v8

    invoke-static/range {v38 .. v45}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->arrayCopy(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JIJII)V

    .line 9
    move-wide/from16 v0, v25

    const/4 v10, 0x1

    invoke-static {v14, v0, v1, v10}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->arrayGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    long-to-int v15, v0

    move/from16 v27, v15

    .line 10
    move-wide/from16 v0, v25

    const/4 v10, 0x3

    invoke-static {v14, v0, v1, v10}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->arrayGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    long-to-int v15, v0

    move/from16 v28, v15

    .line 11
    add-int v20, v27, v28

    .line 12
    move-wide/from16 v6, v33

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move/from16 v8, v20

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 31

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x10

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_11

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    :cond_11
    move v13, v8

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v18, v0

    .line 2
    move/from16 v8, v17

    invoke-static {v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refI31(I)J

    move-result-wide v0

    move-wide/from16 v20, v0

    .line 3
    move-wide/from16 v0, v20

    invoke-static {v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->i31GetSigned(J)J

    move-result-wide v0

    move-wide/from16 v20, v0

    .line 4
    move/from16 v8, v17

    invoke-static {v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refI31(I)J

    move-result-wide v0

    move-wide/from16 v22, v0

    .line 5
    move-wide/from16 v0, v22

    invoke-static {v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->i31GetUnsigned(J)J

    move-result-wide v0

    move-wide/from16 v22, v0

    .line 6
    move-wide/from16 v0, v20

    long-to-int v8, v0

    move-wide/from16 v2, v22

    long-to-int v9, v2

    add-int v17, v8, v9

    .line 7
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 45

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x14

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

    move-wide/from16 v31, v0

    long-to-int v8, v0

    move/from16 v18, v8

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v34, v12

    move/from16 v20, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v21, v0

    const-wide/32 v2, 0x30e01

    move-wide/from16 v23, v2

    move/from16 v25, v15

    move/from16 v26, v15

    move-wide/from16 v27, v0

    move-wide/from16 v29, v0

    const/4 v8, 0x0

    move-object/from16 v33, v8

    .line 3
    :goto_3e
    const/high16 v10, -0x80000000

    add-int v8, v25, v10

    add-int v9, v20, v10

    if-ge v8, v9, :cond_95

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
    move-object/from16 v12, v34

    const/4 v9, 0x3

    aget-wide v4, v12, v9

    long-to-int v11, v4

    move-object/from16 v12, v33

    if-nez v12, :cond_64

    const/4 v8, 0x2

    new-array v12, v8, [J

    move-object/from16 v33, v12

    :cond_64
    move-wide/from16 v0, v27

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move-wide/from16 v0, v29

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    invoke-static {v14, v11, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structNew(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J)J

    move-result-wide v0

    move-wide/from16 v23, v0

    add-int/lit8 v19, v18, 0x0

    aput-wide v0, v17, v19

    .line 6
    move-object/from16 v36, v14

    move-object/from16 v12, v34

    const/4 v9, 0x2

    aget-wide v4, v12, v9

    long-to-int v8, v4

    move/from16 v37, v8

    const/16 v8, 0x40

    move/from16 v38, v8

    const-wide/16 v0, 0x0

    move-wide/from16 v39, v0

    invoke-static/range {v36 .. v40}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->arrayNew(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIJ)J

    move-result-wide v27

    add-int/lit8 v19, v18, 0x1

    aput-wide v27, v17, v19

    .line 7
    add-int/lit8 v25, v25, 0x1

    .line 8
    goto :goto_3e

    .line 10
    :cond_95
    :goto_95
    move-wide/from16 v0, v23

    invoke-static {v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->isNull(J)Z

    move-result v8

    if-nez v8, :cond_b1

    .line 11
    move-wide/from16 v27, v23

    .line 12
    move-wide/from16 v0, v27

    invoke-static {v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refAsNonNull(J)J

    move-result-wide v0

    move-wide/from16 v27, v0

    .line 13
    move-wide/from16 v0, v27

    const/4 v8, 0x1

    invoke-static {v14, v0, v1, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    move-wide/from16 v23, v0

    .line 14
    goto :goto_95

    .line 15
    :cond_b1
    const/16 v25, 0x0

    .line 16
    move-wide/from16 v6, v31

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move/from16 v8, v20

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 41

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

    move-wide/from16 v31, v0

    long-to-int v8, v0

    move/from16 v18, v8

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    move/from16 v20, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v21, v0

    const-wide/16 v2, 0x701

    move-wide/from16 v23, v2

    move-wide/from16 v25, v0

    move-wide/from16 v27, v0

    const/4 v8, 0x0

    move-object/from16 v33, v8

    .line 2
    if-eqz v20, :cond_64

    .line 3
    const-wide/16 v25, 0x5

    add-int/lit8 v19, v18, 0x1

    aput-wide v25, v17, v19

    .line 4
    const-wide/16 v27, 0x6

    add-int/lit8 v19, v18, 0x2

    aput-wide v27, v17, v19

    .line 5
    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x0

    aget-wide v4, v12, v9

    long-to-int v11, v4

    move-object/from16 v12, v33

    if-nez v12, :cond_52

    const/4 v8, 0x2

    new-array v12, v8, [J

    move-object/from16 v33, v12

    :cond_52
    move-wide/from16 v0, v25

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move-wide/from16 v0, v27

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    invoke-static {v14, v11, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structNew(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J)J

    move-result-wide v0

    move-wide/from16 v25, v0

    .line 6
    goto/16 :goto_6c

    .line 7
    :cond_64
    const/16 v8, 0x9

    invoke-static {v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refI31(I)J

    move-result-wide v0

    move-wide/from16 v25, v0

    .line 8
    :goto_6c
    move-wide/from16 v23, v25

    .line 9
    move-wide/from16 v0, v23

    const/4 v9, 0x2

    aget-object v12, v16, v9

    invoke-static {v14, v0, v1, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->castMatches(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JLjava/lang/Object;)Z

    move-result v8

    if-nez v8, :cond_a9

    .line 10
    move-wide/from16 v27, v23

    .line 11
    const/4 v9, 0x3

    aget-object v12, v16, v9

    move-wide/from16 v0, v27

    invoke-static {v14, v0, v1, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refCast(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JLjava/lang/Object;)J

    move-result-wide v0

    move-wide/from16 v27, v0

    .line 12
    move-wide/from16 v0, v27

    invoke-static {v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->i31GetUnsigned(J)J

    move-result-wide v0

    move-wide/from16 v27, v0

    .line 13
    move-wide/from16 v6, v31

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move-wide/from16 v0, v27

    long-to-int v8, v0

    return v8

    .line 14
    :goto_97
    move-wide/from16 v0, v25

    const/4 v8, 0x0

    invoke-static {v14, v0, v1, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    long-to-int v15, v0

    move/from16 v20, v15

    .line 15
    move-wide/from16 v6, v31

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move/from16 v8, v20

    return v8

    .line 16
    :cond_a9
    move-wide/from16 v25, v23

    goto :goto_97

    const/4 v8, 0x0

    return v8
.end method

.method public static f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 29

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x10

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_11

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    :cond_11
    move v13, v8

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v18, v0

    move-wide/from16 v20, v0

    .line 2
    if-eqz v17, :cond_26

    .line 3
    const/4 v8, 0x3

    invoke-static {v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refI31(I)J

    move-result-wide v0

    move-wide/from16 v20, v0

    .line 4
    goto/16 :goto_2a

    .line 5
    :cond_26
    const-wide/16 v0, 0x701

    move-wide/from16 v20, v0

    .line 6
    :goto_2a
    move-wide/from16 v0, v20

    const/4 v9, 0x3

    aget-object v12, v16, v9

    invoke-static {v14, v0, v1, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->castMatches(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JLjava/lang/Object;)Z

    move-result v8

    if-eqz v8, :cond_41

    .line 7
    move-wide/from16 v0, v20

    invoke-static {v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->i31GetSigned(J)J

    move-result-wide v0

    move-wide/from16 v20, v0

    .line 8
    move-wide/from16 v0, v20

    long-to-int v8, v0

    return v8

    .line 9
    :cond_41
    const/4 v8, -0x1

    return v8

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

    packed-switch v4, :pswitch_data_2e

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_19

    :goto_19
    :pswitch_19
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v2

    goto/16 :goto_25

    :pswitch_1f
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v2

    goto/16 :goto_25

    :goto_25
    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_29
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_29} :catch_2a

    return-void

    :catch_2a
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_2e
    .packed-switch 0x0
        :pswitch_19
        :pswitch_1f
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

    packed-switch v4, :pswitch_data_36

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_1e

    :goto_1e
    :pswitch_1e
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v2

    goto/16 :goto_2a

    :pswitch_24
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v2

    goto/16 :goto_2a

    :goto_2a
    int-to-long v0, v2

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_31
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_31} :catch_32

    return-void

    :catch_32
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_36
    .packed-switch 0x0
        :pswitch_1e
        :pswitch_24
    .end packed-switch
.end method

.method public typed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I
    .registers 7

    iget v1, p0, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v0, p0, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object p0, v0

    packed-switch v1, :pswitch_data_18

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_d

    :goto_d
    :pswitch_d
    invoke-static/range {p0 .. p4}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v0

    return v0

    :pswitch_12
    invoke-static/range {p0 .. p4}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v0

    return v0

    nop

    :pswitch_data_18
    .packed-switch 0x0
        :pswitch_d
        :pswitch_12
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
    .registers 15

    :try_start_0
    move-object v2, p0

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p3

    const/4 v3, 0x0

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v8, v2

    const/4 v3, 0x1

    aget-wide v9, p2, v3

    invoke-static/range {v5 .. v10}, Lchasm/jit/Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIJ)J

    move-result-wide v0

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_14
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_14} :catch_15

    return-void

    :catch_15
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void
.end method

.method public invoke(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 15

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

    add-int/lit16 v3, p3, 0x1

    aget-wide v9, p2, v3

    invoke-static/range {v5 .. v10}, Lchasm/jit/Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIJ)J

    move-result-wide v0

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_1c
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_1c} :catch_1d

    return-void

    :catch_1d
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void
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

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p3

    const/4 v3, 0x0

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v8, v2

    packed-switch v4, :pswitch_data_4e

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_14

    :goto_14
    :pswitch_14
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_44

    :pswitch_1a
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_44

    :pswitch_20
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_44

    :pswitch_26
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_44

    :pswitch_2c
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_44

    :pswitch_32
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_44

    :pswitch_38
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_44

    :pswitch_3e
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f12([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_44

    :goto_44
    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_48
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_48} :catch_49

    return-void

    :catch_49
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    nop

    :pswitch_data_4e
    .packed-switch 0x0
        :pswitch_14
        :pswitch_1a
        :pswitch_20
        :pswitch_26
        :pswitch_2c
        :pswitch_32
        :pswitch_38
        :pswitch_3e
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

    packed-switch v4, :pswitch_data_54

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_18

    :goto_18
    :pswitch_18
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_48

    :pswitch_1e
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_48

    :pswitch_24
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_48

    :pswitch_2a
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_48

    :pswitch_30
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_48

    :pswitch_36
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_48

    :pswitch_3c
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_48

    :pswitch_42
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f12([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_48

    :goto_48
    int-to-long v0, v2

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_4f
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_4f} :catch_50

    return-void

    :catch_50
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_54
    .packed-switch 0x0
        :pswitch_18
        :pswitch_1e
        :pswitch_24
        :pswitch_2a
        :pswitch_30
        :pswitch_36
        :pswitch_3c
        :pswitch_42
    .end packed-switch
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
    .registers 15

    :try_start_0
    move-object v2, p0

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

    const/4 v3, 0x2

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v10, v2

    invoke-static/range {v5 .. v10}, Lchasm/jit/Code0;->f10([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIII)I

    move-result v2

    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_1c
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_1c} :catch_1d

    return-void

    :catch_1d
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void
.end method

.method public invoke(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 15

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

    add-int/lit16 v3, p3, 0x1

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v9, v2

    add-int/lit16 v3, p3, 0x2

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v10, v2

    invoke-static/range {v5 .. v10}, Lchasm/jit/Code0;->f10([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIII)I

    move-result v2

    int-to-long v0, v2

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_25
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_25} :catch_26

    return-void

    :catch_26
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void
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

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p3

    packed-switch v4, :pswitch_data_24

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_f

    :goto_f
    :pswitch_f
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f11([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_1b

    :pswitch_15
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f13([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_1b

    :goto_1b
    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_1f
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_1f} :catch_20

    return-void

    :catch_20
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_24
    .packed-switch 0x0
        :pswitch_f
        :pswitch_15
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

    packed-switch v4, :pswitch_data_2a

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_12

    :goto_12
    :pswitch_12
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f11([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_1e

    :pswitch_18
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f13([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_1e

    :goto_1e
    int-to-long v0, v2

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_25
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_25} :catch_26

    return-void

    :catch_26
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_2a
    .packed-switch 0x0
        :pswitch_12
        :pswitch_18
    .end packed-switch
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

    const/4 v2, 0x2

    new-array v4, v2, [J

    fill-array-data v4, :array_24

    const/4 v2, 0x1

    aput-object v4, p1, v2

    const/4 v2, 0x0

    const/4 v3, 0x0

    invoke-virtual {p0, v2, v3}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->typeTest(ZI)Ljava/lang/Object;

    move-result-object v4

    const/4 v2, 0x2

    aput-object v4, p1, v2

    const/4 v2, 0x0

    const/4 v3, -0x7

    invoke-virtual {p0, v2, v3}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->typeTest(ZI)Ljava/lang/Object;

    move-result-object v4

    const/4 v2, 0x3

    aput-object v4, p1, v2

    invoke-virtual {p0}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->getInstance()Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    move-result-object v4

    const/4 v2, 0x4

    aput-object v4, p1, v2

    return-void

    nop

    :array_24
    .array-data 8
        0x0
        0x0
    .end array-data
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

    const/4 v2, 0x2

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->runtimeType(I)I

    move-result v3

    int-to-long v0, v3

    const/4 v2, 0x2

    aput-wide v0, v5, v2

    const/4 v2, 0x3

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->runtimeType(I)I

    move-result v3

    int-to-long v0, v3

    const/4 v2, 0x3

    aput-wide v0, v5, v2

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->functionReference(I)J

    move-result-wide v0

    const/4 v2, 0x4

    aput-wide v0, v5, v2

    const/4 v2, 0x1

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->functionReference(I)J

    move-result-wide v0

    const/4 v2, 0x5

    aput-wide v0, v5, v2

    return-void
.end method


# virtual methods
.method public accept(Ljava/lang/Object;)V
    .registers 5

    check-cast p1, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;

    const/4 v0, 0x5

    new-array v1, v0, [Ljava/lang/Object;

    const/4 v0, 0x6

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

    new-instance v4, Lchasm/jit/Entry0;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry0;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x1

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry1;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry1;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x2

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x3

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x4

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/4 v3, 0x2

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x5

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/4 v3, 0x3

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x6

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/4 v3, 0x4

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x7

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/4 v3, 0x5

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x8

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/4 v3, 0x6

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x9

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry3;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry3;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xa

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry4;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry4;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xb

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/4 v3, 0x7

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xc

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry4;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry4;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xd

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    return-void
.end method

.class public interface abstract Lchasm/jit/Sig0;
.super Ljava/lang/Object;


# virtual methods
.method public abstract typed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I
.end method
