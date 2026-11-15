.class public final Lchasm/jit/Code0;
.super Ljava/lang/Object;
.source "module.wasm"


# static fields
.field private static longs0:[J


# direct methods
.method static constructor <clinit>()V
    .registers 2

    const/4 v1, 0x2

    new-array v0, v1, [J

    fill-array-data v0, :array_a

    sput-object v0, Lchasm/jit/Code0;->longs0:[J

    return-void

    nop

    :array_a
    .array-data 8
        0x0
        0x0
    .end array-data
.end method

.method public static f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I
    .registers 27

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    move/from16 v18, p4

    .line 2
    add-int v17, v17, v18

    .line 3
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I
    .registers 27

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    move/from16 v18, p4

    .line 2
    mul-int v17, v17, v18

    .line 3
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f10([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIII)I
    .registers 39

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

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v25, v12

    move-object/from16 v26, v16

    move-object/from16 v27, v14

    move/from16 v28, v13

    move/from16 v17, p3

    move/from16 v18, p4

    move/from16 v19, p5

    const/4 v8, 0x0

    move-object/from16 v24, v8

    .line 2
    move/from16 v20, v18

    move/from16 v21, v19

    .line 3
    if-eqz v17, :cond_38

    .line 4
    move-object/from16 v12, v25

    const/16 v9, 0x9

    aget-wide v0, v12, v9

    move-wide/from16 v22, v0

    .line 5
    goto/16 :goto_40

    .line 6
    :cond_38
    move-object/from16 v12, v25

    const/16 v9, 0xa

    aget-wide v0, v12, v9

    move-wide/from16 v22, v0

    .line 7
    :goto_40
    move-wide/from16 v4, v22

    invoke-static {v14, v4, v5}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->refTarget(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)Ljava/lang/Object;

    move-result-object v12

    instance-of v11, v12, Lchasm/jit/Sig0;

    if-eqz v11, :cond_62

    check-cast v12, Lchasm/jit/Sig0;

    move-object/from16 v26, v12

    move/from16 v29, v20

    move/from16 v30, v21

    move/from16 v28, v13

    invoke-interface/range {v26 .. v30}, Lchasm/jit/Sig0;->typed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v9

    move-object/from16 v26, v16

    move-object/from16 v27, v14

    move/from16 v28, v13

    move/from16 v20, v9

    goto/16 :goto_9e

    :cond_62
    move-object/from16 v12, v24

    if-nez v12, :cond_6b

    const/4 v8, 0x2

    new-array v12, v8, [J

    move-object/from16 v24, v12

    :cond_6b
    move/from16 v0, v20

    int-to-long v0, v0

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move/from16 v0, v21

    int-to-long v0, v0

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    move-object/from16 v31, v12

    move-object/from16 v27, v14

    const/4 v9, 0x1

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    move-object/from16 v28, v2

    move-wide/from16 v4, v22

    move-wide/from16 v29, v4

    move/from16 v32, v13

    invoke-static/range {v27 .. v32}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->callRef(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;J[JI)V

    move-object/from16 v27, v14

    move/from16 v28, v13

    move-object/from16 v12, v24

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-eqz v8, :cond_98

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->raiseUnwinding(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)V

    :cond_98
    const/4 v10, 0x0

    aget-wide v0, v12, v10

    long-to-int v15, v0

    move/from16 v20, v15

    .line 8
    :goto_9e
    move/from16 v8, v20

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f11([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
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

    move-wide/from16 v23, v0

    long-to-int v8, v0

    move/from16 v18, v8

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    move-object/from16 v26, v16

    move-object/from16 v27, v14

    move/from16 v28, v13

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v21, v0

    const/4 v8, 0x0

    move-object/from16 v25, v8

    .line 2
    const-wide/32 v0, 0x40e01

    move-wide/from16 v21, v0

    add-int/lit8 v19, v18, 0x0

    aput-wide v0, v17, v19

    .line 3
    move-wide/from16 v4, v21

    invoke-static {v14, v4, v5}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->refTarget(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)Ljava/lang/Object;

    move-result-object v12

    instance-of v11, v12, Lchasm/jit/Sig0;

    if-eqz v11, :cond_59

    check-cast v12, Lchasm/jit/Sig0;

    move-object/from16 v26, v12

    const/16 v29, 0x1

    const/16 v30, 0x2

    move/from16 v28, v13

    invoke-interface/range {v26 .. v30}, Lchasm/jit/Sig0;->typed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v9

    move-object/from16 v26, v16

    move-object/from16 v27, v14

    move/from16 v28, v13

    move/from16 v20, v9

    goto/16 :goto_93

    :cond_59
    move-object/from16 v12, v25

    if-nez v12, :cond_62

    const/4 v8, 0x2

    new-array v12, v8, [J

    move-object/from16 v25, v12

    :cond_62
    const-wide/16 v0, 0x1

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    const-wide/16 v0, 0x2

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    move-object/from16 v31, v12

    move-object/from16 v27, v14

    const/4 v9, 0x1

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    move-object/from16 v28, v2

    move-wide/from16 v4, v21

    move-wide/from16 v29, v4

    move/from16 v32, v13

    invoke-static/range {v27 .. v32}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->callRef(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;J[JI)V

    move-object/from16 v27, v14

    move/from16 v28, v13

    move-object/from16 v12, v25

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-eqz v8, :cond_8d

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->raiseUnwinding(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)V

    :cond_8d
    const/4 v10, 0x0

    aget-wide v0, v12, v10

    long-to-int v15, v0

    move/from16 v20, v15

    .line 4
    :goto_93
    move-wide/from16 v6, v23

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move/from16 v8, v20

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f12([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 27

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v18, v0

    .line 2
    move/from16 v8, v17

    invoke-static {v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refI31(I)J

    move-result-wide v0

    move-wide/from16 v18, v0

    .line 3
    move-wide/from16 v21, v18

    invoke-static/range {v21 .. v22}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->externConvertAny(J)J

    move-result-wide v18

    .line 4
    move-wide/from16 v21, v18

    invoke-static/range {v21 .. v22}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->anyConvertExtern(J)J

    move-result-wide v18

    .line 5
    const/16 v9, 0xe

    aget-object v12, v16, v9

    move-wide/from16 v0, v18

    invoke-static {v14, v0, v1, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refCast(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JLjava/lang/Object;)J

    move-result-wide v0

    move-wide/from16 v18, v0

    .line 6
    move-wide/from16 v0, v18

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
    .registers 25

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v17, v0

    .line 2
    const/4 v8, 0x1

    invoke-static {v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refI31(I)J

    move-result-wide v0

    move-wide/from16 v17, v0

    .line 3
    const/16 v9, 0x1c

    aget-object v12, v16, v9

    move-wide/from16 v0, v17

    invoke-static {v14, v0, v1, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refCast(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JLjava/lang/Object;)J

    move-result-wide v0

    move-wide/from16 v17, v0

    .line 4
    const/4 v8, 0x0

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIJ)J
    .registers 44

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v19, p3

    move-wide/from16 v20, p4

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v22, v0

    const/4 v8, 0x0

    move-object/from16 v30, v8

    .line 2
    move/from16 v26, v19

    move-wide/from16 v27, v20

    .line 3
    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x0

    aget-wide v4, v12, v9

    long-to-int v11, v4

    move-object/from16 v12, v30

    if-nez v12, :cond_28

    const/4 v8, 0x2

    new-array v12, v8, [J

    move-object/from16 v30, v12

    :cond_28
    move/from16 v0, v26

    int-to-long v0, v0

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move-wide/from16 v0, v27

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    invoke-static {v14, v11, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structNew(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J)J

    move-result-wide v0

    move-wide/from16 v22, v0

    .line 4
    move-wide/from16 v0, v22

    const/4 v8, 0x0

    invoke-static {v14, v0, v1, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    long-to-int v15, v0

    move/from16 v29, v15

    .line 5
    add-int/lit8 v29, v29, 0x1

    .line 6
    move-object/from16 v32, v14

    move-wide/from16 v0, v22

    move-wide/from16 v33, v0

    const/4 v8, 0x0

    move/from16 v35, v8

    move/from16 v0, v29

    int-to-long v0, v0

    move-wide/from16 v36, v0

    invoke-static/range {v32 .. v37}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structSet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JIJ)V

    .line 7
    move-wide/from16 v0, v22

    const/4 v8, 0x0

    invoke-static {v14, v0, v1, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    long-to-int v15, v0

    move/from16 v26, v15

    .line 8
    move/from16 v0, v26

    int-to-long v0, v0

    move-wide/from16 v24, v0

    .line 9
    move-wide/from16 v0, v22

    const/4 v8, 0x1

    invoke-static {v14, v0, v1, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    move-wide/from16 v27, v0

    .line 10
    add-long v17, v24, v27

    .line 11
    move-wide/from16 v0, v17

    return-wide v0

    const-wide/16 v0, 0x0

    return-wide v0
.end method

.method public static f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 34

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v18, v0

    .line 2
    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x1

    aget-wide v4, v12, v9

    long-to-int v11, v4

    sget-object v2, Lchasm/jit/Code0;->longs0:[J

    invoke-static {v14, v11, v2}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structNewDefault(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J)J

    move-result-wide v0

    move-wide/from16 v18, v0

    .line 3
    move-object/from16 v24, v14

    move-wide/from16 v0, v18

    move-wide/from16 v25, v0

    const/4 v8, 0x0

    move/from16 v27, v8

    move/from16 v0, v17

    int-to-long v0, v0

    move-wide/from16 v28, v0

    invoke-static/range {v24 .. v29}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structSet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JIJ)V

    .line 4
    move-object/from16 v24, v14

    move-wide/from16 v0, v18

    move-wide/from16 v25, v0

    const/4 v8, 0x1

    move/from16 v27, v8

    move/from16 v0, v17

    int-to-long v0, v0

    move-wide/from16 v28, v0

    invoke-static/range {v24 .. v29}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structSet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JIJ)V

    .line 5
    move-wide/from16 v0, v18

    const/4 v8, 0x0

    invoke-static {v14, v0, v1, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    long-to-int v8, v0

    int-to-byte v8, v8

    int-to-long v0, v8

    long-to-int v15, v0

    move/from16 v20, v15

    .line 6
    move-wide/from16 v0, v18

    const/4 v8, 0x0

    invoke-static {v14, v0, v1, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    long-to-int v8, v0

    and-int/lit16 v8, v8, 0xff

    int-to-long v0, v8

    long-to-int v15, v0

    move/from16 v21, v15

    .line 7
    add-int v20, v20, v21

    .line 8
    move-wide/from16 v0, v18

    const/4 v8, 0x1

    invoke-static {v14, v0, v1, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    long-to-int v8, v0

    int-to-short v8, v8

    int-to-long v0, v8

    long-to-int v15, v0

    move/from16 v21, v15

    .line 9
    move-wide/from16 v0, v18

    const/4 v8, 0x1

    invoke-static {v14, v0, v1, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    long-to-int v8, v0

    int-to-char v8, v8

    int-to-long v0, v8

    long-to-int v15, v0

    move/from16 v22, v15

    .line 10
    add-int v21, v21, v22

    .line 11
    add-int v17, v20, v21

    .line 12
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 36

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v18, v15

    move/from16 v19, v15

    move-wide/from16 v20, v0

    .line 2
    move-object/from16 v25, v14

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x2

    aget-wide v4, v12, v9

    long-to-int v8, v4

    move/from16 v26, v8

    move/from16 v27, v17

    const-wide/16 v28, 0x3

    invoke-static/range {v25 .. v29}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->arrayNew(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIJ)J

    move-result-wide v20

    .line 3
    const/4 v9, 0x2

    move/from16 v8, v17

    invoke-static {v8, v9}, Ljava/lang/Integer;->divideUnsigned(II)I

    move-result v8

    move/from16 v23, v8

    .line 4
    move-object/from16 v25, v14

    move-wide/from16 v26, v20

    const/16 v28, 0x0

    const-wide/16 v29, 0x7

    move/from16 v31, v23

    invoke-static/range {v25 .. v31}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->arrayFill(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JIJI)V

    .line 5
    move-object/from16 v25, v14

    move-wide/from16 v0, v20

    move-wide/from16 v26, v0

    const/4 v8, 0x0

    move/from16 v28, v8

    const-wide/16 v0, 0x64

    move-wide/from16 v29, v0

    invoke-static/range {v25 .. v30}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->arraySet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JIJ)V

    .line 7
    :goto_4c
    move-wide/from16 v0, v20

    invoke-static {v14, v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->arrayLen(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)J

    move-result-wide v0

    long-to-int v15, v0

    move/from16 v22, v15

    .line 8
    const/high16 v10, -0x80000000

    add-int v8, v18, v10

    add-int v9, v22, v10

    if-ge v8, v9, :cond_6d

    .line 9
    move-wide/from16 v0, v20

    move/from16 v10, v18

    invoke-static {v14, v0, v1, v10}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->arrayGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    long-to-int v15, v0

    move/from16 v22, v15

    .line 10
    add-int v19, v19, v22

    .line 11
    add-int/lit8 v18, v18, 0x1

    .line 12
    goto :goto_4c

    .line 13
    :cond_6d
    move/from16 v8, v19

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 46

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v8, 0x2

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openRoots(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v0

    move-wide/from16 v29, v0

    long-to-int v8, v0

    move/from16 v18, v8

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v32, v12

    move/from16 v20, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v21, v0

    move-wide/from16 v23, v0

    const/4 v8, 0x0

    move-object/from16 v31, v8

    .line 2
    move/from16 v25, v20

    .line 3
    const/16 v26, 0x2

    .line 4
    const/16 v27, 0x3

    .line 5
    const/16 v28, 0x4

    .line 6
    move-object/from16 v12, v31

    if-nez v12, :cond_38

    const/4 v8, 0x4

    new-array v12, v8, [J

    move-object/from16 v31, v12

    :cond_38
    move/from16 v0, v25

    int-to-long v0, v0

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move/from16 v0, v26

    int-to-long v0, v0

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    move/from16 v0, v27

    int-to-long v0, v0

    const/4 v10, 0x2

    aput-wide v0, v12, v10

    move/from16 v0, v28

    int-to-long v0, v0

    const/4 v10, 0x3

    aput-wide v0, v12, v10

    move-object/from16 v36, v12

    move-object/from16 v34, v14

    move-object/from16 v12, v32

    const/4 v9, 0x2

    aget-wide v4, v12, v9

    long-to-int v8, v4

    move/from16 v35, v8

    const/4 v8, 0x4

    move/from16 v37, v8

    invoke-static/range {v34 .. v37}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->arrayNewFixed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[JI)J

    move-result-wide v0

    move-wide/from16 v21, v0

    add-int/lit8 v19, v18, 0x0

    aput-wide v0, v17, v19

    .line 7
    move-object/from16 v12, v32

    const/4 v9, 0x2

    aget-wide v4, v12, v9

    long-to-int v11, v4

    const/4 v8, 0x4

    const-wide/16 v2, 0x0

    invoke-static {v14, v11, v8, v2, v3}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->arrayNewDefault(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIJ)J

    move-result-wide v0

    move-wide/from16 v23, v0

    .line 8
    move-object/from16 v34, v14

    move-wide/from16 v0, v21

    move-wide/from16 v35, v0

    const/4 v8, 0x0

    move/from16 v37, v8

    move-wide/from16 v0, v23

    move-wide/from16 v38, v0

    const/4 v8, 0x1

    move/from16 v40, v8

    const/4 v8, 0x3

    move/from16 v41, v8

    invoke-static/range {v34 .. v41}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->arrayCopy(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JIJII)V

    .line 9
    move-wide/from16 v0, v23

    const/4 v10, 0x1

    invoke-static {v14, v0, v1, v10}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->arrayGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    long-to-int v15, v0

    move/from16 v25, v15

    .line 10
    move-wide/from16 v0, v23

    const/4 v10, 0x3

    invoke-static {v14, v0, v1, v10}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->arrayGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    long-to-int v15, v0

    move/from16 v26, v15

    .line 11
    add-int v20, v25, v26

    .line 12
    move-wide/from16 v6, v29

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

    move/from16 v17, p3

    .line 2
    move/from16 v8, v17

    invoke-static {v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refI31(I)J

    move-result-wide v0

    move-wide/from16 v18, v0

    .line 3
    move-wide/from16 v0, v18

    invoke-static {v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->i31GetSigned(J)J

    move-result-wide v0

    long-to-int v15, v0

    move/from16 v20, v15

    .line 4
    move/from16 v8, v17

    invoke-static {v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refI31(I)J

    move-result-wide v0

    move-wide/from16 v21, v0

    .line 5
    move-wide/from16 v0, v21

    invoke-static {v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->i31GetUnsigned(J)J

    move-result-wide v0

    long-to-int v15, v0

    move/from16 v23, v15

    .line 6
    add-int v17, v20, v23

    .line 7
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 44

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v8, 0x3

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->openRoots(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)J

    move-result-wide v0

    move-wide/from16 v30, v0

    long-to-int v8, v0

    move/from16 v18, v8

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v17

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v33, v12

    move/from16 v20, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    const-wide/32 v2, 0x30e01

    move-wide/from16 v21, v2

    move/from16 v23, v15

    move/from16 v24, v15

    move/from16 v27, v15

    move-wide/from16 v25, v0

    move-wide/from16 v28, v0

    const/4 v8, 0x0

    move-object/from16 v32, v8

    .line 3
    :goto_32
    const/high16 v10, -0x80000000

    add-int v8, v23, v10

    add-int v9, v20, v10

    if-ge v8, v9, :cond_7b

    .line 4
    move/from16 v27, v23

    move-wide/from16 v28, v21

    add-int/lit8 v19, v18, 0x2

    aput-wide v28, v17, v19

    .line 5
    move-object/from16 v12, v33

    const/4 v9, 0x3

    aget-wide v4, v12, v9

    long-to-int v11, v4

    move-object/from16 v12, v32

    if-nez v12, :cond_51

    const/4 v8, 0x2

    new-array v12, v8, [J

    move-object/from16 v32, v12

    :cond_51
    move/from16 v0, v27

    int-to-long v0, v0

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move-wide/from16 v0, v28

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    invoke-static {v14, v11, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structNew(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J)J

    move-result-wide v0

    move-wide/from16 v21, v0

    add-int/lit8 v19, v18, 0x0

    aput-wide v0, v17, v19

    .line 6
    move-object/from16 v35, v14

    move-object/from16 v12, v33

    const/4 v9, 0x2

    aget-wide v4, v12, v9

    long-to-int v8, v4

    move/from16 v36, v8

    const/16 v37, 0x40

    const-wide/16 v38, 0x0

    invoke-static/range {v35 .. v39}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->arrayNew(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIJ)J

    move-result-wide v25

    .line 7
    add-int/lit8 v23, v23, 0x1

    .line 8
    goto :goto_32

    .line 10
    :cond_7b
    :goto_7b
    move-wide/from16 v0, v21

    long-to-int v8, v0

    and-int/lit16 v8, v8, 0xff

    const/4 v9, 0x1

    if-eq v8, v9, :cond_97

    .line 11
    move-wide/from16 v25, v21

    .line 12
    move-wide/from16 v0, v25

    long-to-int v8, v0

    and-int/lit16 v8, v8, 0xff

    const/4 v9, 0x1

    if-eq v8, v9, :cond_a3

    .line 13
    move-wide/from16 v0, v25

    const/4 v8, 0x1

    invoke-static {v14, v0, v1, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structGet(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JI)J

    move-result-wide v0

    move-wide/from16 v21, v0

    .line 14
    goto :goto_7b

    .line 15
    :cond_97
    const/16 v23, 0x0

    .line 16
    move-wide/from16 v6, v30

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move/from16 v8, v20

    return v8

    const/4 v8, 0x0

    return v8

    .line 12
    :cond_a3
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
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

    const-wide/16 v2, 0x701

    move-wide/from16 v21, v2

    move/from16 v25, v15

    move-wide/from16 v23, v0

    move/from16 v28, v15

    move-wide/from16 v26, v0

    const/4 v8, 0x0

    move-object/from16 v33, v8

    .line 2
    if-eqz v20, :cond_57

    .line 3
    const/16 v25, 0x5

    .line 4
    const-wide/16 v26, 0x6

    add-int/lit8 v19, v18, 0x2

    aput-wide v26, v17, v19

    .line 5
    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x0

    aget-wide v4, v12, v9

    long-to-int v11, v4

    move-object/from16 v12, v33

    if-nez v12, :cond_44

    const/4 v8, 0x2

    new-array v12, v8, [J

    move-object/from16 v33, v12

    :cond_44
    move/from16 v0, v25

    int-to-long v0, v0

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move-wide/from16 v0, v26

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    invoke-static {v14, v11, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->structNew(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J)J

    move-result-wide v0

    move-wide/from16 v23, v0

    .line 6
    goto/16 :goto_5f

    .line 7
    :cond_57
    const/16 v8, 0x9

    invoke-static {v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refI31(I)J

    move-result-wide v0

    move-wide/from16 v23, v0

    .line 8
    :goto_5f
    move-wide/from16 v21, v23

    .line 9
    move-wide/from16 v0, v21

    const/16 v9, 0x1c

    aget-object v12, v16, v9

    invoke-static {v14, v0, v1, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->castMatches(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JLjava/lang/Object;)Z

    move-result v8

    if-nez v8, :cond_9e

    .line 10
    move-wide/from16 v26, v21

    .line 11
    const/16 v9, 0xe

    aget-object v12, v16, v9

    move-wide/from16 v0, v26

    invoke-static {v14, v0, v1, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refCast(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JLjava/lang/Object;)J

    move-result-wide v0

    move-wide/from16 v26, v0

    .line 12
    move-wide/from16 v0, v26

    invoke-static {v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->i31GetUnsigned(J)J

    move-result-wide v0

    long-to-int v15, v0

    move/from16 v28, v15

    .line 13
    move-wide/from16 v6, v31

    invoke-static {v14, v6, v7}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->closeFrame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)V

    move/from16 v8, v28

    return v8

    .line 14
    :goto_8c
    move-wide/from16 v0, v23

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
    :cond_9e
    move-wide/from16 v23, v21

    goto :goto_8c

    const/4 v8, 0x0

    return v8
.end method

.method public static f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 28

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v20, v15

    move-wide/from16 v18, v0

    .line 2
    if-eqz v17, :cond_1a

    .line 3
    const/4 v8, 0x3

    invoke-static {v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->refI31(I)J

    move-result-wide v0

    move-wide/from16 v18, v0

    .line 4
    goto/16 :goto_1e

    .line 5
    :cond_1a
    const-wide/16 v0, 0x701

    move-wide/from16 v18, v0

    .line 6
    :goto_1e
    move-wide/from16 v0, v18

    const/16 v9, 0xe

    aget-object v12, v16, v9

    invoke-static {v14, v0, v1, v12}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->castMatches(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;JLjava/lang/Object;)Z

    move-result v8

    if-eqz v8, :cond_36

    .line 7
    move-wide/from16 v0, v18

    invoke-static {v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;->i31GetSigned(J)J

    move-result-wide v0

    long-to-int v15, v0

    move/from16 v20, v15

    .line 8
    move/from16 v8, v20

    return v8

    .line 9
    :cond_36
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

    invoke-virtual {p0}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->getInstance()Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    move-result-object v4

    const/4 v2, 0x1

    aput-object v4, p1, v2

    const/4 v2, 0x0

    const/4 v3, -0x7

    invoke-virtual {p0, v2, v3}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->typeTest(ZI)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0xe

    aput-object v4, p1, v2

    const/4 v2, 0x0

    const/4 v3, 0x0

    invoke-virtual {p0, v2, v3}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->typeTest(ZI)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x1c

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

    const/16 v2, 0x9

    aput-wide v0, v5, v2

    const/4 v2, 0x1

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->functionReference(I)J

    move-result-wide v0

    const/16 v2, 0xa

    aput-wide v0, v5, v2

    return-void
.end method


# virtual methods
.method public accept(Ljava/lang/Object;)V
    .registers 5

    check-cast p1, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;

    const/16 v0, 0x1d

    new-array v1, v0, [Ljava/lang/Object;

    const/16 v0, 0xb

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
