.class public final Lchasm/jit/Code0;
.super Ljava/lang/Object;
.source "module.wasm"


# direct methods
.method public static f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)J
    .registers 39

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x12

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_12

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    :cond_12
    move v13, v8

    move-wide/from16 v17, p3

    move-wide/from16 v19, p5

    .line 2
    :goto_17
    const-wide/16 v2, 0x0

    cmp-long v8, v17, v2

    if-nez v8, :cond_21

    .line 3
    move-wide/from16 v21, v19

    .line 4
    goto/16 :goto_30

    .line 5
    :cond_21
    const-wide/16 v2, 0x1

    sub-long v21, v17, v2

    .line 6
    add-long v23, v19, v17

    .line 7
    move-wide/from16 v25, v21

    move-wide/from16 v27, v23

    move-wide/from16 v17, v25

    move-wide/from16 v19, v27

    goto :goto_17

    .line 8
    :goto_30
    move-wide/from16 v0, v21

    return-wide v0

    const-wide/16 v0, 0x0

    return-wide v0
.end method

.method public static f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJ)J
    .registers 35

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x11

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_12

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    :cond_12
    move v13, v8

    move-wide/from16 v17, p3

    .line 2
    const-wide/16 v2, 0x0

    cmp-long v8, v17, v2

    if-nez v8, :cond_1f

    .line 3
    const-wide/16 v19, 0x1

    .line 4
    goto/16 :goto_46

    .line 5
    :cond_1f
    const-wide/16 v2, 0x1

    sub-long v19, v17, v2

    .line 6
    const/4 v8, 0x3

    new-array v10, v8, [J

    const/4 v11, 0x0

    const/4 v9, 0x0

    move-wide/from16 v0, v19

    aput-wide v0, v10, v9

    const/4 v9, 0x1

    const-wide/16 v0, 0x7

    aput-wide v0, v10, v9

    const/4 v9, 0x2

    const-wide/16 v0, 0x3

    aput-wide v0, v10, v9

    const/4 v9, 0x1

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    const/4 v9, 0x6

    aget-object v3, v16, v9

    check-cast v3, Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    invoke-static {v14, v2, v3, v10, v11}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->requestTailObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;[J[Ljava/lang/Object;)V

    const-wide/16 v0, 0x0

    return-wide v0

    .line 7
    :goto_46
    move-wide/from16 v0, v19

    return-wide v0

    const-wide/16 v0, 0x0

    return-wide v0
.end method

.method public static f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJI)J
    .registers 37

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x12

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_12

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    :cond_12
    move v13, v8

    move-wide/from16 v17, p3

    move-wide/from16 v19, p5

    move/from16 v21, p7

    .line 2
    const-wide/16 v2, 0x0

    cmp-long v8, v17, v2

    if-nez v8, :cond_23

    .line 3
    const-wide/16 v22, 0x2

    .line 4
    goto/16 :goto_40

    .line 5
    :cond_23
    const-wide/16 v2, 0x1

    sub-long v22, v17, v2

    .line 6
    const/4 v8, 0x1

    new-array v10, v8, [J

    const/4 v11, 0x0

    const/4 v9, 0x0

    move-wide/from16 v0, v22

    aput-wide v0, v10, v9

    const/4 v9, 0x1

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    const/4 v9, 0x5

    aget-object v3, v16, v9

    check-cast v3, Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    invoke-static {v14, v2, v3, v10, v11}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->requestTailObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;[J[Ljava/lang/Object;)V

    const-wide/16 v0, 0x0

    return-wide v0

    .line 7
    :goto_40
    move-wide/from16 v0, v22

    return-wide v0

    const-wide/16 v0, 0x0

    return-wide v0
.end method

.method public static f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)J
    .registers 41

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

    move-wide/from16 v17, p3

    move-wide/from16 v19, p5

    .line 2
    const-wide/16 v2, 0x0

    cmp-long v8, v17, v2

    if-nez v8, :cond_21

    .line 3
    move-wide/from16 v21, v19

    .line 4
    goto/16 :goto_60

    .line 5
    :cond_21
    const-wide/16 v2, 0x1

    sub-long v21, v17, v2

    .line 6
    const-wide/16 v2, 0x2

    add-long v23, v19, v2

    .line 7
    const/4 v8, 0x2

    new-array v10, v8, [J

    const/4 v11, 0x0

    const/4 v9, 0x0

    move-wide/from16 v0, v21

    aput-wide v0, v10, v9

    const/4 v9, 0x1

    move-wide/from16 v0, v23

    aput-wide v0, v10, v9

    move-object/from16 v26, v14

    move-object/from16 v31, v10

    move-object/from16 v32, v11

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x0

    aget-wide v4, v12, v9

    long-to-int v8, v4

    move/from16 v29, v8

    const/4 v9, 0x1

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    move-object/from16 v27, v2

    const/16 v9, 0x2f

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;

    move-object/from16 v28, v2

    const/4 v8, 0x1

    move/from16 v30, v8

    invoke-static/range {v26 .. v32}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->requestTailIndirectObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;II[J[Ljava/lang/Object;)V

    const-wide/16 v0, 0x0

    return-wide v0

    .line 8
    :goto_60
    move-wide/from16 v0, v21

    return-wide v0

    const-wide/16 v0, 0x0

    return-wide v0
.end method

.method public static f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)J
    .registers 39

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x12

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_12

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    :cond_12
    move v13, v8

    move-wide/from16 v17, p3

    move-wide/from16 v19, p5

    .line 2
    const-wide/16 v2, 0x0

    cmp-long v8, v17, v2

    if-nez v8, :cond_21

    .line 3
    move-wide/from16 v21, v19

    .line 4
    goto/16 :goto_5c

    .line 5
    :cond_21
    const-wide/16 v2, 0x1

    sub-long v21, v17, v2

    .line 6
    const/4 v8, 0x2

    new-array v10, v8, [J

    const/4 v11, 0x0

    const/4 v9, 0x0

    move-wide/from16 v0, v21

    aput-wide v0, v10, v9

    const/4 v9, 0x1

    move-wide/from16 v0, v19

    aput-wide v0, v10, v9

    move-object/from16 v24, v14

    move-object/from16 v29, v10

    move-object/from16 v30, v11

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x0

    aget-wide v4, v12, v9

    long-to-int v8, v4

    move/from16 v27, v8

    const/4 v9, 0x1

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    move-object/from16 v25, v2

    const/16 v9, 0x2f

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;

    move-object/from16 v26, v2

    const/4 v8, 0x0

    move/from16 v28, v8

    invoke-static/range {v24 .. v30}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->requestTailIndirectObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;II[J[Ljava/lang/Object;)V

    const-wide/16 v0, 0x0

    return-wide v0

    .line 7
    :goto_5c
    move-wide/from16 v0, v21

    return-wide v0

    const-wide/16 v0, 0x0

    return-wide v0
.end method

.method public static f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJ)J
    .registers 33

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x11

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_12

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    :cond_12
    move v13, v8

    move-object/from16 v21, v16

    move-object/from16 v22, v14

    move/from16 v23, v13

    move-wide/from16 v17, p3

    .line 2
    move-wide/from16 v24, v17

    const-wide/16 v26, 0x0

    invoke-static/range {v21 .. v27}, Lchasm/jit/Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)J

    move-result-wide v0

    iget-object v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->pendingTailFunction:Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    if-eqz v8, :cond_38

    const/4 v8, 0x1

    new-array v10, v8, [J

    const/4 v11, 0x0

    invoke-static {v14, v10, v11, v13}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->completeTailObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[J[Ljava/lang/Object;I)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-eqz v8, :cond_35

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->raiseUnwinding(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)V

    :cond_35
    const/4 v8, 0x0

    aget-wide v0, v10, v8

    :cond_38
    move-wide/from16 v19, v0

    .line 3
    move-wide/from16 v0, v19

    return-wide v0

    const-wide/16 v0, 0x0

    return-wide v0
.end method

.method public static f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)[J
    .registers 27

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    move/from16 v18, p4

    .line 2
    const/4 v8, 0x2

    new-array v12, v8, [J

    move/from16 v0, v18

    int-to-long v0, v0

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move/from16 v0, v17

    int-to-long v0, v0

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    return-object v12

    const/4 v8, 0x0

    return-object v8
.end method

.method public static f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)[J
    .registers 33

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x11

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_11

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return-object v8

    :cond_11
    move v13, v8

    move/from16 v17, p3

    move/from16 v18, p4

    .line 2
    :goto_16
    if-nez v17, :cond_36

    .line 3
    const/4 v8, 0x2

    new-array v10, v8, [J

    const/4 v11, 0x0

    const/4 v9, 0x0

    move/from16 v0, v18

    int-to-long v0, v0

    aput-wide v0, v10, v9

    const/4 v9, 0x1

    const-wide/16 v0, 0x9

    aput-wide v0, v10, v9

    const/4 v9, 0x1

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    const/4 v9, 0x7

    aget-object v3, v16, v9

    check-cast v3, Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    invoke-static {v14, v2, v3, v10, v11}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->requestTailObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;[J[Ljava/lang/Object;)V

    const/4 v8, 0x0

    return-object v8

    .line 4
    :cond_36
    add-int/lit8 v19, v17, -0x1

    .line 5
    add-int/lit8 v20, v18, 0x1

    .line 6
    move/from16 v21, v19

    move/from16 v23, v20

    move/from16 v17, v21

    move/from16 v18, v23

    goto :goto_16

    const/4 v8, 0x0

    return-object v8
.end method

.method public static f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
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

    move-object/from16 v20, v16

    move-object/from16 v21, v14

    move/from16 v22, v13

    move/from16 v17, p3

    .line 2
    move/from16 v23, v17

    const/16 v24, 0x0

    invoke-static/range {v20 .. v24}, Lchasm/jit/Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)[J

    move-result-object v12

    iget-object v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->pendingTailFunction:Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    if-eqz v8, :cond_35

    const/4 v8, 0x2

    new-array v10, v8, [J

    const/4 v11, 0x0

    invoke-static {v14, v10, v11, v13}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->completeTailObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[J[Ljava/lang/Object;I)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-eqz v8, :cond_34

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->raiseUnwinding(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)V

    :cond_34
    move-object v12, v10

    :cond_35
    const/4 v10, 0x0

    aget-wide v0, v12, v10

    long-to-int v15, v0

    move/from16 v18, v15

    const/4 v10, 0x1

    aget-wide v0, v12, v10

    long-to-int v15, v0

    move/from16 v19, v15

    .line 3
    sub-int v17, v18, v19

    .line 4
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJ)J
    .registers 31

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x10

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_12

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    :cond_12
    move v13, v8

    move-object/from16 v21, v16

    move-object/from16 v22, v14

    move/from16 v23, v13

    move-wide/from16 v17, p3

    .line 2
    move-wide/from16 v24, v17

    invoke-static/range {v21 .. v25}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJ)J

    move-result-wide v0

    iget-object v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->pendingTailFunction:Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    if-eqz v8, :cond_36

    const/4 v8, 0x1

    new-array v10, v8, [J

    const/4 v11, 0x0

    invoke-static {v14, v10, v11, v13}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->completeTailObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[J[Ljava/lang/Object;I)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-eqz v8, :cond_33

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->raiseUnwinding(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)V

    :cond_33
    const/4 v8, 0x0

    aget-wide v0, v10, v8

    :cond_36
    move-wide/from16 v19, v0

    .line 3
    const-wide/16 v2, 0x64

    add-long v17, v19, v2

    .line 4
    move-wide/from16 v0, v17

    return-wide v0

    const-wide/16 v0, 0x0

    return-wide v0
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
    .registers 16

    :try_start_0
    move-object v2, p0

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p3

    const/4 v3, 0x0

    aget-wide v8, p2, v3

    const/4 v3, 0x1

    aget-wide v10, p2, v3

    packed-switch v4, :pswitch_data_34

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_15

    :goto_15
    :pswitch_15
    invoke-static/range {v5 .. v11}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)J

    move-result-wide v0

    goto/16 :goto_27

    :pswitch_1b
    invoke-static/range {v5 .. v11}, Lchasm/jit/Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)J

    move-result-wide v0

    goto/16 :goto_27

    :pswitch_21
    invoke-static/range {v5 .. v11}, Lchasm/jit/Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)J

    move-result-wide v0

    goto/16 :goto_27

    :goto_27
    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_2a
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_2a} :catch_2b
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_2a} :catch_2f

    return-void

    :catch_2b
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_2f
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    nop

    :pswitch_data_34
    .packed-switch 0x0
        :pswitch_15
        :pswitch_1b
        :pswitch_21
    .end packed-switch
.end method

.method public invoke(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 16

    :try_start_0
    move-object v2, p0

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    invoke-static {v6}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->depth(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v7

    add-int/lit16 v3, p3, 0x0

    aget-wide v8, p2, v3

    add-int/lit16 v3, p3, 0x1

    aget-wide v10, p2, v3

    packed-switch v4, :pswitch_data_3c

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_1a

    :goto_1a
    :pswitch_1a
    invoke-static/range {v5 .. v11}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)J

    move-result-wide v0

    goto/16 :goto_2c

    :pswitch_20
    invoke-static/range {v5 .. v11}, Lchasm/jit/Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)J

    move-result-wide v0

    goto/16 :goto_2c

    :pswitch_26
    invoke-static/range {v5 .. v11}, Lchasm/jit/Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)J

    move-result-wide v0

    goto/16 :goto_2c

    :goto_2c
    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_32
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_32} :catch_33
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_32} :catch_37

    return-void

    :catch_33
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_37
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    nop

    :pswitch_data_3c
    .packed-switch 0x0
        :pswitch_1a
        :pswitch_20
        :pswitch_26
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
    .registers 14

    :try_start_0
    move-object v2, p0

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p3

    const/4 v3, 0x0

    aget-wide v8, p2, v3

    packed-switch v4, :pswitch_data_30

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_12

    :goto_12
    :pswitch_12
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJ)J

    move-result-wide v0

    goto/16 :goto_24

    :pswitch_18
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJ)J

    move-result-wide v0

    goto/16 :goto_24

    :pswitch_1e
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJ)J

    move-result-wide v0

    goto/16 :goto_24

    :goto_24
    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_27
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_27} :catch_28
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_27} :catch_2c

    return-void

    :catch_28
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_2c
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_30
    .packed-switch 0x0
        :pswitch_12
        :pswitch_18
        :pswitch_1e
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

    aget-wide v8, p2, v3

    packed-switch v4, :pswitch_data_38

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_16

    :goto_16
    :pswitch_16
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJ)J

    move-result-wide v0

    goto/16 :goto_28

    :pswitch_1c
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJ)J

    move-result-wide v0

    goto/16 :goto_28

    :pswitch_22
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJ)J

    move-result-wide v0

    goto/16 :goto_28

    :goto_28
    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_2e
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_2e} :catch_2f
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_2e} :catch_33

    return-void

    :catch_2f
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_33
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    nop

    :pswitch_data_38
    .packed-switch 0x0
        :pswitch_16
        :pswitch_1c
        :pswitch_22
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
    .registers 17

    :try_start_0
    move-object v2, p0

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move/from16 v7, p3

    const/4 v3, 0x0

    aget-wide v8, p2, v3

    const/4 v3, 0x1

    aget-wide v10, p2, v3

    const/4 v3, 0x2

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v12, v2

    invoke-static/range {v5 .. v12}, Lchasm/jit/Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJI)J

    move-result-wide v0

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_18
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_18} :catch_19
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_18} :catch_1d

    return-void

    :catch_19
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_1d
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void
.end method

