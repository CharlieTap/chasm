.class public final Lchasm/dex/WasmModule$Code0;
.super Ljava/lang/Object;
.source "module.wasm"


# direct methods
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

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v24, v12

    move-object/from16 v25, v16

    move-object/from16 v26, v14

    move/from16 v27, v13

    move/from16 v17, p3

    move/from16 v18, p4

    move/from16 v19, p5

    const/4 v8, 0x0

    move-object/from16 v23, v8

    .line 2
    move/from16 v20, v18

    move/from16 v21, v19

    .line 3
    if-eqz v17, :cond_37

    .line 4
    const/4 v9, 0x4

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFunction;

    move-object/from16 v22, v12

    .line 5
    goto/16 :goto_3e

    .line 6
    :cond_37
    const/4 v9, 0x5

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFunction;

    move-object/from16 v22, v12

    .line 7
    :goto_3e
    move-object/from16 v12, v22

    invoke-static {v12}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->referenceTarget(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v12

    instance-of v11, v12, Lchasm/dex/WasmModule$Sig0;

    if-eqz v11, :cond_5e

    move-object/from16 v25, v12

    move/from16 v28, v20

    move/from16 v29, v21

    move/from16 v27, v13

    invoke-interface/range {v25 .. v29}, Lchasm/dex/WasmModule$Sig0;->typed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v9

    move-object/from16 v25, v16

    move-object/from16 v26, v14

    move/from16 v27, v13

    move/from16 v20, v9

    goto/16 :goto_9a

    :cond_5e
    move-object/from16 v12, v23

    if-nez v12, :cond_67

    const/4 v8, 0x2

    new-array v12, v8, [J

    move-object/from16 v23, v12

    :cond_67
    move-object v10, v12

    const/4 v11, 0x0

    const/4 v9, 0x0

    move/from16 v0, v20

    int-to-long v0, v0

    aput-wide v0, v10, v9

    const/4 v9, 0x1

    move/from16 v0, v21

    int-to-long v0, v0

    aput-wide v0, v10, v9

    move-object/from16 v26, v14

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    move-object/from16 v27, v12

    move-object/from16 v28, v22

    move-object/from16 v29, v10

    move-object/from16 v30, v11

    move/from16 v31, v13

    invoke-static/range {v26 .. v31}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->callReferenceObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;Ljava/lang/Object;[J[Ljava/lang/Object;I)V

    move-object/from16 v26, v14

    move/from16 v27, v13

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-eqz v8, :cond_94

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->raiseUnwinding(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)V

    :cond_94
    const/4 v9, 0x0

    aget-wide v0, v10, v9

    long-to-int v15, v0

    move/from16 v20, v15

    .line 8
    :goto_9a
    move/from16 v8, v20

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f11([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
    .registers 32

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

    const/4 v8, 0x0

    move-object/from16 v19, v8

    .line 2
    const/4 v12, 0x0

    move-object/from16 v18, v12

    .line 3
    move-object/from16 v12, v18

    invoke-static {v12}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->referenceTarget(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v12

    instance-of v11, v12, Lchasm/dex/WasmModule$Sig0;

    if-eqz v11, :cond_3e

    move-object/from16 v20, v12

    const/16 v23, 0x1

    const/16 v24, 0x2

    move/from16 v22, v13

    invoke-interface/range {v20 .. v24}, Lchasm/dex/WasmModule$Sig0;->typed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v9

    move-object/from16 v20, v16

    move-object/from16 v21, v14

    move/from16 v22, v13

    move/from16 v17, v9

    goto/16 :goto_78

    :cond_3e
    move-object/from16 v12, v19

    if-nez v12, :cond_47

    const/4 v8, 0x2

    new-array v12, v8, [J

    move-object/from16 v19, v12

    :cond_47
    move-object v10, v12

    const/4 v11, 0x0

    const/4 v9, 0x0

    const-wide/16 v0, 0x1

    aput-wide v0, v10, v9

    const/4 v9, 0x1

    const-wide/16 v0, 0x2

    aput-wide v0, v10, v9

    move-object/from16 v21, v14

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    move-object/from16 v22, v12

    move-object/from16 v23, v18

    move-object/from16 v24, v10

    move-object/from16 v25, v11

    move/from16 v26, v13

    invoke-static/range {v21 .. v26}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->callReferenceObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;Ljava/lang/Object;[J[Ljava/lang/Object;I)V

    move-object/from16 v21, v14

    move/from16 v22, v13

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-eqz v8, :cond_72

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->raiseUnwinding(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)V

    :cond_72
    const/4 v9, 0x0

    aget-wide v0, v10, v9

    long-to-int v15, v0

    move/from16 v17, v15

    .line 4
    :goto_78
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f12([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 26

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    .line 2
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_16

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_16
    move/from16 v8, v17

    shl-int/lit8 v8, v8, 0x1

    ushr-int/lit8 v8, v8, 0x1

    new-instance v12, Lio/github/charlietap/chasm/reference/I31Reference;

    invoke-direct {v12, v8}, Lio/github/charlietap/chasm/reference/I31Reference;-><init>(I)V

    move-object/from16 v18, v12

    .line 5
    move-object/from16 v12, v18

    if-eqz v12, :cond_2b

    instance-of v8, v12, Lio/github/charlietap/chasm/reference/I31Reference;

    if-nez v8, :cond_30

    :cond_2b
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->castFailed()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 6
    :cond_30
    move-object/from16 v12, v18

    if-eqz v12, :cond_43

    check-cast v12, Lio/github/charlietap/chasm/reference/I31Reference;

    iget v8, v12, Lio/github/charlietap/chasm/reference/I31Reference;->value:I

    shl-int/lit8 v8, v8, 0x1

    shr-int/lit8 v8, v8, 0x1

    move/from16 v17, v8

    .line 7
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 6
    :cond_43
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f13([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
    .registers 24

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    .line 2
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_14

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_14
    const/4 v8, 0x1

    shl-int/lit8 v8, v8, 0x1

    ushr-int/lit8 v8, v8, 0x1

    new-instance v12, Lio/github/charlietap/chasm/reference/I31Reference;

    invoke-direct {v12, v8}, Lio/github/charlietap/chasm/reference/I31Reference;-><init>(I)V

    move-object/from16 v17, v12

    .line 3
    move-object/from16 v12, v17

    if-eqz v12, :cond_34

    instance-of v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    if-eqz v8, :cond_34

    move-object v11, v12

    check-cast v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;->rtt:Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    const/4 v9, 0x7

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    if-eq v11, v10, :cond_39

    :cond_34
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->castFailed()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 4
    :cond_39
    const/4 v8, 0x0

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIJ)J
    .registers 42

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v19, p3

    move-wide/from16 v20, p4

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v22, v15

    .line 2
    move/from16 v25, v19

    move-wide/from16 v26, v20

    .line 3
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_21

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_21
    const/4 v9, 0x7

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_II;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_II;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v25

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I;->i0:I

    move-wide/from16 v2, v26

    long-to-int v8, v2

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_II;->i1:I

    const/16 v15, 0x20

    ushr-long/2addr v2, v15

    long-to-int v8, v2

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_II;->i2:I

    move-object/from16 v22, v12

    .line 4
    move-object/from16 v12, v22

    if-eqz v12, :cond_81

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_II;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I;->i0:I

    move/from16 v28, v8

    .line 5
    add-int/lit8 v28, v28, 0x1

    .line 6
    move-object/from16 v12, v22

    if-eqz v12, :cond_87

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_II;

    move/from16 v8, v28

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I;->i0:I

    .line 7
    move-object/from16 v12, v22

    if-eqz v12, :cond_8d

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_II;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I;->i0:I

    move/from16 v25, v8

    .line 8
    move/from16 v0, v25

    int-to-long v0, v0

    move-wide/from16 v23, v0

    .line 9
    move-object/from16 v12, v22

    if-eqz v12, :cond_93

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_II;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_II;->i1:I

    iget v9, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_II;->i2:I

    int-to-long v0, v8

    const-wide v2, 0xffffffffL

    and-long/2addr v0, v2

    int-to-long v2, v9

    const/16 v15, 0x20

    shl-long/2addr v2, v15

    or-long/2addr v0, v2

    move-wide/from16 v26, v0

    .line 10
    add-long v17, v23, v26

    .line 11
    move-wide/from16 v0, v17

    return-wide v0

    const-wide/16 v0, 0x0

    return-wide v0

    .line 4
    :cond_81
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    .line 6
    :cond_87
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    .line 7
    :cond_8d
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    .line 9
    :cond_93
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0
.end method

.method public static f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 33

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v18, v15

    .line 2
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_1b

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_1b
    const/16 v9, 0x8

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move-object/from16 v18, v12

    .line 3
    move-object/from16 v12, v18

    if-eqz v12, :cond_74

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_IR;

    move/from16 v8, v17

    and-int/lit16 v8, v8, 0xff

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I;->i0:I

    .line 4
    move-object/from16 v12, v18

    if-eqz v12, :cond_79

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_IR;

    move/from16 v8, v17

    int-to-char v8, v8

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_IR;->i1:I

    .line 5
    move-object/from16 v12, v18

    if-eqz v12, :cond_7e

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_IR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I;->i0:I

    int-to-byte v8, v8

    move/from16 v19, v8

    .line 6
    move-object/from16 v12, v18

    if-eqz v12, :cond_83

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_IR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I;->i0:I

    move/from16 v20, v8

    .line 7
    add-int v19, v19, v20

    .line 8
    move-object/from16 v12, v18

    if-eqz v12, :cond_88

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_IR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_IR;->i1:I

    int-to-short v8, v8

    move/from16 v20, v8

    .line 9
    move-object/from16 v12, v18

    if-eqz v12, :cond_8d

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_IR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_IR;->i1:I

    move/from16 v21, v8

    .line 10
    add-int v20, v20, v21

    .line 11
    add-int v17, v19, v20

    .line 12
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 3
    :cond_74
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 4
    :cond_79
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 5
    :cond_7e
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 6
    :cond_83
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 8
    :cond_88
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_8d
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 35

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v18, v15

    move/from16 v19, v15

    move-object/from16 v20, v15

    .line 2
    move/from16 v9, v17

    if-ltz v9, :cond_91

    const v1, 0x1ffffff7

    if-gt v9, v1, :cond_91

    shl-int/lit8 v1, v9, 0x2

    add-int/lit16 v1, v1, 0x20

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_2a

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_2a
    new-array v10, v9, [I

    const/4 v8, 0x3

    invoke-static {v10, v8}, Ljava/util/Arrays;->fill([II)V

    const/16 v9, 0x9

    aget-object v11, v16, v9

    check-cast v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;

    invoke-direct {v12, v11, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;[I)V

    move-object/from16 v20, v12

    .line 3
    const/4 v9, 0x2

    move/from16 v8, v17

    invoke-static {v8, v9}, Ljava/lang/Integer;->divideUnsigned(II)I

    move-result v8

    move/from16 v22, v8

    .line 4
    move-object/from16 v12, v20

    const/4 v8, 0x0

    const/4 v9, 0x7

    move/from16 v11, v22

    invoke-static {v12, v8, v9, v11}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedArrays;->fillInts(Ljava/lang/Object;III)V

    .line 5
    move-object/from16 v12, v20

    if-eqz v12, :cond_96

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;->data:[I

    const/4 v9, 0x0

    array-length v8, v10

    if-ltz v9, :cond_9b

    if-ge v9, v8, :cond_a0

    const/16 v8, 0x64

    aput v8, v10, v9

    .line 7
    :goto_61
    move-object/from16 v12, v20

    if-eqz v12, :cond_a5

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;->data:[I

    array-length v8, v10

    move/from16 v21, v8

    .line 8
    const/high16 v10, -0x80000000

    add-int v8, v18, v10

    add-int v9, v21, v10

    if-ge v8, v9, :cond_8c

    .line 9
    move-object/from16 v12, v20

    if-eqz v12, :cond_aa

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;->data:[I

    move/from16 v9, v18

    array-length v8, v10

    if-ltz v9, :cond_af

    if-ge v9, v8, :cond_b4

    aget v8, v10, v9

    move/from16 v21, v8

    .line 10
    add-int v19, v19, v21

    .line 11
    add-int/lit8 v18, v18, 0x1

    .line 12
    goto :goto_61

    .line 13
    :cond_8c
    move/from16 v8, v19

    return v8

    const/4 v8, 0x0

    return v8

    .line 2
    :cond_91
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 5
    :cond_96
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 5
    :cond_9b
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 5
    :cond_a0
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_a5
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_aa
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_af
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_b4
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 38

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/16 v9, 0x9

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    move-object/from16 v24, v12

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v18, v15

    move-object/from16 v19, v15

    .line 2
    move/from16 v20, v17

    .line 3
    const/16 v21, 0x2

    .line 4
    const/16 v22, 0x3

    .line 5
    const/16 v23, 0x4

    .line 6
    const/16 v1, 0x30

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_2d

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_2d
    const/4 v9, 0x4

    new-array v10, v9, [I

    const/4 v11, 0x0

    move/from16 v8, v20

    aput v8, v10, v11

    const/4 v11, 0x1

    move/from16 v8, v21

    aput v8, v10, v11

    const/4 v11, 0x2

    move/from16 v8, v22

    aput v8, v10, v11

    const/4 v11, 0x3

    move/from16 v8, v23

    aput v8, v10, v11

    move-object/from16 v11, v24

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;

    invoke-direct {v12, v11, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;[I)V

    move-object/from16 v18, v12

    .line 7
    const/4 v9, 0x4

    if-ltz v9, :cond_a5

    const v1, 0x1ffffff7

    if-gt v9, v1, :cond_a5

    shl-int/lit8 v1, v9, 0x2

    add-int/lit16 v1, v1, 0x20

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_65

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_65
    new-array v10, v9, [I

    move-object/from16 v11, v24

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;

    invoke-direct {v12, v11, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;[I)V

    move-object/from16 v19, v12

    .line 8
    move-object/from16 v12, v19

    const/4 v8, 0x1

    move-object/from16 v10, v18

    const/4 v9, 0x0

    const/4 v11, 0x3

    invoke-static {v12, v8, v10, v9, v11}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedArrays;->copyArray(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 9
    move-object/from16 v12, v19

    if-eqz v12, :cond_aa

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;->data:[I

    const/4 v9, 0x1

    array-length v8, v10

    if-ltz v9, :cond_af

    if-ge v9, v8, :cond_b4

    aget v8, v10, v9

    move/from16 v20, v8

    .line 10
    move-object/from16 v12, v19

    if-eqz v12, :cond_b9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;->data:[I

    const/4 v9, 0x3

    array-length v8, v10

    if-ltz v9, :cond_be

    if-ge v9, v8, :cond_c3

    aget v8, v10, v9

    move/from16 v21, v8

    .line 11
    add-int v17, v20, v21

    .line 12
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_a5
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_aa
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_af
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_b4
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 10
    :cond_b9
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 10
    :cond_be
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 10
    :cond_c3
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 29

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    .line 2
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_16

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_16
    move/from16 v8, v17

    shl-int/lit8 v8, v8, 0x1

    ushr-int/lit8 v8, v8, 0x1

    new-instance v12, Lio/github/charlietap/chasm/reference/I31Reference;

    invoke-direct {v12, v8}, Lio/github/charlietap/chasm/reference/I31Reference;-><init>(I)V

    move-object/from16 v19, v12

    .line 3
    move-object/from16 v12, v19

    if-eqz v12, :cond_5d

    check-cast v12, Lio/github/charlietap/chasm/reference/I31Reference;

    iget v8, v12, Lio/github/charlietap/chasm/reference/I31Reference;->value:I

    shl-int/lit8 v8, v8, 0x1

    shr-int/lit8 v8, v8, 0x1

    move/from16 v18, v8

    .line 4
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_3f

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_3f
    move/from16 v8, v17

    shl-int/lit8 v8, v8, 0x1

    ushr-int/lit8 v8, v8, 0x1

    new-instance v12, Lio/github/charlietap/chasm/reference/I31Reference;

    invoke-direct {v12, v8}, Lio/github/charlietap/chasm/reference/I31Reference;-><init>(I)V

    move-object/from16 v21, v12

    .line 5
    move-object/from16 v12, v21

    if-eqz v12, :cond_62

    check-cast v12, Lio/github/charlietap/chasm/reference/I31Reference;

    iget v8, v12, Lio/github/charlietap/chasm/reference/I31Reference;->value:I

    move/from16 v20, v8

    .line 6
    add-int v17, v18, v20

    .line 7
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 3
    :cond_5d
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 5
    :cond_62
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 34

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v18, v15

    move/from16 v19, v15

    move/from16 v20, v15

    .line 3
    :goto_11
    const/high16 v10, -0x80000000

    add-int v8, v19, v10

    add-int v9, v17, v10

    if-ge v8, v9, :cond_6b

    .line 4
    move/from16 v21, v19

    move-object/from16 v23, v18

    .line 5
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_2b

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_2b
    const/16 v9, 0xa

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_RR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_RR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v21

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I;->i0:I

    move-object/from16 v10, v23

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_RR;->r0:Ljava/lang/Object;

    move-object/from16 v18, v12

    .line 6
    const/16 v9, 0x40

    if-ltz v9, :cond_87

    const v1, 0x1ffffff7

    if-gt v9, v1, :cond_87

    shl-int/lit8 v1, v9, 0x2

    add-int/lit16 v1, v1, 0x20

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_59

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_59
    new-array v10, v9, [I

    const/16 v9, 0x9

    aget-object v11, v16, v9

    check-cast v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;

    invoke-direct {v12, v11, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;[I)V

    move-object/from16 v22, v12

    .line 7
    add-int/lit8 v19, v19, 0x1

    .line 8
    goto :goto_11

    .line 10
    :cond_6b
    :goto_6b
    move-object/from16 v12, v18

    if-eqz v12, :cond_80

    .line 11
    move-object/from16 v22, v18

    .line 12
    move-object/from16 v12, v22

    if-eqz v12, :cond_8c

    .line 13
    move-object/from16 v12, v22

    if-eqz v12, :cond_91

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_RR;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_RR;->r0:Ljava/lang/Object;

    move-object/from16 v18, v10

    .line 14
    goto :goto_6b

    .line 15
    :cond_80
    const/16 v19, 0x0

    .line 16
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 6
    :cond_87
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 12
    :cond_8c
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 13
    :cond_91
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 35

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x7

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    move-object/from16 v27, v12

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v18, v15

    .line 2
    if-eqz v17, :cond_42

    .line 3
    const/16 v19, 0x5

    .line 4
    const-wide/16 v21, 0x6

    .line 5
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_28

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_28
    move-object/from16 v10, v27

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_II;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_II;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v19

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I;->i0:I

    move-wide/from16 v2, v21

    long-to-int v8, v2

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_II;->i1:I

    const/16 v15, 0x20

    ushr-long/2addr v2, v15

    long-to-int v8, v2

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_II;->i2:I

    move-object/from16 v20, v12

    .line 6
    goto/16 :goto_5d

    .line 7
    :cond_42
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_50

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_50
    const/16 v8, 0x9

    shl-int/lit8 v8, v8, 0x1

    ushr-int/lit8 v8, v8, 0x1

    new-instance v12, Lio/github/charlietap/chasm/reference/I31Reference;

    invoke-direct {v12, v8}, Lio/github/charlietap/chasm/reference/I31Reference;-><init>(I)V

    move-object/from16 v20, v12

    .line 8
    :goto_5d
    move-object/from16 v18, v20

    .line 9
    move-object/from16 v12, v18

    if-eqz v12, :cond_70

    instance-of v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    if-eqz v8, :cond_70

    move-object v11, v12

    check-cast v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;->rtt:Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    move-object/from16 v10, v27

    if-eq v11, v10, :cond_99

    .line 10
    :cond_70
    move-object/from16 v24, v18

    .line 11
    move-object/from16 v12, v24

    if-eqz v12, :cond_7a

    instance-of v8, v12, Lio/github/charlietap/chasm/reference/I31Reference;

    if-nez v8, :cond_7f

    :cond_7a
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->castFailed()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 12
    :cond_7f
    move-object/from16 v12, v24

    if-eqz v12, :cond_9e

    check-cast v12, Lio/github/charlietap/chasm/reference/I31Reference;

    iget v8, v12, Lio/github/charlietap/chasm/reference/I31Reference;->value:I

    move/from16 v23, v8

    .line 13
    move/from16 v8, v23

    return v8

    .line 14
    :goto_8c
    move-object/from16 v12, v20

    if-eqz v12, :cond_a3

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_II;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I;->i0:I

    move/from16 v17, v8

    .line 15
    move/from16 v8, v17

    return v8

    .line 16
    :cond_99
    move-object/from16 v20, v18

    goto :goto_8c

    const/4 v8, 0x0

    return v8

    .line 12
    :cond_9e
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 14
    :cond_a3
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 27

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    .line 2
    if-eqz v17, :cond_26

    .line 3
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_18

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_18
    const/4 v8, 0x3

    shl-int/lit8 v8, v8, 0x1

    ushr-int/lit8 v8, v8, 0x1

    new-instance v12, Lio/github/charlietap/chasm/reference/I31Reference;

    invoke-direct {v12, v8}, Lio/github/charlietap/chasm/reference/I31Reference;-><init>(I)V

    move-object/from16 v19, v12

    .line 4
    goto/16 :goto_29

    .line 5
    :cond_26
    const/4 v12, 0x0

    move-object/from16 v19, v12

    .line 6
    :goto_29
    move-object/from16 v12, v19

    if-eqz v12, :cond_31

    instance-of v8, v12, Lio/github/charlietap/chasm/reference/I31Reference;

    if-nez v8, :cond_33

    :cond_31
    goto/16 :goto_44

    .line 7
    :cond_33
    move-object/from16 v12, v19

    if-eqz v12, :cond_48

    check-cast v12, Lio/github/charlietap/chasm/reference/I31Reference;

    iget v8, v12, Lio/github/charlietap/chasm/reference/I31Reference;->value:I

    shl-int/lit8 v8, v8, 0x1

    shr-int/lit8 v8, v8, 0x1

    move/from16 v18, v8

    .line 8
    move/from16 v8, v18

    return v8

    .line 9
    :goto_44
    const/4 v8, -0x1

    return v8

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_48
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.class final Lchasm/dex/WasmModule$Declare0;
.super Ljava/lang/Object;


# direct methods
.method static declare(Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;)V
    .registers 12

    move-object v0, p0

    const-string v1, "pointpackedarray_sumfixedi31listcastcast_failcall_refcall_nullexternbad_cast"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->strings(Ljava/lang/String;)V

    const-string v1, "\u0005\u0006\t\u0005\u0003\u0004\u0004\t\u0008\t\u0006\u0008"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->stringLengths(Ljava/lang/String;)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->endStrings()V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->group(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->group(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->group(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->group(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->group(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->group(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->group(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->group(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->group(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    const/4 v1, 0x2

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->structComposite(I)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x0

    const-string v2, "63301eae39cf0650d95c11feecc7cb11"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x1

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    const/4 v1, 0x2

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    const/4 v1, 0x2

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->structComposite(I)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x1

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x1

    const-string v2, "8c99daf2393d8945426b4aac69b2df32"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->arrayComposite()V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x2

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x2

    const-string v2, "2e8ee4310ddddb4f73f38eb8f1c214e4"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->recursiveHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x0

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    const/4 v1, 0x2

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->structComposite(I)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x3

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x3

    const-string v2, "0cb30b1bf33839d21a02419f300cc916"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x2

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x4

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x4

    const-string v2, "cf82dc9bf03d8bcf2bd2284ea54f52fa"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x2

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x5

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x5

    const-string v2, "86010a575a00de869bf02ee4326641e9"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x1

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x6

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x6

    const-string v2, "0ef75fe501975ab105bb8f78dea59994"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x3

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x7

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x7

    const-string v2, "276d71474c53807bccc0fca223bf020d"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/16 v1, 0x8

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/16 v1, 0x8

    const-string v2, "5e754b92492ccd4fac81f1d5b5fff84b"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const-string v1, "\u0004\u0004\u0005\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0007\u0008\u0006\u0008"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functions(Ljava/lang/String;)V

    const-string v1, "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->exportKinds(Ljava/lang/String;)V

    const-string v1, "\u0002\u0003\u0004\u0005\u0006\u0007\u0008\t\n\u000b\u000c\r"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->exportIndices(Ljava/lang/String;)V

    const-string v1, "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\u0008\t\n\u000b"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->exportNames(Ljava/lang/String;)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->abstractHeapType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    const-string v1, "\u0000\u0001"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->refFuncConstantExpressions(Ljava/lang/String;)V

    const/4 v1, 0x2

    const/4 v2, 0x0

    const/4 v3, 0x2

    invoke-virtual/range {v0 .. v3}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->elementSegment(III)V

    return-void
.end method

.class public final Lchasm/dex/WasmModule$Entry0;
.super Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;

# interfaces
.implements Lchasm/dex/WasmModule$Sig0;


# direct methods
.method public constructor <init>([Ljava/lang/Object;I)V
    .registers 3

    invoke-direct {p0, p1, p2}, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;-><init>([Ljava/lang/Object;I)V

    return-void
.end method


# virtual methods
.method public callObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[J[Ljava/lang/Object;I)V
    .registers 15

    :try_start_0
    move-object v2, p0

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p4

    const/4 v3, 0x0

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v8, v2

    const/4 v3, 0x1

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v9, v2

    packed-switch v4, :pswitch_data_32

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_19

    :goto_19
    :pswitch_19
    invoke-static/range {v5 .. v9}, Lchasm/dex/WasmModule$Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v2

    goto/16 :goto_25

    :pswitch_1f
    invoke-static/range {v5 .. v9}, Lchasm/dex/WasmModule$Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v2

    goto/16 :goto_25

    :goto_25
    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_29
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_29} :catch_2a
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_29} :catch_2e

    return-void

    :catch_2a
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_2e
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_32
    .packed-switch 0x0
        :pswitch_19
        :pswitch_1f
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
    invoke-static/range {p0 .. p4}, Lchasm/dex/WasmModule$Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v0

    return v0

    :pswitch_12
    invoke-static/range {p0 .. p4}, Lchasm/dex/WasmModule$Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v0

    return v0

    nop

    :pswitch_data_18
    .packed-switch 0x0
        :pswitch_d
        :pswitch_12
    .end packed-switch
.end method

.class public final Lchasm/dex/WasmModule$Entry1;
.super Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;


# direct methods
.method public constructor <init>([Ljava/lang/Object;I)V
    .registers 3

    invoke-direct {p0, p1, p2}, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;-><init>([Ljava/lang/Object;I)V

    return-void
.end method


# virtual methods
.method public callObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[J[Ljava/lang/Object;I)V
    .registers 16

    :try_start_0
    move-object v2, p0

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p4

    const/4 v3, 0x0

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v8, v2

    const/4 v3, 0x1

    aget-wide v9, p2, v3

    invoke-static/range {v5 .. v10}, Lchasm/dex/WasmModule$Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIJ)J

    move-result-wide v0

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_14
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_14} :catch_15
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_14} :catch_19

    return-void

    :catch_15
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_19
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void
.end method

.class public final Lchasm/dex/WasmModule$Entry2;
.super Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;


# direct methods
.method public constructor <init>([Ljava/lang/Object;I)V
    .registers 3

    invoke-direct {p0, p1, p2}, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;-><init>([Ljava/lang/Object;I)V

    return-void
.end method


# virtual methods
.method public callObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[J[Ljava/lang/Object;I)V
    .registers 14

    :try_start_0
    move-object v2, p0

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p4

    const/4 v3, 0x0

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v8, v2

    packed-switch v4, :pswitch_data_52

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_14

    :goto_14
    :pswitch_14
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_44

    :pswitch_1a
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_44

    :pswitch_20
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_44

    :pswitch_26
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_44

    :pswitch_2c
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_44

    :pswitch_32
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_44

    :pswitch_38
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_44

    :pswitch_3e
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f12([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_44

    :goto_44
    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_48
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_48} :catch_49
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_48} :catch_4d

    return-void

    :catch_49
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_4d
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    nop

    :pswitch_data_52
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

.class public final Lchasm/dex/WasmModule$Entry3;
.super Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;


# direct methods
.method public constructor <init>([Ljava/lang/Object;I)V
    .registers 3

    invoke-direct {p0, p1, p2}, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;-><init>([Ljava/lang/Object;I)V

    return-void
.end method


# virtual methods
.method public callObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[J[Ljava/lang/Object;I)V
    .registers 16

    :try_start_0
    move-object v2, p0

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p4

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

    invoke-static/range {v5 .. v10}, Lchasm/dex/WasmModule$Code0;->f10([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIII)I

    move-result v2

    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_1c
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_1c} :catch_1d
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_1c} :catch_21

    return-void

    :catch_1d
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_21
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void
.end method

.class public final Lchasm/dex/WasmModule$Entry4;
.super Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;


# direct methods
.method public constructor <init>([Ljava/lang/Object;I)V
    .registers 3

    invoke-direct {p0, p1, p2}, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;-><init>([Ljava/lang/Object;I)V

    return-void
.end method


# virtual methods
.method public callObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[J[Ljava/lang/Object;I)V
    .registers 13

    :try_start_0
    move-object v2, p0

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p4

    packed-switch v4, :pswitch_data_28

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_f

    :goto_f
    :pswitch_f
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f11([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_1b

    :pswitch_15
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f13([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_1b

    :goto_1b
    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_1f
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_1f} :catch_20
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_1f} :catch_24

    return-void

    :catch_20
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_24
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_28
    .packed-switch 0x0
        :pswitch_f
        :pswitch_15
    .end packed-switch
.end method

.class final Lchasm/dex/WasmModule$Install;
.super Ljava/lang/Object;


# direct methods
.method static bindObjects0(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V
    .registers 8

    invoke-virtual {p0}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->getInstance()Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    move-result-object v4

    const/4 v2, 0x1

    aput-object v4, p1, v2

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->managedFunction(I)Ljava/lang/Object;

    move-result-object v4

    const/4 v2, 0x4

    aput-object v4, p1, v2

    const/4 v2, 0x1

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->managedFunction(I)Ljava/lang/Object;

    move-result-object v4

    const/4 v2, 0x5

    aput-object v4, p1, v2

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->rtt(I)Ljava/lang/Object;

    move-result-object v4

    const/4 v2, 0x7

    aput-object v4, p1, v2

    const/4 v2, 0x1

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->rtt(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x8

    aput-object v4, p1, v2

    const/4 v2, 0x2

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->rtt(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x9

    aput-object v4, p1, v2

    const/4 v2, 0x3

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->rtt(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0xa

    aput-object v4, p1, v2

    return-void
.end method

.class final Lchasm/dex/WasmModule$InstallEntries0;
.super Ljava/lang/Object;


# direct methods
.method static installEntries0(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V
    .registers 8

    invoke-static {p0, p1}, Lchasm/dex/WasmModule$InstallEntries0;->installEntries1(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V

    return-void
.end method

.method private static installEntries1(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V
    .registers 8

    new-instance v4, Lchasm/dex/WasmModule$Entry0;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry0;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x0

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry0;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry0;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x1

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry1;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry1;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x2

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x3

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x4

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/4 v3, 0x2

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x5

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/4 v3, 0x3

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x6

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/4 v3, 0x4

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x7

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/4 v3, 0x5

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x8

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/4 v3, 0x6

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x9

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry3;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry3;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xa

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry4;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry4;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xb

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/4 v3, 0x7

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xc

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry4;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry4;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xd

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    return-void
.end method

.class public interface abstract Lchasm/dex/WasmModule$Sig0;
.super Ljava/lang/Object;


# virtual methods
.method public abstract typed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I
.end method

.class final Lchasm/dex/WasmModule$Structs0;
.super Ljava/lang/Object;


# direct methods
.method static newStruct(ILio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;
    .registers 5

    packed-switch p0, :pswitch_data_1a

    :pswitch_3
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    const/4 v0, 0x0

    return-object v0

    :pswitch_8
    new-instance v0, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_II;

    invoke-direct {v0, p1}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_II;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    return-object v0

    :pswitch_e
    new-instance v0, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_IR;

    invoke-direct {v0, p1}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    return-object v0

    :pswitch_14
    new-instance v0, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_RR;

    invoke-direct {v0, p1}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/S_I_RR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    return-object v0

    :pswitch_data_1a
    .packed-switch 0x0
        :pswitch_8
        :pswitch_e
        :pswitch_3
        :pswitch_14
    .end packed-switch
.end method

.class public final Lchasm/dex/WasmModule;
.super Ljava/lang/Object;

# interfaces
.implements Lio/github/charlietap/chasm/compiled/CompiledModule;


# static fields
.field public static final COMPILED:Lchasm/dex/WasmModule;


# direct methods
.method static constructor <clinit>()V
    .registers 1

    new-instance v0, Lchasm/dex/WasmModule;

    invoke-direct {v0}, Lchasm/dex/WasmModule;-><init>()V

    sput-object v0, Lchasm/dex/WasmModule;->COMPILED:Lchasm/dex/WasmModule;

    return-void
.end method

.method private constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method private static classes0([Ljava/lang/String;)V
    .registers 3

    const/4 v0, 0x0

    const-string v1, "chasm.dex.WasmModule$Code0"

    aput-object v1, p0, v0

    const/4 v0, 0x1

    const-string v1, "chasm.dex.WasmModule$Sig0"

    aput-object v1, p0, v0

    const/4 v0, 0x2

    const-string v1, "chasm.dex.WasmModule$Entry0"

    aput-object v1, p0, v0

    const/4 v0, 0x3

    const-string v1, "chasm.dex.WasmModule$Entry1"

    aput-object v1, p0, v0

    const/4 v0, 0x4

    const-string v1, "chasm.dex.WasmModule$Entry2"

    aput-object v1, p0, v0

    const/4 v0, 0x5

    const-string v1, "chasm.dex.WasmModule$Entry3"

    aput-object v1, p0, v0

    const/4 v0, 0x6

    const-string v1, "chasm.dex.WasmModule$Entry4"

    aput-object v1, p0, v0

    const/4 v0, 0x7

    const-string v1, "chasm.dex.WasmModule$InstallEntries0"

    aput-object v1, p0, v0

    const/16 v0, 0x8

    const-string v1, "chasm.dex.WasmModule$Install"

    aput-object v1, p0, v0

    const/16 v0, 0x9

    const-string v1, "chasm.dex.WasmModule$Declare0"

    aput-object v1, p0, v0

    const/16 v0, 0xa

    const-string v1, "chasm.dex.WasmModule$Structs0"

    aput-object v1, p0, v0

    return-void
.end method


# virtual methods
.method public buildDeclarations(Lio/github/charlietap/chasm/declarations/DataSource;)Lio/github/charlietap/chasm/declarations/ModuleDeclarations;
    .registers 3

    new-instance v0, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;

    invoke-direct {v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;-><init>()V

    invoke-static {v0}, Lchasm/dex/WasmModule$Declare0;->declare(Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;)V

    invoke-virtual {v0, p1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->build(Lio/github/charlietap/chasm/declarations/DataSource;)Lio/github/charlietap/chasm/declarations/ModuleDeclarations;

    move-result-object v0

    return-object v0
.end method

.method public byteArrayBaseOffset()J
    .registers 3

    const-wide/16 v0, -0x1

    return-wide v0
.end method

.method public classes()[Ljava/lang/String;
    .registers 3

    const/16 v0, 0xb

    new-array v1, v0, [Ljava/lang/String;

    invoke-static {v1}, Lchasm/dex/WasmModule;->classes0([Ljava/lang/String;)V

    return-object v1
.end method

.method public fuel()J
    .registers 3

    const-wide/16 v0, -0x1

    return-wide v0
.end method

.method public gcHeapBytes()J
    .registers 3

    const-wide/16 v0, -0x1

    return-wide v0
.end method

.method public initializesEagerly()Z
    .registers 3

    const/4 v0, 0x1

    return v0
.end method

.method public install(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;)V
    .registers 5

    const/16 v0, 0xb

    new-array v1, v0, [Ljava/lang/Object;

    const/4 v0, 0x0

    new-array v2, v0, [J

    const/4 v0, 0x0

    aput-object v2, v1, v0

    invoke-static {p1, v1}, Lchasm/dex/WasmModule$Install;->bindObjects0(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V

    invoke-static {p1, v1}, Lchasm/dex/WasmModule$InstallEntries0;->installEntries0(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V

    return-void
.end method

.method public interrupts()Z
    .registers 3

    const/4 v0, 0x0

    return v0
.end method

.method public linearMemoryBytes()J
    .registers 3

    const-wide/16 v0, -0x1

    return-wide v0
.end method

.method public newStruct(ILjava/lang/Object;)Ljava/lang/Object;
    .registers 5

    div-int/lit16 v0, p1, 0x80

    packed-switch v0, :pswitch_data_12

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    const/4 v0, 0x0

    return-object v0

    :pswitch_a
    move-object v1, p2

    check-cast v1, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    invoke-static {p1, v1}, Lchasm/dex/WasmModule$Structs0;->newStruct(ILio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    move-result-object v0

    return-object v0

    :pswitch_data_12
    .packed-switch 0x0
        :pswitch_a
    .end packed-switch
.end method

.method public resourceRoot()Ljava/lang/String;
    .registers 3

    const-string v0, "chasm/dex/WasmModule"

    return-object v0
.end method

.method public runtimeAbi()I
    .registers 3

    const/4 v0, 0x4

    return v0
.end method

.method public stackBytes()J
    .registers 3

    const-wide/16 v0, -0x1

    return-wide v0
.end method

.method public stacktraces()Z
    .registers 3

    const/4 v0, 0x1

    return v0
.end method

.method public structLanes()[J
    .registers 3

    const/16 v0, 0x10

    new-array v1, v0, [J

    fill-array-data v1, :array_8

    return-object v1

    :array_8
    .array-data 8
        0x0
        0x18
        0x3
        0x0
        0x2
        0x4
        0x1
        0x18
        0x2
        0x0
        0x2
        0x3
        0x18
        0x2
        0x0
        0x0
    .end array-data
.end method

.method public tableBytes()J
    .registers 3

    const-wide/16 v0, -0x1

    return-wide v0
.end method

.method public target()I
    .registers 3

    const/4 v0, 0x1

    return v0
.end method

.method public trapOnGrowthFailure()Z
    .registers 3

    const/4 v0, 0x0

    return v0
.end method
