.class public final Lchasm/jit/Code0;
.super Ljava/lang/Object;
.source "module.wasm"


# direct methods
.method public static f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)J
    .registers 36

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v18, v0

    move/from16 v20, v15

    move-wide/from16 v21, v0

    .line 3
    :cond_11
    move/from16 v0, v20

    int-to-long v0, v0

    const-wide v2, 0xffffffffL

    and-long v25, v0, v2

    .line 4
    const-wide/16 v2, 0x3

    mul-long v25, v25, v2

    .line 5
    add-long v21, v21, v25

    .line 6
    add-int/lit8 v20, v20, 0x1

    .line 7
    const/high16 v10, -0x80000000

    add-int v8, v20, v10

    add-int v9, v17, v10

    if-lt v8, v9, :cond_11

    .line 8
    move-wide/from16 v0, v21

    return-wide v0

    const-wide/16 v0, 0x0

    return-wide v0
.end method

.method public static f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
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

    move-object/from16 v25, v16

    move-object/from16 v26, v14

    move/from16 v27, v13

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v18, v0

    const/4 v8, 0x0

    move-object/from16 v24, v8

    .line 2
    const/high16 v10, -0x80000000

    add-int v8, v17, v10

    const v9, -0x7ffffffe

    if-ge v8, v9, :cond_2f

    .line 3
    move/from16 v20, v17

    .line 4
    goto/16 :goto_41

    .line 5
    :cond_2f
    add-int/lit8 v20, v17, -0x1

    .line 6
    move/from16 v28, v20

    invoke-static/range {v25 .. v28}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v20

    .line 7
    add-int/lit8 v21, v17, -0x2

    .line 8
    move/from16 v28, v21

    invoke-static/range {v25 .. v28}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v21

    .line 9
    add-int v20, v20, v21

    .line 10
    :goto_41
    move/from16 v8, v20

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 37

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v18, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    move-object/from16 v29, v12

    move/from16 v19, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v20, v0

    move/from16 v22, v15

    move/from16 v23, v15

    move/from16 v24, v15

    .line 2
    move-object/from16 v0, v29

    move/from16 v8, v19

    const/4 v9, 0x1

    const/4 v10, 0x0

    invoke-static {v0, v10, v9, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;->memoryFill(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;III)V

    .line 3
    const/16 v24, 0x2

    .line 5
    :goto_32
    mul-int v25, v24, v24

    .line 6
    const/high16 v10, -0x80000000

    add-int v8, v25, v10

    add-int v9, v19, v10

    if-ge v8, v9, :cond_69

    .line 7
    move/from16 v8, v24

    if-ltz v8, :cond_8b

    add-int/lit8 v11, v17, -0x1

    if-gt v8, v11, :cond_8b

    aget-byte v9, v18, v8

    and-int/lit16 v9, v9, 0xff

    move/from16 v25, v9

    .line 8
    if-eqz v25, :cond_66

    .line 9
    mul-int v22, v24, v24

    .line 11
    :goto_4e
    const/high16 v10, -0x80000000

    add-int v8, v22, v10

    add-int v9, v19, v10

    if-ge v8, v9, :cond_64

    .line 12
    move/from16 v8, v22

    if-ltz v8, :cond_a0

    add-int/lit8 v11, v17, -0x1

    if-gt v8, v11, :cond_a0

    const/4 v9, 0x0

    aput-byte v9, v18, v8

    .line 13
    add-int v22, v22, v24

    .line 14
    goto :goto_4e

    .line 15
    :cond_64
    goto/16 :goto_66

    .line 16
    :cond_66
    :goto_66
    add-int/lit8 v24, v24, 0x1

    .line 17
    goto :goto_32

    .line 18
    :cond_69
    const/16 v24, 0x2

    .line 20
    :goto_6b
    const/high16 v10, -0x80000000

    add-int v8, v24, v10

    add-int v9, v19, v10

    if-ge v8, v9, :cond_86

    .line 21
    move/from16 v8, v24

    if-ltz v8, :cond_b5

    add-int/lit8 v11, v17, -0x1

    if-gt v8, v11, :cond_b5

    aget-byte v9, v18, v8

    and-int/lit16 v9, v9, 0xff

    move/from16 v26, v9

    .line 22
    add-int v23, v23, v26

    .line 23
    add-int/lit8 v24, v24, 0x1

    .line 24
    goto :goto_6b

    .line 25
    :cond_86
    move/from16 v8, v23

    return v8

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_8b
    move-object/from16 v12, v29

    const/4 v10, 0x0

    move/from16 v9, v24

    int-to-long v2, v9

    const-wide v4, 0xffffffffL

    and-long/2addr v2, v4

    const-wide/16 v4, 0x0

    add-long/2addr v2, v4

    const/4 v11, 0x1

    invoke-static {v12, v10, v2, v3, v11}, Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;->memoryAccessOutOfBounds(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;IJI)Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 12
    :cond_a0
    move-object/from16 v12, v29

    const/4 v10, 0x1

    move/from16 v9, v22

    int-to-long v2, v9

    const-wide v4, 0xffffffffL

    and-long/2addr v2, v4

    const-wide/16 v4, 0x0

    add-long/2addr v2, v4

    const/4 v11, 0x1

    invoke-static {v12, v10, v2, v3, v11}, Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;->memoryAccessOutOfBounds(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;IJI)Ljava/lang/Void;

    const/4 v8, 0x0

    return v8

    .line 21
    :cond_b5
    move-object/from16 v12, v29

    const/4 v10, 0x0

    move/from16 v9, v24

    int-to-long v2, v9

    const-wide v4, 0xffffffffL

    and-long/2addr v2, v4

    const-wide/16 v4, 0x0

    add-long/2addr v2, v4

    const/4 v11, 0x1

    invoke-static {v12, v10, v2, v3, v11}, Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;->memoryAccessOutOfBounds(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;IJI)Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
.end method

.method public static f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)D
    .registers 47

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v18, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    move-object/from16 v39, v12

    move/from16 v19, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v20, v0

    move/from16 v22, v15

    move/from16 v23, v15

    move-wide/from16 v24, v0

    move-wide/from16 v26, v0

    move/from16 v28, v15

    move/from16 v29, v15

    move/from16 v30, v15

    .line 2
    mul-int v31, v19, v19

    .line 3
    mul-int/lit8 v29, v31, 0x8

    .line 4
    mul-int/lit8 v30, v29, 0x2

    .line 5
    const/16 v28, 0x0

    .line 7
    :cond_37
    shl-int/lit8 v31, v28, 0x3

    .line 8
    const/4 v9, 0x7

    move/from16 v8, v28

    int-to-long v0, v8

    int-to-long v2, v9

    const-wide v4, 0xffffffffL

    and-long/2addr v0, v4

    and-long/2addr v2, v4

    rem-long/2addr v0, v2

    long-to-int v8, v0

    int-to-long v0, v8

    move-wide/from16 v32, v0

    .line 9
    move-wide/from16 v0, v32

    long-to-int v8, v0

    int-to-long v2, v8

    const-wide v6, 0xffffffffL

    and-long/2addr v2, v6

    long-to-double v4, v2

    invoke-static {v4, v5}, Ljava/lang/Double;->doubleToRawLongBits(D)J

    move-result-wide v4

    move-wide/from16 v32, v4

    .line 10
    move/from16 v8, v31

    if-ltz v8, :cond_2ef

    add-int/lit8 v11, v17, -0x8

    if-gt v8, v11, :cond_2ef

    move-wide/from16 v2, v32

    long-to-int v9, v2

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

    const/16 v10, 0x20

    ushr-long v4, v2, v10

    long-to-int v9, v4

    add-int/lit16 v11, v8, 0x4

    aput-byte v9, v18, v11

    ushr-int/lit8 v10, v9, 0x8

    add-int/lit16 v11, v8, 0x5

    aput-byte v10, v18, v11

    ushr-int/lit8 v10, v9, 0x10

    add-int/lit16 v11, v8, 0x6

    aput-byte v10, v18, v11

    ushr-int/lit8 v10, v9, 0x18

    add-int/lit16 v11, v8, 0x7

    aput-byte v10, v18, v11

    .line 11
    shl-int/lit8 v8, v28, 0x3

    int-to-long v0, v8

    move-wide/from16 v32, v0

    .line 12
    move-wide/from16 v2, v32

    long-to-int v9, v2

    add-int v31, v29, v9

    .line 13
    const/4 v9, 0x5

    move/from16 v8, v28

    int-to-long v0, v8

    int-to-long v2, v9

    const-wide v4, 0xffffffffL

    and-long/2addr v0, v4

    and-long/2addr v2, v4

    rem-long/2addr v0, v2

    long-to-int v8, v0

    int-to-long v0, v8

    move-wide/from16 v32, v0

    .line 14
    move-wide/from16 v0, v32

    long-to-int v8, v0

    int-to-long v2, v8

    const-wide v6, 0xffffffffL

    and-long/2addr v2, v6

    long-to-double v4, v2

    invoke-static {v4, v5}, Ljava/lang/Double;->doubleToRawLongBits(D)J

    move-result-wide v4

    move-wide/from16 v32, v4

    .line 15
    move/from16 v8, v31

    if-ltz v8, :cond_306

    add-int/lit8 v11, v17, -0x8

    if-gt v8, v11, :cond_306

    move-wide/from16 v2, v32

    long-to-int v9, v2

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

    const/16 v10, 0x20

    ushr-long v4, v2, v10

    long-to-int v9, v4

    add-int/lit16 v11, v8, 0x4

    aput-byte v9, v18, v11

    ushr-int/lit8 v10, v9, 0x8

    add-int/lit16 v11, v8, 0x5

    aput-byte v10, v18, v11

    ushr-int/lit8 v10, v9, 0x10

    add-int/lit16 v11, v8, 0x6

    aput-byte v10, v18, v11

    ushr-int/lit8 v10, v9, 0x18

    add-int/lit16 v11, v8, 0x7

    aput-byte v10, v18, v11

    .line 16
    add-int/lit8 v28, v28, 0x1

    .line 17
    mul-int v8, v19, v19

    int-to-long v0, v8

    move-wide/from16 v32, v0

    .line 18
    const/high16 v10, -0x80000000

    add-int v8, v28, v10

    move-wide/from16 v2, v32

    long-to-int v9, v2

    add-int/2addr v9, v10

    if-lt v8, v9, :cond_37

    .line 19
    const/16 v28, 0x0

    .line 21
    :cond_10e
    const/16 v22, 0x0

    .line 23
    :cond_110
    const-wide/16 v24, 0x0

    .line 24
    const/16 v23, 0x0

    .line 26
    :cond_114
    mul-int v8, v28, v19

    int-to-long v0, v8

    move-wide/from16 v32, v0

    .line 27
    move-wide/from16 v0, v32

    long-to-int v8, v0

    add-int v8, v8, v23

    int-to-long v0, v8

    move-wide/from16 v32, v0

    .line 28
    move-wide/from16 v0, v32

    long-to-int v8, v0

    shl-int/lit8 v8, v8, 0x3

    int-to-long v0, v8

    move-wide/from16 v32, v0

    .line 29
    move-wide/from16 v0, v32

    long-to-int v8, v0

    if-ltz v8, :cond_31d

    add-int/lit8 v11, v17, -0x8

    if-gt v8, v11, :cond_31d

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

    int-to-long v2, v9

    const-wide v4, 0xffffffffL

    and-long/2addr v2, v4

    add-int/lit16 v11, v8, 0x4

    aget-byte v9, v18, v11

    and-int/lit16 v9, v9, 0xff

    add-int/lit16 v11, v8, 0x5

    aget-byte v10, v18, v11

    and-int/lit16 v10, v10, 0xff

    shl-int/lit8 v10, v10, 0x8

    or-int/2addr v9, v10

    add-int/lit16 v11, v8, 0x6

    aget-byte v10, v18, v11

    and-int/lit16 v10, v10, 0xff

    shl-int/lit8 v10, v10, 0x10

    or-int/2addr v9, v10

    add-int/lit16 v11, v8, 0x7

    aget-byte v10, v18, v11

    shl-int/lit8 v10, v10, 0x18

    or-int/2addr v9, v10

    int-to-long v0, v9

    const/16 v11, 0x20

    shl-long/2addr v0, v11

    or-long/2addr v0, v2

    move-wide/from16 v32, v0

    .line 30
    mul-int v36, v23, v19

    .line 31
    add-int v36, v36, v22

    .line 32
    shl-int/lit8 v36, v36, 0x3

    .line 33
    add-int v8, v29, v36

    int-to-long v0, v8

    move-wide/from16 v34, v0

    .line 34
    move-wide/from16 v0, v34

    long-to-int v8, v0

    if-ltz v8, :cond_335

    add-int/lit8 v11, v17, -0x8

    if-gt v8, v11, :cond_335

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

    int-to-long v2, v9

    const-wide v4, 0xffffffffL

    and-long/2addr v2, v4

    add-int/lit16 v11, v8, 0x4

    aget-byte v9, v18, v11

    and-int/lit16 v9, v9, 0xff

    add-int/lit16 v11, v8, 0x5

    aget-byte v10, v18, v11

    and-int/lit16 v10, v10, 0xff

    shl-int/lit8 v10, v10, 0x8

    or-int/2addr v9, v10

    add-int/lit16 v11, v8, 0x6

    aget-byte v10, v18, v11

    and-int/lit16 v10, v10, 0xff

    shl-int/lit8 v10, v10, 0x10

    or-int/2addr v9, v10

    add-int/lit16 v11, v8, 0x7

    aget-byte v10, v18, v11

    shl-int/lit8 v10, v10, 0x18

    or-int/2addr v9, v10

    int-to-long v0, v9

    const/16 v11, 0x20

    shl-long/2addr v0, v11

    or-long/2addr v0, v2

    move-wide/from16 v34, v0

    .line 35
    move-wide/from16 v0, v32

    invoke-static {v0, v1}, Ljava/lang/Double;->longBitsToDouble(J)D

    move-result-wide v0

    move-wide/from16 v2, v34

    invoke-static {v2, v3}, Ljava/lang/Double;->longBitsToDouble(J)D

    move-result-wide v2

    mul-double/2addr v0, v2

    invoke-static {v0, v1}, Ljava/lang/Double;->doubleToRawLongBits(D)J

    move-result-wide v0

    move-wide/from16 v32, v0

    .line 36
    move-wide/from16 v2, v32

    invoke-static {v2, v3}, Ljava/lang/Double;->longBitsToDouble(J)D

    move-result-wide v2

    add-double v24, v24, v2

    .line 37
    add-int/lit8 v23, v23, 0x1

    .line 38
    const/high16 v10, -0x80000000

    add-int v8, v23, v10

    add-int v9, v19, v10

    if-lt v8, v9, :cond_114

    .line 39
    mul-int v8, v28, v19

    int-to-long v0, v8

    move-wide/from16 v32, v0

    .line 40
    move-wide/from16 v0, v32

    long-to-int v8, v0

    add-int v8, v8, v22

    int-to-long v0, v8

    move-wide/from16 v32, v0

    .line 41
    move-wide/from16 v0, v32

    long-to-int v8, v0

    shl-int/lit8 v8, v8, 0x3

    int-to-long v0, v8

    move-wide/from16 v32, v0

    .line 42
    move-wide/from16 v2, v32

    long-to-int v9, v2

    add-int v31, v30, v9

    .line 43
    move/from16 v8, v31

    if-ltz v8, :cond_34d

    add-int/lit8 v11, v17, -0x8

    if-gt v8, v11, :cond_34d

    move-wide/from16 v2, v24

    invoke-static {v2, v3}, Ljava/lang/Double;->doubleToRawLongBits(D)J

    move-result-wide v2

    long-to-int v9, v2

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

    const/16 v10, 0x20

    ushr-long v4, v2, v10

    long-to-int v9, v4

    add-int/lit16 v11, v8, 0x4

    aput-byte v9, v18, v11

    ushr-int/lit8 v10, v9, 0x8

    add-int/lit16 v11, v8, 0x5

    aput-byte v10, v18, v11

    ushr-int/lit8 v10, v9, 0x10

    add-int/lit16 v11, v8, 0x6

    aput-byte v10, v18, v11

    ushr-int/lit8 v10, v9, 0x18

    add-int/lit16 v11, v8, 0x7

    aput-byte v10, v18, v11

    .line 44
    add-int/lit8 v22, v22, 0x1

    .line 45
    const/high16 v10, -0x80000000

    add-int v8, v22, v10

    add-int v9, v19, v10

    if-lt v8, v9, :cond_110

    .line 46
    add-int/lit8 v28, v28, 0x1

    .line 47
    const/high16 v10, -0x80000000

    add-int v8, v28, v10

    add-int v9, v19, v10

    if-lt v8, v9, :cond_10e

    .line 48
    const/16 v28, 0x0

    .line 50
    :cond_26d
    add-int/lit8 v36, v19, 0x1

    .line 51
    mul-int v8, v28, v36

    int-to-long v0, v8

    move-wide/from16 v34, v0

    .line 52
    move-wide/from16 v0, v34

    long-to-int v8, v0

    shl-int/lit8 v8, v8, 0x3

    int-to-long v0, v8

    move-wide/from16 v34, v0

    .line 53
    move-wide/from16 v2, v34

    long-to-int v9, v2

    add-int v8, v30, v9

    int-to-long v0, v8

    move-wide/from16 v32, v0

    .line 54
    move-wide/from16 v0, v32

    long-to-int v8, v0

    if-ltz v8, :cond_364

    add-int/lit8 v11, v17, -0x8

    if-gt v8, v11, :cond_364

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

    int-to-long v2, v9

    const-wide v4, 0xffffffffL

    and-long/2addr v2, v4

    add-int/lit16 v11, v8, 0x4

    aget-byte v9, v18, v11

    and-int/lit16 v9, v9, 0xff

    add-int/lit16 v11, v8, 0x5

    aget-byte v10, v18, v11

    and-int/lit16 v10, v10, 0xff

    shl-int/lit8 v10, v10, 0x8

    or-int/2addr v9, v10

    add-int/lit16 v11, v8, 0x6

    aget-byte v10, v18, v11

    and-int/lit16 v10, v10, 0xff

    shl-int/lit8 v10, v10, 0x10

    or-int/2addr v9, v10

    add-int/lit16 v11, v8, 0x7

    aget-byte v10, v18, v11

    shl-int/lit8 v10, v10, 0x18

    or-int/2addr v9, v10

    int-to-long v0, v9

    const/16 v11, 0x20

    shl-long/2addr v0, v11

    or-long/2addr v0, v2

    move-wide/from16 v32, v0

    .line 55
    move-wide/from16 v2, v32

    invoke-static {v2, v3}, Ljava/lang/Double;->longBitsToDouble(J)D

    move-result-wide v2

    add-double v26, v26, v2

    .line 56
    add-int/lit8 v28, v28, 0x1

    .line 57
    const/high16 v10, -0x80000000

    add-int v8, v28, v10

    add-int v9, v19, v10

    if-lt v8, v9, :cond_26d

    .line 58
    move-wide/from16 v0, v26

    return-wide v0

    const-wide/16 v0, 0x0

    return-wide v0

    .line 10
    :cond_2ef
    move-object/from16 v12, v39

    const/4 v10, 0x1

    move/from16 v9, v31

    int-to-long v2, v9

    const-wide v4, 0xffffffffL

    and-long/2addr v2, v4

    const-wide/16 v4, 0x0

    add-long/2addr v2, v4

    const/16 v11, 0x8

    invoke-static {v12, v10, v2, v3, v11}, Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;->memoryAccessOutOfBounds(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;IJI)Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    .line 15
    :cond_306
    move-object/from16 v12, v39

    const/4 v10, 0x1

    move/from16 v9, v31

    int-to-long v2, v9

    const-wide v4, 0xffffffffL

    and-long/2addr v2, v4

    const-wide/16 v4, 0x0

    add-long/2addr v2, v4

    const/16 v11, 0x8

    invoke-static {v12, v10, v2, v3, v11}, Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;->memoryAccessOutOfBounds(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;IJI)Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    .line 29
    :cond_31d
    move-object/from16 v12, v39

    const/4 v10, 0x0

    move-wide/from16 v0, v32

    long-to-int v9, v0

    int-to-long v2, v9

    const-wide v4, 0xffffffffL

    and-long/2addr v2, v4

    const-wide/16 v4, 0x0

    add-long/2addr v2, v4

    const/16 v11, 0x8

    invoke-static {v12, v10, v2, v3, v11}, Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;->memoryAccessOutOfBounds(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;IJI)Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    .line 34
    :cond_335
    move-object/from16 v12, v39

    const/4 v10, 0x0

    move-wide/from16 v0, v34

    long-to-int v9, v0

    int-to-long v2, v9

    const-wide v4, 0xffffffffL

    and-long/2addr v2, v4

    const-wide/16 v4, 0x0

    add-long/2addr v2, v4

    const/16 v11, 0x8

    invoke-static {v12, v10, v2, v3, v11}, Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;->memoryAccessOutOfBounds(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;IJI)Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    .line 43
    :cond_34d
    move-object/from16 v12, v39

    const/4 v10, 0x1

    move/from16 v9, v31

    int-to-long v2, v9

    const-wide v4, 0xffffffffL

    and-long/2addr v2, v4

    const-wide/16 v4, 0x0

    add-long/2addr v2, v4

    const/16 v11, 0x8

    invoke-static {v12, v10, v2, v3, v11}, Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;->memoryAccessOutOfBounds(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;IJI)Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    .line 54
    :cond_364
    move-object/from16 v12, v39

    const/4 v10, 0x0

    move-wide/from16 v0, v32

    long-to-int v9, v0

    int-to-long v2, v9

    const-wide v4, 0xffffffffL

    and-long/2addr v2, v4

    const-wide/16 v4, 0x0

    add-long/2addr v2, v4

    const/16 v11, 0x8

    invoke-static {v12, v10, v2, v3, v11}, Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;->memoryAccessOutOfBounds(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;IJI)Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0
