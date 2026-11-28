.class public final Lchasm/dex/WasmModule$Code0;
.super Ljava/lang/Object;
.source "module.wasm"


# direct methods
.method public static f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)[J
    .registers 35

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move-wide/from16 v17, p3

    move-wide/from16 v19, p5

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

    move-wide/from16 v21, v4

    move-wide/from16 v23, v0

    .line 3
    const/4 v8, 0x2

    new-array v12, v8, [J

    move-wide/from16 v0, v21

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move-wide/from16 v0, v23

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    return-object v12

    const/4 v8, 0x0

    return-object v8
.end method

.method public static f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)[J
    .registers 35

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move-wide/from16 v17, p3

    move-wide/from16 v19, p5

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

    move-wide/from16 v21, v4

    move-wide/from16 v23, v0

    .line 3
    const/4 v8, 0x2

    new-array v12, v8, [J

    move-wide/from16 v0, v21

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move-wide/from16 v0, v23

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    return-object v12

    const/4 v8, 0x0

    return-object v8
.end method

.method public static f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)[J
    .registers 35

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move-wide/from16 v17, p3

    move-wide/from16 v19, p5

    .line 2
    move-wide/from16 v0, v17

    move-wide/from16 v2, v19

    mul-long v4, v0, v2

    invoke-static {v0, v1, v2, v3}, Lio/github/charlietap/chasm/runtime/compiled/CompiledNumerics;->i64MulWideHighS(JJ)J

    move-result-wide v0

    move-wide/from16 v21, v4

    move-wide/from16 v23, v0

    .line 3
    const/4 v8, 0x2

    new-array v12, v8, [J

    move-wide/from16 v0, v21

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move-wide/from16 v0, v23

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    return-object v12

    const/4 v8, 0x0

    return-object v8
.end method

.method public static f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)[J
    .registers 35

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move-wide/from16 v17, p3

    move-wide/from16 v19, p5

    .line 2
    move-wide/from16 v0, v17

    move-wide/from16 v2, v19

    mul-long v4, v0, v2

    invoke-static {v0, v1, v2, v3}, Lio/github/charlietap/chasm/runtime/compiled/CompiledNumerics;->i64MulWideHighU(JJ)J

    move-result-wide v0

    move-wide/from16 v21, v4

    move-wide/from16 v23, v0

    .line 3
    const/4 v8, 0x2

    new-array v12, v8, [J

    move-wide/from16 v0, v21

    const/4 v10, 0x0

    aput-wide v0, v12, v10

    move-wide/from16 v0, v23

    const/4 v10, 0x1

    aput-wide v0, v12, v10

    return-object v12

    const/4 v8, 0x0

    return-object v8
.end method

.method public static f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)J
    .registers 39

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move-wide/from16 v17, p3

    move-wide/from16 v19, p5

    const-wide/16 v0, 0x0

    const/4 v15, 0x0

    move-wide/from16 v21, v0

    move-wide/from16 v23, v0

    .line 2
    move-wide/from16 v0, v17

    move-wide/from16 v2, v19

    mul-long v4, v0, v2

    invoke-static {v0, v1, v2, v3}, Lio/github/charlietap/chasm/runtime/compiled/CompiledNumerics;->i64MulWideHighU(JJ)J

    move-result-wide v0

    move-wide/from16 v25, v4

    move-wide/from16 v27, v0

    .line 3
    move-wide/from16 v19, v27

    move-wide/from16 v17, v25

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

    move-wide/from16 v25, v4

    move-wide/from16 v27, v0

    .line 5
    xor-long v17, v25, v27

    .line 6
    move-wide/from16 v0, v17

    return-wide v0

    const-wide/16 v0, 0x0

    return-wide v0
.end method

.class final Lchasm/dex/WasmModule$Declare0;
.super Ljava/lang/Object;


# direct methods
.method static declare(Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;)V
    .registers 12

    move-object v0, p0

    const-string v1, "add128sub128mul_wide_smul_wide_uchain"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->strings(Ljava/lang/String;)V

    const-string v1, "\u0006\u0006\n\n\u0005"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->stringLengths(Ljava/lang/String;)V

    invoke-virtual/range {v0 .. v0}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->endStrings()V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->group(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->group(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x1

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->numberType(I)V

    const/4 v1, 0x2

    const/4 v2, 0x2

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functionComposite(II)V

    const/4 v1, 0x1

    const/4 v2, 0x0

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->subType(ZI)V

    const/4 v1, 0x0

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x0

    const-string v2, "4517b5814f1fe280b841e0a436838b90"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const/4 v1, 0x1

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

    const/4 v1, 0x1

    const/4 v2, 0x1

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupBody(II)V

    const/4 v1, 0x1

    const-string v2, "908385d9121956062a8797f8d6580f8f"

    invoke-virtual/range {v0 .. v2}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->groupKey(ILjava/lang/String;)V

    const-string v1, "\u0000\u0000\u0000\u0000\u0001"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->functions(Ljava/lang/String;)V

    const-string v1, "\u0000\u0000\u0000\u0000\u0000"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->exportKinds(Ljava/lang/String;)V

    const-string v1, "\u0000\u0001\u0002\u0003\u0004"

    invoke-virtual/range {v0 .. v1}, Lio/github/charlietap/chasm/declarations/construction/DeclarationsBuilder;->exportIndices(Ljava/lang/String;)V

    const-string v1, "\u0000\u0001\u0002\u0003\u0004"

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
    .registers 17

    :try_start_0
    move-object v2, p0

    iget v4, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->id:I

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move/from16 v7, p4

    const/4 v3, 0x0

    aget-wide v8, p2, v3

    const/4 v3, 0x1

    aget-wide v10, p2, v3

    packed-switch v4, :pswitch_data_44

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    goto/16 :goto_16

    :goto_16
    :pswitch_16
    invoke-static/range {v5 .. v11}, Lchasm/dex/WasmModule$Code0;->f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)[J

    move-result-object v2

    goto/16 :goto_2e

    :pswitch_1c
    invoke-static/range {v5 .. v11}, Lchasm/dex/WasmModule$Code0;->f1([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)[J

    move-result-object v2

    goto/16 :goto_2e

    :pswitch_22
    invoke-static/range {v5 .. v11}, Lchasm/dex/WasmModule$Code0;->f2([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)[J

    move-result-object v2

    goto/16 :goto_2e

    :pswitch_28
    invoke-static/range {v5 .. v11}, Lchasm/dex/WasmModule$Code0;->f3([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)[J

    move-result-object v2

    goto/16 :goto_2e

    :goto_2e
    if-eqz v2, :cond_3a

    const/4 v3, 0x0

    aget-wide v0, v2, v3

    aput-wide v0, p2, v3

    const/4 v3, 0x1

    aget-wide v0, v2, v3

    aput-wide v0, p2, v3
    :try_end_3a
    .catch Ljava/lang/StackOverflowError; {:try_start_0 .. :try_end_3a} :catch_3b
    .catch Ljava/lang/OutOfMemoryError; {:try_start_0 .. :try_end_3a} :catch_3f

    :cond_3a
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
        :pswitch_16
        :pswitch_1c
        :pswitch_22
        :pswitch_28
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
    .registers 17

    :try_start_0
    move-object v2, p0

    iget-object v5, v2, Lio/github/charlietap/chasm/runtime/compiled/BindingEntry;->binding:[Ljava/lang/Object;

    move-object v6, p1

    move/from16 v7, p4

    const/4 v3, 0x0

    aget-wide v8, p2, v3

    const/4 v3, 0x1

    aget-wide v10, p2, v3

    invoke-static/range {v5 .. v11}, Lchasm/dex/WasmModule$Code0;->f4([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IJJ)J

    move-result-wide v0

    const/4 v3, 0x0

    aput-wide v0, p2, v3
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

.class final Lchasm/dex/WasmModule$Install;
.super Ljava/lang/Object;

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

    new-instance v4, Lchasm/dex/WasmModule$Entry0;

    const/4 v3, 0x2

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry0;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x2

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry0;

    const/4 v3, 0x3

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry0;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x3

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    new-instance v4, Lchasm/dex/WasmModule$Entry1;

    const/4 v3, 0x0

    invoke-direct {v4, p1, v3}, Lchasm/dex/WasmModule$Entry1;-><init>([Ljava/lang/Object;I)V

    const/4 v2, 0x4

    invoke-virtual {p0, v2, v4}, Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;->install(ILio/github/charlietap/chasm/runtime/function/CompiledFunction;)V

    return-void
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

    const-string v1, "chasm.dex.WasmModule$InstallEntries0"

    aput-object v1, p0, v0

    const/4 v0, 0x4

    const-string v1, "chasm.dex.WasmModule$Install"

    aput-object v1, p0, v0

    const/4 v0, 0x5

    const-string v1, "chasm.dex.WasmModule$Declare0"

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

    const/4 v0, 0x6

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

    const/4 v0, 0x1

    new-array v1, v0, [Ljava/lang/Object;

    const/4 v0, 0x0

    new-array v2, v0, [J

    const/4 v0, 0x0

    aput-object v2, v1, v0

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

    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;->unreachable()Ljava/lang/Void;

    const/4 v0, 0x0

    return-object v0
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

    const/4 v0, 0x0

    new-array v1, v0, [J

    return-object v1
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
