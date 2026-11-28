.class public final Lchasm/dex/WasmModule$Code0;
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

    invoke-static/range {v18 .. v21}, Lchasm/dex/WasmModule$Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

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

    invoke-static/range {v29 .. v32}, Lchasm/dex/WasmModule$Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V

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
    invoke-static/range {v23 .. v25}, Lchasm/dex/WasmModule$Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

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
    .registers 32

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move/from16 v18, v15

    move-object/from16 v19, v15

    move/from16 v22, v15

    move-wide/from16 v20, v0

    move-object/from16 v23, v15

    move-object/from16 v24, v15

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

    const/4 v9, 0x0

    aget-object v12, v16, v9

    check-cast v12, [J

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

    goto/16 :goto_3e

    .line 4
    :goto_3e
    move-object/from16 v19, v24

    .line 6
    :goto_40
    const/high16 v10, -0x80000000

    add-int v8, v18, v10

    const v9, -0x7ffff830

    if-ge v8, v9, :cond_6c

    .line 7
    move/from16 v22, v18

    .line 8
    const/16 v1, 0x10

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_59

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_59
    const/4 v9, 0x4

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v22

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move-object/from16 v23, v12

    .line 9
    add-int/lit8 v18, v18, 0x1

    .line 10
    goto :goto_40

    .line 11
    :cond_6c
    move-object/from16 v23, v19

    .line 12
    move-object/from16 v12, v23

    invoke-static {v14, v12}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->rethrowException(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Ljava/lang/Object;)V

    goto/16 :goto_77

    const/4 v8, 0x0

    return v8

    :goto_77
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

    invoke-static/range {v21 .. v24}, Lchasm/dex/WasmModule$Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

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

.class final Lchasm/dex/WasmModule$Declare0;
.super Ljava/lang/Object;


# direct methods
.method static declare(Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;)V
    .registers 12

    move-object v0, p0

    const-string v1, "after_trysame_functionnestedcatch_allgrow_throwcatch_refthrow_nulluncaught"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->strings(Ljava/lang/String;)V

    const-string v1, "\t\r\u0006\t\n\t\n\u0008"

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

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->fieldType(IZ)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->structComposite(I)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x0

    const-string v2, "7cd61ed60e3e32738fd5976a89584478"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x1

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x1

    const-string v2, "56fea0d3d9dff2e917c8513666724464"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x2

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x2

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x2

    const-string v2, "3c9fdc07080286255dd9b9b5301301a1"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x0

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x3

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x3

    const-string v2, "c663c838ba102d405623fdadf53aeb86"

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

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x1

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x5

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x5

    const-string v2, "f4ca4f5a2991bec9ca3a69a0c464f9b4"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    const/4 v2, 0x2

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x6

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x6

    const-string v2, "9e65d43dcbc1bae36a8b4cbc029c8fb1"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x7

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x7

    const-string v2, "5e754b92492ccd4fac81f1d5b5fff84b"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x0

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x4

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->abstractHeapType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceType(Z)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->referenceValueType()V

    const/4 v1, 0x0

    const/4 v2, 0x2

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/16 v1, 0x8

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/16 v1, 0x8

    const-string v2, "65f7bf54e851a4bb2827f04c60f2427c"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const-string v1, "\u0001\u0004\u0004\u0005\u0004\u0003\u0007\u0004\u0004\u0003\u0004"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functions(Ljava/lang/String;)V

    const-wide/16 v1, 0x1

    const/4 v3, 0x1

    const-wide/16 v4, 0x4

    invoke-virtual/range {v0 .. v5}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->limits(JZJ)V

    const/4 v1, 0x0

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->memoryType(ZZ)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->memory()V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->tag(I)V

    const/4 v1, 0x2

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->tag(I)V

    const/4 v1, 0x3

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->tag(I)V

    const-string v1, "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->exportKinds(Ljava/lang/String;)V

    const-string v1, "\u0001\u0002\u0003\u0004\u0006\u0008\t\n"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->exportIndices(Ljava/lang/String;)V

    const-string v1, "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->exportNames(Ljava/lang/String;)V

    return-void
.end method

.class public final Lchasm/dex/WasmModule$Entry0;
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

    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)V
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
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_38

    :pswitch_26
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_38

    :pswitch_2c
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f8([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_38

    :pswitch_32
    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f10([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

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

    invoke-static/range {v5 .. v8}, Lchasm/dex/WasmModule$Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)J

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
    .registers 13

    :try_start_0
    move-object v2, p0

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p4

    packed-switch v4, :pswitch_data_22

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_f

    :goto_f
    :pswitch_f
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    goto/16 :goto_19

    :pswitch_14
    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f9([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

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

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p4

    invoke-static/range {v5 .. v7}, Lchasm/dex/WasmModule$Code0;->f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)I

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

.class final Lchasm/dex/WasmModule$Install;
.super Ljava/lang/Object;


# direct methods
.method static bindObjects0(Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;[Ljava/lang/Object;)V
    .registers 8

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->memory(I)Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    move-result-object v4

    const/4 v2, 0x3

    aput-object v4, p1, v2

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->rtt(I)Ljava/lang/Object;

    move-result-object v4

    const/4 v2, 0x4

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

    new-instance v4, Lchasm/dex/WasmModule$Entry2;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry2;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x3

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry1;

    const/4 v3, 0x2

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry1;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x4

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry3;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry3;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x5

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry4;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry4;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x6

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry1;

    const/4 v3, 0x3

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry1;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x7

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry1;

    const/4 v3, 0x4

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry1;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x8

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry3;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry3;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0x9

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry1;

    const/4 v3, 0x5

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry1;-><init>([Ljava/lang/Object;I)V

    const/16 v2, 0xa

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    return-void
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
    new-instance v0, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;

    invoke-direct {v0, p1}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

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

    const-string v1, "chasm.dex.WasmModule$Entry0"

    aput-object v1, p0, v0

    const/4 v0, 0x2

    const-string v1, "chasm.dex.WasmModule$Entry1"

    aput-object v1, p0, v0

    const/4 v0, 0x3

    const-string v1, "chasm.dex.WasmModule$Entry2"

    aput-object v1, p0, v0

    const/4 v0, 0x4

    const-string v1, "chasm.dex.WasmModule$Entry3"

    aput-object v1, p0, v0

    const/4 v0, 0x5

    const-string v1, "chasm.dex.WasmModule$Entry4"

    aput-object v1, p0, v0

    const/4 v0, 0x6

    const-string v1, "chasm.dex.WasmModule$InstallEntries0"

    aput-object v1, p0, v0

    const/4 v0, 0x7

    const-string v1, "chasm.dex.WasmModule$Install"

    aput-object v1, p0, v0

    const/16 v0, 0x8

    const-string v1, "chasm.dex.WasmModule$Declare0"

    aput-object v1, p0, v0

    const/16 v0, 0x9

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

    const/16 v0, 0xa

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

    const/4 v0, 0x0

    invoke-virtual {p1, v0}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->memory(I)Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    move-result-object v1

    invoke-static {v1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;->requireByteArray(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;)V

    const/4 v0, 0x5

    new-array v1, v0, [Ljava/lang/Object;

    const/16 v0, 0xc

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

    const/4 v0, 0x4

    new-array v1, v0, [J

    fill-array-data v1, :array_8

    return-object v1

    nop

    :array_8
    .array-data 8
        0x0
        0x10
        0x1
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
