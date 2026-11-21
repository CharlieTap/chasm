.class public final Lchasm/jit/Code0;
.super Ljava/lang/Object;
.source "module.wasm"


# direct methods
.method public static f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V
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

    const/16 v9, 0x9

    aget-wide v4, v12, v9

    long-to-int v8, v4

    invoke-static {v14, v8, v10, v11}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->throwException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J[Ljava/lang/Object;)V

    goto/16 :goto_29

    return-void

    :goto_29
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    return-void
.end method

.method public static f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
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
    if-eqz v17, :cond_2c

    .line 3
    const/16 v20, 0x7

    .line 4
    const/4 v8, 0x1

    new-array v10, v8, [J

    const/4 v11, 0x0

    const/4 v9, 0x0

    move/from16 v0, v20

    int-to-long v0, v0

    aput-wide v0, v10, v9

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/16 v9, 0x9

    aget-wide v4, v12, v9

    long-to-int v8, v4

    invoke-static {v14, v8, v10, v11}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->throwException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J[Ljava/lang/Object;)V

    goto/16 :goto_35

    .line 5
    :cond_2c
    const/4 v8, 0x1

    return v8

    .line 6
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 7
    const/4 v8, 0x0

    return v8

    :goto_35
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const/4 v8, 0x0

    return v8
.end method

.method public static f10([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 26

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

    move-object/from16 v18, v16

    move-object/from16 v19, v14

    move/from16 v20, v13

    move/from16 v17, p3

    .line 2
    move/from16 v21, v17

    invoke-static/range {v18 .. v21}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_27

    .line 3
    const/4 v8, 0x0

    return v8

    const/4 v8, 0x0

    return v8

    :cond_27
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const/4 v8, 0x0

    return v8
.end method

.method public static f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
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
    move/from16 v8, v17

    const/4 v9, 0x5

    if-le v8, v9, :cond_39

    .line 3
    move/from16 v20, v17

    .line 4
    const/4 v8, 0x1

    new-array v10, v8, [J

    const/4 v11, 0x0

    const/4 v9, 0x0

    move/from16 v0, v20

    int-to-long v0, v0

    aput-wide v0, v10, v9

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/16 v9, 0x9

    aget-wide v4, v12, v9

    long-to-int v8, v4

    invoke-static {v14, v8, v10, v11}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->createException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J[Ljava/lang/Object;)Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;

    move-result-object v12

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;->words:[J

    iget-object v11, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;->references:[Ljava/lang/Object;

    const/4 v9, 0x0

    aget-wide v0, v10, v9

    move-wide/from16 v18, v0

    goto/16 :goto_3e

    .line 5
    :cond_39
    move/from16 v20, v17

    .line 6
    move/from16 v8, v20

    return v8

    .line 7
    :goto_3e
    move-wide/from16 v0, v18

    long-to-int v8, v0

    const/16 v9, 0x3e8

    add-int v17, v8, v9

    .line 8
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)J
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

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v28, v12

    move-object/from16 v29, v16

    move-object/from16 v30, v14

    move/from16 v31, v13

    move/from16 v19, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v20, v0

    move/from16 v24, v15

    move-wide/from16 v22, v0

    move/from16 v27, v15

    move-wide/from16 v25, v0

    .line 2
    move/from16 v8, v19

    const/4 v9, 0x1

    if-ne v8, v9, :cond_56

    .line 3
    const/16 v24, 0xb

    .line 4
    const/4 v8, 0x1

    new-array v10, v8, [J

    const/4 v11, 0x0

    const/4 v9, 0x0

    move/from16 v0, v24

    int-to-long v0, v0

    aput-wide v0, v10, v9

    move-object/from16 v12, v28

    const/16 v9, 0x9

    aget-wide v4, v12, v9

    long-to-int v8, v4

    invoke-static {v14, v8, v10, v11}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->createException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J[Ljava/lang/Object;)Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;

    move-result-object v12

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;->words:[J

    iget-object v11, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;->references:[Ljava/lang/Object;

    const/4 v9, 0x0

    aget-wide v0, v10, v9

    move-wide/from16 v22, v0

    goto/16 :goto_b0

    .line 5
    :cond_56
    move/from16 v8, v19

    const/4 v9, 0x2

    if-ne v8, v9, :cond_89

    .line 6
    const-wide/16 v22, 0x16

    .line 7
    const/16 v27, 0x3

    .line 8
    const/4 v8, 0x2

    new-array v10, v8, [J

    const/4 v11, 0x0

    const/4 v9, 0x0

    move-wide/from16 v0, v22

    aput-wide v0, v10, v9

    const/4 v9, 0x1

    move/from16 v0, v27

    int-to-long v0, v0

    aput-wide v0, v10, v9

    move-object/from16 v12, v28

    const/16 v9, 0xa

    aget-wide v4, v12, v9

    long-to-int v8, v4

    invoke-static {v14, v8, v10, v11}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->createException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J[Ljava/lang/Object;)Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;

    move-result-object v12

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;->words:[J

    iget-object v11, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;->references:[Ljava/lang/Object;

    const/4 v9, 0x0

    aget-wide v0, v10, v9

    move-wide/from16 v22, v0

    const/4 v9, 0x1

    aget-wide v0, v10, v9

    move-wide/from16 v25, v0

    goto/16 :goto_b7

    .line 9
    :cond_89
    move/from16 v8, v19

    const/4 v9, 0x3

    if-ne v8, v9, :cond_99

    .line 10
    const/16 v32, 0x21

    invoke-static/range {v29 .. v32}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_c3

    .line 11
    goto/16 :goto_99

    .line 12
    :cond_99
    :goto_99
    move/from16 v8, v19

    const/4 v9, 0x4

    if-ne v8, v9, :cond_ad

    .line 13
    const/4 v8, 0x0

    const/4 v10, 0x0

    const/4 v11, 0x0

    move-object/from16 v12, v28

    const/16 v9, 0xb

    aget-wide v4, v12, v9

    long-to-int v8, v4

    invoke-static {v14, v8, v10, v11}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->throwException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J[Ljava/lang/Object;)V

    goto/16 :goto_c3

    .line 14
    :cond_ad
    const-wide/16 v0, -0x1

    return-wide v0

    .line 15
    :goto_b0
    move-wide/from16 v20, v22

    .line 16
    move-wide/from16 v22, v20

    .line 17
    move-wide/from16 v0, v22

    return-wide v0

    .line 18
    :goto_b7
    move-wide/from16 v20, v25

    .line 19
    move-wide/from16 v25, v20

    .line 20
    add-long v17, v22, v25

    .line 21
    move-wide/from16 v0, v17

    return-wide v0

    const-wide/16 v0, 0x0

    return-wide v0

    :cond_c3
    :goto_c3
    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->pendingExceptionTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v8

    move-object/from16 v12, v28

    const/16 v9, 0xa

    aget-wide v4, v12, v9

    long-to-int v9, v4

    if-ne v8, v9, :cond_e3

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->takeException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;

    move-result-object v12

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;->words:[J

    iget-object v11, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;->references:[Ljava/lang/Object;

    const/4 v9, 0x0

    aget-wide v0, v10, v9

    move-wide/from16 v22, v0

    const/4 v9, 0x1

    aget-wide v0, v10, v9

    move-wide/from16 v25, v0

    goto :goto_b7

    :cond_e3
    goto/16 :goto_e5

    :goto_e5
    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->pendingExceptionTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v8

    move-object/from16 v12, v28

    const/16 v9, 0x9

    aget-wide v4, v12, v9

    long-to-int v9, v4

    if-ne v8, v9, :cond_100

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->takeException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;

    move-result-object v12

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;->words:[J

    iget-object v11, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;->references:[Ljava/lang/Object;

    const/4 v9, 0x0

    aget-wide v0, v10, v9

    move-wide/from16 v22, v0

    goto :goto_b0

    :cond_100
    goto/16 :goto_102

    :goto_102
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const-wide/16 v0, 0x0

    return-wide v0
.end method

.method public static f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 25

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    .line 2
    if-eqz v17, :cond_c

    .line 3
    goto/16 :goto_18

    .line 4
    :cond_c
    move/from16 v8, v17

    const/4 v9, 0x2

    if-ne v8, v9, :cond_16

    .line 5
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 6
    :cond_16
    const/4 v8, 0x0

    return v8

    .line 7
    :goto_18
    const/4 v8, 0x1

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V
    .registers 28

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x3

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v18, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17

    move-object/from16 v20, v12

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v19, v15

    .line 2
    move-object/from16 v22, v14

    move-object/from16 v2, v20

    move-object/from16 v23, v2

    const/16 v24, 0x1

    invoke-static/range {v22 .. v24}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->memoryGrow(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;I)J

    move-result-wide v0

    long-to-int v15, v0

    move/from16 v19, v15

    move-object/from16 v12, v20

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v18, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17

    .line 3
    const v8, 0x10004

    add-int/lit8 v11, v17, -0x4

    if-gt v8, v11, :cond_67

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

    const/4 v10, 0x0

    const/4 v11, 0x0

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/16 v9, 0xb

    aget-wide v4, v12, v9

    long-to-int v8, v4

    invoke-static {v14, v8, v10, v11}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->throwException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J[Ljava/lang/Object;)V

    goto/16 :goto_63

    return-void

    :goto_63
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    return-void

    .line 3
    :cond_67
    move-object/from16 v12, v20

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
    .registers 29

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x3

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v18, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17

    move-object/from16 v22, v12

    add-int/lit16 v8, v13, 0x10

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_20

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    :cond_20
    move v13, v8

    move-object/from16 v23, v16

    move-object/from16 v24, v14

    move/from16 v25, v13

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v19, v15

    move/from16 v20, v15

    move/from16 v21, v15

    .line 2
    invoke-static/range {v23 .. v25}, Lchasm/jit/Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_7f

    move-object/from16 v12, v22

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v18, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17

    .line 3
    const/4 v8, -0x1

    return v8

    .line 4
    :goto_43
    move-object/from16 v2, v22

    move-object/from16 v24, v2

    invoke-static/range {v24 .. v24}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->memorySize(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;)J

    move-result-wide v0

    move-object/from16 v24, v14

    move/from16 v25, v13

    long-to-int v15, v0

    move/from16 v20, v15

    .line 5
    const v8, 0x10004

    add-int/lit8 v11, v17, -0x4

    if-gt v8, v11, :cond_a5

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

    move/from16 v21, v9

    .line 6
    add-int v19, v20, v21

    .line 7
    move/from16 v8, v19

    return v8

    const/4 v8, 0x0

    return v8

    :cond_7f
    move-object/from16 v12, v22

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v18, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->pendingExceptionTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v8

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/16 v9, 0xb

    aget-wide v4, v12, v9

    long-to-int v9, v4

    if-ne v8, v9, :cond_9e

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->takeException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;

    move-result-object v12

    goto :goto_43

    :cond_9e
    goto/16 :goto_a0

    :goto_a0
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const/4 v8, 0x0

    return v8

    .line 5
    :cond_a5
    move-object/from16 v12, v22

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
    .registers 34

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    move-object/from16 v26, v12

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v18, v15

    move-object/from16 v19, v15

    move/from16 v22, v15

    move-wide/from16 v20, v0

    move-object/from16 v23, v15

    move-object/from16 v24, v15

    const/4 v8, 0x0

    move-object/from16 v25, v8

    .line 2
    move/from16 v22, v17

    .line 3
    const/4 v8, 0x1

    new-array v10, v8, [J

    const/4 v11, 0x0

    const/4 v9, 0x0

    move/from16 v0, v22

    int-to-long v0, v0

    aput-wide v0, v10, v9

    move-object/from16 v12, v26

    const/16 v9, 0x9

    aget-wide v4, v12, v9

    long-to-int v8, v4

    invoke-static {v14, v8, v10, v11}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->createException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I[J[Ljava/lang/Object;)Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;

    move-result-object v12

    move-object/from16 v24, v12

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;->words:[J

    iget-object v11, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;->references:[Ljava/lang/Object;

    const/4 v9, 0x0

    aget-wide v0, v10, v9

    move-wide/from16 v20, v0

    goto/16 :goto_45

    .line 4
    :goto_45
    move-object/from16 v19, v24

    .line 6
    :goto_47
    const/high16 v10, -0x80000000

    add-int v8, v18, v10

    const v9, -0x7ffff830

    if-ge v8, v9, :cond_6e

    .line 7
    move/from16 v22, v18

    .line 8
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_60

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_60
    new-instance v12, Lchasm/gc/S7cd61ed60e3e32738fd5976a89584478_0;

    invoke-direct {v12}, Lchasm/gc/S7cd61ed60e3e32738fd5976a89584478_0;-><init>()V

    move/from16 v8, v22

    iput v8, v12, Lchasm/gc/S7cd61ed60e3e32738fd5976a89584478_0;->f0:I

    move-object/from16 v23, v12

    .line 9
    add-int/lit8 v18, v18, 0x1

    .line 10
    goto :goto_47

    .line 11
    :cond_6e
    move-object/from16 v23, v19

    .line 12
    move-object/from16 v12, v23

    invoke-static {v14, v12}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->rethrowException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Ljava/lang/Object;)V

    goto/16 :goto_79

    const/4 v8, 0x0

    return v8

    :goto_79
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const/4 v8, 0x0

    return v8
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

    move-object/from16 v21, v16

    move-object/from16 v22, v14

    move/from16 v23, v13

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v20, v15

    move-wide/from16 v18, v0

    .line 2
    move/from16 v24, v17

    invoke-static/range {v21 .. v24}, Lchasm/jit/Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v9

    iget-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    if-nez v8, :cond_3a

    move/from16 v20, v9

    .line 3
    move/from16 v8, v20

    return v8

    .line 4
    :goto_30
    move-wide/from16 v0, v18

    long-to-int v8, v0

    add-int/lit8 v17, v8, 0x5

    .line 5
    move/from16 v8, v17

    return v8

    const/4 v8, 0x0

    return v8

    :cond_3a
    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->pendingExceptionTag(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)I

    move-result v8

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

    const/16 v9, 0x9

    aget-wide v4, v12, v9

    long-to-int v9, v4

    if-ne v8, v9, :cond_58

    invoke-static {v14}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->takeException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;

    move-result-object v12

    iget-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;->words:[J

    iget-object v11, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;->references:[Ljava/lang/Object;

    const/4 v9, 0x0

    aget-wide v0, v10, v9

    move-wide/from16 v18, v0

    goto :goto_30

    :cond_58
    goto/16 :goto_5a

    :goto_5a
    const/4 v8, 0x1

    iput-boolean v8, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledUnwinding:Z

    const/4 v8, 0x0

    return v8
.end method

.method public static f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V
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

    invoke-static {v14, v12}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->rethrowException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Ljava/lang/Object;)V

    goto/16 :goto_16

    return-void

    :goto_16
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
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_d} :catch_12

    return-void

    :catch_e
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_12
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

    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V
    :try_end_11
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_11} :catch_12
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_11} :catch_16

    return-void

    :catch_12
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_16
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

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

    packed-switch v4, :pswitch_data_46

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

    packed-switch v4, :pswitch_data_4c

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
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_43} :catch_48

    return-void

    :catch_44
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_48
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_4c
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
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_11} :catch_16

    return-void

    :catch_12
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_16
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

    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)J

    move-result-wide v0

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
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

    packed-switch v4, :pswitch_data_22

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
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_19} :catch_1e

    :goto_19
    return-void

    :catch_1a
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_1e
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_22
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

    packed-switch v4, :pswitch_data_26

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
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_1c} :catch_21

    :goto_1c
    return-void

    :catch_1d
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_21
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    return-void

    nop

    :pswitch_data_26
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
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_d} :catch_12

    return-void

    :catch_e
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_12
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

    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

    move-result v2

    int-to-long v0, v2

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_13
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_13} :catch_14
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_13} :catch_18

    return-void

    :catch_14
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :catch_18
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

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->memory(I)Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

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

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->tagAddress(I)I

    move-result v3

    int-to-long v0, v3

    const/16 v2, 0x9

    aput-wide v0, v5, v2

    const/4 v2, 0x1

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->tagAddress(I)I

    move-result v3

    int-to-long v0, v3

    const/16 v2, 0xa

    aput-wide v0, v5, v2

    const/4 v2, 0x2

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->tagAddress(I)I

    move-result v3

    int-to-long v0, v3

    const/16 v2, 0xb

    aput-wide v0, v5, v2

    return-void
.end method


# virtual methods
.method public accept(Ljava/lang/Object;)V
    .registers 5

    check-cast p1, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;

    const/4 v0, 0x4

    new-array v1, v0, [Ljava/lang/Object;

    const/16 v0, 0xc

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
