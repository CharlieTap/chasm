.class public final Lchasm/jit/Code0;
.super Ljava/lang/Object;
.source "module.wasm"


# direct methods
.method public static f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIJ)J
    .registers 46

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v19, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17

    const/4 v9, 0x2

    aget-object v18, v16, v9

    check-cast v18, Lsun/misc/Unsafe;

    const-wide/16 v24, 0x10

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    move-object/from16 v36, v12

    move/from16 v8, p3

    int-to-long v0, v8

    move-wide/from16 v26, v0

    move-wide/from16 v28, p4

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v30, v0

    .line 2
    move-wide/from16 v0, v26

    long-to-int v8, v0

    if-ltz v8, :cond_6b

    add-int/lit8 v11, v17, -0xc

    if-gt v8, v11, :cond_6b

    add-int/lit16 v8, v8, 0x4

    move-wide/from16 v22, v28

    int-to-long v2, v8

    add-long v20, v2, v24

    invoke-virtual/range {v18 .. v23}, Lsun/misc/Unsafe;->putLong(Ljava/lang/Object;JJ)V

    .line 3
    move-wide/from16 v0, v26

    long-to-int v8, v0

    add-int/lit16 v8, v8, 0x4

    int-to-long v2, v8

    add-long v20, v2, v24

    invoke-virtual/range {v18 .. v21}, Lsun/misc/Unsafe;->getLong(Ljava/lang/Object;J)J

    move-result-wide v32

    .line 4
    move-wide/from16 v0, v26

    long-to-int v8, v0

    add-int/lit16 v8, v8, 0x5

    int-to-long v2, v8

    add-long v20, v2, v24

    invoke-virtual/range {v18 .. v21}, Lsun/misc/Unsafe;->getInt(Ljava/lang/Object;J)I

    move-result v9

    int-to-char v9, v9

    int-to-long v0, v9

    move-wide/from16 v34, v0

    .line 5
    const-wide v2, 0xffffffffL

    and-long v34, v34, v2

    .line 6
    add-long v26, v32, v34

    .line 7
    move-wide/from16 v0, v26

    return-wide v0

    const-wide/16 v0, 0x0

    return-wide v0

    .line 2
    :cond_6b
    move-object/from16 v12, v36

    const/4 v10, 0x1

    move-wide/from16 v0, v26

    long-to-int v9, v0

    int-to-long v2, v9

    const-wide v4, 0xffffffffL

    and-long/2addr v2, v4

    const-wide/16 v4, 0x4

    add-long/2addr v2, v4

    const/16 v11, 0x8

    invoke-static {v12, v10, v2, v3, v11}, Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;->memoryAccessOutOfBounds(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;IJI)Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0
.end method

.method public static f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I
    .registers 41

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v19, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17

    const/4 v9, 0x2

    aget-object v18, v16, v9

    check-cast v18, Lsun/misc/Unsafe;

    const-wide/16 v24, 0x10

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    move-object/from16 v32, v12

    move/from16 v26, p3

    move/from16 v27, p4

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v28, v0

    .line 2
    move/from16 v8, v26

    if-ltz v8, :cond_49

    add-int/lit8 v11, v17, -0x1

    if-gt v8, v11, :cond_49

    aput-byte v27, v19, v8

    .line 3
    move/from16 v8, v26

    aget-byte v9, v19, v8

    move/from16 v30, v9

    .line 4
    move/from16 v8, v26

    aget-byte v9, v19, v8

    and-int/lit16 v9, v9, 0xff

    move/from16 v31, v9

    .line 5
    add-int v26, v30, v31

    .line 6
    move/from16 v8, v26

    return v8

    const/4 v8, 0x0

    return v8

    .line 2
    :cond_49
    move-object/from16 v12, v32

    const/4 v10, 0x1

    move/from16 v9, v26

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

