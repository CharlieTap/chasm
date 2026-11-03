.class public final Lchasm/jit/Code0;
.super Ljava/lang/Object;
.source "module.wasm"


# direct methods
.method public static f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)[J
    .registers 41

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x13

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_11

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return-object v8

    :cond_11
    move v13, v8

    move-wide/from16 v17, p3

    move-wide/from16 v19, p5

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v21, v0

    .line 2
    move-wide/from16 v0, v17

    move-wide/from16 v2, v19

    add-long v4, v0, v2

    const-wide/high16 v6, -0x8000000000000000L

    xor-long v2, v4, v6

    xor-long/2addr v0, v6

    cmp-long v8, v2, v0

    ushr-int/lit8 v8, v8, 0x1f

    int-to-long v2, v8

    move-wide/from16 v0, v19

    move-wide/from16 v6, v17

    add-long/2addr v0, v6

    add-long/2addr v0, v2

    move-wide/from16 v23, v4

    move-wide/from16 v25, v0

    .line 3
    const/4 v8, 0x2

    new-array v12, v8, [J

    move-wide/from16 v0, v23

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move-wide/from16 v0, v25

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    return-object v12

    const/4 v8, 0x0

    return-object v8
.end method

.method public static f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)[J
    .registers 41

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x13

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_11

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return-object v8

    :cond_11
    move v13, v8

    move-wide/from16 v17, p3

    move-wide/from16 v19, p5

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v21, v0

    .line 2
    move-wide/from16 v0, v17

    move-wide/from16 v2, v19

    sub-long v4, v0, v2

    const-wide/high16 v6, -0x8000000000000000L

    xor-long/2addr v0, v6

    xor-long/2addr v2, v6

    move-wide v6, v0

    move-wide v0, v2

    move-wide v2, v6

    cmp-long v8, v2, v0

    ushr-int/lit8 v8, v8, 0x1f

    int-to-long v2, v8

    move-wide/from16 v0, v19

    move-wide/from16 v6, v17

    sub-long/2addr v0, v6

    sub-long/2addr v0, v2

    move-wide/from16 v23, v4

    move-wide/from16 v25, v0

    .line 3
    const/4 v8, 0x2

    new-array v12, v8, [J

    move-wide/from16 v0, v23

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move-wide/from16 v0, v25

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    return-object v12

    const/4 v8, 0x0

    return-object v8
.end method

.method public static f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)[J
    .registers 37

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x12

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_11

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return-object v8

    :cond_11
    move v13, v8

    move-wide/from16 v17, p3

    move-wide/from16 v19, p5

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v21, v0

    .line 2
    move-wide/from16 v0, v17

    move-wide/from16 v2, v19

    mul-long v4, v0, v2

    invoke-static {v0, v1, v2, v3}, Lio/github/charlietap/chasm/runtime/compiled/CompiledNumerics;->i64MulWideHighS(JJ)J

    move-result-wide v0

    move-wide/from16 v23, v4

    move-wide/from16 v25, v0

    .line 3
    const/4 v8, 0x2

    new-array v12, v8, [J

    move-wide/from16 v0, v23

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move-wide/from16 v0, v25

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    return-object v12

    const/4 v8, 0x0

    return-object v8
.end method

.method public static f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)[J
    .registers 37

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x12

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_11

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const/4 v8, 0x0

    return-object v8

    :cond_11
    move v13, v8

    move-wide/from16 v17, p3

    move-wide/from16 v19, p5

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v21, v0

    .line 2
    move-wide/from16 v0, v17

    move-wide/from16 v2, v19

    mul-long v4, v0, v2

    invoke-static {v0, v1, v2, v3}, Lio/github/charlietap/chasm/runtime/compiled/CompiledNumerics;->i64MulWideHighU(JJ)J

    move-result-wide v0

    move-wide/from16 v23, v4

    move-wide/from16 v25, v0

    .line 3
    const/4 v8, 0x2

    new-array v12, v8, [J

    move-wide/from16 v0, v23

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move-wide/from16 v0, v25

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    return-object v12

    const/4 v8, 0x0

    return-object v8
.end method

.method public static f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)J
    .registers 45

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    add-int/lit16 v8, v13, 0x14

    iget v9, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->compiledStackLimit:I

    if-le v8, v9, :cond_12

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    const-wide/16 v0, 0x0

    return-wide v0

    :cond_12
    move v13, v8

    move-wide/from16 v17, p3

    move-wide/from16 v19, p5

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v21, v0

    move-wide/from16 v23, v0

    move-wide/from16 v25, v0

    .line 2
    move-wide/from16 v0, v17

    move-wide/from16 v2, v19

    mul-long v4, v0, v2

    invoke-static {v0, v1, v2, v3}, Lio/github/charlietap/chasm/runtime/compiled/CompiledNumerics;->i64MulWideHighU(JJ)J

    move-result-wide v0

    move-wide/from16 v27, v4

    move-wide/from16 v29, v0

    .line 3
    move-wide/from16 v19, v29

    move-wide/from16 v17, v27

    .line 4
    move-wide/from16 v0, v17

    const-wide/16 v2, 0x5

    add-long v4, v0, v2

    const-wide/high16 v6, -0x8000000000000000L

    xor-long v2, v4, v6

    xor-long/2addr v0, v6

    cmp-long v8, v2, v0

    ushr-int/lit8 v8, v8, 0x1f

    int-to-long v2, v8

    move-wide/from16 v0, v19

    const-wide/16 v6, -0x1

    add-long/2addr v0, v6

    add-long/2addr v0, v2

    move-wide/from16 v27, v4

    move-wide/from16 v29, v0

    .line 5
    xor-long v17, v27, v29

    .line 6
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

    packed-switch v4, :pswitch_data_3e

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_15

    :goto_15
    :pswitch_15
    invoke-static/range {v5 .. v11}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)[J

    move-result-object v2

    goto/16 :goto_2d

    :pswitch_1b
    invoke-static/range {v5 .. v11}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)[J

    move-result-object v2

    goto/16 :goto_2d

    :pswitch_21
    invoke-static/range {v5 .. v11}, Lchasm/jit/Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)[J

    move-result-object v2

    goto/16 :goto_2d

    :pswitch_27
    invoke-static/range {v5 .. v11}, Lchasm/jit/Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)[J

    move-result-object v2

    goto/16 :goto_2d

    :goto_2d
    if-eqz v2, :cond_39

    const/4 v3, 0x0

    aget-wide v0, v2, v3

    aput-wide v0, p2, v3

    const/4 v3, 0x1

    aget-wide v0, v2, v3

    aput-wide v0, p2, v3
    :try_end_39
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_39} :catch_3a

    :cond_39
    return-void

    :catch_3a
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    :pswitch_data_3e
    .packed-switch 0x0
        :pswitch_15
        :pswitch_1b
        :pswitch_21
        :pswitch_27
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

    packed-switch v4, :pswitch_data_4e

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_1a

    :goto_1a
    :pswitch_1a
    invoke-static/range {v5 .. v11}, Lchasm/jit/Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)[J

    move-result-object v2

    goto/16 :goto_32

    :pswitch_20
    invoke-static/range {v5 .. v11}, Lchasm/jit/Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)[J

    move-result-object v2

    goto/16 :goto_32

    :pswitch_26
    invoke-static/range {v5 .. v11}, Lchasm/jit/Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)[J

    move-result-object v2

    goto/16 :goto_32

    :pswitch_2c
    invoke-static/range {v5 .. v11}, Lchasm/jit/Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)[J

    move-result-object v2

    goto/16 :goto_32

    :goto_32
    if-eqz v2, :cond_48

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
    :try_end_48
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_48} :catch_49

    :cond_48
    return-void

    :catch_49
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->callStackExhausted()Ljava/lang/Void;

    return-void

    nop

    :pswitch_data_4e
    .packed-switch 0x0
        :pswitch_1a
        :pswitch_20
        :pswitch_26
        :pswitch_2c
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
    .registers 16

    :try_start_0
    move-object v2, p0

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move v7, p3

    const/4 v3, 0x0

    aget-wide v8, p2, v3

    const/4 v3, 0x1

    aget-wide v10, p2, v3

    invoke-static/range {v5 .. v11}, Lchasm/jit/Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)J

    move-result-wide v0

    const/4 v3, 0x0

    aput-wide v0, p2, v3
    :try_end_12
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_12} :catch_13

    return-void

    :catch_13
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

    aget-wide v8, p2, v3

    add-int/lit16 v3, p3, 0x1

    aget-wide v10, p2, v3

    invoke-static/range {v5 .. v11}, Lchasm/jit/Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)J

    move-result-wide v0

    invoke-static {p1}, Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;->frame(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;)[J

    move-result-object v5

    aput-wide v0, v5, p3
    :try_end_1a
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_1a} :catch_1b

    return-void

    :catch_1b
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


# virtual methods
.method public accept(Ljava/lang/Object;)V
    .registers 5

    check-cast p1, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;

    const/4 v0, 0x1

    new-array v1, v0, [Ljava/lang/Object;

    const/4 v0, 0x0

    new-array v2, v0, [J

    const/4 v0, 0x0

    aput-object v2, v1, v0

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

    new-instance v4, Lchasm/jit/Entry0;

    const/4 v3, 0x2

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry0;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x2

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry0;

    const/4 v3, 0x3

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry0;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x3

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/jit/Entry1;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/jit/Entry1;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x4

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    return-void
.end method
