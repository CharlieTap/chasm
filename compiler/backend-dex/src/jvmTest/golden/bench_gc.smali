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

.method public static f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 32

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v18, v15

    move/from16 v19, v15

    move/from16 v20, v15

    move/from16 v21, v15

    move-object/from16 v22, v15

    move/from16 v23, v15

    move-object/from16 v24, v15

    .line 3
    :goto_19
    const/high16 v10, -0x80000000

    add-int v8, v19, v10

    add-int v9, v17, v10

    if-ge v8, v9, :cond_4b

    .line 4
    move/from16 v21, v19

    move-object/from16 v24, v18

    .line 5
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_33

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_33
    const/16 v9, 0x8

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R2__I_RR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R2__I_RR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v21

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move-object/from16 v10, v24

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R2__I_RR;->r0:Ljava/lang/Object;

    move-object/from16 v18, v12

    .line 6
    add-int/lit8 v19, v19, 0x1

    .line 7
    goto :goto_19

    .line 9
    :cond_4b
    :goto_4b
    move-object/from16 v22, v18

    .line 10
    move-object/from16 v12, v22

    if-eqz v12, :cond_54

    const/4 v11, 0x0

    goto/16 :goto_55

    :cond_54
    const/4 v11, 0x1

    :goto_55
    move/from16 v21, v11

    .line 11
    if-nez v21, :cond_7c

    .line 12
    move-object/from16 v24, v18

    .line 13
    move-object/from16 v12, v24

    if-eqz v12, :cond_81

    .line 14
    move-object/from16 v12, v24

    if-eqz v12, :cond_86

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R2__I_RR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v23, v8

    .line 15
    add-int v20, v20, v23

    .line 16
    move-object/from16 v22, v18

    .line 17
    move-object/from16 v12, v22

    if-eqz v12, :cond_8b

    .line 18
    move-object/from16 v12, v22

    if-eqz v12, :cond_90

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R2__I_RR;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R2__I_RR;->r0:Ljava/lang/Object;

    move-object/from16 v18, v10

    .line 19
    goto :goto_4b

    .line 20
    :cond_7c
    move/from16 v8, v20

    return v8

    const/4 v8, 0x0

    return v8

    .line 13
    :cond_81
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 14
    :cond_86
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 17
    :cond_8b
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 18
    :cond_90
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 34

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v18, v15

    move/from16 v19, v15

    move-object/from16 v20, v15

    move/from16 v21, v15

    move/from16 v22, v15

    .line 2
    const/16 v9, 0x400

    if-ltz v9, :cond_7f

    const v1, 0x1ffffff7

    if-gt v9, v1, :cond_7f

    shl-int/lit8 v1, v9, 0x2

    add-int/lit16 v1, v1, 0x20

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_2e

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_2e
    new-array v10, v9, [I

    const/16 v9, 0x9

    aget-object v11, v16, v9

    check-cast v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;

    invoke-direct {v12, v11, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;[I)V

    move-object/from16 v20, v12

    .line 4
    :goto_3d
    const/high16 v10, -0x80000000

    add-int v8, v18, v10

    add-int v9, v17, v10

    if-ge v8, v9, :cond_7a

    .line 5
    const/16 v9, 0x3ff

    and-int v21, v18, v9

    .line 6
    move-object/from16 v12, v20

    if-eqz v12, :cond_84

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;->data:[I

    move/from16 v9, v21

    array-length v8, v10

    if-ltz v9, :cond_89

    if-ge v9, v8, :cond_8e

    move/from16 v8, v18

    aput v8, v10, v9

    .line 7
    mul-int/lit8 v22, v18, 0x7

    .line 8
    const/16 v9, 0x3ff

    and-int v22, v22, v9

    .line 9
    move-object/from16 v12, v20

    if-eqz v12, :cond_93

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I32Array;->data:[I

    move/from16 v9, v22

    array-length v8, v10

    if-ltz v9, :cond_98

    if-ge v9, v8, :cond_9d

    aget v8, v10, v9

    move/from16 v21, v8

    .line 10
    add-int v19, v19, v21

    .line 11
    add-int/lit8 v18, v18, 0x1

    .line 12
    goto :goto_3d

    .line 13
    :cond_7a
    move/from16 v8, v19

    return v8

    const/4 v8, 0x0

    return v8

    .line 2
    :cond_7f
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 6
    :cond_84
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 6
    :cond_89
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 6
    :cond_8e
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_93
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_98
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_9d
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 29

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v18, v15

    move/from16 v19, v15

    move/from16 v20, v15

    move-object/from16 v21, v15

    .line 3
    :goto_13
    const/high16 v10, -0x80000000

    add-int v8, v18, v10

    add-int v9, v17, v10

    if-ge v8, v9, :cond_49

    .line 4
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_29

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_29
    move/from16 v8, v18

    shl-int/lit8 v8, v8, 0x1

    ushr-int/lit8 v8, v8, 0x1

    new-instance v12, Lio/github/charlietap/chasm/reference/I31Reference;

    invoke-direct {v12, v8}, Lio/github/charlietap/chasm/reference/I31Reference;-><init>(I)V

    move-object/from16 v21, v12

    .line 5
    move-object/from16 v12, v21

    if-eqz v12, :cond_4e

    check-cast v12, Lio/github/charlietap/chasm/reference/I31Reference;

    iget v8, v12, Lio/github/charlietap/chasm/reference/I31Reference;->value:I

    shl-int/lit8 v8, v8, 0x1

    shr-int/lit8 v8, v8, 0x1

    move/from16 v20, v8

    .line 6
    add-int v19, v19, v20

    .line 7
    add-int/lit8 v18, v18, 0x1

    .line 8
    goto :goto_13

    .line 9
    :cond_49
    move/from16 v8, v19

    return v8

    const/4 v8, 0x0

    return v8

    .line 5
    :cond_4e
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
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

    move-object/from16 v22, v16

    move-object/from16 v23, v14

    move/from16 v24, v13

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v18, v15

    move/from16 v19, v15

    move-object/from16 v20, v15

    const/4 v8, 0x0

    move-object/from16 v21, v8

    .line 2
    const/4 v9, 0x6

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFunction;

    move-object/from16 v20, v12

    .line 4
    :goto_2d
    const/high16 v10, -0x80000000

    add-int v8, v18, v10

    add-int v9, v17, v10

    if-ge v8, v9, :cond_a8

    .line 5
    move-object/from16 v12, v20

    invoke-static {v12}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->referenceTarget(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v12

    instance-of v11, v12, Lchasm/dex/WasmModule$Sig0;

    if-eqz v11, :cond_6c

    move-object/from16 v22, v12

    move/from16 v25, v19

    move/from16 v26, v18

    move/from16 v24, v13

    invoke-interface/range {v22 .. v26}, Lchasm/dex/WasmModule$Sig0;->typed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v9

    move-object/from16 v22, v16

    move-object/from16 v23, v14

    move/from16 v24, v13

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_ad

    iget-object v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->pendingTailFunction:Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    if-eqz v8, :cond_68

    const/4 v8, 0x1

    new-array v10, v8, [J

    const/4 v11, 0x0

    invoke-static {v14, v10, v11, v13}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->completeTailObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[J[Ljava/lang/Object;I)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_ad

    const/4 v8, 0x0

    aget-wide v0, v10, v8

    long-to-int v9, v0

    :cond_68
    move/from16 v19, v9

    goto/16 :goto_a5

    :cond_6c
    move-object/from16 v12, v21

    if-nez v12, :cond_75

    const/4 v8, 0x2

    new-array v12, v8, [J

    move-object/from16 v21, v12

    :cond_75
    move-object v10, v12

    const/4 v11, 0x0

    const/4 v9, 0x0

    move/from16 v0, v19

    int-to-long v0, v0

    aput-wide v0, v10, v9

    const/4 v9, 0x1

    move/from16 v0, v18

    int-to-long v0, v0

    aput-wide v0, v10, v9

    move-object/from16 v23, v14

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    move-object/from16 v24, v12

    move-object/from16 v25, v20

    move-object/from16 v26, v10

    move-object/from16 v27, v11

    move/from16 v28, v13

    invoke-static/range {v23 .. v28}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->callReferenceObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;Ljava/lang/Object;[J[Ljava/lang/Object;I)V

    move-object/from16 v23, v14

    move/from16 v24, v13

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_ad

    const/4 v9, 0x0

    aget-wide v0, v10, v9

    long-to-int v15, v0

    move/from16 v19, v15

    .line 6
    :goto_a5
    add-int/lit8 v18, v18, 0x1

    .line 7
    goto :goto_2d

    .line 8
    :cond_a8
    move/from16 v8, v19

    return v8

    const/4 v8, 0x0

    return v8

    :cond_ad
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const/4 v8, 0x0

    return v8
.end method

.method public static f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V
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
    const/4 v8, 0x1

    new-array v10, v8, [J

    const/4 v11, 0x0

    const/4 v9, 0x0

    move/from16 v0, v18

    int-to-long v0, v0

    aput-wide v0, v10, v9

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x6

    aget-wide v4, v12, v9

    long-to-int v8, v4

    invoke-static {v14, v8, v10, v11}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->throwException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J[Ljava/lang/Object;)V

    goto/16 :goto_28

    return-void

    :goto_28
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    return-void
.end method

.method public static f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 30

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

    move-object/from16 v22, v16

    move-object/from16 v23, v14

    move/from16 v24, v13

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v18, v15

    move/from16 v19, v15

    move-wide/from16 v20, v0

    .line 3
    :goto_23
    const/high16 v10, -0x80000000

    add-int v8, v18, v10

    add-int v9, v17, v10

    if-ge v8, v9, :cond_41

    .line 4
    move/from16 v25, v18

    invoke-static/range {v22 .. v25}, Lchasm/dex/WasmModule$Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_46

    .line 5
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 6
    :goto_39
    move-wide/from16 v0, v20

    long-to-int v8, v0

    add-int v19, v8, v19

    .line 7
    add-int/lit8 v18, v18, 0x1

    .line 8
    goto :goto_23

    .line 9
    :cond_41
    move/from16 v8, v19

    return v8

    const/4 v8, 0x0

    return v8

    :cond_46
    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->pendingExceptionTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v8

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/4 v9, 0x6

    aget-wide v4, v12, v9

    long-to-int v9, v4

    if-ne v8, v9, :cond_63

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->takeException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;

    move-result-object v12

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;->words:[J

    iget-object v11, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;->references:[Ljava/lang/Object;

    const/4 v9, 0x0

    aget-wide v0, v10, v9

    move-wide/from16 v20, v0

    goto :goto_39

    :cond_63
    goto/16 :goto_65

    :goto_65
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const/4 v8, 0x0

    return v8
.end method

.method public static f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I
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

    move/from16 v19, v15

    move/from16 v20, v15

    .line 2
    if-nez v17, :cond_23

    .line 3
    move/from16 v19, v18

    .line 4
    goto/16 :goto_46

    .line 5
    :cond_23
    add-int/lit8 v19, v17, -0x1

    .line 6
    add-int/lit8 v20, v18, 0x1

    .line 7
    const/4 v8, 0x2

    new-array v10, v8, [J

    const/4 v11, 0x0

    const/4 v9, 0x0

    move/from16 v0, v19

    int-to-long v0, v0

    aput-wide v0, v10, v9

    const/4 v9, 0x1

    move/from16 v0, v20

    int-to-long v0, v0

    aput-wide v0, v10, v9

    const/4 v9, 0x1

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    const/4 v9, 0x4

    aget-object v3, v16, v9

    check-cast v3, Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    invoke-static {v14, v2, v3, v10, v11}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->requestTailObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;[J[Ljava/lang/Object;)V

    const/4 v8, 0x0

    return v8

    .line 8
    :goto_46
    move/from16 v8, v19

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I
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

    move/from16 v19, v15

    move/from16 v20, v15

    .line 2
    if-nez v17, :cond_23

    .line 3
    move/from16 v19, v18

    .line 4
    goto/16 :goto_46

    .line 5
    :cond_23
    add-int/lit8 v19, v17, -0x1

    .line 6
    add-int/lit8 v20, v18, 0x2

    .line 7
    const/4 v8, 0x2

    new-array v10, v8, [J

    const/4 v11, 0x0

    const/4 v9, 0x0

    move/from16 v0, v19

    int-to-long v0, v0

    aput-wide v0, v10, v9

    const/4 v9, 0x1

    move/from16 v0, v20

    int-to-long v0, v0

    aput-wide v0, v10, v9

    const/4 v9, 0x1

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    const/4 v9, 0x3

    aget-object v3, v16, v9

    check-cast v3, Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    invoke-static {v14, v2, v3, v10, v11}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->requestTailObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;[J[Ljava/lang/Object;)V

    const/4 v8, 0x0

    return v8

    .line 8
    :goto_46
    move/from16 v8, v19

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 28

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

    move-object/from16 v19, v16

    move-object/from16 v20, v14

    move/from16 v21, v13

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v18, v15

    .line 2
    move/from16 v22, v17

    const/16 v23, 0x0

    invoke-static/range {v19 .. v23}, Lchasm/dex/WasmModule$Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v9

    iget-object v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->pendingTailFunction:Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    if-eqz v8, :cond_3a

    const/4 v8, 0x1

    new-array v10, v8, [J

    const/4 v11, 0x0

    invoke-static {v14, v10, v11, v13}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->completeTailObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[J[Ljava/lang/Object;I)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_41

    const/4 v8, 0x0

    aget-wide v0, v10, v8

    long-to-int v9, v0

    :cond_3a
    move/from16 v18, v9

    .line 3
    move/from16 v8, v18

    return v8

    const/4 v8, 0x0

    return v8

    :cond_41
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const/4 v8, 0x0

    return v8
.end method

.class final Lchasm/dex/WasmModule$Declare0;
.super Ljava/lang/Object;


# direct methods
.method static declare(Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;)V
    .registers 12

    move-object v0, p0

    const-string v1, "listarrayi31call_refthrowtail"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->strings(Ljava/lang/String;)V

    const-string v1, "\u0004\u0005\u0003\u0008\u0005\u0004"

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

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x0

    const-string v2, "0cb30b1bf33839d21a02419f300cc916"

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

    const/4 v1, 0x1

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x1

    const-string v2, "2e8ee4310ddddb4f73f38eb8f1c214e4"

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

    const/4 v1, 0x2

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x2

    const-string v2, "cf82dc9bf03d8bcf2bd2284ea54f52fa"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x3

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x3

    const-string v2, "56fea0d3d9dff2e917c8513666724464"

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

    const/4 v1, 0x4

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x4

    const-string v2, "0ef75fe501975ab105bb8f78dea59994"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const-string v1, "\u0002\u0004\u0004\u0004\u0004\u0003\u0004\u0002\u0002\u0004"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functions(Ljava/lang/String;)V

    const/4 v1, 0x3

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->tag(I)V

    const-string v1, "\u0000\u0000\u0000\u0000\u0000\u0000"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->exportKinds(Ljava/lang/String;)V

    const-string v1, "\u0001\u0002\u0003\u0004\u0006\t"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->exportIndices(Ljava/lang/String;)V

    const-string v1, "\u0000\u0001\u0002\u0003\u0004\u0005"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->exportNames(Ljava/lang/String;)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->abstractHeapType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    const-string v1, "\u0000"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->refFuncConstantExpressions(Ljava/lang/String;)V

    const/4 v1, 0x2

    const/4 v2, 0x0

    const/4 v3, 0x1

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

    packed-switch v4, :pswitch_data_38

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_19

    :goto_19
    :pswitch_19
    invoke-static/range {v5 .. v9}, Lchasm/dex/WasmModule$Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v2

    goto/16 :goto_2b

    :pswitch_1f
    invoke-static/range {v5 .. v9}, Lchasm/dex/WasmModule$Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v2

    goto/16 :goto_2b

    :pswitch_25
    invoke-static/range {v5 .. v9}, Lchasm/dex/WasmModule$Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v2

    goto/16 :goto_2b

    :goto_2b
    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_2f
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_2f} :catch_30
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_2f} :catch_34

    return-void

    :catch_30
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_34
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_38
    .packed-switch 0x0
        :pswitch_19
        :pswitch_1f
        :pswitch_25
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
    invoke-static/range {p0 .. p4}, Lchasm/dex/WasmModule$Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v0

    return v0

    :pswitch_12
    invoke-static/range {p0 .. p4}, Lchasm/dex/WasmModule$Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v0

    return v0

    :pswitch_17
    invoke-static/range {p0 .. p4}, Lchasm/dex/WasmModule$Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v0

    return v0

    :pswitch_data_1c
    .packed-switch 0x0
        :pswitch_d
        :pswitch_12
        :pswitch_17
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

    packed-switch v4, :pswitch_data_46

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_14

    :goto_14
    :pswitch_14
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_38

    :pswitch_1a
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_38

    :pswitch_20
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_38

    :pswitch_26
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_38

    :pswitch_2c
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_38

    :pswitch_32
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_38

    :goto_38
    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_3c
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_3c} :catch_3d
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_3c} :catch_41

    return-void

    :catch_3d
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_41
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    nop

    :pswitch_data_46
    .packed-switch 0x0
        :pswitch_14
        :pswitch_1a
        :pswitch_20
        :pswitch_26
        :pswitch_2c
        :pswitch_32
    .end packed-switch
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

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p4

    const/4 v3, 0x0

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v8, v2

    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V
    :try_end_d
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_d} :catch_e
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_d} :catch_12

    return-void

    :catch_e
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_12
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void
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

    const/4 v2, 0x7

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->function(I)Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    move-result-object v4

    const/4 v2, 0x3

    aput-object v4, p1, v2

    const/16 v2, 0x8

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->function(I)Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    move-result-object v4

    const/4 v2, 0x4

    aput-object v4, p1, v2

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->managedFunction(I)Ljava/lang/Object;

    move-result-object v4

    const/4 v2, 0x6

    aput-object v4, p1, v2

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->rtt(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x8

    aput-object v4, p1, v2

    const/4 v2, 0x1

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->rtt(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x9

    aput-object v4, p1, v2

    return-void
.end method

.method static bindValues0(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V
    .registers 8

    const/4 v2, 0x0

    aget-object v5, p1, v2

    check-cast v5, [J

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->tagAddress(I)I

    move-result v3

    int-to-long v0, v3

    const/4 v2, 0x6

    aput-wide v0, v5, v2

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

    new-instance v4, Lchasm/dex/WasmModule$Entry1;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry1;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x1

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry1;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry1;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x2

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry1;

    const/4 v3, 0x2

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry1;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x3

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry1;

    const/4 v3, 0x3

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry1;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x4

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x5

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry1;

    const/4 v3, 0x4

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry1;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x6

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry0;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry0;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x7

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry0;

    const/4 v3, 0x2

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry0;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x8

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry1;

    const/4 v3, 0x5

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry1;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x9

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

    packed-switch p0, :pswitch_data_e

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    const/4 v0, 0x0

    return-object v0

    :pswitch_8
    new-instance v0, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R2__I_RR;

    invoke-direct {v0, p1}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R2__I_RR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    return-object v0

    :pswitch_data_e
    .packed-switch 0x0
        :pswitch_8
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

    const-string v1, "chasm.dex.WasmModule$InstallEntries0"

    aput-object v1, p0, v0

    const/4 v0, 0x6

    const-string v1, "chasm.dex.WasmModule$Install"

    aput-object v1, p0, v0

    const/4 v0, 0x7

    const-string v1, "chasm.dex.WasmModule$Declare0"

    aput-object v1, p0, v0

    const/16 v0, 0x8

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

    const/16 v0, 0x9

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

    const/16 v0, 0xa

    new-array v1, v0, [Ljava/lang/Object;

    const/4 v0, 0x7

    new-array v2, v0, [J

    const/4 v0, 0x0

    aput-object v2, v1, v0

    invoke-static {p1, v1}, Lchasm/dex/WasmModule$Install;->bindObjects0(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V

    invoke-static {p1, v1}, Lchasm/dex/WasmModule$Install;->bindValues0(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V

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

    const/4 v0, 0x5

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

    const/4 v0, 0x5

    new-array v1, v0, [J

    fill-array-data v1, :array_8

    return-object v1

    nop

    :array_8
    .array-data 8
        0x0
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