.method public static f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIFD)D
    .registers 48

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v19, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17

    const/4 v9, 0x2

    aget-object v18, v16, v9

    check-cast v18, Lsun/misc/Unsafe;

    const-wide/16 v24, 0x10

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    move-object/from16 v37, v12

    move/from16 v8, p3

    int-to-long v0, v8

    move-wide/from16 v26, v0

    move/from16 v28, p4

    move-wide/from16 v29, p5

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v31, v0

    .line 2
    move-wide/from16 v0, v26

    long-to-int v8, v0

    if-ltz v8, :cond_a2

    add-int/lit8 v11, v17, -0x4

    if-gt v8, v11, :cond_a2

    move/from16 v9, v28

    invoke-static {v9}, Ljava/lang/Float;->floatToRawIntBits(F)I

    move-result v9

    move/from16 v22, v9

    int-to-long v2, v8

    add-long v20, v2, v24

    invoke-virtual/range {v18 .. v22}, Lsun/misc/Unsafe;->putInt(Ljava/lang/Object;JI)V

    .line 3
    move-wide/from16 v0, v26

    long-to-int v8, v0

    if-ltz v8, :cond_b9

    add-int/lit8 v11, v17, -0x10

    if-gt v8, v11, :cond_b9

    add-int/lit16 v8, v8, 0x8

    move-wide/from16 v4, v29

    invoke-static {v4, v5}, Ljava/lang/Double;->doubleToRawLongBits(D)J

    move-result-wide v4

    move-wide/from16 v22, v4

    int-to-long v2, v8

    add-long v20, v2, v24

    invoke-virtual/range {v18 .. v23}, Lsun/misc/Unsafe;->putLong(Ljava/lang/Object;JJ)V

    .line 4
    move-wide/from16 v0, v26

    long-to-int v8, v0

    int-to-long v2, v8

    add-long v20, v2, v24

    invoke-virtual/range {v18 .. v21}, Lsun/misc/Unsafe;->getInt(Ljava/lang/Object;J)I

    move-result v9

    int-to-long v0, v9

    move-wide/from16 v33, v0

    .line 5
    move-wide/from16 v0, v33

    long-to-int v8, v0

    invoke-static {v8}, Ljava/lang/Float;->intBitsToFloat(I)F

    move-result v8

    float-to-double v4, v8

    invoke-static {v4, v5}, Ljava/lang/Double;->doubleToRawLongBits(D)J

    move-result-wide v4

    move-wide/from16 v33, v4

    .line 6
    move-wide/from16 v0, v26

    long-to-int v8, v0

    add-int/lit16 v8, v8, 0x8

    int-to-long v2, v8

    add-long v20, v2, v24

    invoke-virtual/range {v18 .. v21}, Lsun/misc/Unsafe;->getLong(Ljava/lang/Object;J)J

    move-result-wide v0

    invoke-static {v0, v1}, Ljava/lang/Double;->longBitsToDouble(J)D

    move-result-wide v35

    .line 7
    move-wide/from16 v0, v33

    invoke-static {v0, v1}, Ljava/lang/Double;->longBitsToDouble(J)D

    move-result-wide v0

    add-double v0, v0, v35

    invoke-static {v0, v1}, Ljava/lang/Double;->doubleToRawLongBits(D)J

    move-result-wide v0

    move-wide/from16 v26, v0

    .line 8
    move-wide/from16 v0, v26

    invoke-static {v0, v1}, Ljava/lang/Double;->longBitsToDouble(J)D

    move-result-wide v0

    return-wide v0

    const-wide/16 v0, 0x0

    return-wide v0

    .line 2
    :cond_a2
    move-object/from16 v12, v37

    const/4 v10, 0x1

    move-wide/from16 v0, v26

    long-to-int v9, v0

    int-to-long v2, v9

    const-wide v4, 0xffffffffL

    and-long/2addr v2, v4

    const-wide/16 v4, 0x0

    add-long/2addr v2, v4

    const/4 v11, 0x4

    invoke-static {v12, v10, v2, v3, v11}, Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;->memoryAccessOutOfBounds(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;IJI)Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    .line 3
    :cond_b9
    move-object/from16 v12, v37

    const/4 v10, 0x1

    move-wide/from16 v0, v26

    long-to-int v9, v0

    int-to-long v2, v9

    const-wide v4, 0xffffffffL

    and-long/2addr v2, v4

    const-wide/16 v4, 0x8

    add-long/2addr v2, v4

    const/16 v11, 0x8

    invoke-static {v12, v10, v2, v3, v11}, Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;->memoryAccessOutOfBounds(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;IJI)Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0
.end method

.method public static f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 31

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v8, p3

    int-to-long v0, v8

    move-wide/from16 v17, v0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v19, v0

    .line 2
    move-object/from16 v24, v14

    const/4 v9, 0x1

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    move-object/from16 v25, v2

    move-wide/from16 v0, v17

    long-to-int v8, v0

    move/from16 v26, v8

    invoke-static/range {v24 .. v26}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->memoryGrow(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;I)J

    move-result-wide v21

    .line 3
    const/4 v9, 0x1

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    move-object/from16 v24, v2

    invoke-static/range {v24 .. v24}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->memorySize(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;)J

    move-result-wide v17

    .line 4
    move-wide/from16 v0, v17

    long-to-int v8, v0

    return v8

    const/4 v8, 0x0

    return v8
