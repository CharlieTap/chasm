.class public final Lchasm/jit/Code0;
.super Ljava/lang/Object;
.source "module.wasm"


# direct methods
.method public static f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;
    .registers 27

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v17, v15

    move/from16 v18, v15

    move/from16 v19, v15

    const/4 v8, 0x0

    move-object/from16 v20, v8

    .line 2
    const/16 v18, 0x3

    .line 3
    const/16 v19, 0x4

    .line 4
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_24

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_24
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v18

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v19

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

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

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v27, v12

    move-object/from16 v28, v16

    move-object/from16 v29, v14

    move/from16 v30, v13

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

    const/4 v8, 0x0

    move-object/from16 v26, v8

    .line 2
    const/16 v18, 0x1

    .line 3
    const/16 v20, 0x14

    .line 4
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_47

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_47
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v18

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v20

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move-object/from16 v19, v12

    .line 5
    move-object/from16 v31, v19

    invoke-static/range {v28 .. v31}, Lchasm/jit/Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

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
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_2;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_2;-><init>()V

    move/from16 v8, v20

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move-wide/from16 v2, v23

    iput-wide v2, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_2;->f1:D

    move-object/from16 v21, v12

    .line 9
    move-object/from16 v31, v21

    invoke-static/range {v28 .. v31}, Lchasm/jit/Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v20

    .line 10
    add-int v18, v18, v20

    .line 11
    const/16 v20, 0x1

    .line 12
    const/16 v22, 0x12c

    .line 13
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_97

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_97
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v20

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v22

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move-object/from16 v21, v12

    .line 14
    move-object/from16 v31, v21

    invoke-static/range {v28 .. v31}, Lchasm/jit/Code0;->f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v20

    .line 15
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_ba

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_ba
    const/16 v8, 0x9

    shl-int/lit8 v8, v8, 0x1

    ushr-int/lit8 v8, v8, 0x1

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;

    invoke-direct {v12, v8}, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;-><init>(I)V

    move-object/from16 v25, v12

    .line 16
    move-object/from16 v31, v25

    invoke-static/range {v28 .. v31}, Lchasm/jit/Code0;->f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

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

    if-eqz v12, :cond_1b

    instance-of v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_2;

    if-nez v8, :cond_1e

    :cond_1b
    const/4 v11, 0x0

    goto/16 :goto_1f

    :cond_1e
    const/4 v11, 0x1

    :goto_1f
    move/from16 v18, v11

    .line 4
    if-eqz v18, :cond_27

    .line 5
    const/16 v18, -0x1

    .line 6
    goto/16 :goto_53

    .line 7
    :cond_27
    move-object/from16 v12, v17

    if-eqz v12, :cond_2f

    instance-of v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    if-nez v8, :cond_34

    :cond_2f
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->castFailed()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    :cond_34
    if-eqz v12, :cond_58

    check-cast v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iget v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move/from16 v18, v8

    .line 8
    move-object/from16 v12, v17

    if-eqz v12, :cond_44

    instance-of v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;

    if-nez v8, :cond_49

    :cond_44
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->castFailed()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    :cond_49
    if-eqz v12, :cond_5d

    check-cast v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;

    iget v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v20, v8

    .line 9
    add-int v18, v18, v20

    .line 10
    :goto_53
    move/from16 v8, v18

    return v8

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_58
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 8
    :cond_5d
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f12([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;
    .registers 27

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v17, v15

    move/from16 v18, v15

    move/from16 v19, v15

    const/4 v8, 0x0

    move-object/from16 v20, v8

    .line 2
    const/16 v18, 0xb

    .line 3
    const/16 v19, 0x16

    .line 4
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_24

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_24
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v18

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v19

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move-object/from16 v17, v12

    .line 5
    move-object/from16 v12, v17

    return-object v12

    const/4 v12, 0x0

    return-object v12
.end method

.method public static f13([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;
    .registers 28

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v17, v15

    move/from16 v18, v15

    move-wide/from16 v19, v0

    const/4 v8, 0x0

    move-object/from16 v21, v8

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

    if-gez v0, :cond_24

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_24
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_2;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_2;-><init>()V

    move/from16 v8, v18

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move-wide/from16 v2, v19

    iput-wide v2, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_2;->f1:D

    move-object/from16 v17, v12

    .line 5
    move-object/from16 v12, v17

    return-object v12

    const/4 v12, 0x0

    return-object v12
.end method

.method public static f14([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 33

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

    move-object/from16 v20, v15

    move/from16 v21, v15

    move-wide/from16 v22, v0

    const/4 v8, 0x0

    move-object/from16 v24, v8

    .line 2
    if-eqz v17, :cond_44

    .line 3
    const/16 v19, 0x28

    .line 4
    const/16 v21, 0x0

    .line 5
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_33

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_33
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v19

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v21

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move-object/from16 v20, v12

    .line 6
    goto/16 :goto_65

    .line 7
    :cond_44
    const/16 v19, 0x32

    .line 8
    const-wide/16 v22, 0x0

    .line 9
    const/16 v1, 0x18

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_56

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_56
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_2;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_2;-><init>()V

    move/from16 v8, v19

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move-wide/from16 v2, v22

    iput-wide v2, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_2;->f1:D

    move-object/from16 v20, v12

    .line 10
    :goto_65
    move-object/from16 v18, v20

    .line 11
    move/from16 v8, v17

    const/4 v9, 0x2

    if-ne v8, v9, :cond_78

    .line 12
    const/16 v9, 0x56

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceGlobal;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceGlobal;->value:Ljava/lang/Object;

    move-object/from16 v18, v10

    .line 13
    goto/16 :goto_78

    .line 14
    :cond_78
    :goto_78
    move-object/from16 v20, v18

    .line 15
    move-object/from16 v12, v20

    if-eqz v12, :cond_81

    const/4 v11, 0x0

    goto/16 :goto_82

    :cond_81
    const/4 v11, 0x1

    :goto_82
    move/from16 v19, v11

    .line 16
    if-eqz v19, :cond_8a

    .line 17
    const/16 v19, 0x0

    .line 18
    goto/16 :goto_94

    .line 19
    :cond_8a
    move-object/from16 v12, v18

    if-eqz v12, :cond_99

    check-cast v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;

    iget v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v19, v8

    .line 20
    :goto_94
    move/from16 v8, v19

    return v8

    const/4 v8, 0x0

    return v8

    .line 19
    :cond_99
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f15([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
    .registers 31

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

    const/4 v8, 0x0

    move-object/from16 v24, v8

    .line 3
    :cond_1a
    move/from16 v20, v18

    move-object/from16 v23, v17

    .line 4
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_2c

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_2c
    new-instance v12, Lchasm/gc/S0cb30b1bf33839d21a02419f300cc916_0;

    invoke-direct {v12}, Lchasm/gc/S0cb30b1bf33839d21a02419f300cc916_0;-><init>()V

    move/from16 v8, v20

    iput v8, v12, Lchasm/gc/S0cb30b1bf33839d21a02419f300cc916_0;->f0:I

    move-object/from16 v10, v23

    check-cast v10, Lchasm/gc/S0cb30b1bf33839d21a02419f300cc916_0;

    iput-object v10, v12, Lchasm/gc/S0cb30b1bf33839d21a02419f300cc916_0;->f1:Lchasm/gc/S0cb30b1bf33839d21a02419f300cc916_0;

    move-object/from16 v17, v12

    .line 5
    add-int/lit8 v18, v18, 0x1

    .line 6
    const/high16 v10, -0x80000000

    add-int v8, v18, v10

    const v9, -0x7ffffff6

    if-lt v8, v9, :cond_1a

    .line 8
    :goto_48
    move-object/from16 v21, v17

    .line 9
    move-object/from16 v12, v21

    if-eqz v12, :cond_51

    const/4 v11, 0x0

    goto/16 :goto_52

    :cond_51
    const/4 v11, 0x1

    :goto_52
    move/from16 v20, v11

    .line 10
    if-nez v20, :cond_6d

    .line 11
    move-object/from16 v12, v17

    if-eqz v12, :cond_72

    check-cast v12, Lchasm/gc/S0cb30b1bf33839d21a02419f300cc916_0;

    iget v8, v12, Lchasm/gc/S0cb30b1bf33839d21a02419f300cc916_0;->f0:I

    move/from16 v22, v8

    .line 12
    add-int v19, v19, v22

    .line 13
    move-object/from16 v12, v17

    if-eqz v12, :cond_77

    check-cast v12, Lchasm/gc/S0cb30b1bf33839d21a02419f300cc916_0;

    iget-object v10, v12, Lchasm/gc/S0cb30b1bf33839d21a02419f300cc916_0;->f1:Lchasm/gc/S0cb30b1bf33839d21a02419f300cc916_0;

    move-object/from16 v17, v10

    .line 14
    goto :goto_48

    .line 15
    :cond_6d
    move/from16 v8, v19

    return v8

    const/4 v8, 0x0

    return v8

    .line 11
    :cond_72
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 13
    :cond_77
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

    move-object/from16 v23, v16

    move-object/from16 v24, v14

    move/from16 v25, v13

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move/from16 v18, v15

    move-object/from16 v19, v15

    move/from16 v20, v15

    move-object/from16 v21, v15

    const/4 v8, 0x0

    move-object/from16 v22, v8

    .line 2
    const/16 v18, 0x0

    .line 3
    const/16 v20, 0xd

    .line 4
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_3a

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_3a
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v18

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v20

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move-object/from16 v19, v12

    .line 5
    const/4 v12, 0x0

    move-object/from16 v21, v12

    .line 6
    move-object/from16 v26, v19

    move-object/from16 v27, v21

    invoke-static/range {v23 .. v27}, Lchasm/jit/Code0;->f16([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v19

    .line 7
    move-object/from16 v12, v19

    if-eqz v12, :cond_63

    check-cast v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iget v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move/from16 v17, v8

    .line 8
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_63
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f18([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)[J
    .registers 31

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v24, v12

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move-object/from16 v18, v15

    move/from16 v19, v15

    move-object/from16 v20, v15

    move-wide/from16 v21, v0

    const/4 v8, 0x0

    move-object/from16 v23, v8

    .line 2
    const/16 v17, 0x1

    .line 3
    const/16 v19, 0x2

    .line 4
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_2f

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_2f
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v17

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v19

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

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

    if-gez v0, :cond_50

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_50
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_2;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_2;-><init>()V

    move/from16 v8, v19

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move-wide/from16 v2, v21

    iput-wide v2, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_2;->f1:D

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
    invoke-static/range {v25 .. v27}, Lchasm/jit/Code0;->f18([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)[J

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

    check-cast v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iget v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move/from16 v22, v8

    .line 5
    move-object/from16 v12, v19

    if-eqz v12, :cond_6c

    check-cast v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;

    iget v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

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
    .registers 36

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v24, v12

    move-object/from16 v18, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-object/from16 v19, v15

    move/from16 v20, v15

    move-object/from16 v21, v15

    move/from16 v22, v15

    const/4 v8, 0x0

    move-object/from16 v23, v8

    .line 2
    const/16 v20, 0x1

    .line 3
    const/16 v22, 0x2

    .line 4
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_2f

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_2f
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v20

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v22

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move-object/from16 v21, v12

    .line 5
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_4c

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_4c
    new-instance v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    invoke-direct {v12}, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;-><init>()V

    move-object/from16 v10, v21

    check-cast v10, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iput-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    move-object/from16 v19, v12

    .line 6
    move-object/from16 v12, v19

    if-eqz v12, :cond_7e

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    move-object/from16 v10, v18

    check-cast v10, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iput-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    .line 7
    move-object/from16 v12, v19

    if-eqz v12, :cond_83

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    iget-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    move-object/from16 v21, v10

    .line 8
    move-object/from16 v12, v21

    if-eqz v12, :cond_76

    const/4 v11, 0x0

    goto/16 :goto_77

    :cond_76
    const/4 v11, 0x1

    :goto_77
    move/from16 v17, v11

    .line 9
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 6
    :cond_7e
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_83
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f20([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;I)I
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

    move-object/from16 v17, p3

    move/from16 v18, p4

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v19, v15

    move-object/from16 v20, v15

    move/from16 v21, v15

    move/from16 v22, v15

    move/from16 v23, v15

    const/4 v8, 0x0

    move-object/from16 v28, v8

    .line 2
    :goto_26
    if-nez v18, :cond_34

    .line 3
    move-object/from16 v12, v17

    if-eqz v12, :cond_7e

    check-cast v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iget v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move/from16 v19, v8

    .line 4
    goto/16 :goto_79

    .line 5
    :cond_34
    move-object/from16 v12, v17

    if-eqz v12, :cond_83

    check-cast v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iget v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move/from16 v21, v8

    .line 6
    add-int/lit8 v21, v21, 0x1

    .line 7
    const/16 v22, 0x0

    .line 8
    move/from16 v23, v21

    .line 9
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_52

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_52
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v22

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v23

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

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

    goto :goto_26

    .line 12
    :goto_79
    move/from16 v8, v19

    return v8

    const/4 v8, 0x0

    return v8

    .line 3
    :cond_7e
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 5
    :cond_83
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

    move-object/from16 v21, v16

    move-object/from16 v22, v14

    move/from16 v23, v13

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move-object/from16 v18, v15

    move/from16 v19, v15

    const/4 v8, 0x0

    move-object/from16 v20, v8

    .line 2
    const/16 v17, 0x0

    .line 3
    const/16 v19, 0x5

    .line 4
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_36

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_36
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v17

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v19

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move-object/from16 v18, v12

    .line 5
    move-object/from16 v24, v18

    const/16 v25, 0x3e8

    invoke-static/range {v21 .. v25}, Lchasm/jit/Code0;->f21([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;I)I

    move-result v9

    iget-object v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->pendingTailFunction:Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    if-eqz v8, :cond_60

    const/4 v8, 0x1

    new-array v10, v8, [J

    const/4 v11, 0x0

    invoke-static {v14, v10, v11, v13}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->completeTailObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[J[Ljava/lang/Object;I)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_67

    const/4 v8, 0x0

    aget-wide v0, v10, v8

    long-to-int v9, v0

    :cond_60
    move/from16 v17, v9

    .line 6
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    :cond_67
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const/4 v8, 0x0

    return v8
.end method

.method public static f23([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
    .registers 33

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v26, v12

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move/from16 v18, v15

    move-object/from16 v19, v15

    move/from16 v22, v15

    move-wide/from16 v20, v0

    move-object/from16 v23, v15

    move/from16 v24, v15

    const/4 v8, 0x0

    move-object/from16 v25, v8

    .line 2
    const/16 v18, 0x0

    .line 3
    const/16 v22, 0x3c

    .line 4
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_33

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_33
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v18

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v22

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

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

    move-object/from16 v12, v26

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

    goto/16 :goto_71

    .line 8
    :goto_71
    move-object/from16 v12, v19

    if-eqz v12, :cond_80

    check-cast v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iget v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move/from16 v17, v8

    .line 9
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 8
    :cond_80
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

    const/16 v9, 0x57

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceGlobal;

    move-object/from16 v24, v12

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

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
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_4b

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_4b
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v18

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v20

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move-object/from16 v19, v12

    .line 5
    move-object/from16 v12, v24

    move-object/from16 v10, v19

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceGlobal;->value:Ljava/lang/Object;

    .line 6
    const/16 v20, 0x8

    .line 7
    const/16 v22, 0x0

    .line 8
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_72

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_72
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v20

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v22

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move-object/from16 v21, v12

    .line 9
    const/16 v9, 0x58

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;

    const/4 v8, 0x2

    move-object/from16 v10, v21

    invoke-static {v12, v8, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedTables;->tableSet(Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;ILjava/lang/Object;)V

    .line 10
    move-object/from16 v12, v24

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceGlobal;->value:Ljava/lang/Object;

    move-object/from16 v19, v10

    .line 11
    move-object/from16 v12, v19

    if-eqz v12, :cond_143

    check-cast v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iget v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move/from16 v18, v8

    .line 12
    const/16 v9, 0x58

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;

    const/4 v8, 0x2

    invoke-static {v12, v8}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedTables;->tableGet(Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;I)Ljava/lang/Object;

    move-result-object v10

    move-object/from16 v21, v10

    .line 13
    move-object/from16 v12, v21

    if-eqz v12, :cond_148

    check-cast v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;

    iget v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v20, v8

    .line 14
    add-int v18, v18, v20

    .line 15
    move-object/from16 v12, v25

    const/16 v9, 0xd

    aget-wide v4, v12, v9

    long-to-int v10, v4

    const/16 v9, 0x5a

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;

    const/4 v8, 0x0

    invoke-static {v14, v2, v10, v8}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedTables;->indirectEntry(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;II)Ljava/lang/Object;

    move-result-object v12

    instance-of v11, v12, Lchasm/jit/Sig0;

    if-eqz v11, :cond_f5

    move-object/from16 v26, v12

    move/from16 v28, v13

    invoke-interface/range {v26 .. v28}, Lchasm/jit/Sig0;->typed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v12

    move-object/from16 v26, v16

    move-object/from16 v27, v14

    move/from16 v28, v13

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_13e

    iget-object v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->pendingTailFunction:Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    if-eqz v8, :cond_f1

    const/4 v8, 0x1

    new-array v10, v8, [J

    new-array v11, v8, [Ljava/lang/Object;

    invoke-static {v14, v10, v11, v13}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->completeTailObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[J[Ljava/lang/Object;I)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_13e

    const/4 v8, 0x0

    aget-object v12, v11, v8

    :cond_f1
    move-object/from16 v21, v12

    goto/16 :goto_12d

    :cond_f5
    const/4 v8, 0x1

    new-array v10, v8, [J

    new-array v11, v8, [Ljava/lang/Object;

    move-object/from16 v27, v14

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;

    move-object/from16 v28, v12

    const/16 v9, 0x5a

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;

    move-object/from16 v29, v12

    move-object/from16 v12, v25

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

    if-nez v8, :cond_13e

    const/4 v9, 0x0

    aget-object v12, v11, v9

    move-object/from16 v21, v12

    .line 16
    :goto_12d
    move-object/from16 v12, v21

    if-eqz v12, :cond_14d

    check-cast v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;

    iget v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v20, v8

    .line 17
    add-int v17, v18, v20

    .line 18
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    :cond_13e
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const/4 v8, 0x0

    return v8

    .line 11
    :cond_143
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 13
    :cond_148
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 16
    :cond_14d
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f25([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
    .registers 33

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v24, v12

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move-object/from16 v18, v15

    move/from16 v19, v15

    move-object/from16 v20, v15

    move/from16 v21, v15

    move-object/from16 v22, v15

    const/4 v8, 0x0

    move-object/from16 v23, v8

    .line 2
    const/16 v19, 0x0

    .line 3
    const/16 v21, 0x50

    .line 4
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_31

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_31
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v19

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v21

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

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

    if-gez v0, :cond_51

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_51
    const/4 v9, 0x2

    new-array v10, v9, [Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;

    const/4 v11, 0x0

    move-object/from16 v12, v20

    aput-object v12, v10, v11

    const/4 v11, 0x1

    move-object/from16 v12, v22

    aput-object v12, v10, v11

    new-instance v12, Lchasm/gc/A95fc3eaf2c1001312cfb9d22eb131c19_0;

    invoke-direct {v12, v10}, Lchasm/gc/A95fc3eaf2c1001312cfb9d22eb131c19_0;-><init>([Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;)V

    move-object/from16 v18, v12

    .line 7
    move-object/from16 v12, v18

    if-eqz v12, :cond_a4

    check-cast v12, Lchasm/gc/Acd33bddc19f6eaf06ca41a712d41ffa1_0;

    iget-object v10, v12, Lchasm/gc/Acd33bddc19f6eaf06ca41a712d41ffa1_0;->data:[Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;

    const/4 v9, 0x0

    array-length v8, v10

    if-ltz v9, :cond_a9

    if-ge v9, v8, :cond_ae

    aget-object v12, v10, v9

    move-object/from16 v20, v12

    .line 8
    move-object/from16 v12, v20

    if-eqz v12, :cond_b3

    check-cast v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iget v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move/from16 v19, v8

    .line 9
    move-object/from16 v12, v18

    if-eqz v12, :cond_b8

    check-cast v12, Lchasm/gc/Acd33bddc19f6eaf06ca41a712d41ffa1_0;

    iget-object v10, v12, Lchasm/gc/Acd33bddc19f6eaf06ca41a712d41ffa1_0;->data:[Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;

    const/4 v9, 0x1

    array-length v8, v10

    if-ltz v9, :cond_bd

    if-ge v9, v8, :cond_c2

    aget-object v12, v10, v9

    move-object/from16 v22, v12

    .line 10
    move-object/from16 v12, v22

    if-eqz v12, :cond_9a

    const/4 v11, 0x0

    goto/16 :goto_9b

    :cond_9a
    const/4 v11, 0x1

    :goto_9b
    move/from16 v21, v11

    .line 11
    add-int v17, v19, v21

    .line 12
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_a4
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_a9
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_ae
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 8
    :cond_b3
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_b8
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_bd
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_c2
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f26([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
    .registers 28

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move/from16 v18, v15

    move-object/from16 v19, v15

    move/from16 v20, v15

    const/4 v8, 0x0

    move-object/from16 v21, v8

    .line 2
    const/16 v18, 0x0

    .line 3
    const/16 v20, 0x5a

    .line 4
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_26

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_26
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v18

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v20

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move-object/from16 v19, v12

    .line 7
    move-object/from16 v12, v19

    if-eqz v12, :cond_3d

    instance-of v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    if-nez v8, :cond_42

    :cond_3d
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->castFailed()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    :cond_42
    if-eqz v12, :cond_4f

    check-cast v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iget v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move/from16 v17, v8

    .line 8
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_4f
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f27([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 32

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

    const/4 v8, 0x0

    move-object/from16 v24, v8

    .line 2
    if-gez v17, :cond_3b

    .line 3
    const/16 v19, 0x0

    .line 4
    const/16 v21, 0x0

    .line 5
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_2a

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_2a
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v19

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v21

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move-object/from16 v20, v12

    .line 6
    goto/16 :goto_56

    .line 7
    :cond_3b
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_49

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_49
    move/from16 v8, v17

    shl-int/lit8 v8, v8, 0x1

    ushr-int/lit8 v8, v8, 0x1

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;

    invoke-direct {v12, v8}, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;-><init>(I)V

    move-object/from16 v20, v12

    .line 8
    :goto_56
    move-object/from16 v18, v20

    .line 9
    move-object/from16 v12, v18

    if-eqz v12, :cond_60

    instance-of v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;

    if-nez v8, :cond_73

    .line 10
    :cond_60
    const/4 v8, -0x1

    return v8

    .line 11
    :goto_62
    move-object/from16 v12, v20

    if-eqz v12, :cond_78

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;

    iget v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmI31;->value:I

    shl-int/lit8 v8, v8, 0x1

    shr-int/lit8 v8, v8, 0x1

    move/from16 v17, v8

    .line 12
    move/from16 v8, v17

    return v8

    .line 13
    :cond_73
    move-object/from16 v20, v18

    goto :goto_62

    const/4 v8, 0x0

    return v8

    .line 11
    :cond_78
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
    .registers 30

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v23, v12

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move-object/from16 v18, v15

    move/from16 v19, v15

    move-object/from16 v20, v15

    move/from16 v21, v15

    const/4 v8, 0x0

    move-object/from16 v22, v8

    .line 2
    const/16 v19, 0x7

    .line 3
    const/16 v21, 0x9

    .line 4
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_2f

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_2f
    new-instance v12, Lchasm/gc/Sa193b3b89af49f73d18c53d2d345f8e1_0;

    invoke-direct {v12}, Lchasm/gc/Sa193b3b89af49f73d18c53d2d345f8e1_0;-><init>()V

    move/from16 v8, v19

    iput v8, v12, Lchasm/gc/S7bd594b7c9e9880a314713d25bbb6d41_0;->f0:I

    move/from16 v8, v21

    iput v8, v12, Lchasm/gc/Sa193b3b89af49f73d18c53d2d345f8e1_0;->f1:I

    move-object/from16 v20, v12

    .line 5
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_4c

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_4c
    new-instance v12, Lchasm/gc/S08a4e2423d4067e409985f908e39096e_0;

    invoke-direct {v12}, Lchasm/gc/S08a4e2423d4067e409985f908e39096e_0;-><init>()V

    move-object/from16 v10, v20

    check-cast v10, Lchasm/gc/S7bd594b7c9e9880a314713d25bbb6d41_0;

    iput-object v10, v12, Lchasm/gc/Sa892348105fed1d3044a6b7ee13bfee6_0;->f0:Lchasm/gc/S7bd594b7c9e9880a314713d25bbb6d41_0;

    move-object/from16 v18, v12

    .line 6
    move-object/from16 v12, v18

    if-eqz v12, :cond_80

    check-cast v12, Lchasm/gc/Sa892348105fed1d3044a6b7ee13bfee6_0;

    iget-object v12, v12, Lchasm/gc/Sa892348105fed1d3044a6b7ee13bfee6_0;->f0:Lchasm/gc/S7bd594b7c9e9880a314713d25bbb6d41_0;

    if-eqz v12, :cond_85

    check-cast v12, Lchasm/gc/Sa193b3b89af49f73d18c53d2d345f8e1_0;

    iget v8, v12, Lchasm/gc/Sa193b3b89af49f73d18c53d2d345f8e1_0;->f1:I

    move/from16 v19, v8

    .line 7
    move-object/from16 v12, v18

    if-eqz v12, :cond_8a

    check-cast v12, Lchasm/gc/Sa892348105fed1d3044a6b7ee13bfee6_0;

    iget-object v12, v12, Lchasm/gc/Sa892348105fed1d3044a6b7ee13bfee6_0;->f0:Lchasm/gc/S7bd594b7c9e9880a314713d25bbb6d41_0;

    if-eqz v12, :cond_8f

    check-cast v12, Lchasm/gc/S7bd594b7c9e9880a314713d25bbb6d41_0;

    iget v8, v12, Lchasm/gc/S7bd594b7c9e9880a314713d25bbb6d41_0;->f0:I

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

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v25, v12

    move-object/from16 v26, v16

    move-object/from16 v27, v14

    move/from16 v28, v13

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move-object/from16 v18, v15

    move/from16 v19, v15

    move-object/from16 v20, v15

    move/from16 v21, v15

    move-object/from16 v22, v15

    move/from16 v23, v15

    const/4 v8, 0x0

    move-object/from16 v24, v8

    .line 2
    const/16 v19, 0x1

    .line 3
    const/16 v21, 0x2

    .line 4
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_45

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_45
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v19

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v21

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

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
    new-instance v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    invoke-direct {v12}, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;-><init>()V

    move-object/from16 v10, v20

    check-cast v10, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iput-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

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

    if-gez v0, :cond_80

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_80
    new-instance v12, Lchasm/gc/S81c84c3e82fd52b1509124c62f75e32a_0;

    invoke-direct {v12}, Lchasm/gc/S81c84c3e82fd52b1509124c62f75e32a_0;-><init>()V

    move-object/from16 v10, v22

    iput-object v10, v12, Lchasm/gc/S81c84c3e82fd52b1509124c62f75e32a_0;->f0:Ljava/lang/Object;

    move-object/from16 v22, v12

    .line 8
    move-object/from16 v12, v22

    if-eqz v12, :cond_2ac

    check-cast v12, Lchasm/gc/S81c84c3e82fd52b1509124c62f75e32a_0;

    iget-object v10, v12, Lchasm/gc/S81c84c3e82fd52b1509124c62f75e32a_0;->f0:Ljava/lang/Object;

    move-object/from16 v22, v10

    .line 9
    move-object/from16 v12, v18

    if-eqz v12, :cond_2b1

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    move-object/from16 v10, v22

    check-cast v10, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iput-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    .line 10
    move-object/from16 v12, v18

    if-eqz v12, :cond_2b6

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    iget-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    move-object/from16 v22, v10

    .line 11
    move-object/from16 v12, v22

    if-eqz v12, :cond_b2

    const/4 v11, 0x0

    goto/16 :goto_b3

    :cond_b2
    const/4 v11, 0x1

    :goto_b3
    move/from16 v21, v11

    .line 12
    add-int v17, v17, v21

    .line 13
    const/16 v21, 0x1

    .line 14
    const/16 v23, 0x2

    .line 15
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_c9

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_c9
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v21

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v23

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move-object/from16 v22, v12

    .line 16
    move-object/from16 v12, v18

    if-eqz v12, :cond_2bb

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    move-object/from16 v10, v22

    check-cast v10, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iput-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    .line 17
    const/16 v9, 0x56

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceGlobal;

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceGlobal;->value:Ljava/lang/Object;

    move-object/from16 v22, v10

    .line 18
    move-object/from16 v12, v18

    if-eqz v12, :cond_2c0

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    move-object/from16 v10, v22

    check-cast v10, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iput-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    .line 19
    move-object/from16 v12, v18

    if-eqz v12, :cond_2c5

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    iget-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    move-object/from16 v22, v10

    .line 20
    move-object/from16 v12, v22

    if-eqz v12, :cond_10b

    const/4 v11, 0x0

    goto/16 :goto_10c

    :cond_10b
    const/4 v11, 0x1

    :goto_10c
    move/from16 v21, v11

    .line 21
    add-int v17, v17, v21

    .line 22
    const/16 v21, 0x1

    .line 23
    const/16 v23, 0x2

    .line 24
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_122

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_122
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v21

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v23

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move-object/from16 v22, v12

    .line 25
    move-object/from16 v12, v18

    if-eqz v12, :cond_2ca

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    move-object/from16 v10, v22

    check-cast v10, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iput-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    .line 26
    invoke-static/range {v26 .. v28}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v22

    .line 27
    move-object/from16 v12, v18

    if-eqz v12, :cond_2cf

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    move-object/from16 v10, v22

    check-cast v10, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iput-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    .line 28
    move-object/from16 v12, v18

    if-eqz v12, :cond_2d4

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    iget-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    move-object/from16 v22, v10

    .line 29
    move-object/from16 v12, v22

    if-eqz v12, :cond_15e

    const/4 v11, 0x0

    goto/16 :goto_15f

    :cond_15e
    const/4 v11, 0x1

    :goto_15f
    move/from16 v21, v11

    .line 30
    add-int v17, v17, v21

    .line 31
    const/16 v21, 0x1

    .line 32
    const/16 v23, 0x2

    .line 33
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_175

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_175
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v21

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v23

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move-object/from16 v22, v12

    .line 34
    move-object/from16 v12, v18

    if-eqz v12, :cond_2d9

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    move-object/from16 v10, v22

    check-cast v10, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iput-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    .line 35
    const/16 v9, 0x59

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;

    const/4 v8, 0x1

    invoke-static {v12, v8}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedTables;->tableGet(Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;I)Ljava/lang/Object;

    move-result-object v10

    move-object/from16 v22, v10

    .line 36
    move-object/from16 v12, v18

    if-eqz v12, :cond_2de

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    move-object/from16 v10, v22

    check-cast v10, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iput-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    .line 37
    move-object/from16 v12, v18

    if-eqz v12, :cond_2e3

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    iget-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    move-object/from16 v22, v10

    .line 38
    move-object/from16 v12, v22

    if-eqz v12, :cond_1ba

    const/4 v11, 0x0

    goto/16 :goto_1bb

    :cond_1ba
    const/4 v11, 0x1

    :goto_1bb
    move/from16 v21, v11

    .line 39
    add-int v17, v17, v21

    .line 40
    const/16 v21, 0x1

    .line 41
    const/16 v23, 0x2

    .line 42
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_1d1

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_1d1
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v21

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v23

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move-object/from16 v22, v12

    .line 43
    move-object/from16 v12, v18

    if-eqz v12, :cond_2e8

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    move-object/from16 v10, v22

    check-cast v10, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iput-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    .line 44
    const/4 v9, 0x2

    if-ltz v9, :cond_2ed

    const v1, 0x1ffffff7

    if-gt v9, v1, :cond_2ed

    shl-int/lit8 v1, v9, 0x2

    add-int/lit16 v1, v1, 0x20

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_204

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_204
    new-array v10, v9, [Ljava/lang/Object;

    new-instance v12, Lchasm/gc/Af223571bf8f31d6921474ede74e927d1_0;

    invoke-direct {v12, v10}, Lchasm/gc/Af223571bf8f31d6921474ede74e927d1_0;-><init>([Ljava/lang/Object;)V

    move-object/from16 v22, v12

    .line 45
    move-object/from16 v12, v22

    if-eqz v12, :cond_2f2

    check-cast v12, Lchasm/gc/Af223571bf8f31d6921474ede74e927d1_0;

    iget-object v10, v12, Lchasm/gc/Af223571bf8f31d6921474ede74e927d1_0;->data:[Ljava/lang/Object;

    const/4 v9, 0x1

    array-length v8, v10

    if-ltz v9, :cond_2f7

    if-ge v9, v8, :cond_2fc

    aget-object v12, v10, v9

    move-object/from16 v22, v12

    .line 46
    move-object/from16 v12, v18

    if-eqz v12, :cond_301

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    move-object/from16 v10, v22

    check-cast v10, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iput-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    .line 47
    move-object/from16 v12, v18

    if-eqz v12, :cond_306

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    iget-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    move-object/from16 v22, v10

    .line 48
    move-object/from16 v12, v22

    if-eqz v12, :cond_23c

    const/4 v11, 0x0

    goto/16 :goto_23d

    :cond_23c
    const/4 v11, 0x1

    :goto_23d
    move/from16 v21, v11

    .line 49
    add-int v17, v17, v21

    .line 50
    const/16 v21, 0x1

    .line 51
    const/16 v23, 0x2

    .line 52
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_253

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_253
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v21

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v23

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move-object/from16 v22, v12

    .line 53
    move-object/from16 v12, v18

    if-eqz v12, :cond_30b

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    move-object/from16 v10, v22

    check-cast v10, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iput-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    .line 54
    const/4 v12, 0x0

    move-object/from16 v22, v12

    .line 55
    move-object/from16 v12, v22

    if-eqz v12, :cond_27a

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->castFailed()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 56
    :cond_27a
    move-object/from16 v12, v18

    if-eqz v12, :cond_310

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    move-object/from16 v10, v22

    check-cast v10, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iput-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    .line 57
    move-object/from16 v12, v18

    if-eqz v12, :cond_315

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    iget-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    move-object/from16 v22, v10

    .line 58
    move-object/from16 v12, v22

    if-eqz v12, :cond_297

    const/4 v11, 0x0

    goto/16 :goto_298

    :cond_297
    const/4 v11, 0x1

    :goto_298
    move/from16 v21, v11

    .line 59
    add-int v17, v17, v21

    .line 60
    const/4 v12, 0x0

    move-object/from16 v22, v12

    .line 61
    move-object/from16 v29, v22

    invoke-static/range {v26 .. v29}, Lchasm/jit/Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v21

    .line 62
    add-int v17, v17, v21

    .line 63
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 8
    :cond_2ac
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_2b1
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 10
    :cond_2b6
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 16
    :cond_2bb
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 18
    :cond_2c0
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 19
    :cond_2c5
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 25
    :cond_2ca
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 27
    :cond_2cf
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 28
    :cond_2d4
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 34
    :cond_2d9
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 36
    :cond_2de
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 37
    :cond_2e3
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 43
    :cond_2e8
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 44
    :cond_2ed
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 45
    :cond_2f2
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 45
    :cond_2f7
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 45
    :cond_2fc
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->arrayOutOfBounds()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 46
    :cond_301
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 47
    :cond_306
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 53
    :cond_30b
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 56
    :cond_310
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 57
    :cond_315
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

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v27, v12

    move-object/from16 v28, v16

    move-object/from16 v29, v14

    move/from16 v30, v13

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move-object/from16 v18, v15

    move-object/from16 v19, v15

    move/from16 v20, v15

    move-object/from16 v21, v15

    move/from16 v22, v15

    move-object/from16 v23, v15

    const/4 v8, 0x0

    move-object/from16 v26, v8

    .line 2
    invoke-static/range {v28 .. v30}, Lchasm/jit/Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v19

    .line 3
    const/16 v20, 0x1

    .line 4
    const/16 v22, 0x2

    .line 5
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_49

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_49
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v20

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v22

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move-object/from16 v21, v12

    .line 6
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_66

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_66
    new-instance v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    invoke-direct {v12}, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;-><init>()V

    move-object/from16 v10, v21

    check-cast v10, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iput-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    move-object/from16 v18, v12

    .line 7
    move-object/from16 v21, v18

    .line 8
    move-object/from16 v12, v19

    if-eqz v12, :cond_9e

    .line 9
    const/4 v8, -0x1

    return v8

    .line 10
    :goto_7b
    move-object/from16 v12, v21

    if-eqz v12, :cond_a3

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    move-object/from16 v10, v23

    check-cast v10, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iput-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    .line 11
    move-object/from16 v12, v18

    if-eqz v12, :cond_a8

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    iget-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    move-object/from16 v21, v10

    .line 12
    move-object/from16 v12, v21

    if-eqz v12, :cond_98

    const/4 v11, 0x0

    goto/16 :goto_99

    :cond_98
    const/4 v11, 0x1

    :goto_99
    move/from16 v17, v11

    .line 13
    move/from16 v8, v17

    return v8

    .line 14
    :cond_9e
    move-object/from16 v23, v19

    goto :goto_7b

    const/4 v8, 0x0

    return v8

    .line 10
    :cond_a3
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 11
    :cond_a8
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I
    .registers 35

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v24, v12

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v17, v15

    move-object/from16 v18, v15

    move/from16 v19, v15

    move-object/from16 v20, v15

    move/from16 v21, v15

    move-object/from16 v22, v15

    const/4 v8, 0x0

    move-object/from16 v23, v8

    .line 2
    const/16 v19, 0x1

    .line 3
    const/16 v21, 0x2

    .line 4
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_31

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_31
    new-instance v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    invoke-direct {v12}, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;-><init>()V

    move/from16 v8, v19

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v8, v21

    iput v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move-object/from16 v20, v12

    .line 5
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_4e

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_4e
    new-instance v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    invoke-direct {v12}, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;-><init>()V

    move-object/from16 v10, v20

    check-cast v10, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iput-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

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

    move-object/from16 v12, v24

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

    goto/16 :goto_7f

    .line 9
    :goto_7f
    move-object/from16 v12, v20

    if-eqz v12, :cond_a4

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    move-object/from16 v10, v22

    check-cast v10, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iput-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    .line 10
    move-object/from16 v12, v18

    if-eqz v12, :cond_a9

    check-cast v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;

    iget-object v10, v12, Lchasm/gc/S8402150f1b479980c8a184c03a5f907e_0;->f0:Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    move-object/from16 v20, v10

    .line 11
    move-object/from16 v12, v20

    if-eqz v12, :cond_9c

    const/4 v11, 0x0

    goto/16 :goto_9d

    :cond_9c
    const/4 v11, 0x1

    :goto_9d
    move/from16 v17, v11

    .line 12
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    .line 9
    :cond_a4
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 10
    :cond_a9
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

    if-eqz v12, :cond_17

    instance-of v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    if-nez v8, :cond_3d

    .line 3
    :cond_17
    move-object/from16 v12, v18

    if-eqz v12, :cond_1f

    instance-of v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_2;

    if-nez v8, :cond_40

    .line 4
    :cond_1f
    const/4 v8, -0x1

    return v8

    .line 5
    :goto_21
    move-object/from16 v12, v20

    if-eqz v12, :cond_45

    check-cast v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;

    iget v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_0;->f0:I

    move/from16 v19, v8

    .line 6
    add-int/lit8 v19, v19, 0x64

    .line 7
    move/from16 v8, v19

    return v8

    .line 8
    :goto_30
    move-object/from16 v12, v20

    if-eqz v12, :cond_4a

    check-cast v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iget v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move/from16 v17, v8

    .line 9
    move/from16 v8, v17

    return v8

    .line 10
    :cond_3d
    move-object/from16 v20, v18

    goto :goto_30

    .line 11
    :cond_40
    move-object/from16 v20, v18

    goto :goto_21

    const/4 v8, 0x0

    return v8

    .line 5
    :cond_45
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->nullReference()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 8
    :cond_4a
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

    if-eqz v12, :cond_17

    instance-of v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    if-nez v8, :cond_19

    :cond_17
    goto/16 :goto_28

    .line 3
    :cond_19
    move-object/from16 v12, v17

    if-eqz v12, :cond_2d

    check-cast v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;

    iget v8, v12, Lchasm/gc/Se99d84c39f357f9238d1e7821f7fb616_1;->f1:I

    move/from16 v18, v8

    .line 4
    move/from16 v8, v18

    return v8

    .line 5
    :goto_26
    const/4 v8, -0x1

    return v8

    .line 6
    :goto_28
    move-object/from16 v19, v17

    goto :goto_26

    const/4 v8, 0x0

    return v8

    .line 3
    :cond_2d
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
    .registers 12

    :try_start_0
    move-object v2, p0

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p3

    packed-switch v4, :pswitch_data_40

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_f

    :goto_f
    :pswitch_f
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    goto/16 :goto_2d

    :pswitch_15
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    goto/16 :goto_2d

    :pswitch_1b
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    goto/16 :goto_2d

    :pswitch_21
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f12([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    goto/16 :goto_2d

    :pswitch_27
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f13([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    goto/16 :goto_2d

    :goto_2d
    move-object v2, p1

    const/4 v3, 0x0

    invoke-static {v2, v0, v3}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedValues;->referenceToHost(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Ljava/lang/Object;Z)J

    move-result-wide v0

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_36
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_36} :catch_37
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_36} :catch_3b

    return-void

    :catch_37
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_3b
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    nop

    :pswitch_data_40
    .packed-switch 0x0
        :pswitch_f
        :pswitch_15
        :pswitch_1b
        :pswitch_21
        :pswitch_27
    .end packed-switch
.end method

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
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    goto/16 :goto_2d

    :pswitch_15
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    goto/16 :goto_2d

    :pswitch_1b
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    goto/16 :goto_2d

    :pswitch_21
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f12([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    goto/16 :goto_2d

    :pswitch_27
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f13([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

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

.method public invoke(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 12

    :try_start_0
    move-object v2, p0

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    invoke-static {v6}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->depth(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v7

    packed-switch v4, :pswitch_data_46

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_12

    :goto_12
    :pswitch_12
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    goto/16 :goto_30

    :pswitch_18
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    goto/16 :goto_30

    :pswitch_1e
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    goto/16 :goto_30

    :pswitch_24
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f12([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    goto/16 :goto_30

    :pswitch_2a
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f13([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    goto/16 :goto_30

    :goto_30
    move-object v2, p1

    const/4 v3, 0x0

    invoke-static {v2, v0, v3}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedValues;->referenceToHost(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Ljava/lang/Object;Z)J

    move-result-wide v0

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
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
        :pswitch_12
        :pswitch_18
        :pswitch_1e
        :pswitch_24
        :pswitch_2a
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
    invoke-static/range {p0 .. p2}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    return-object v0

    :pswitch_12
    invoke-static/range {p0 .. p2}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    return-object v0

    :pswitch_17
    invoke-static/range {p0 .. p2}, Lchasm/jit/Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    return-object v0

    :pswitch_1c
    invoke-static/range {p0 .. p2}, Lchasm/jit/Code0;->f12([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

    move-result-object v0

    return-object v0

    :pswitch_21
    invoke-static/range {p0 .. p2}, Lchasm/jit/Code0;->f13([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;

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

    move-object v2, p1

    invoke-static {v2, v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedValues;->referenceFromHost(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)Ljava/lang/Object;

    move-result-object v8

    packed-switch v4, :pswitch_data_3c

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_17

    :goto_17
    :pswitch_17
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v2

    goto/16 :goto_2f

    :pswitch_1d
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v2

    goto/16 :goto_2f

    :pswitch_23
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v2

    goto/16 :goto_2f

    :pswitch_29
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f11([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v2

    goto/16 :goto_2f

    :goto_2f
    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_33
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_33} :catch_34
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_33} :catch_38

    return-void

    :catch_34
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_38
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_3c
    .packed-switch 0x0
        :pswitch_17
        :pswitch_1d
        :pswitch_23
        :pswitch_29
    .end packed-switch
.end method

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
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v2

    goto/16 :goto_2a

    :pswitch_18
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v2

    goto/16 :goto_2a

    :pswitch_1e
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v2

    goto/16 :goto_2a

    :pswitch_24
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f11([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

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

    move-object v2, p1

    invoke-static {v2, v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedValues;->referenceFromHost(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)Ljava/lang/Object;

    move-result-object v8

    packed-switch v4, :pswitch_data_44

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_1b

    :goto_1b
    :pswitch_1b
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v2

    goto/16 :goto_33

    :pswitch_21
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v2

    goto/16 :goto_33

    :pswitch_27
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v2

    goto/16 :goto_33

    :pswitch_2d
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f11([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;)I

    move-result v2

    goto/16 :goto_33

    :goto_33
    int-to-long v0, v2

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_3a
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_3a} :catch_3b
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_3a} :catch_3f

    return-void

    :catch_3b
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_3f
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    nop

    :pswitch_data_44
    .packed-switch 0x0
        :pswitch_1b
        :pswitch_21
        :pswitch_27
        :pswitch_2d
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
    .registers 12

    :try_start_0
    move-object v2, p0

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p3

    packed-switch v4, :pswitch_data_6a

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_f

    :goto_f
    :pswitch_f
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_15
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_1b
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_21
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_27
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f10([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_2d
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f15([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_33
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f17([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_39
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f19([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_3f
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f22([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_45
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f23([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_4b
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f24([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_51
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f25([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_5d

    :pswitch_57
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f26([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

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

.method public invoke(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 12

    :try_start_0
    move-object v2, p0

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    invoke-static {v6}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->depth(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v7

    packed-switch v4, :pswitch_data_70

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_12

    :goto_12
    :pswitch_12
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_60

    :pswitch_18
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_60

    :pswitch_1e
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_60

    :pswitch_24
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_60

    :pswitch_2a
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f10([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_60

    :pswitch_30
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f15([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_60

    :pswitch_36
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f17([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_60

    :pswitch_3c
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f19([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_60

    :pswitch_42
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f22([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_60

    :pswitch_48
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f23([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_60

    :pswitch_4e
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f24([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_60

    :pswitch_54
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f25([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_60

    :pswitch_5a
    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f26([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    goto/16 :goto_60

    :goto_60
    int-to-long v0, v2

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_67
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_67} :catch_68
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_67} :catch_6c

    return-void

    :catch_68
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_6c
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_70
    .packed-switch 0x0
        :pswitch_12
        :pswitch_18
        :pswitch_1e
        :pswitch_24
        :pswitch_2a
        :pswitch_30
        :pswitch_36
        :pswitch_3c
        :pswitch_42
        :pswitch_48
        :pswitch_4e
        :pswitch_54
        :pswitch_5a
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

    packed-switch v4, :pswitch_data_2e

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_14

    :goto_14
    :pswitch_14
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f14([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_20

    :pswitch_1a
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f27([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

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

    packed-switch v4, :pswitch_data_34

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_18

    :goto_18
    :pswitch_18
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f14([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_24

    :pswitch_1e
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f27([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_24

    :goto_24
    int-to-long v0, v2

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_2b
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_2b} :catch_2c
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_2b} :catch_30

    return-void

    :catch_2c
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_30
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_34
    .packed-switch 0x0
        :pswitch_18
        :pswitch_1e
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
    .registers 14

    :try_start_0
    move-object v2, p0

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p3

    const/4 v3, 0x0

    aget-wide v0, p2, v3

    move-object v2, p1

    invoke-static {v2, v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedValues;->referenceFromHost(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)Ljava/lang/Object;

    move-result-object v8

    const/4 v3, 0x1

    aget-wide v0, p2, v3

    move-object v2, p1

    invoke-static {v2, v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedValues;->referenceFromHost(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)Ljava/lang/Object;

    move-result-object v9

    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f16([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    move-object v2, p1

    const/4 v3, 0x0

    invoke-static {v2, v0, v3}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedValues;->referenceToHost(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Ljava/lang/Object;Z)J

    move-result-wide v0

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_22
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_22} :catch_23
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_22} :catch_27

    return-void

    :catch_23
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_27
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void
.end method

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

    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f16([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

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

.method public invoke(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 14

    :try_start_0
    move-object v2, p0

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    invoke-static {v6}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->depth(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v7

    add-int/lit16 v3, p3, 0x0

    aget-wide v0, p2, v3

    move-object v2, p1

    invoke-static {v2, v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedValues;->referenceFromHost(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)Ljava/lang/Object;

    move-result-object v8

    add-int/lit16 v3, p3, 0x1

    aget-wide v0, p2, v3

    move-object v2, p1

    invoke-static {v2, v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedValues;->referenceFromHost(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)Ljava/lang/Object;

    move-result-object v9

    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f16([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    move-object v2, p1

    const/4 v3, 0x0

    invoke-static {v2, v0, v3}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedValues;->referenceToHost(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Ljava/lang/Object;Z)J

    move-result-wide v0

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
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
.end method

.class public final Lchasm/jit/Entry5;
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

    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f18([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)[J

    move-result-object v2

    if-eqz v2, :cond_29

    const/4 v3, 0x3

    invoke-static {v6, v3}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->resultReferences(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)[Ljava/lang/Object;

    move-result-object v4

    const/4 v3, 0x0

    aget-object v7, v4, v3

    const/4 p0, 0x0

    invoke-static {v6, v7, p0}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedValues;->referenceToHost(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Ljava/lang/Object;Z)J

    move-result-wide v0

    aput-wide v0, p2, v3

    const/4 v3, 0x1

    aget-object v7, v4, v3

    const/4 p0, 0x0

    invoke-static {v6, v7, p0}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedValues;->referenceToHost(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Ljava/lang/Object;Z)J

    move-result-wide v0

    aput-wide v0, p2, v3

    const/4 v3, 0x2

    aget-wide v0, v2, v3

    aput-wide v0, p2, v3
    :try_end_29
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_29} :catch_2a
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_29} :catch_2e

    :cond_29
    return-void

    :catch_2a
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_2e
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void
.end method

.method public callObjects(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[J[Ljava/lang/Object;I)V
    .registers 13

    :try_start_0
    move-object v2, p0

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p4

    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f18([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)[J

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

.method public invoke(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 12

    :try_start_0
    move-object v2, p0

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    invoke-static {v6}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->depth(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v7

    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f18([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)[J

    move-result-object v2

    if-eqz v2, :cond_39

    const/4 v3, 0x3

    invoke-static {v6, v3}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->resultReferences(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)[Ljava/lang/Object;

    move-result-object v4

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    const/4 v3, 0x0

    aget-object v7, v4, v3

    const/4 p0, 0x0

    invoke-static {v6, v7, p0}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedValues;->referenceToHost(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Ljava/lang/Object;Z)J

    move-result-wide v0

    const/4 v3, 0x0

    add-int v3, p3, v3

    aput-wide v0, v5, v3

    const/4 v3, 0x1

    aget-object v7, v4, v3

    const/4 p0, 0x0

    invoke-static {v6, v7, p0}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedValues;->referenceToHost(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Ljava/lang/Object;Z)J

    move-result-wide v0

    const/4 v3, 0x1

    add-int v3, p3, v3

    aput-wide v0, v5, v3

    const/4 v3, 0x2

    aget-wide v0, v2, v3

    const/4 v3, 0x2

    add-int v3, p3, v3

    aput-wide v0, v5, v3
    :try_end_39
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_39} :catch_3a
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_39} :catch_3e

    :cond_39
    return-void

    :catch_3a
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_3e
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void
.end method

.class public final Lchasm/jit/Entry6;
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

    move-object v2, p1

    invoke-static {v2, v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedValues;->referenceFromHost(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)Ljava/lang/Object;

    move-result-object v8

    const/4 v3, 0x1

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v9, v2

    packed-switch v4, :pswitch_data_36

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_1c

    :goto_1c
    :pswitch_1c
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f20([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;I)I

    move-result v2

    goto/16 :goto_28

    :pswitch_22
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f21([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;I)I

    move-result v2

    goto/16 :goto_28

    :goto_28
    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_2c
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_2c} :catch_2d
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_2c} :catch_31

    return-void

    :catch_2d
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_31
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    nop

    :pswitch_data_36
    .packed-switch 0x0
        :pswitch_1c
        :pswitch_22
    .end packed-switch
.end method

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
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f20([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;I)I

    move-result v2

    goto/16 :goto_23

    :pswitch_1d
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f21([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;I)I

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

    move-object v2, p1

    invoke-static {v2, v0, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedValues;->referenceFromHost(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;J)Ljava/lang/Object;

    move-result-object v8

    add-int/lit16 v3, p3, 0x1

    aget-wide v0, p2, v3

    long-to-int v2, v0

    move v9, v2

    packed-switch v4, :pswitch_data_3e

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_21

    :goto_21
    :pswitch_21
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f20([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;I)I

    move-result v2

    goto/16 :goto_2d

    :pswitch_27
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f21([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;ILjava/lang/Object;I)I

    move-result v2

    goto/16 :goto_2d

    :goto_2d
    int-to-long v0, v2

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_34
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_34} :catch_35
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_34} :catch_39

    return-void

    :catch_35
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_39
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    nop

    :pswitch_data_3e
    .packed-switch 0x0
        :pswitch_21
        :pswitch_27
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

    const/16 v2, 0x14

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->function(I)Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;

    move-result-object v4

    const/16 v2, 0x8

    aput-object v4, p1, v2

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->referenceGlobal(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x56

    aput-object v4, p1, v2

    const/4 v2, 0x1

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->referenceGlobal(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x57

    aput-object v4, p1, v2

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->referenceTable(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x58

    aput-object v4, p1, v2

    const/4 v2, 0x1

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->referenceTable(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x59

    aput-object v4, p1, v2

    const/4 v2, 0x2

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->referenceTable(I)Ljava/lang/Object;

    move-result-object v4

    const/16 v2, 0x5a

    aput-object v4, p1, v2

    return-void
.end method

.method private static bindValues0(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V
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


# virtual methods
.method public accept(Ljava/lang/Object;)V
    .registers 5

    check-cast p1, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;

    const/16 v0, 0x5b

    new-array v1, v0, [Ljava/lang/Object;

    const/16 v0, 0x1c

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

    new-instance v4, Lchasm/jit/Entry0;

    const/4 v3, 0x2

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry0;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x5

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/4 v3, 0x2

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x6

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/4 v3, 0x3

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x7

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry1;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry1;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x8

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry1;

    const/4 v3, 0x2

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry1;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x9

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/4 v3, 0x4

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xa

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry1;

    const/4 v3, 0x3

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry1;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xb

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry0;

    const/4 v3, 0x3

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry0;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xc

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry0;

    const/4 v3, 0x4

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry0;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xd

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry3;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry3;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xe

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/4 v3, 0x5

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xf

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry4;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry4;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x10

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/4 v3, 0x6

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x11

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry5;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry5;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x12

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/4 v3, 0x7

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x13

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry6;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry6;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x14

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry6;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry6;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x15

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/16 v3, 0x8

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x16

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/16 v3, 0x9

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x17

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/16 v3, 0xa

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x18

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/16 v3, 0xb

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x19

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry2;

    const/16 v3, 0xc

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x1a

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry3;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry3;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x1b

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    return-void
.end method

.class public interface abstract Lchasm/jit/Sig0;
.super Ljava/lang/Object;


# virtual methods
.method public abstract typed(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)Ljava/lang/Object;
.end method
