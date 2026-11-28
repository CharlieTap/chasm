.class public final Lchasm/dex/WasmModule$Code0;
.super Ljava/lang/Object;
.source "module.wasm"


# direct methods
.method public static f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;
    .registers 26

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v17, v15

    move/from16 v18, v15

    move/from16 v19, v15

    .line 2
    const/16 v18, 0x3

    .line 3
    const/16 v19, 0x4

    .line 4
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_21

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_21
    const/16 v9, 0x11

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v18

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v19

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v17, v12

    .line 5
    move-object/from16 v12, v17

    return-object v12

    const/4 v12, 0x0

    return-object v12
.end method

.method public static f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;
    .registers 24

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v17, v15

    .line 2
    const/4 v12, 0x0

    move-object/from16 v17, v12

    .line 3
    move-object/from16 v12, v17

    return-object v12

    const/4 v12, 0x0

    return-object v12
.end method

.method public static f10([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
    .registers 34

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

    const/16 v9, 0x11

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    move-object/from16 v26, v12

    move-object/from16 v27, v16

    move-object/from16 v28, v14

    move/from16 v29, v13

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move/from16 v18, v15

    move-object/from16 v19, v15

    move/from16 v20, v15

    move-object/from16 v21, v15

    move/from16 v22, v15

    move-wide/from16 v23, v0

    move-object/from16 v25, v15

    .line 2
    const/16 v18, 0x1

    .line 3
    const/16 v20, 0x14

    .line 4
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_45

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_45
    move-object/from16 v10, v26

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v18

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v20

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v19, v12

    .line 5
    move-object/from16 v30, v19

    invoke-static/range {v27 .. v30}, Lchasm/dex/WasmModule$Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v18

    .line 6
    const/16 v20, 0x5

    .line 7
    const-wide/high16 v23, 0x3ff0000000000000L    # 1.0

    .line 8
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_6e

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_6e
    const/16 v9, 0x12

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v20

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move-wide/from16 v2, v23

    invoke-static {v2, v3}, Ljava/lang/Double;->doubleToRawLongBits(D)J

    move-result-wide v2

    long-to-int v8, v2

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;->i1:I

    const/16 v15, 0x20

    ushr-long/2addr v2, v15

    long-to-int v8, v2

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;->i2:I

    move-object/from16 v21, v12

    .line 9
    move-object/from16 v30, v21

    invoke-static/range {v27 .. v30}, Lchasm/dex/WasmModule$Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v20

    .line 10
    add-int v18, v18, v20

    .line 11
    const/16 v20, 0x1

    .line 12
    const/16 v22, 0x12c

    .line 13
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_a8

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_a8
    move-object/from16 v10, v26

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v20

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v22

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v21, v12

    .line 14
    move-object/from16 v30, v21

    invoke-static/range {v27 .. v30}, Lchasm/dex/WasmModule$Code0;->f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v20

    .line 15
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_cd

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_cd
    const/16 v8, 0x9

    shl-int/lit8 v8, v8, 0x1

    ushr-int/lit8 v8, v8, 0x1

    new-instance v12, Lio/github/charlietap/chasm/reference/I31Reference;

    invoke-direct {v12, v8}, Lio/github/charlietap/chasm/reference/I31Reference;-><init>(I)V

    move-object/from16 v25, v12

    .line 16
    move-object/from16 v30, v25

    invoke-static/range {v27 .. v30}, Lchasm/dex/WasmModule$Code0;->f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v22

    .line 17
    add-int v20, v20, v22

    .line 18
    add-int v17, v18, v20

    .line 19
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f11([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I
    .registers 28

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move-object/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v18, v15

    move-object/from16 v19, v15

    move/from16 v20, v15

    .line 2
    move-object/from16 v19, v17

    .line 3
    move-object/from16 v12, v19

    if-eqz v12, :cond_34

    instance-of v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    if-eqz v8, :cond_34

    move-object v11, v12

    check-cast v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;->rtt:Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    const/16 v9, 0x12

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    if-eq v11, v10, :cond_37

    iget v8, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;->depth:I

    const/4 v9, 0x1

    if-lt v8, v9, :cond_34

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;->supertypes:[Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    const/4 v9, 0x1

    aget-object v11, v11, v9

    if-eq v11, v10, :cond_37

    :cond_34
    const/4 v11, 0x0

    goto/16 :goto_38

    :cond_37
    const/4 v11, 0x1

    :goto_38
    move/from16 v18, v11

    .line 4
    if-eqz v18, :cond_40

    .line 5
    const/16 v18, -0x1

    .line 6
    goto/16 :goto_99

    .line 7
    :cond_40
    move-object/from16 v12, v17

    if-eqz v12, :cond_61

    instance-of v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    if-eqz v8, :cond_61

    move-object v11, v12

    check-cast v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;->rtt:Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    const/16 v9, 0x11

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    if-eq v11, v10, :cond_66

    iget v8, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;->depth:I

    const/4 v9, 0x1

    if-lt v8, v9, :cond_61

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;->supertypes:[Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    const/4 v9, 0x1

    aget-object v11, v11, v9

    if-eq v11, v10, :cond_66

    :cond_61
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->castFailed()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    :cond_66
    if-eqz v12, :cond_9e

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move/from16 v18, v8

    .line 8
    move-object/from16 v12, v17

    if-eqz v12, :cond_8a

    instance-of v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    if-eqz v8, :cond_8a

    move-object v11, v12

    check-cast v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;->rtt:Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    const/16 v9, 0x10

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    if-eq v11, v10, :cond_8f

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;->supertypes:[Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    const/4 v9, 0x0

    aget-object v11, v11, v9

    if-eq v11, v10, :cond_8f

    :cond_8a
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->castFailed()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    :cond_8f
    if-eqz v12, :cond_a3

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v20, v8

    .line 9
    add-int v18, v18, v20

    .line 10
    :goto_99
    move/from16 v8, v18

    return v8

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_9e
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 8
    :cond_a3
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f12([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;
    .registers 26

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v17, v15

    move/from16 v18, v15

    move/from16 v19, v15

    .line 2
    const/16 v18, 0xb

    .line 3
    const/16 v19, 0x16

    .line 4
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_21

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_21
    const/16 v9, 0x11

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v18

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v19

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v17, v12

    .line 5
    move-object/from16 v12, v17

    return-object v12

    const/4 v12, 0x0

    return-object v12
.end method

.method public static f13([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;
    .registers 27

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v17, v15

    move/from16 v18, v15

    move-wide/from16 v19, v0

    .line 2
    const/16 v18, 0xb

    .line 3
    const-wide/high16 v19, 0x4000000000000000L    # 2.0

    .line 4
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_21

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_21
    const/16 v9, 0x12

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v18

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move-wide/from16 v2, v19

    invoke-static {v2, v3}, Ljava/lang/Double;->doubleToRawLongBits(D)J

    move-result-wide v2

    long-to-int v8, v2

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;->i1:I

    const/16 v15, 0x20

    ushr-long/2addr v2, v15

    long-to-int v8, v2

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;->i2:I

    move-object/from16 v17, v12

    .line 5
    move-object/from16 v12, v17

    return-object v12

    const/4 v12, 0x0

    return-object v12
.end method

.method public static f14([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 31

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v18, v15

    move/from16 v19, v15

    move-object/from16 v20, v15

    move/from16 v21, v15

    move-wide/from16 v22, v0

    .line 2
    if-eqz v17, :cond_40

    .line 3
    const/16 v19, 0x28

    .line 4
    const/16 v21, 0x0

    .line 5
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_29

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_29
    const/16 v9, 0x11

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v19

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v21

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v20, v12

    .line 6
    goto/16 :goto_72

    .line 7
    :cond_40
    const/16 v19, 0x32

    .line 8
    const-wide/16 v22, 0x0

    .line 9
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_52

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_52
    const/16 v9, 0x12

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v19

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move-wide/from16 v2, v22

    invoke-static {v2, v3}, Ljava/lang/Double;->doubleToRawLongBits(D)J

    move-result-wide v2

    long-to-int v8, v2

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;->i1:I

    const/16 v15, 0x20

    ushr-long/2addr v2, v15

    long-to-int v8, v2

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;->i2:I

    move-object/from16 v20, v12

    .line 10
    :goto_72
    move-object/from16 v18, v20

    .line 11
    move/from16 v8, v17

    const/4 v9, 0x2

    if-ne v8, v9, :cond_85

    .line 12
    const/16 v9, 0xa

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceGlobal;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceGlobal;->value:Ljava/lang/Object;

    move-object/from16 v18, v10

    .line 13
    goto/16 :goto_85

    .line 14
    :cond_85
    :goto_85
    move-object/from16 v20, v18

    .line 15
    move-object/from16 v12, v20

    if-eqz v12, :cond_8e

    const/4 v11, 0x0

    goto/16 :goto_8f

    :cond_8e
    const/4 v11, 0x1

    :goto_8f
    move/from16 v19, v11

    .line 16
    if-eqz v19, :cond_97

    .line 17
    const/16 v19, 0x0

    .line 18
    goto/16 :goto_a1

    .line 19
    :cond_97
    move-object/from16 v12, v18

    if-eqz v12, :cond_a6

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v19, v8

    .line 20
    :goto_a1
    move/from16 v8, v19

    return v8

    const/4 v8, 0x0

    return v8

    .line 19
    :cond_a6
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f15([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
    .registers 30

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v17, v15

    move/from16 v18, v15

    move/from16 v19, v15

    move/from16 v20, v15

    move-object/from16 v21, v15

    move/from16 v22, v15

    move-object/from16 v23, v15

    .line 3
    :cond_17
    move/from16 v20, v18

    move-object/from16 v23, v17

    .line 4
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_29

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_29
    const/16 v9, 0x1c

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R2__I_RR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R2__I_RR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v20

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move-object/from16 v10, v23

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R2__I_RR;->r0:Ljava/lang/Object;

    move-object/from16 v17, v12

    .line 5
    add-int/lit8 v18, v18, 0x1

    .line 6
    const/high16 v10, -0x80000000

    add-int v8, v18, v10

    const v9, -0x7ffffff6

    if-lt v8, v9, :cond_17

    .line 8
    :goto_49
    move-object/from16 v21, v17

    .line 9
    move-object/from16 v12, v21

    if-eqz v12, :cond_52

    const/4 v11, 0x0

    goto/16 :goto_53

    :cond_52
    const/4 v11, 0x1

    :goto_53
    move/from16 v20, v11

    .line 10
    if-nez v20, :cond_6e

    .line 11
    move-object/from16 v12, v17

    if-eqz v12, :cond_73

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R2__I_RR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v22, v8

    .line 12
    add-int v19, v19, v22

    .line 13
    move-object/from16 v12, v17

    if-eqz v12, :cond_78

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R2__I_RR;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R2__I_RR;->r0:Ljava/lang/Object;

    move-object/from16 v17, v10

    .line 14
    goto :goto_49

    .line 15
    :cond_6e
    move/from16 v8, v19

    return v8

    const/4 v8, 0x0

    return v8

    .line 11
    :cond_73
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 13
    :cond_78
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f16([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    .registers 27

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move-object/from16 v17, p3

    move-object/from16 v18, p4

    .line 2
    move-object/from16 v12, v17

    return-object v12

    const/4 v12, 0x0

    return-object v12
.end method

.method public static f17([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
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

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move/from16 v18, v15

    move-object/from16 v19, v15

    move/from16 v20, v15

    move-object/from16 v21, v15

    .line 2
    const/16 v18, 0x0

    .line 3
    const/16 v20, 0xd

    .line 4
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_37

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_37
    const/16 v9, 0x11

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v18

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v20

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v19, v12

    .line 5
    const/4 v12, 0x0

    move-object/from16 v21, v12

    .line 6
    move-object/from16 v25, v19

    move-object/from16 v26, v21

    invoke-static/range {v22 .. v26}, Lchasm/dex/WasmModule$Code0;->f16([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v19

    .line 7
    move-object/from16 v12, v19

    if-eqz v12, :cond_66

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move/from16 v17, v8

    .line 8
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_66
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f18([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)[J
    .registers 29

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move-object/from16 v18, v15

    move/from16 v19, v15

    move-object/from16 v20, v15

    move-wide/from16 v21, v0

    .line 2
    const/16 v17, 0x1

    .line 3
    const/16 v19, 0x2

    .line 4
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_25

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_25
    const/16 v9, 0x11

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v17

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v19

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v18, v12

    .line 5
    const/16 v19, 0x1e

    .line 6
    const-wide/16 v21, 0x0

    .line 7
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_4c

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_4c
    const/16 v9, 0x12

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v19

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move-wide/from16 v2, v21

    invoke-static {v2, v3}, Ljava/lang/Double;->doubleToRawLongBits(D)J

    move-result-wide v2

    long-to-int v8, v2

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;->i1:I

    const/16 v15, 0x20

    ushr-long/2addr v2, v15

    long-to-int v8, v2

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;->i2:I

    move-object/from16 v20, v12

    .line 8
    const/4 v8, 0x3

    new-array v12, v8, [J

    const-wide/16 v0, 0x190

    const/4 v10, 0x2

    aput-wide v0, v12, v10

    const/4 v8, 0x3

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->resultReferences(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)[Ljava/lang/Object;

    move-result-object v10

    move-object/from16 v11, v18

    const/4 v9, 0x0

    aput-object v11, v10, v9

    move-object/from16 v11, v20

    const/4 v9, 0x1

    aput-object v11, v10, v9

    return-object v12

    const/4 v8, 0x0

    return-object v8
.end method

.method public static f19([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
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

    move-object/from16 v25, v16

    move-object/from16 v26, v14

    move/from16 v27, v13

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move-object/from16 v18, v15

    move-object/from16 v19, v15

    move/from16 v20, v15

    move-object/from16 v21, v15

    move/from16 v22, v15

    move-object/from16 v23, v15

    move/from16 v24, v15

    .line 2
    invoke-static/range {v25 .. v27}, Lchasm/dex/WasmModule$Code0;->f18([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)[J

    move-result-object v12

    const/4 v8, 0x3

    invoke-static {v14, v8}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->resultReferences(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)[Ljava/lang/Object;

    move-result-object v11

    const/4 v9, 0x0

    aget-object v10, v11, v9

    move-object/from16 v21, v10

    const/4 v9, 0x1

    aget-object v10, v11, v9

    move-object/from16 v23, v10

    const/4 v10, 0x2

    aget-wide v0, v12, v10

    long-to-int v15, v0

    move/from16 v24, v15

    .line 3
    move/from16 v20, v24

    move-object/from16 v19, v23

    move-object/from16 v18, v21

    .line 4
    move-object/from16 v12, v18

    if-eqz v12, :cond_67

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move/from16 v22, v8

    .line 5
    move-object/from16 v12, v19

    if-eqz v12, :cond_6c

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v24, v8

    .line 6
    add-int v22, v22, v24

    .line 7
    add-int v17, v20, v22

    .line 8
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 4
    :cond_67
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 5
    :cond_6c
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I
    .registers 34

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move-object/from16 v18, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v19, v15

    move/from16 v20, v15

    move-object/from16 v21, v15

    move/from16 v22, v15

    .line 2
    const/16 v20, 0x1

    .line 3
    const/16 v22, 0x2

    .line 4
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_25

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_25
    const/16 v9, 0x11

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v20

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v22

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v21, v12

    .line 5
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_48

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_48
    const/16 v9, 0x18

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move-object/from16 v10, v21

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    move-object/from16 v19, v12

    .line 6
    move-object/from16 v12, v19

    if-eqz v12, :cond_7c

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    move-object/from16 v10, v18

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    .line 7
    move-object/from16 v12, v19

    if-eqz v12, :cond_81

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    move-object/from16 v21, v10

    .line 8
    move-object/from16 v12, v21

    if-eqz v12, :cond_74

    const/4 v11, 0x0

    goto/16 :goto_75

    :cond_74
    const/4 v11, 0x1

    :goto_75
    move/from16 v17, v11

    .line 9
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 6
    :cond_7c
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_81
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f20([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;I)I
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

    move-object/from16 v17, p3

    move/from16 v18, p4

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v19, v15

    move-object/from16 v20, v15

    move/from16 v21, v15

    move/from16 v22, v15

    move/from16 v23, v15

    .line 2
    :goto_23
    if-nez v18, :cond_31

    .line 3
    move-object/from16 v12, v17

    if-eqz v12, :cond_81

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move/from16 v19, v8

    .line 4
    goto/16 :goto_7c

    .line 5
    :cond_31
    move-object/from16 v12, v17

    if-eqz v12, :cond_86

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move/from16 v21, v8

    .line 6
    add-int/lit8 v21, v21, 0x1

    .line 7
    const/16 v22, 0x0

    .line 8
    move/from16 v23, v21

    .line 9
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_4f

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_4f
    const/16 v9, 0x11

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v22

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v23

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v20, v12

    .line 10
    add-int/lit8 v21, v18, -0x1

    .line 11
    move-object/from16 v24, v20

    move/from16 v26, v21

    move-object/from16 v17, v24

    move/from16 v18, v26

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v19, v15

    move-object/from16 v20, v15

    move/from16 v21, v15

    move/from16 v22, v15

    move/from16 v23, v15

    goto :goto_23

    .line 12
    :goto_7c
    move/from16 v8, v19

    return v8

    const/4 v8, 0x0

    return v8

    .line 3
    :cond_81
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 5
    :cond_86
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f21([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;I)I
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

    move-object/from16 v17, p3

    move/from16 v18, p4

    .line 2
    const/4 v8, 0x2

    new-array v10, v8, [J

    new-array v11, v8, [Ljava/lang/Object;

    const/4 v9, 0x0

    move-object/from16 v12, v17

    aput-object v12, v11, v9

    const/4 v9, 0x1

    move/from16 v0, v18

    int-to-long v0, v0

    aput-wide v0, v10, v9

    const/4 v9, 0x1

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    const/16 v9, 0x8

    aget-object v3, v16, v9

    check-cast v3, Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    invoke-static {v14, v2, v3, v10, v11}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->requestTailObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;[J[Ljava/lang/Object;)V

    const/4 v8, 0x0

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f22([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
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

    move-object/from16 v20, v16

    move-object/from16 v21, v14

    move/from16 v22, v13

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move-object/from16 v18, v15

    move/from16 v19, v15

    .line 2
    const/16 v17, 0x0

    .line 3
    const/16 v19, 0x5

    .line 4
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_33

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_33
    const/16 v9, 0x11

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v17

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v19

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v18, v12

    .line 5
    move-object/from16 v23, v18

    const/16 v24, 0x3e8

    invoke-static/range {v20 .. v24}, Lchasm/dex/WasmModule$Code0;->f21([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;I)I

    move-result v9

    iget-object v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->pendingTailFunction:Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    if-eqz v8, :cond_63

    const/4 v8, 0x1

    new-array v10, v8, [J

    const/4 v11, 0x0

    invoke-static {v14, v10, v11, v13}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->completeTailObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[J[Ljava/lang/Object;I)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_6a

    const/4 v8, 0x0

    aget-wide v0, v10, v8

    long-to-int v9, v0

    :cond_63
    move/from16 v17, v9

    .line 6
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    :cond_6a
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const/4 v8, 0x0

    return v8
.end method

.method public static f23([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
    .registers 31

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move/from16 v18, v15

    move-object/from16 v19, v15

    move/from16 v22, v15

    move-wide/from16 v20, v0

    move-object/from16 v23, v15

    move/from16 v24, v15

    .line 2
    const/16 v18, 0x0

    .line 3
    const/16 v22, 0x3c

    .line 4
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_29

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_29
    const/16 v9, 0x11

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v18

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v22

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v19, v12

    .line 5
    move-object/from16 v23, v19

    .line 6
    const/16 v24, 0x7

    .line 7
    const/4 v8, 0x2

    new-array v10, v8, [J

    new-array v11, v8, [Ljava/lang/Object;

    const/4 v9, 0x0

    move-object/from16 v12, v23

    aput-object v12, v11, v9

    const/4 v9, 0x1

    move/from16 v0, v24

    int-to-long v0, v0

    aput-wide v0, v10, v9

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/16 v9, 0x1a

    aget-wide v4, v12, v9

    long-to-int v8, v4

    invoke-static {v14, v8, v10, v11}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->createException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J[Ljava/lang/Object;)Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;

    move-result-object v12

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;->words:[J

    iget-object v11, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;->references:[Ljava/lang/Object;

    const/4 v9, 0x0

    aget-object v12, v11, v9

    move-object/from16 v19, v12

    const/4 v9, 0x1

    aget-wide v0, v10, v9

    move-wide/from16 v20, v0

    goto/16 :goto_70

    .line 8
    :goto_70
    move-object/from16 v12, v19

    if-eqz v12, :cond_7f

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move/from16 v17, v8

    .line 9
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 8
    :cond_7f
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f24([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
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

    const/16 v9, 0x11

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    move-object/from16 v24, v12

    const/16 v9, 0xb

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceGlobal;

    move-object/from16 v25, v12

    move-object/from16 v26, v16

    move-object/from16 v27, v14

    move/from16 v28, v13

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move/from16 v18, v15

    move-object/from16 v19, v15

    move/from16 v20, v15

    move-object/from16 v21, v15

    move/from16 v22, v15

    const/4 v8, 0x0

    move-object/from16 v23, v8

    .line 2
    const/16 v18, 0x0

    .line 3
    const/16 v20, 0x46

    .line 4
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_4c

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_4c
    move-object/from16 v10, v24

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v18

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v20

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v19, v12

    .line 5
    move-object/from16 v12, v25

    move-object/from16 v10, v19

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceGlobal;->value:Ljava/lang/Object;

    .line 6
    const/16 v20, 0x8

    .line 7
    const/16 v22, 0x0

    .line 8
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_75

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_75
    move-object/from16 v10, v24

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v20

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v22

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v21, v12

    .line 9
    const/16 v9, 0xc

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;

    const/4 v8, 0x2

    move-object/from16 v10, v21

    invoke-static {v12, v8, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedTables;->tableSet(Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;ILjava/lang/Object;)V

    .line 10
    move-object/from16 v12, v25

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceGlobal;->value:Ljava/lang/Object;

    move-object/from16 v19, v10

    .line 11
    move-object/from16 v12, v19

    if-eqz v12, :cond_156

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move/from16 v18, v8

    .line 12
    const/16 v9, 0xc

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;

    const/4 v8, 0x2

    invoke-static {v12, v8}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedTables;->tableGet(Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;I)Ljava/lang/Object;

    move-result-object v10

    move-object/from16 v21, v10

    .line 13
    move-object/from16 v12, v21

    if-eqz v12, :cond_15b

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v20, v8

    .line 14
    add-int v18, v18, v20

    .line 15
    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/16 v9, 0xd

    aget-wide v4, v12, v9

    long-to-int v10, v4

    const/16 v9, 0xe

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;

    const/4 v8, 0x0

    invoke-static {v14, v2, v10, v8}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedTables;->indirectEntry(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;II)Ljava/lang/Object;

    move-result-object v12

    instance-of v11, v12, Lchasm/dex/WasmModule$Sig0;

    if-eqz v11, :cond_fd

    move-object/from16 v26, v12

    move/from16 v28, v13

    invoke-interface/range {v26 .. v28}, Lchasm/dex/WasmModule$Sig0;->typed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v12

    move-object/from16 v26, v16

    move-object/from16 v27, v14

    move/from16 v28, v13

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_151

    iget-object v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->pendingTailFunction:Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    if-eqz v8, :cond_f9

    const/4 v8, 0x1

    new-array v10, v8, [J

    new-array v11, v8, [Ljava/lang/Object;

    invoke-static {v14, v10, v11, v13}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->completeTailObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[J[Ljava/lang/Object;I)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_151

    const/4 v8, 0x0

    aget-object v12, v11, v8

    :cond_f9
    move-object/from16 v21, v12

    goto/16 :goto_140

    :cond_fd
    move-object/from16 v12, v23

    if-nez v12, :cond_106

    const/4 v8, 0x1

    new-array v12, v8, [J

    move-object/from16 v23, v12

    :cond_106
    move-object v10, v12

    const/4 v8, 0x1

    new-array v11, v8, [Ljava/lang/Object;

    move-object/from16 v27, v14

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    move-object/from16 v28, v12

    const/16 v9, 0xe

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;

    move-object/from16 v29, v12

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/16 v9, 0xd

    aget-wide v4, v12, v9

    long-to-int v8, v4

    move/from16 v30, v8

    const/4 v8, 0x0

    move/from16 v31, v8

    move-object/from16 v32, v10

    move-object/from16 v33, v11

    move/from16 v34, v13

    invoke-static/range {v27 .. v34}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->callIndirectObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;II[J[Ljava/lang/Object;I)V

    move-object/from16 v27, v14

    move/from16 v28, v13

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_151

    const/4 v9, 0x0

    aget-object v12, v11, v9

    move-object/from16 v21, v12

    .line 16
    :goto_140
    move-object/from16 v12, v21

    if-eqz v12, :cond_160

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v20, v8

    .line 17
    add-int v17, v18, v20

    .line 18
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    :cond_151
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const/4 v8, 0x0

    return v8

    .line 11
    :cond_156
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 13
    :cond_15b
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 16
    :cond_160
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f25([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
    .registers 31

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move-object/from16 v18, v15

    move/from16 v19, v15

    move-object/from16 v20, v15

    move/from16 v21, v15

    move-object/from16 v22, v15

    .line 2
    const/16 v19, 0x0

    .line 3
    const/16 v21, 0x50

    .line 4
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_27

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_27
    const/16 v9, 0x11

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v19

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v21

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v20, v12

    .line 5
    const/4 v12, 0x0

    move-object/from16 v22, v12

    .line 6
    const/16 v1, 0x28

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_4d

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_4d
    const/4 v9, 0x2

    new-array v10, v9, [Ljava/lang/Object;

    const/4 v11, 0x0

    move-object/from16 v12, v20

    aput-object v12, v10, v11

    const/4 v11, 0x1

    move-object/from16 v12, v22

    aput-object v12, v10, v11

    const/16 v9, 0x1b

    aget-object v11, v16, v9

    check-cast v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/RefArray;

    invoke-direct {v12, v11, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/RefArray;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;[Ljava/lang/Object;)V

    move-object/from16 v18, v12

    .line 7
    move-object/from16 v12, v18

    if-eqz v12, :cond_a6

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/RefArray;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/RefArray;->data:[Ljava/lang/Object;

    const/4 v9, 0x0

    array-length v8, v10

    if-ltz v9, :cond_ab

    if-ge v9, v8, :cond_b0

    aget-object v12, v10, v9

    move-object/from16 v20, v12

    .line 8
    move-object/from16 v12, v20

    if-eqz v12, :cond_b5

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move/from16 v19, v8

    .line 9
    move-object/from16 v12, v18

    if-eqz v12, :cond_ba

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/RefArray;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/RefArray;->data:[Ljava/lang/Object;

    const/4 v9, 0x1

    array-length v8, v10

    if-ltz v9, :cond_bf

    if-ge v9, v8, :cond_c4

    aget-object v12, v10, v9

    move-object/from16 v22, v12

    .line 10
    move-object/from16 v12, v22

    if-eqz v12, :cond_9c

    const/4 v11, 0x0

    goto/16 :goto_9d

    :cond_9c
    const/4 v11, 0x1

    :goto_9d
    move/from16 v21, v11

    .line 11
    add-int v17, v19, v21

    .line 12
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_a6
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_ab
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_b0
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 8
    :cond_b5
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_ba
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_bf
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_c4
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f26([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
    .registers 28

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/16 v9, 0x11

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    move-object/from16 v21, v12

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move/from16 v18, v15

    move-object/from16 v19, v15

    move/from16 v20, v15

    .line 2
    const/16 v18, 0x0

    .line 3
    const/16 v20, 0x5a

    .line 4
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_2b

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_2b
    move-object/from16 v10, v21

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v18

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v20

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v19, v12

    .line 7
    move-object/from16 v12, v19

    if-eqz v12, :cond_59

    instance-of v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    if-eqz v8, :cond_59

    move-object v11, v12

    check-cast v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;->rtt:Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    move-object/from16 v10, v21

    if-eq v11, v10, :cond_5e

    iget v8, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;->depth:I

    const/4 v9, 0x1

    if-lt v8, v9, :cond_59

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;->supertypes:[Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    const/4 v9, 0x1

    aget-object v11, v11, v9

    if-eq v11, v10, :cond_5e

    :cond_59
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->castFailed()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    :cond_5e
    if-eqz v12, :cond_6b

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move/from16 v17, v8

    .line 8
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_6b
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f27([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 31

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v18, v15

    move/from16 v19, v15

    move-object/from16 v20, v15

    move/from16 v21, v15

    .line 2
    if-gez v17, :cond_3e

    .line 3
    const/16 v19, 0x0

    .line 4
    const/16 v21, 0x0

    .line 5
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_27

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_27
    const/16 v9, 0x11

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v19

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v21

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v20, v12

    .line 6
    goto/16 :goto_59

    .line 7
    :cond_3e
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_4c

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_4c
    move/from16 v8, v17

    shl-int/lit8 v8, v8, 0x1

    ushr-int/lit8 v8, v8, 0x1

    new-instance v12, Lio/github/charlietap/chasm/reference/I31Reference;

    invoke-direct {v12, v8}, Lio/github/charlietap/chasm/reference/I31Reference;-><init>(I)V

    move-object/from16 v20, v12

    .line 8
    :goto_59
    move-object/from16 v18, v20

    .line 9
    move-object/from16 v12, v18

    if-eqz v12, :cond_63

    instance-of v8, v12, Lio/github/charlietap/chasm/reference/I31Reference;

    if-nez v8, :cond_76

    .line 10
    :cond_63
    const/4 v8, -0x1

    return v8

    .line 11
    :goto_65
    move-object/from16 v12, v20

    if-eqz v12, :cond_7b

    check-cast v12, Lio/github/charlietap/chasm/reference/I31Reference;

    iget v8, v12, Lio/github/charlietap/chasm/reference/I31Reference;->value:I

    shl-int/lit8 v8, v8, 0x1

    shr-int/lit8 v8, v8, 0x1

    move/from16 v17, v8

    .line 12
    move/from16 v8, v17

    return v8

    .line 13
    :cond_76
    move-object/from16 v20, v18

    goto :goto_65

    const/4 v8, 0x0

    return v8

    .line 11
    :cond_7b
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
    .registers 28

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move-object/from16 v18, v15

    move/from16 v19, v15

    move-object/from16 v20, v15

    move/from16 v21, v15

    .line 2
    const/16 v19, 0x7

    .line 3
    const/16 v21, 0x9

    .line 4
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_25

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_25
    const/16 v9, 0x14

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v19

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v21

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v20, v12

    .line 5
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_48

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_48
    const/16 v9, 0x16

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move-object/from16 v10, v20

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    move-object/from16 v18, v12

    .line 6
    move-object/from16 v12, v18

    if-eqz v12, :cond_80

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    iget-object v12, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    if-eqz v12, :cond_85

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move/from16 v19, v8

    .line 7
    move-object/from16 v12, v18

    if-eqz v12, :cond_8a

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    iget-object v12, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    if-eqz v12, :cond_8f

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v21, v8

    .line 8
    add-int v17, v19, v21

    .line 9
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 6
    :cond_80
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 6
    :cond_85
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_8a
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_8f
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
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

    const/16 v9, 0x11

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    move-object/from16 v24, v12

    move-object/from16 v25, v16

    move-object/from16 v26, v14

    move/from16 v27, v13

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move-object/from16 v18, v15

    move/from16 v19, v15

    move-object/from16 v20, v15

    move/from16 v21, v15

    move-object/from16 v22, v15

    move/from16 v23, v15

    .line 2
    const/16 v19, 0x1

    .line 3
    const/16 v21, 0x2

    .line 4
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_43

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_43
    move-object/from16 v10, v24

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v19

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v21

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v20, v12

    .line 5
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_62

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_62
    const/16 v9, 0x18

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move-object/from16 v10, v20

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    move-object/from16 v18, v12

    .line 6
    const/4 v12, 0x0

    move-object/from16 v22, v12

    .line 7
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_84

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_84
    const/16 v9, 0x17

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move-object/from16 v10, v22

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    move-object/from16 v22, v12

    .line 8
    move-object/from16 v12, v22

    if-eqz v12, :cond_2b0

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    move-object/from16 v22, v10

    .line 9
    move-object/from16 v12, v18

    if-eqz v12, :cond_2b5

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    move-object/from16 v10, v22

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    .line 10
    move-object/from16 v12, v18

    if-eqz v12, :cond_2ba

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    move-object/from16 v22, v10

    .line 11
    move-object/from16 v12, v22

    if-eqz v12, :cond_ba

    const/4 v11, 0x0

    goto/16 :goto_bb

    :cond_ba
    const/4 v11, 0x1

    :goto_bb
    move/from16 v21, v11

    .line 12
    add-int v17, v17, v21

    .line 13
    const/16 v21, 0x1

    .line 14
    const/16 v23, 0x2

    .line 15
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_d1

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_d1
    move-object/from16 v10, v24

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v21

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v23

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v22, v12

    .line 16
    move-object/from16 v12, v18

    if-eqz v12, :cond_2bf

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    move-object/from16 v10, v22

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    .line 17
    const/16 v9, 0xa

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceGlobal;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceGlobal;->value:Ljava/lang/Object;

    move-object/from16 v22, v10

    .line 18
    move-object/from16 v12, v18

    if-eqz v12, :cond_2c4

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    move-object/from16 v10, v22

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    .line 19
    move-object/from16 v12, v18

    if-eqz v12, :cond_2c9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    move-object/from16 v22, v10

    .line 20
    move-object/from16 v12, v22

    if-eqz v12, :cond_111

    const/4 v11, 0x0

    goto/16 :goto_112

    :cond_111
    const/4 v11, 0x1

    :goto_112
    move/from16 v21, v11

    .line 21
    add-int v17, v17, v21

    .line 22
    const/16 v21, 0x1

    .line 23
    const/16 v23, 0x2

    .line 24
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_128

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_128
    move-object/from16 v10, v24

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v21

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v23

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v22, v12

    .line 25
    move-object/from16 v12, v18

    if-eqz v12, :cond_2ce

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    move-object/from16 v10, v22

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    .line 26
    invoke-static/range {v25 .. v27}, Lchasm/dex/WasmModule$Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v22

    .line 27
    move-object/from16 v12, v18

    if-eqz v12, :cond_2d3

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    move-object/from16 v10, v22

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    .line 28
    move-object/from16 v12, v18

    if-eqz v12, :cond_2d8

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    move-object/from16 v22, v10

    .line 29
    move-object/from16 v12, v22

    if-eqz v12, :cond_162

    const/4 v11, 0x0

    goto/16 :goto_163

    :cond_162
    const/4 v11, 0x1

    :goto_163
    move/from16 v21, v11

    .line 30
    add-int v17, v17, v21

    .line 31
    const/16 v21, 0x1

    .line 32
    const/16 v23, 0x2

    .line 33
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_179

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_179
    move-object/from16 v10, v24

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v21

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v23

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v22, v12

    .line 34
    move-object/from16 v12, v18

    if-eqz v12, :cond_2dd

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    move-object/from16 v10, v22

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    .line 35
    const/16 v9, 0xd

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;

    const/4 v8, 0x1

    invoke-static {v12, v8}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedTables;->tableGet(Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;I)Ljava/lang/Object;

    move-result-object v10

    move-object/from16 v22, v10

    .line 36
    move-object/from16 v12, v18

    if-eqz v12, :cond_2e2

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    move-object/from16 v10, v22

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    .line 37
    move-object/from16 v12, v18

    if-eqz v12, :cond_2e7

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    move-object/from16 v22, v10

    .line 38
    move-object/from16 v12, v22

    if-eqz v12, :cond_1bc

    const/4 v11, 0x0

    goto/16 :goto_1bd

    :cond_1bc
    const/4 v11, 0x1

    :goto_1bd
    move/from16 v21, v11

    .line 39
    add-int v17, v17, v21

    .line 40
    const/16 v21, 0x1

    .line 41
    const/16 v23, 0x2

    .line 42
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_1d3

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_1d3
    move-object/from16 v10, v24

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v21

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v23

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v22, v12

    .line 43
    move-object/from16 v12, v18

    if-eqz v12, :cond_2ec

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    move-object/from16 v10, v22

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    .line 44
    const/4 v9, 0x2

    if-ltz v9, :cond_2f1

    const v1, 0x1ffffff7

    if-gt v9, v1, :cond_2f1

    shl-int/lit8 v1, v9, 0x2

    add-int/lit16 v1, v1, 0x20

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_206

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_206
    new-array v10, v9, [Ljava/lang/Object;

    const/16 v9, 0x19

    aget-object v11, v16, v9

    check-cast v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/RefArray;

    invoke-direct {v12, v11, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/RefArray;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;[Ljava/lang/Object;)V

    move-object/from16 v22, v12

    .line 45
    move-object/from16 v12, v22

    if-eqz v12, :cond_2f6

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/RefArray;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/RefArray;->data:[Ljava/lang/Object;

    const/4 v9, 0x1

    array-length v8, v10

    if-ltz v9, :cond_2fb

    if-ge v9, v8, :cond_300

    aget-object v12, v10, v9

    move-object/from16 v22, v12

    .line 46
    move-object/from16 v12, v18

    if-eqz v12, :cond_305

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    move-object/from16 v10, v22

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    .line 47
    move-object/from16 v12, v18

    if-eqz v12, :cond_30a

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    move-object/from16 v22, v10

    .line 48
    move-object/from16 v12, v22

    if-eqz v12, :cond_242

    const/4 v11, 0x0

    goto/16 :goto_243

    :cond_242
    const/4 v11, 0x1

    :goto_243
    move/from16 v21, v11

    .line 49
    add-int v17, v17, v21

    .line 50
    const/16 v21, 0x1

    .line 51
    const/16 v23, 0x2

    .line 52
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_259

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_259
    move-object/from16 v10, v24

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v21

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v23

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v22, v12

    .line 53
    move-object/from16 v12, v18

    if-eqz v12, :cond_30f

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    move-object/from16 v10, v22

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    .line 54
    const/4 v12, 0x0

    move-object/from16 v22, v12

    .line 55
    move-object/from16 v12, v22

    if-eqz v12, :cond_280

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->castFailed()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 56
    :cond_280
    move-object/from16 v12, v18

    if-eqz v12, :cond_314

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    move-object/from16 v10, v22

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    .line 57
    move-object/from16 v12, v18

    if-eqz v12, :cond_319

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    move-object/from16 v22, v10

    .line 58
    move-object/from16 v12, v22

    if-eqz v12, :cond_29b

    const/4 v11, 0x0

    goto/16 :goto_29c

    :cond_29b
    const/4 v11, 0x1

    :goto_29c
    move/from16 v21, v11

    .line 59
    add-int v17, v17, v21

    .line 60
    const/4 v12, 0x0

    move-object/from16 v22, v12

    .line 61
    move-object/from16 v28, v22

    invoke-static/range {v25 .. v28}, Lchasm/dex/WasmModule$Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v21

    .line 62
    add-int v17, v17, v21

    .line 63
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 8
    :cond_2b0
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_2b5
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 10
    :cond_2ba
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 16
    :cond_2bf
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 18
    :cond_2c4
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 19
    :cond_2c9
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 25
    :cond_2ce
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 27
    :cond_2d3
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 28
    :cond_2d8
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 34
    :cond_2dd
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 36
    :cond_2e2
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 37
    :cond_2e7
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 43
    :cond_2ec
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 44
    :cond_2f1
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 45
    :cond_2f6
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 45
    :cond_2fb
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 45
    :cond_300
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 46
    :cond_305
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 47
    :cond_30a
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 53
    :cond_30f
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 56
    :cond_314
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 57
    :cond_319
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;
    .registers 24

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v17, v15

    .line 2
    const/4 v12, 0x0

    move-object/from16 v17, v12

    .line 3
    move-object/from16 v12, v17

    return-object v12

    const/4 v12, 0x0

    return-object v12
.end method

.method public static f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
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

    move-object/from16 v26, v16

    move-object/from16 v27, v14

    move/from16 v28, v13

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move-object/from16 v18, v15

    move-object/from16 v19, v15

    move/from16 v20, v15

    move-object/from16 v21, v15

    move/from16 v22, v15

    move-object/from16 v23, v15

    .line 2
    invoke-static/range {v26 .. v28}, Lchasm/dex/WasmModule$Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v19

    .line 3
    const/16 v20, 0x1

    .line 4
    const/16 v22, 0x2

    .line 5
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_3f

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_3f
    const/16 v9, 0x11

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v20

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v22

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v21, v12

    .line 6
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_62

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_62
    const/16 v9, 0x18

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move-object/from16 v10, v21

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    move-object/from16 v18, v12

    .line 7
    move-object/from16 v21, v18

    .line 8
    move-object/from16 v12, v19

    if-eqz v12, :cond_9c

    .line 9
    const/4 v8, -0x1

    return v8

    .line 10
    :goto_7b
    move-object/from16 v12, v21

    if-eqz v12, :cond_a1

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    move-object/from16 v10, v23

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    .line 11
    move-object/from16 v12, v18

    if-eqz v12, :cond_a6

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    move-object/from16 v21, v10

    .line 12
    move-object/from16 v12, v21

    if-eqz v12, :cond_96

    const/4 v11, 0x0

    goto/16 :goto_97

    :cond_96
    const/4 v11, 0x1

    :goto_97
    move/from16 v17, v11

    .line 13
    move/from16 v8, v17

    return v8

    .line 14
    :cond_9c
    move-object/from16 v23, v19

    goto :goto_7b

    const/4 v8, 0x0

    return v8

    .line 10
    :cond_a1
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 11
    :cond_a6
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
    .registers 33

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move-object/from16 v18, v15

    move/from16 v19, v15

    move-object/from16 v20, v15

    move/from16 v21, v15

    move-object/from16 v22, v15

    .line 2
    const/16 v19, 0x1

    .line 3
    const/16 v21, 0x2

    .line 4
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_27

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_27
    const/16 v9, 0x11

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v19

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v8, v21

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move-object/from16 v20, v12

    .line 5
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_4a

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_4a
    const/16 v9, 0x18

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move-object/from16 v10, v20

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    move-object/from16 v18, v12

    .line 6
    move-object/from16 v20, v18

    .line 7
    const/4 v12, 0x0

    move-object/from16 v22, v12

    .line 8
    const/4 v8, 0x1

    const/4 v10, 0x0

    new-array v11, v8, [Ljava/lang/Object;

    const/4 v9, 0x0

    move-object/from16 v12, v22

    aput-object v12, v11, v9

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/16 v9, 0x1b

    aget-wide v4, v12, v9

    long-to-int v8, v4

    invoke-static {v14, v8, v10, v11}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->createException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J[Ljava/lang/Object;)Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;

    move-result-object v12

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;->words:[J

    iget-object v11, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;->references:[Ljava/lang/Object;

    const/4 v9, 0x0

    aget-object v12, v11, v9

    move-object/from16 v22, v12

    goto/16 :goto_82

    .line 9
    :goto_82
    move-object/from16 v12, v20

    if-eqz v12, :cond_a5

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    move-object/from16 v10, v22

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    .line 10
    move-object/from16 v12, v18

    if-eqz v12, :cond_aa

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;->r0:Ljava/lang/Object;

    move-object/from16 v20, v10

    .line 11
    move-object/from16 v12, v20

    if-eqz v12, :cond_9d

    const/4 v11, 0x0

    goto/16 :goto_9e

    :cond_9d
    const/4 v11, 0x1

    :goto_9e
    move/from16 v17, v11

    .line 12
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_a5
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 10
    :cond_aa
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I
    .registers 30

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move-object/from16 v18, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v19, v15

    move-object/from16 v20, v15

    .line 2
    move-object/from16 v12, v18

    if-eqz v12, :cond_30

    instance-of v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    if-eqz v8, :cond_30

    move-object v11, v12

    check-cast v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;->rtt:Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    const/16 v9, 0x11

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    if-eq v11, v10, :cond_6f

    iget v8, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;->depth:I

    const/4 v9, 0x1

    if-lt v8, v9, :cond_30

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;->supertypes:[Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    const/4 v9, 0x1

    aget-object v11, v11, v9

    if-eq v11, v10, :cond_6f

    .line 3
    :cond_30
    move-object/from16 v12, v18

    if-eqz v12, :cond_51

    instance-of v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    if-eqz v8, :cond_51

    move-object v11, v12

    check-cast v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;->rtt:Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    const/16 v9, 0x12

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    if-eq v11, v10, :cond_72

    iget v8, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;->depth:I

    const/4 v9, 0x1

    if-lt v8, v9, :cond_51

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;->supertypes:[Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    const/4 v9, 0x1

    aget-object v11, v11, v9

    if-eq v11, v10, :cond_72

    .line 4
    :cond_51
    const/4 v8, -0x1

    return v8

    .line 5
    :goto_53
    move-object/from16 v12, v20

    if-eqz v12, :cond_77

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move/from16 v19, v8

    .line 6
    add-int/lit8 v19, v19, 0x64

    .line 7
    move/from16 v8, v19

    return v8

    .line 8
    :goto_62
    move-object/from16 v12, v20

    if-eqz v12, :cond_7c

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move/from16 v17, v8

    .line 9
    move/from16 v8, v17

    return v8

    .line 10
    :cond_6f
    move-object/from16 v20, v18

    goto :goto_62

    .line 11
    :cond_72
    move-object/from16 v20, v18

    goto :goto_53

    const/4 v8, 0x0

    return v8

    .line 5
    :cond_77
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 8
    :cond_7c
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I
    .registers 29

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move-object/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v18, v15

    move-object/from16 v19, v15

    .line 2
    move-object/from16 v12, v17

    if-eqz v12, :cond_30

    instance-of v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    if-eqz v8, :cond_30

    move-object v11, v12

    check-cast v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;->rtt:Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    const/16 v9, 0x11

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    if-eq v11, v10, :cond_32

    iget v8, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;->depth:I

    const/4 v9, 0x1

    if-lt v8, v9, :cond_30

    iget-object v11, v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;->supertypes:[Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    const/4 v9, 0x1

    aget-object v11, v11, v9

    if-eq v11, v10, :cond_32

    :cond_30
    goto/16 :goto_41

    .line 3
    :cond_32
    move-object/from16 v12, v17

    if-eqz v12, :cond_46

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;->i1:I

    move/from16 v18, v8

    .line 4
    move/from16 v8, v18

    return v8

    .line 5
    :goto_3f
    const/4 v8, -0x1

    return v8

    .line 6
    :goto_41
    move-object/from16 v19, v17

    goto :goto_3f

    const/4 v8, 0x0

    return v8

    .line 3
    :cond_46
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

    const-string v1, "covariant_readnull_only_definitionsnull_only_branchnull_only_payloadcast_branchessibling_castsmake_leftmake_rightjoinloop_variablepass_throughmulti_valuestail_referencesexception_payloadglobals_and_tablescovariant_arrayconversionsi31_paths"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->strings(Ljava/lang/String;)V

    const-string v1, "\u000e\u0015\u0010\u0011\r\r\t\n\u0004\r\u000c\u000c\u000f\u0011\u0012\u000f\u000b\t"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->stringLengths(Ljava/lang/String;)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->endStrings()V

    const/4 v1, 0x3

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

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->structComposite(I)V

    const/4 v1, 0x0

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->recursiveHeapType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    const/4 v1, 0x2

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->structComposite(I)V

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->recursiveHeapType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    const/4 v1, 0x3

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    const/4 v1, 0x2

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->structComposite(I)V

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x0

    const/4 v2, 0x3

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x0

    const-string v2, "e99d84c39f357f9238d1e7821f7fb616"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->structComposite(I)V

    const/4 v1, 0x0

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x1

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x1

    const-string v2, "7bd594b7c9e9880a314713d25bbb6d41"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x3

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->definedHeapType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    const/4 v1, 0x2

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->structComposite(I)V

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x2

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x2

    const-string v2, "a193b3b89af49f73d18c53d2d345f8e1"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x3

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->definedHeapType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x0

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->structComposite(I)V

    const/4 v1, 0x0

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x3

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x3

    const-string v2, "a892348105fed1d3044a6b7ee13bfee6"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x5

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->definedHeapType(I)V

    const/4 v1, 0x4

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->definedHeapType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x0

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->structComposite(I)V

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x4

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x4

    const-string v2, "08a4e2423d4067e409985f908e39096e"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/16 v1, 0xb

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->abstractHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->structComposite(I)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x5

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x5

    const-string v2, "81c84c3e82fd52b1509124c62f75e32a"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->definedHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->structComposite(I)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x6

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x6

    const-string v2, "8402150f1b479980c8a184c03a5f907e"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/16 v1, 0xb

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->abstractHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->arrayComposite()V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x7

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x7

    const-string v2, "f223571bf8f31d6921474ede74e927d1"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->definedHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x0

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->arrayComposite()V

    const/4 v1, 0x0

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/16 v1, 0x8

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/16 v1, 0x8

    const-string v2, "cd33bddc19f6eaf06ca41a712d41ffa1"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/16 v1, 0xa

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->definedHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->definedHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x0

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->arrayComposite()V

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/16 v1, 0x9

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/16 v1, 0x9

    const-string v2, "95fc3eaf2c1001312cfb9d22eb131c19"

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

    const/16 v1, 0xa

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/16 v1, 0xa

    const-string v2, "0cb30b1bf33839d21a02419f300cc916"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->definedHeapType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/16 v1, 0xb

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/16 v1, 0xb

    const-string v2, "97995d87deefe1cfe778a4d9feada03b"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->definedHeapType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x2

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->definedHeapType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    const/4 v2, 0x3

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/16 v1, 0xc

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/16 v1, 0xc

    const-string v2, "275b4be3a379a2c5d6e369160351f1b8"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->definedHeapType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x2

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/16 v1, 0xd

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/16 v1, 0xd

    const-string v2, "c044a93a2482b418030da5d00f912f1c"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/16 v1, 0xb

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->abstractHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/16 v1, 0xe

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/16 v1, 0xe

    const-string v2, "26990466a88195ebe6e193675b8fe703"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/16 v1, 0xb

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->abstractHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/16 v1, 0xf

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/16 v1, 0xf

    const-string v2, "ae0d1b0fa70d14d42ae3b44f248ee5ff"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/16 v1, 0xb

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->abstractHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x1

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/16 v1, 0x10

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/16 v1, 0x10

    const-string v2, "641b371643e362c23d53401683743907"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/16 v1, 0x11

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/16 v1, 0x11

    const-string v2, "5e754b92492ccd4fac81f1d5b5fff84b"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x6

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->abstractHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/16 v1, 0x12

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/16 v1, 0x12

    const-string v2, "3defb022ef03326b63f826ae899d4583"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x6

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->abstractHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x1

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/16 v1, 0x13

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/16 v1, 0x13

    const-string v2, "f50d4884afd2d0c89cef6e96d7ce9238"

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

    const/16 v1, 0x14

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/16 v1, 0x14

    const-string v2, "0ef75fe501975ab105bb8f78dea59994"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->definedHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x2

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->definedHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->definedHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x2

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/16 v1, 0x15

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/16 v1, 0x15

    const-string v2, "ab1b6a6d5dc60037cce78efa61f6881d"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->definedHeapType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

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

    const/16 v1, 0x16

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/16 v1, 0x16

    const-string v2, "1de9dca6685874400c6675db700a5412"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->definedHeapType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    const/4 v2, 0x2

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/16 v1, 0x17

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/16 v1, 0x17

    const-string v2, "143137cd10863cf53d9f42f2b673003b"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const-string v1, "\r\u0011\u0012\u0013\u0013\u0014\u0013\u0013\u0015\u0015\u0013\u0015\u0014\u0014\u0016\u0013\u0017\u0013\u000e\u0013\u0018\u0018\u0013\u0013\u0013\u0013\u0013\u0016"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functions(Ljava/lang/String;)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->typeIndexHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    const-wide/16 v1, 0x4

    const/4 v3, 0x0

    const-wide/16 v4, 0x0

    invoke-virtual/range {v0 .. v5}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->limits(JZJ)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->tableType(Z)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->typeIndexHeapType(I)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->refNull()V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->constantExpression(I)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->table()V

    const/16 v1, 0xb

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->abstractHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    const-wide/16 v1, 0x2

    const/4 v3, 0x0

    const-wide/16 v4, 0x0

    invoke-virtual/range {v0 .. v5}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->limits(JZJ)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->tableType(Z)V

    const/16 v1, 0xb

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->abstractHeapType(I)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->refNull()V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->constantExpression(I)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->table()V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->abstractHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    const-wide/16 v1, 0x1

    const/4 v3, 0x0

    const-wide/16 v4, 0x0

    invoke-virtual/range {v0 .. v5}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->limits(JZJ)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->tableType(Z)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->abstractHeapType(I)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->refNull()V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->constantExpression(I)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->table()V

    const/16 v1, 0xf

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->tag(I)V

    const/16 v1, 0x10

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->tag(I)V

    const/16 v1, 0xb

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->abstractHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->globalType(Z)V

    const/16 v1, 0xb

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->abstractHeapType(I)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->refNull()V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->constantExpression(I)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->global()V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->typeIndexHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->globalType(Z)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->typeIndexHeapType(I)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->refNull()V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->constantExpression(I)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->global()V

    const-string v1, "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->exportKinds(Ljava/lang/String;)V

    const-string v1, "\u0003\u0004\u0006\u0007\n\u000b\u000c\r\u000e\u000f\u0011\u0013\u0016\u0017\u0018\u0019\u001a\u001b"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->exportIndices(Ljava/lang/String;)V

    const-string v1, "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\u0008\t\n\u000b\u000c\r\u000e\u000f\u0010\u0011"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->exportNames(Ljava/lang/String;)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->abstractHeapType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->i32Const(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->constantExpression(I)V

    const-string v1, "\u0000"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->refFuncConstantExpressions(Ljava/lang/String;)V

    const/4 v1, 0x1

    const/4 v2, 0x2

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
    .registers 13

    :try_start_0
    move-object v2, p0

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p4

    packed-switch v4, :pswitch_data_3a

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_f

    :goto_f
    :pswitch_f
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    goto/16 :goto_2d

    :pswitch_15
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    goto/16 :goto_2d

    :pswitch_1b
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    goto/16 :goto_2d

    :pswitch_21
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f12([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    goto/16 :goto_2d

    :pswitch_27
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f13([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    goto/16 :goto_2d

    :goto_2d
    const/4 v3, 0x0

    aput-object v0, p3, v3
    :try_end_30
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_30} :catch_31
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_30} :catch_35

    return-void

    :catch_31
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_35
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    nop

    :pswitch_data_3a
    .packed-switch 0x0
        :pswitch_f
        :pswitch_15
        :pswitch_1b
        :pswitch_21
        :pswitch_27
    .end packed-switch
.end method

.method public typed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;
    .registers 5

    iget v1, p0, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v0, p0, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object p0, v0

    packed-switch v1, :pswitch_data_26

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_d

    :goto_d
    :pswitch_d
    invoke-static/range {p0 .. p2}, Lchasm/dex/WasmModule$Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    return-object v0

    :pswitch_12
    invoke-static/range {p0 .. p2}, Lchasm/dex/WasmModule$Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    return-object v0

    :pswitch_17
    invoke-static/range {p0 .. p2}, Lchasm/dex/WasmModule$Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    return-object v0

    :pswitch_1c
    invoke-static/range {p0 .. p2}, Lchasm/dex/WasmModule$Code0;->f12([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    return-object v0

    :pswitch_21
    invoke-static/range {p0 .. p2}, Lchasm/dex/WasmModule$Code0;->f13([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    return-object v0

    :pswitch_data_26
    .packed-switch 0x0
        :pswitch_d
        :pswitch_12
        :pswitch_17
        :pswitch_1c
        :pswitch_21
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

    aget-object v8, p3, v3

    packed-switch v4, :pswitch_data_38

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_12

    :goto_12
    :pswitch_12
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v2

    goto/16 :goto_2a

    :pswitch_18
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v2

    goto/16 :goto_2a

    :pswitch_1e
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v2

    goto/16 :goto_2a

    :pswitch_24
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f11([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v2

    goto/16 :goto_2a

    :goto_2a
    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
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
        :pswitch_12
        :pswitch_18
        :pswitch_1e
        :pswitch_24
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
    .registers 13

    :try_start_0
    move-object v2, p0

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p4

    packed-switch v4, :pswitch_data_6a

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_f

    :goto_f
    :pswitch_f
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_15
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_1b
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_21
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_27
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f10([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_2d
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f15([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_33
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f17([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_39
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f19([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_3f
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f22([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_45
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f23([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_4b
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f24([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_51
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f25([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_57
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f26([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :goto_5d
    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_61
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_61} :catch_62
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_61} :catch_66

    return-void

    :catch_62
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_66
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_6a
    .packed-switch 0x0
        :pswitch_f
        :pswitch_15
        :pswitch_1b
        :pswitch_21
        :pswitch_27
        :pswitch_2d
        :pswitch_33
        :pswitch_39
        :pswitch_3f
        :pswitch_45
        :pswitch_4b
        :pswitch_51
        :pswitch_57
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

    packed-switch v4, :pswitch_data_2e

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_14

    :goto_14
    :pswitch_14
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f14([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_20

    :pswitch_1a
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f27([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_20

    :goto_20
    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_24
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_24} :catch_25
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_24} :catch_29

    return-void

    :catch_25
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_29
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    nop

    :pswitch_data_2e
    .packed-switch 0x0
        :pswitch_14
        :pswitch_1a
    .end packed-switch
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
    .registers 15

    :try_start_0
    move-object v2, p0

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p4

    const/4 v3, 0x0

    aget-object v8, p3, v3

    const/4 v3, 0x1

    aget-object v9, p3, v3

    invoke-static/range {v5 .. v9}, Lchasm/dex/WasmModule$Code0;->f16([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    const/4 v3, 0x0

    aput-object v0, p3, v3
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

.class public final Lchasm/dex/WasmModule$Entry5;
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

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p4

    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f18([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)[J

    move-result-object v2

    if-eqz v2, :cond_1f

    const/4 v3, 0x3

    invoke-static {v6, v3}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->resultReferences(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)[Ljava/lang/Object;

    move-result-object v4

    const/4 v3, 0x0

    aget-object v7, v4, v3

    aput-object v7, p3, v3

    const/4 v3, 0x1

    aget-object v7, v4, v3

    aput-object v7, p3, v3

    const/4 v3, 0x2

    aget-wide v0, v2, v3

    aput-wide v0, p2, v3
    :try_end_1f
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_1f} :catch_20
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_1f} :catch_24

    :cond_1f
    return-void

    :catch_20
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_24
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void
.end method

.class public final Lchasm/dex/WasmModule$Entry6;
.super Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;


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

    aget-object v8, p3, v3

    const/4 v3, 0x1

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v9, v2

    packed-switch v4, :pswitch_data_30

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_17

    :goto_17
    :pswitch_17
    invoke-static/range {v5 .. v9}, Lchasm/dex/WasmModule$Code0;->f20([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;I)I

    move-result v2

    goto/16 :goto_23

    :pswitch_1d
    invoke-static/range {v5 .. v9}, Lchasm/dex/WasmModule$Code0;->f21([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;I)I

    move-result v2

    goto/16 :goto_23

    :goto_23
    int-to-long v0, v2

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
        :pswitch_17
        :pswitch_1d
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

    const/16 v2, 0x14

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->function(I)Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    move-result-object v4

    const/16 v2, 0x8

    aput-object v4, p1, v2

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->referenceGlobal(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0xa

    aput-object v4, p1, v2

    const/4 v2, 0x1

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->referenceGlobal(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0xb

    aput-object v4, p1, v2

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->referenceTable(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0xc

    aput-object v4, p1, v2

    const/4 v2, 0x1

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->referenceTable(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0xd

    aput-object v4, p1, v2

    const/4 v2, 0x2

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->referenceTable(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0xe

    aput-object v4, p1, v2

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->rtt(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x10

    aput-object v4, p1, v2

    const/4 v2, 0x1

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->rtt(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x11

    aput-object v4, p1, v2

    const/4 v2, 0x2

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->rtt(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x12

    aput-object v4, p1, v2

    const/4 v2, 0x4

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->rtt(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x14

    aput-object v4, p1, v2

    const/4 v2, 0x6

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->rtt(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x16

    aput-object v4, p1, v2

    const/4 v2, 0x7

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->rtt(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x17

    aput-object v4, p1, v2

    const/16 v2, 0x8

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->rtt(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x18

    aput-object v4, p1, v2

    const/16 v2, 0x9

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->rtt(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x19

    aput-object v4, p1, v2

    const/16 v2, 0xb

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->rtt(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x1b

    aput-object v4, p1, v2

    const/16 v2, 0xc

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->rtt(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x1c

    aput-object v4, p1, v2

    return-void
.end method

.method static bindValues0(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V
    .registers 8

    const/4 v2, 0x0

    aget-object v5, p1, v2

    check-cast v5, [J

    const/16 v2, 0xd

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->runtimeType(I)I

    move-result v3

    int-to-long v0, v3

    const/16 v2, 0xd

    aput-wide v0, v5, v2

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->tagAddress(I)I

    move-result v3

    int-to-long v0, v3

    const/16 v2, 0x1a

    aput-wide v0, v5, v2

    const/4 v2, 0x1

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->tagAddress(I)I

    move-result v3

    int-to-long v0, v3

    const/16 v2, 0x1b

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

    new-instance v4, Lchasm/dex/WasmModule$Entry0;

    const/4 v3, 0x2

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry0;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x5

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/4 v3, 0x2

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x6

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/4 v3, 0x3

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x7

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry1;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry1;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x8

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry1;

    const/4 v3, 0x2

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry1;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x9

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/4 v3, 0x4

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xa

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry1;

    const/4 v3, 0x3

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry1;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xb

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry0;

    const/4 v3, 0x3

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry0;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xc

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry0;

    const/4 v3, 0x4

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry0;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xd

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry3;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry3;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xe

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/4 v3, 0x5

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xf

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry4;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry4;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x10

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/4 v3, 0x6

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x11

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry5;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry5;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x12

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/4 v3, 0x7

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x13

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry6;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry6;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x14

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry6;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry6;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x15

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/16 v3, 0x8

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x16

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/16 v3, 0x9

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x17

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/16 v3, 0xa

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x18

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/16 v3, 0xb

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x19

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/16 v3, 0xc

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x1a

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry3;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry3;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x1b

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    return-void
.end method

.class public interface abstract Lchasm/dex/WasmModule$Sig0;
.super Ljava/lang/Object;


# virtual methods
.method public abstract typed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;
.end method

.class final Lchasm/dex/WasmModule$Structs0;
.super Ljava/lang/Object;


# direct methods
.method static newStruct(ILio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;
    .registers 5

    packed-switch p0, :pswitch_data_44

    :pswitch_3
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    const/4 v0, 0x0

    return-object v0

    :pswitch_8
    new-instance v0, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;

    invoke-direct {v0, p1}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    return-object v0

    :pswitch_e
    new-instance v0, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v0, p1}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    return-object v0

    :pswitch_14
    new-instance v0, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;

    invoke-direct {v0, p1}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    return-object v0

    :pswitch_1a
    new-instance v0, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;

    invoke-direct {v0, p1}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    return-object v0

    :pswitch_20
    new-instance v0, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;

    invoke-direct {v0, p1}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I2_R1__I_IR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    return-object v0

    :pswitch_26
    new-instance v0, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    invoke-direct {v0, p1}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    return-object v0

    :pswitch_2c
    new-instance v0, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    invoke-direct {v0, p1}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    return-object v0

    :pswitch_32
    new-instance v0, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    invoke-direct {v0, p1}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    return-object v0

    :pswitch_38
    new-instance v0, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;

    invoke-direct {v0, p1}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I0_R1__R;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    return-object v0

    :pswitch_3e
    new-instance v0, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R2__I_RR;

    invoke-direct {v0, p1}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R2__I_RR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    return-object v0

    :pswitch_data_44
    .packed-switch 0x0
        :pswitch_8
        :pswitch_e
        :pswitch_14
        :pswitch_1a
        :pswitch_20
        :pswitch_26
        :pswitch_2c
        :pswitch_32
        :pswitch_38
        :pswitch_3
        :pswitch_3
        :pswitch_3
        :pswitch_3e
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

    const-string v1, "chasm.dex.WasmModule$Entry5"

    aput-object v1, p0, v0

    const/16 v0, 0x8

    const-string v1, "chasm.dex.WasmModule$Entry6"

    aput-object v1, p0, v0

    const/16 v0, 0x9

    const-string v1, "chasm.dex.WasmModule$InstallEntries0"

    aput-object v1, p0, v0

    const/16 v0, 0xa

    const-string v1, "chasm.dex.WasmModule$Install"

    aput-object v1, p0, v0

    const/16 v0, 0xb

    const-string v1, "chasm.dex.WasmModule$Declare0"

    aput-object v1, p0, v0

    const/16 v0, 0xc

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

    const/16 v0, 0xd

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

    const/16 v0, 0x1d

    new-array v1, v0, [Ljava/lang/Object;

    const/16 v0, 0x1c

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

    const/16 v0, 0x2d

    new-array v1, v0, [J

    fill-array-data v1, :array_8

    return-object v1

    :array_8
    .array-data 8
        0x0
        0x10
        0x1
        0x0
        0x1
        0x18
        0x2
        0x0
        0x2
        0x2
        0x18
        0x3
        0x0
        0x2
        0x4
        0x3
        0x10
        0x1
        0x0
        0x4
        0x18
        0x2
        0x0
        0x2
        0x5
        0x10
        0x1
        0x0
        0x6
        0x10
        0x1
        0x0
        0x7
        0x10
        0x1
        0x0
        0x8
        0x10
        0x1
        0x0
        0xc
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