.end method

.method public static f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I
    .registers 47

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    iget-object v8, v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->byteArray:[B

    move-object/from16 v19, v8

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;->getSize()I

    move-result v17

    const/4 v9, 0x2

    aget-object v18, v16, v9

    check-cast v18, Lsun/misc/Unsafe;

    const-wide/16 v24, 0x10

    const/4 v9, 0x1

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;

    move-object/from16 v35, v12

    move/from16 v26, p3

    move/from16 v27, p4

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v28, v0

    .line 2
    move-object/from16 v0, v35

    move/from16 v8, v27

    const/16 v9, 0x41

    move/from16 v10, v26

    invoke-static {v0, v10, v9, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;->memoryFill(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;III)V

    .line 3
    add-int/lit8 v30, v26, 0x3

    .line 4
    move-object/from16 v0, v35

    move-object/from16 v1, v35

    move/from16 v8, v27

    move/from16 v9, v26

    move/from16 v10, v30

    invoke-static {v0, v1, v9, v10, v8}, Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;->memoryCopy(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;III)V

    .line 5
    move-object/from16 v2, v35

    move-object/from16 v37, v2

    const/4 v9, 0x3

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/DataInstance;

    move-object/from16 v38, v2

    const/4 v8, 0x0

    move/from16 v39, v8

    move/from16 v8, v26

    move/from16 v40, v8

    const/4 v8, 0x5

    move/from16 v41, v8

    invoke-static/range {v37 .. v41}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->memoryInit(Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;Lio/github/charlietap/chasm/runtime/instance/DataInstance;III)V

    .line 6
    add-int/lit8 v30, v26, 0x2

    .line 7
    move/from16 v8, v30

    if-ltz v8, :cond_72

    add-int/lit8 v11, v17, -0x4

    if-gt v8, v11, :cond_72

    int-to-long v2, v8

    add-long v20, v2, v24

    invoke-virtual/range {v18 .. v21}, Lsun/misc/Unsafe;->getInt(Ljava/lang/Object;J)I

    move-result v26

    .line 8
    move/from16 v8, v26

    return v8

    const/4 v8, 0x0

    return v8

    .line 7
    :cond_72
    move-object/from16 v12, v35

    const/4 v10, 0x0

    move/from16 v9, v30

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

.method public static f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V
    .registers 25

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v17, v0

    .line 2
    const/4 v9, 0x3

    aget-object v2, v16, v9

    check-cast v2, Lio/github/charlietap/chasm/runtime/instance/DataInstance;

    move-object/from16 v20, v2

    invoke-static/range {v20 .. v20}, Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;->dataDrop(Lio/github/charlietap/chasm/runtime/instance/DataInstance;)V

    .line 3
    return-void

    return-void
.end method

.method public static f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I
    .registers 33

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    const/4 v9, 0x4

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/GlobalInstance;

    move-object/from16 v24, v12

    const/4 v9, 0x5

    aget-object v12, v16, v9

    check-cast v12, Lio/github/charlietap/chasm/runtime/instance/GlobalInstance;

    move-object/from16 v25, v12

    move/from16 v17, p3

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v18, v0

    .line 2
    move-object/from16 v12, v24

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/GlobalInstance;->getValue()J

    move-result-wide v0

    move-wide/from16 v20, v0

    .line 3
    move-wide/from16 v0, v20

    long-to-int v8, v0

    add-int v8, v8, v17

    int-to-long v0, v8

    move-wide/from16 v20, v0

    .line 4
    move-object/from16 v12, v24

    move-wide/from16 v0, v20

    invoke-virtual {v12, v0, v1}, Lio/github/charlietap/chasm/runtime/instance/GlobalInstance;->setValue(J)V

    .line 5
    move-object/from16 v12, v25

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/GlobalInstance;->getValue()J

    move-result-wide v0

    move-wide/from16 v20, v0

    .line 6
    const-wide/16 v2, 0x3

    mul-long v20, v20, v2

    .line 7
    move-object/from16 v12, v25

    move-wide/from16 v0, v20

    invoke-virtual {v12, v0, v1}, Lio/github/charlietap/chasm/runtime/instance/GlobalInstance;->setValue(J)V

    .line 8
    move-object/from16 v12, v24

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/GlobalInstance;->getValue()J

    move-result-wide v0

    move-wide/from16 v20, v0

    .line 9
    move-object/from16 v12, v25

    invoke-virtual {v12}, Lio/github/charlietap/chasm/runtime/instance/GlobalInstance;->getValue()J

    move-result-wide v0

    move-wide/from16 v22, v0

    .line 10
    move-wide/from16 v0, v22

    long-to-int v8, v0

    int-to-long v0, v8

    move-wide/from16 v22, v0

    .line 11
    move-wide/from16 v0, v20

    long-to-int v8, v0

    move-wide/from16 v2, v22

    long-to-int v9, v2

    add-int v17, v8, v9

    .line 12
    move/from16 v8, v17

    return v8

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

    invoke-static/range {v5 .. v10}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIJ)J

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

    invoke-static/range {v5 .. v10}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIJ)J

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
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v2

    goto/16 :goto_25

    :pswitch_1f
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

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
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

    move-result v2

    goto/16 :goto_2a

    :pswitch_24
    invoke-static/range {v5 .. v9}, Lchasm/jit/Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I

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
    .registers 16

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

    invoke-static {v2}, Ljava/lang/Float;->intBitsToFloat(I)F

    move-result v2

    move v9, v2

    const/4 v3, 0x2

    aget-wide v0, p2, v3

    invoke-static {v0, v1}, Ljava/lang/Double;->longBitsToDouble(J)D

    move-result-wide v0

    move-wide v10, v0

    invoke-static/range {v5 .. v11}, Lchasm/jit/Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIFD)D

    move-result-wide v0

    invoke-static {v0, v1}, Ljava/lang/Double;->doubleToRawLongBits(D)J

    move-result-wide v0

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_26
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_26} :catch_27

    return-void

    :catch_27
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void
.end method