.end method

.method public static f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 39

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v18, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    move-object/from16 v31, v12

    move/from16 v19, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v20, v0

    move/from16 v22, v15

    move/from16 v23, v15

    move/from16 v24, v15

    .line 2
    const/16 v24, -0x1

    .line 4
    :cond_29
    move/from16 v8, v22

    if-ltz v8, :cond_65

    add-int/lit8 v11, v17, -0x1

    if-gt v8, v11, :cond_65

    aget-byte v9, v18, v8

    and-int/lit16 v9, v9, 0xff

    move/from16 v26, v9

    .line 5
    xor-int v24, v24, v26

    .line 6
    const/16 v23, 0x0

    .line 8
    :cond_3b
    ushr-int/lit8 v25, v24, 0x1

    .line 9
    and-int/lit8 v28, v24, 0x1

    .line 10
    const/4 v8, 0x0

    sub-int v27, v8, v28

    .line 11
    const v8, -0x12477ce0

    and-int v26, v8, v27

    .line 12
    xor-int v24, v25, v26

    .line 13
    add-int/lit8 v23, v23, 0x1

    .line 14
    const/high16 v10, -0x80000000

    add-int v8, v23, v10

    const v9, -0x7ffffff8

    if-lt v8, v9, :cond_3b

    .line 15
    add-int/lit8 v22, v22, 0x1

    .line 16
    const/high16 v10, -0x80000000

    add-int v8, v22, v10

    add-int v9, v19, v10

    if-lt v8, v9, :cond_29

    .line 17
    xor-int/lit8 v19, v24, -0x1

    .line 18
    move/from16 v8, v19

    return v8

    const/4 v8, 0x0

    return v8

    .line 4
    :cond_65
    move-object/from16 v12, v31

    const/4 v10, 0x0

    move/from16 v9, v22

    int-to-long v2, v9

    const-wide v4, 0xffffffffL

    and-long/2addr v2, v4

    const-wide/16 v4, 0x0

    add-long/2addr v2, v4

    const/4 v11, 0x1

    invoke-static {v12, v10, v2, v3, v11}, Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;->memoryAccessOutOfBounds(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;IJI)Ljava/lang/Void;

    const/4 v8, 0x0

    return v8
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

    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)J

    move-result-wide v0

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_11
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_11} :catch_12

    return-void

    :catch_12
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

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

    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)J

    move-result-wide v0

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_18
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_18} :catch_19

    return-void

    :catch_19
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

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

    packed-switch v4, :pswitch_data_30

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_14

    :goto_14
    :pswitch_14
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_26

    :pswitch_1a
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_26

    :pswitch_20
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_26

    :goto_26
    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_2a
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_2a} :catch_2b

    return-void

    :catch_2b
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    nop

    :pswitch_data_30
    .packed-switch 0x0
        :pswitch_14
        :pswitch_1a
        :pswitch_20
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

    packed-switch v4, :pswitch_data_36

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_18

    :goto_18
    :pswitch_18
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_2a

    :pswitch_1e
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_2a

    :pswitch_24
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

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
        :pswitch_18
        :pswitch_1e
        :pswitch_24
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

    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)D

    move-result-wide v0

    invoke-static {v0, v1}, Ljava/lang/Double;->doubleToRawLongBits(D)J

    move-result-wide v0

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_15
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_15} :catch_16

    return-void

    :catch_16
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

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

    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)D

    move-result-wide v0

    invoke-static {v0, v1}, Ljava/lang/Double;->doubleToRawLongBits(D)J

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

    const/4 v2, 0x1

    aput-object v4, p1, v2

    return-void
.end method


# virtual methods
.method public accept(Ljava/lang/Object;)V
    .registers 5

    check-cast p1, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;

    const/4 v0, 0x2

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

    return-void
.end method
