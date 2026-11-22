.class public final Lchasm/gc/Ac0286ece071e69880d9c42db8f326591_0;
.super Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedArray;


# instance fields
.field public data:[Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;


# direct methods
.method public constructor <init>([Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;)V
    .registers 3

    invoke-direct {p0}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedArray;-><init>()V

    iput-object p1, p0, Lchasm/gc/Ac0286ece071e69880d9c42db8f326591_0;->data:[Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;

    return-void
.end method


# virtual methods
.method public engine()Lio/github/charlietap/chasm/embedding/engine/Engine;
    .registers 2

    sget-object v0, Lchasm/gc/ShapeOwner;->engine:Ljava/lang/ref/WeakReference;

    invoke-virtual {v0}, Ljava/lang/ref/WeakReference;->get()Ljava/lang/Object;

    move-result-object v0

    if-eqz v0, :cond_b

    check-cast v0, Lio/github/charlietap/chasm/embedding/engine/Engine;

    return-object v0

    :cond_b
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->ownerCollectedError()Ljava/lang/IllegalStateException;

    move-result-object v0

    throw v0
.end method

.method public length()I
    .registers 2

    iget-object v0, p0, Lchasm/gc/Ac0286ece071e69880d9c42db8f326591_0;->data:[Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;

    array-length v0, v0

    return v0
.end method

.method public storage()Ljava/lang/Object;
    .registers 2

    iget-object v0, p0, Lchasm/gc/Ac0286ece071e69880d9c42db8f326591_0;->data:[Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;

    return-object v0
.end method

.class public final Lchasm/gc/S4b42c662b0be7f667e4eec6ebd3e0627_0;
.super Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedStruct;


# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedStruct;-><init>()V

    return-void
.end method


# virtual methods
.method public engine()Lio/github/charlietap/chasm/embedding/engine/Engine;
    .registers 2

    sget-object v0, Lchasm/gc/ShapeOwner;->engine:Ljava/lang/ref/WeakReference;

    invoke-virtual {v0}, Ljava/lang/ref/WeakReference;->get()Ljava/lang/Object;

    move-result-object v0

    if-eqz v0, :cond_b

    check-cast v0, Lio/github/charlietap/chasm/embedding/engine/Engine;

    return-object v0

    :cond_b
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->ownerCollectedError()Ljava/lang/IllegalStateException;

    move-result-object v0

    throw v0
.end method

.class public Lchasm/gc/S61c363bddcc3f2bdf003efba6c189cdb_0;
.super Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedStruct;


# instance fields
.field public f0:I


# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedStruct;-><init>()V

    return-void
.end method


# virtual methods
.method public engine()Lio/github/charlietap/chasm/embedding/engine/Engine;
    .registers 2

    sget-object v0, Lchasm/gc/ShapeOwner;->engine:Ljava/lang/ref/WeakReference;

    invoke-virtual {v0}, Ljava/lang/ref/WeakReference;->get()Ljava/lang/Object;

    move-result-object v0

    if-eqz v0, :cond_b

    check-cast v0, Lio/github/charlietap/chasm/embedding/engine/Engine;

    return-object v0

    :cond_b
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->ownerCollectedError()Ljava/lang/IllegalStateException;

    move-result-object v0

    throw v0
.end method

.class public final Lchasm/gc/S9f72d2687ac2d87deef22760eae82777_0;
.super Lchasm/gc/S61c363bddcc3f2bdf003efba6c189cdb_0;


# instance fields
.field public f1:F

.field public f2:Ljava/lang/Object;


# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Lchasm/gc/S61c363bddcc3f2bdf003efba6c189cdb_0;-><init>()V

    return-void
.end method

.class public final Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;
.super Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedStruct;


# instance fields
.field public f0:I

.field public f1:J

.field public f2:F

.field public f3:D

.field public f4:B

.field public f5:C

.field public f6:Ljava/lang/Object;

.field public f7:I

.field public f8:Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;


# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedStruct;-><init>()V

    return-void
.end method


# virtual methods
.method public engine()Lio/github/charlietap/chasm/embedding/engine/Engine;
    .registers 2

    sget-object v0, Lchasm/gc/ShapeOwner;->engine:Ljava/lang/ref/WeakReference;

    invoke-virtual {v0}, Ljava/lang/ref/WeakReference;->get()Ljava/lang/Object;

    move-result-object v0

    if-eqz v0, :cond_b

    check-cast v0, Lio/github/charlietap/chasm/embedding/engine/Engine;

    return-object v0

    :cond_b
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->ownerCollectedError()Ljava/lang/IllegalStateException;

    move-result-object v0

    throw v0
.end method

.class public final Lchasm/gc/ShapeOwner;
.super Ljava/lang/Object;


# static fields
.field public static engine:Ljava/lang/ref/WeakReference;