.method public invoke(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;[JI)V
    .registers 16

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

    invoke-static {v2}, Ljava/lang/Float;->intBitsToFloat(I)F

    move-result v2

    move v9, v2

    add-int/lit16 v3, p3, 0x2

    aget-wide v0, p2, v3

    invoke-static {v0, v1}, Ljava/lang/Double;->longBitsToDouble(J)D

    move-result-wide v0

    move-wide v10, v0

    invoke-static/range {v5 .. v11}, Lchasm/jit/Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIFD)D

    move-result-wide v0

    invoke-static {v0, v1}, Ljava/lang/Double;->doubleToRawLongBits(D)J

    move-result-wide v0

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_2f
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_2f} :catch_30

    return-void

    :catch_30
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

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

    packed-switch v4, :pswitch_data_2a

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_14

    :goto_14
    :pswitch_14
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_20

    :pswitch_1a
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_20

    :goto_20
    int-to-long v0, v2

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_24
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_24} :catch_25

    return-void

    :catch_25
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    nop

    :pswitch_data_2a
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

    packed-switch v4, :pswitch_data_30

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_18

    :goto_18
    :pswitch_18
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_24

    :pswitch_1e
    invoke-static/range {v5 .. v8}, Lchasm/jit/Code0;->f6([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)I

    move-result v2

    goto/16 :goto_24

    :goto_24
    int-to-long v0, v2

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_2b
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_2b} :catch_2c

    return-void

    :catch_2c
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_30
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
    .registers 12

    :try_start_0
    move-object v2, p0

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p3

    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V
    :try_end_8
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_8} :catch_9

    return-void

    :catch_9
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

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

    invoke-static/range {v5 .. v7}, Lchasm/jit/Code0;->f5([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V
    :try_end_b
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_b} :catch_c

    return-void

    :catch_c
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

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;->unsafe()Ljava/lang/Object;

    move-result-object v4

    const/4 v2, 0x2

    aput-object v4, p1, v2

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->data(I)Lio/github/charlietap/chasm/runtime/instance/DataInstance;

    move-result-object v4

    const/4 v2, 0x3

    aput-object v4, p1, v2

    const/4 v2, 0x0

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->global(I)Lio/github/charlietap/chasm/runtime/instance/GlobalInstance;

    move-result-object v4

    const/4 v2, 0x4

    aput-object v4, p1, v2

    const/4 v2, 0x1

    invoke-virtual {p0, v2}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->global(I)Lio/github/charlietap/chasm/runtime/instance/GlobalInstance;

    move-result-object v4

    const/4 v2, 0x5

    aput-object v4, p1, v2

    return-void
.end method


# virtual methods
.method public accept(Ljava/lang/Object;)V
    .registers 5

    check-cast p1, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;

    const/4 v0, 0x6

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

    new-instance v4, Lchasm/jit/Entry2;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry2;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x2

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry3;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry3;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x3

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry1;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry1;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x4

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry4;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry4;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x5

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry3;

    const/4 v3, 0x1

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry3;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x6

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    return-void
.end method