.method public invoke(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 17

    :try_start_0
    move-object v2, p0

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    invoke-static {v6}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->depth(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v7

    add-int/lit8 v3, p3, 0x0

    aget-wide v8, p2, v3

    add-int/lit8 v3, p3, 0x1

    aget-wide v10, p2, v3

    add-int/lit8 v3, p3, 0x2

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v12, v2

    invoke-static/range {v5 .. v12}, Lchasm/jit/Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJI)J

    move-result-wide v0

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_20
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_20} :catch_21
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_20} :catch_25

    return-void

    :catch_21
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_25
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

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

    packed-switch v4, :pswitch_data_3a

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_19

    :goto_19
    :pswitch_19
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)[J

    move-result-object v2

    goto/16 :goto_25

    :pswitch_1f
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)[J

    move-result-object v2

    goto/16 :goto_25

    :goto_25
    if-eqz v2, :cond_31

    const/4 v3, 0x0

    aget-wide v0, v2, v3

    aput-wide v0, p2, v3

    const/4 v3, 0x1

    aget-wide v0, v2, v3

    aput-wide v0, p2, v3
    :try_end_31
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_31} :catch_32
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_31} :catch_36

    :cond_31
    return-void

    :catch_32
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_36
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_3a
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

    packed-switch v4, :pswitch_data_4a

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_1e

    :goto_1e
    :pswitch_1e
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)[J

    move-result-object v2

    goto/16 :goto_2a

    :pswitch_24
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)[J

    move-result-object v2

    goto/16 :goto_2a

    :goto_2a
    if-eqz v2, :cond_40

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    const/4 v3, 0x0

    aget-wide v0, v2, v3

    const/4 v3, 0x0

    add-int v3, p3, v3

    aput-wide v0, v5, v3

    const/4 v3, 0x1

    aget-wide v0, v2, v3

    const/4 v3, 0x1

    add-int v3, p3, v3

    aput-wide v0, v5, v3
    :try_end_40
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_40} :catch_41
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_40} :catch_45

    :cond_40
    return-void

    :catch_41
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_45
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    nop

    :pswitch_data_4a
    .packed-switch 0x0
        :pswitch_1e
        :pswitch_24
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

    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_12
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_12} :catch_13
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_12} :catch_17

    return-void

    :catch_13
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_17
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

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

    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    int-to-long v0, v2

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_19
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_19} :catch_1a
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_19} :catch_1e

    return-void

    :catch_1a
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_1e
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

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

    const/4 v2, 0x1

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->function(I)Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    move-result-object v4

    const/4 v2, 0x5

    aput-object v4, p1, v2

    const/4 v2, 0x2

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->function(I)Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    move-result-object v4

    const/4 v2, 0x6

    aput-object v4, p1, v2

    const/4 v2, 0x6

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->function(I)Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    move-result-object v4

    const/4 v2, 0x7

    aput-object v4, p1, v2

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->referenceTable(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x2f

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

    return-void
.end method


# virtual methods
.method public accept(Ljava/lang/Object;)V
    .registers 5

    check-cast p1, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;

    const/16 v0, 0x30

    new-array v1, v0, [Ljava/lang/Object;

    const/4 v0, 0x1

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

    new-instance v4, Lchasm/jit/Entry2;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x2

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry0;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry0;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x3

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry0;

    const/4 v3, 0x2

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry0;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x4

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry1;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry1;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x5

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry3;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry3;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x6

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry3;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry3;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x7

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry4;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry4;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x8

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry1;

    const/4 v3, 0x2

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry1;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x9

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    return-void
.end method
