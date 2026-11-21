.class public final Lchasm/jit/Code0;
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
    if-eqz v17, :cond_38

    .line 4
    const/16 v9, 0x2e

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    move-object/from16 v22, v12

    .line 5
    goto/16 :goto_40

    .line 6
    :cond_38
    const/16 v9, 0x2f

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    move-object/from16 v22, v12

    .line 7
    :goto_40
    move-object/from16 v12, v22

    invoke-static {v12}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->referenceTarget(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v12

    instance-of v11, v12, Lchasm/jit/Sig0;

    if-eqz v11, :cond_60

    move-object/from16 v25, v12

    move/from16 v28, v20

    move/from16 v29, v21

    move/from16 v27, v13

    invoke-interface/range {v25 .. v29}, Lchasm/jit/Sig0;->typed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v9

    move-object/from16 v25, v16

    move-object/from16 v26, v14

    move/from16 v27, v13

    move/from16 v20, v9

    goto/16 :goto_9a

    :cond_60
    move-object/from16 v12, v23

    if-nez v12, :cond_69

    const/4 v8, 0x2

    new-array v12, v8, [J

    move-object/from16 v23, v12

    :cond_69
    move/from16 v0, v20

    int-to-long v0, v0

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move/from16 v0, v21

    int-to-long v0, v0

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    move-object/from16 v26, v14

    const/4 v9, 0x1

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    move-object/from16 v27, v2

    move-object/from16 v29, v12

    move-object/from16 v28, v22

    move/from16 v30, v13

    invoke-static/range {v26 .. v30}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->callReference(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;Ljava/lang/Object;[JI)V

    move-object/from16 v26, v14

    move/from16 v27, v13

    move-object/from16 v12, v23

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-eqz v8, :cond_94

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->raiseUnwinding(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)V

    :cond_94
    const/4 v10, 0x0

    aget-wide v0, v12, v10

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

    instance-of v11, v12, Lchasm/jit/Sig0;

    if-eqz v11, :cond_3e

    move-object/from16 v20, v12

    const/16 v23, 0x1

    const/16 v24, 0x2

    move/from16 v22, v13

    invoke-interface/range {v20 .. v24}, Lchasm/jit/Sig0;->typed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v9

    move-object/from16 v20, v16

    move-object/from16 v21, v14

    move/from16 v22, v13

    move/from16 v17, v9

    goto/16 :goto_76

    :cond_3e
    move-object/from16 v12, v19

    if-nez v12, :cond_47

    const/4 v8, 0x2

    new-array v12, v8, [J

    move-object/from16 v19, v12

    :cond_47
    const-wide/16 v0, 0x1

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    const-wide/16 v0, 0x2

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    move-object/from16 v21, v14

    const/4 v9, 0x1

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    move-object/from16 v22, v2

    move-object/from16 v24, v12

    move-object/from16 v23, v18

    move/from16 v25, v13

    invoke-static/range {v21 .. v25}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->callReference(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;Ljava/lang/Object;[JI)V

    move-object/from16 v21, v14

    move/from16 v22, v13

    move-object/from16 v12, v19

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-eqz v8, :cond_70

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->raiseUnwinding(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)V

    :cond_70
    const/4 v10, 0x0

    aget-wide v0, v12, v10

    long-to-int v15, v0

    move/from16 v17, v15

    .line 4
    :goto_76
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

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;

    invoke-direct {v12, v8}, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;-><init>(I)V

    move-object/from16 v18, v12

    .line 5
    move-object/from16 v12, v18

    if-eqz v12, :cond_2b

    instance-of v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;

    if-nez v8, :cond_30

    :cond_2b
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->castFailed()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 6
    :cond_30
    move-object/from16 v12, v18

    if-eqz v12, :cond_43

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;->value:I

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

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;

    invoke-direct {v12, v8}, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;-><init>(I)V

    move-object/from16 v17, v12

    .line 3
    move-object/from16 v12, v17

    if-eqz v12, :cond_28

    instance-of v8, v12, Lchasm/gc/S63301eae39cf0650d95c11feecc7cb11_0;

    if-nez v8, :cond_2d

    :cond_28
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->castFailed()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 4
    :cond_2d
    const/4 v8, 0x0

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIJ)J
    .registers 43

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v19, p3

    move-wide/from16 v20, p4

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v22, v15

    const/4 v8, 0x0

    move-object/from16 v29, v8

    .line 2
    move/from16 v25, v19

    move-wide/from16 v26, v20

    .line 3
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_24

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_24
    new-instance v12, Lchasm/gc/S63301eae39cf0650d95c11feecc7cb11_0;

    invoke-direct {v12}, Lchasm/gc/S63301eae39cf0650d95c11feecc7cb11_0;-><init>()V

    move/from16 v8, v25

    iput v8, v12, Lchasm/gc/S63301eae39cf0650d95c11feecc7cb11_0;->f0:I

    move-wide/from16 v2, v26

    iput-wide v2, v12, Lchasm/gc/S63301eae39cf0650d95c11feecc7cb11_0;->f1:J

    move-object/from16 v22, v12

    .line 4
    move-object/from16 v12, v22

    if-eqz v12, :cond_6a

    check-cast v12, Lchasm/gc/S63301eae39cf0650d95c11feecc7cb11_0;

    iget v8, v12, Lchasm/gc/S63301eae39cf0650d95c11feecc7cb11_0;->f0:I

    move/from16 v28, v8

    .line 5
    add-int/lit8 v28, v28, 0x1

    .line 6
    move-object/from16 v12, v22

    if-eqz v12, :cond_70

    check-cast v12, Lchasm/gc/S63301eae39cf0650d95c11feecc7cb11_0;

    move/from16 v8, v28

    iput v8, v12, Lchasm/gc/S63301eae39cf0650d95c11feecc7cb11_0;->f0:I

    .line 7
    move-object/from16 v12, v22

    if-eqz v12, :cond_76

    check-cast v12, Lchasm/gc/S63301eae39cf0650d95c11feecc7cb11_0;

    iget v8, v12, Lchasm/gc/S63301eae39cf0650d95c11feecc7cb11_0;->f0:I

    move/from16 v25, v8

    .line 8
    move/from16 v0, v25

    int-to-long v0, v0

    move-wide/from16 v23, v0

    .line 9
    move-object/from16 v12, v22

    if-eqz v12, :cond_7c

    check-cast v12, Lchasm/gc/S63301eae39cf0650d95c11feecc7cb11_0;

    iget-wide v0, v12, Lchasm/gc/S63301eae39cf0650d95c11feecc7cb11_0;->f1:J

    move-wide/from16 v26, v0

    .line 10
    add-long v17, v23, v26

    .line 11
    move-wide/from16 v0, v17

    return-wide v0

    const-wide/16 v0, 0x0

    return-wide v0

    .line 4
    :cond_6a
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    .line 6
    :cond_70
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    .line 7
    :cond_76
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    .line 9
    :cond_7c
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
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_1b

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_1b
    new-instance v12, Lchasm/gc/S8c99daf2393d8945426b4aac69b2df32_0;

    invoke-direct {v12}, Lchasm/gc/S8c99daf2393d8945426b4aac69b2df32_0;-><init>()V

    move-object/from16 v18, v12

    .line 3
    move-object/from16 v12, v18

    if-eqz v12, :cond_6e

    check-cast v12, Lchasm/gc/S8c99daf2393d8945426b4aac69b2df32_0;

    move/from16 v8, v17

    int-to-byte v8, v8

    iput-byte v8, v12, Lchasm/gc/S8c99daf2393d8945426b4aac69b2df32_0;->f0:B

    .line 4
    move-object/from16 v12, v18

    if-eqz v12, :cond_73

    check-cast v12, Lchasm/gc/S8c99daf2393d8945426b4aac69b2df32_0;

    move/from16 v8, v17

    int-to-char v8, v8

    iput-char v8, v12, Lchasm/gc/S8c99daf2393d8945426b4aac69b2df32_0;->f1:C

    .line 5
    move-object/from16 v12, v18

    if-eqz v12, :cond_78

    check-cast v12, Lchasm/gc/S8c99daf2393d8945426b4aac69b2df32_0;

    iget-byte v8, v12, Lchasm/gc/S8c99daf2393d8945426b4aac69b2df32_0;->f0:B

    move/from16 v19, v8

    .line 6
    move-object/from16 v12, v18

    if-eqz v12, :cond_7d

    check-cast v12, Lchasm/gc/S8c99daf2393d8945426b4aac69b2df32_0;

    iget-byte v8, v12, Lchasm/gc/S8c99daf2393d8945426b4aac69b2df32_0;->f0:B

    and-int/lit16 v8, v8, 0xff

    move/from16 v20, v8

    .line 7
    add-int v19, v19, v20

    .line 8
    move-object/from16 v12, v18

    if-eqz v12, :cond_82

    check-cast v12, Lchasm/gc/S8c99daf2393d8945426b4aac69b2df32_0;

    iget-char v8, v12, Lchasm/gc/S8c99daf2393d8945426b4aac69b2df32_0;->f1:C

    int-to-short v8, v8

    move/from16 v20, v8

    .line 9
    move-object/from16 v12, v18

    if-eqz v12, :cond_87

    check-cast v12, Lchasm/gc/S8c99daf2393d8945426b4aac69b2df32_0;

    iget-char v8, v12, Lchasm/gc/S8c99daf2393d8945426b4aac69b2df32_0;->f1:C

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
    :cond_6e
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 4
    :cond_73
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 5
    :cond_78
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 6
    :cond_7d
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 8
    :cond_82
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_87
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

    if-ltz v9, :cond_8b

    const v1, 0x1ffffff7

    if-gt v9, v1, :cond_8b

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

    new-instance v12, Lchasm/gc/A2e8ee4310ddddb4f73f38eb8f1c214e4_0;

    invoke-direct {v12, v10}, Lchasm/gc/A2e8ee4310ddddb4f73f38eb8f1c214e4_0;-><init>([I)V

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

    if-eqz v12, :cond_90

    check-cast v12, Lchasm/gc/A2e8ee4310ddddb4f73f38eb8f1c214e4_0;

    iget-object v10, v12, Lchasm/gc/A2e8ee4310ddddb4f73f38eb8f1c214e4_0;->data:[I

    const/4 v9, 0x0

    array-length v8, v10

    if-ltz v9, :cond_95

    if-ge v9, v8, :cond_9a

    const/16 v8, 0x64

    aput v8, v10, v9

    .line 7
    :goto_5b
    move-object/from16 v12, v20

    if-eqz v12, :cond_9f

    check-cast v12, Lchasm/gc/A2e8ee4310ddddb4f73f38eb8f1c214e4_0;

    iget-object v10, v12, Lchasm/gc/A2e8ee4310ddddb4f73f38eb8f1c214e4_0;->data:[I

    array-length v8, v10

    move/from16 v21, v8

    .line 8
    const/high16 v10, -0x80000000

    add-int v8, v18, v10

    add-int v9, v21, v10

    if-ge v8, v9, :cond_86

    .line 9
    move-object/from16 v12, v20

    if-eqz v12, :cond_a4

    check-cast v12, Lchasm/gc/A2e8ee4310ddddb4f73f38eb8f1c214e4_0;

    iget-object v10, v12, Lchasm/gc/A2e8ee4310ddddb4f73f38eb8f1c214e4_0;->data:[I

    move/from16 v9, v18

    array-length v8, v10

    if-ltz v9, :cond_a9

    if-ge v9, v8, :cond_ae

    aget v8, v10, v9

    move/from16 v21, v8

    .line 10
    add-int v19, v19, v21

    .line 11
    add-int/lit8 v18, v18, 0x1

    .line 12
    goto :goto_5b

    .line 13
    :cond_86
    move/from16 v8, v19

    return v8

    const/4 v8, 0x0

    return v8

    .line 2
    :cond_8b
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 5
    :cond_90
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 5
    :cond_95
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 5
    :cond_9a
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_9f
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_a4
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_a9
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_ae
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 39

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v25, v12

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v18, v15

    move-object/from16 v19, v15

    const/4 v8, 0x0

    move-object/from16 v24, v8

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

    if-gez v0, :cond_2f

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_2f
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

    new-instance v12, Lchasm/gc/A2e8ee4310ddddb4f73f38eb8f1c214e4_0;

    invoke-direct {v12, v10}, Lchasm/gc/A2e8ee4310ddddb4f73f38eb8f1c214e4_0;-><init>([I)V

    move-object/from16 v18, v12

    .line 7
    const/4 v9, 0x4

    if-ltz v9, :cond_a3

    const v1, 0x1ffffff7

    if-gt v9, v1, :cond_a3

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

    new-instance v12, Lchasm/gc/A2e8ee4310ddddb4f73f38eb8f1c214e4_0;

    invoke-direct {v12, v10}, Lchasm/gc/A2e8ee4310ddddb4f73f38eb8f1c214e4_0;-><init>([I)V

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

    if-eqz v12, :cond_a8

    check-cast v12, Lchasm/gc/A2e8ee4310ddddb4f73f38eb8f1c214e4_0;

    iget-object v10, v12, Lchasm/gc/A2e8ee4310ddddb4f73f38eb8f1c214e4_0;->data:[I

    const/4 v9, 0x1

    array-length v8, v10

    if-ltz v9, :cond_ad

    if-ge v9, v8, :cond_b2

    aget v8, v10, v9

    move/from16 v20, v8

    .line 10
    move-object/from16 v12, v19

    if-eqz v12, :cond_b7

    check-cast v12, Lchasm/gc/A2e8ee4310ddddb4f73f38eb8f1c214e4_0;

    iget-object v10, v12, Lchasm/gc/A2e8ee4310ddddb4f73f38eb8f1c214e4_0;->data:[I

    const/4 v9, 0x3

    array-length v8, v10

    if-ltz v9, :cond_bc

    if-ge v9, v8, :cond_c1

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
    :cond_a3
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_a8
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_ad
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_b2
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 10
    :cond_b7
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 10
    :cond_bc
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 10
    :cond_c1
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

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;

    invoke-direct {v12, v8}, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;-><init>(I)V

    move-object/from16 v19, v12

    .line 3
    move-object/from16 v12, v19

    if-eqz v12, :cond_5d

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;->value:I

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

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;

    invoke-direct {v12, v8}, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;-><init>(I)V

    move-object/from16 v21, v12

    .line 5
    move-object/from16 v12, v21

    if-eqz v12, :cond_62

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;->value:I

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
    .registers 36

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v25, v12

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v18, v15

    move/from16 v19, v15

    move/from16 v20, v15

    const/4 v8, 0x0

    move-object/from16 v24, v8

    .line 3
    :goto_1b
    const/high16 v10, -0x80000000

    add-int v8, v19, v10

    add-int v9, v17, v10

    if-ge v8, v9, :cond_6b

    .line 4
    move/from16 v21, v19

    move-object/from16 v23, v18

    .line 5
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_35

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_35
    new-instance v12, Lchasm/gc/S0cb30b1bf33839d21a02419f300cc916_0;

    invoke-direct {v12}, Lchasm/gc/S0cb30b1bf33839d21a02419f300cc916_0;-><init>()V

    move/from16 v8, v21

    iput v8, v12, Lchasm/gc/S0cb30b1bf33839d21a02419f300cc916_0;->f0:I

    move-object/from16 v10, v23

    check-cast v10, Lchasm/gc/S0cb30b1bf33839d21a02419f300cc916_0;

    iput-object v10, v12, Lchasm/gc/S0cb30b1bf33839d21a02419f300cc916_0;->f1:Lchasm/gc/S0cb30b1bf33839d21a02419f300cc916_0;

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

    if-gez v0, :cond_5f

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_5f
    new-array v10, v9, [I

    new-instance v12, Lchasm/gc/A2e8ee4310ddddb4f73f38eb8f1c214e4_0;

    invoke-direct {v12, v10}, Lchasm/gc/A2e8ee4310ddddb4f73f38eb8f1c214e4_0;-><init>([I)V

    move-object/from16 v22, v12

    .line 7
    add-int/lit8 v19, v19, 0x1

    .line 8
    goto :goto_1b

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

    check-cast v12, Lchasm/gc/S0cb30b1bf33839d21a02419f300cc916_0;

    iget-object v10, v12, Lchasm/gc/S0cb30b1bf33839d21a02419f300cc916_0;->f1:Lchasm/gc/S0cb30b1bf33839d21a02419f300cc916_0;

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

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v18, v15

    const/4 v8, 0x0

    move-object/from16 v27, v8

    .line 2
    if-eqz v17, :cond_35

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

    if-gez v0, :cond_24

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_24
    new-instance v12, Lchasm/gc/S63301eae39cf0650d95c11feecc7cb11_0;

    invoke-direct {v12}, Lchasm/gc/S63301eae39cf0650d95c11feecc7cb11_0;-><init>()V

    move/from16 v8, v19

    iput v8, v12, Lchasm/gc/S63301eae39cf0650d95c11feecc7cb11_0;->f0:I

    move-wide/from16 v2, v21

    iput-wide v2, v12, Lchasm/gc/S63301eae39cf0650d95c11feecc7cb11_0;->f1:J

    move-object/from16 v20, v12

    .line 6
    goto/16 :goto_50

    .line 7
    :cond_35
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_43

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_43
    const/16 v8, 0x9

    shl-int/lit8 v8, v8, 0x1

    ushr-int/lit8 v8, v8, 0x1

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;

    invoke-direct {v12, v8}, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;-><init>(I)V

    move-object/from16 v20, v12

    .line 8
    :goto_50
    move-object/from16 v18, v20

    .line 9
    move-object/from16 v12, v18

    if-eqz v12, :cond_5a

    instance-of v8, v12, Lchasm/gc/S63301eae39cf0650d95c11feecc7cb11_0;

    if-nez v8, :cond_83

    .line 10
    :cond_5a
    move-object/from16 v24, v18

    .line 11
    move-object/from16 v12, v24

    if-eqz v12, :cond_64

    instance-of v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;

    if-nez v8, :cond_69

    :cond_64
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->castFailed()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 12
    :cond_69
    move-object/from16 v12, v24

    if-eqz v12, :cond_88

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;->value:I

    move/from16 v23, v8

    .line 13
    move/from16 v8, v23

    return v8

    .line 14
    :goto_76
    move-object/from16 v12, v20

    if-eqz v12, :cond_8d

    check-cast v12, Lchasm/gc/S63301eae39cf0650d95c11feecc7cb11_0;

    iget v8, v12, Lchasm/gc/S63301eae39cf0650d95c11feecc7cb11_0;->f0:I

    move/from16 v17, v8

    .line 15
    move/from16 v8, v17

    return v8

    .line 16
    :cond_83
    move-object/from16 v20, v18

    goto :goto_76

    const/4 v8, 0x0

    return v8

    .line 12
    :cond_88
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 14
    :cond_8d
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

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;

    invoke-direct {v12, v8}, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;-><init>(I)V

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

    instance-of v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;

    if-nez v8, :cond_33

    :cond_31
    goto/16 :goto_44

    .line 7
    :cond_33
    move-object/from16 v12, v19

    if-eqz v12, :cond_48

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;->value:I

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

    packed-switch v4, :pswitch_data_32

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

    packed-switch v4, :pswitch_data_3a

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
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_31} :catch_36

    return-void

    :catch_32
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_36
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_3a
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
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_14} :catch_19

    return-void

    :catch_15
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_19
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

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
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_1c} :catch_21

    return-void

    :catch_1d
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_21
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

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

    packed-switch v4, :pswitch_data_52

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

    packed-switch v4, :pswitch_data_58

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
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_4f} :catch_54

    return-void

    :catch_50
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_54
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_58
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
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_1c} :catch_21

    return-void

    :catch_1d
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_21
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

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
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_25} :catch_2a

    return-void

    :catch_26
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_2a
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

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

    packed-switch v4, :pswitch_data_28

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

.method public invoke(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 12

    :try_start_0
    move-object v2, p0

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    invoke-static {v6}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->depth(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v7

    packed-switch v4, :pswitch_data_2e

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
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_25} :catch_2a

    return-void

    :catch_26
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_2a
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_2e
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

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->function(I)Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    move-result-object v4

    const/16 v2, 0x2e

    aput-object v4, p1, v2

    const/4 v2, 0x1

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->function(I)Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    move-result-object v4

    const/16 v2, 0x2f

    aput-object v4, p1, v2

    return-void
.end method


# virtual methods
.method public accept(Ljava/lang/Object;)V
    .registers 5

    check-cast p1, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;

    const/16 v0, 0x30

    new-array v1, v0, [Ljava/lang/Object;

    const/4 v0, 0x0

    new-array v2, v0, [J

    const/4 v0, 0x0

    aput-object v2, v1, v0

    invoke-static {p1, v1}, Lchasm/jit/Install;->bindObjects0(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V

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
