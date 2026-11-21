.class public final Lchasm/gc/Ac0286ece071e69880d9c42db8f326591_0$Accessor;
.super Ljava/lang/Object;

# interfaces
.implements Lio/github/charlietap/chasm/runtime/compiled/gc/ArrayAccessor;


# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public newArray(Ljava/lang/Object;)Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedArray;
    .registers 3

    check-cast p1, [Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;

    new-instance v0, Lchasm/gc/Ac0286ece071e69880d9c42db8f326591_0;

    invoke-direct {v0, p1}, Lchasm/gc/Ac0286ece071e69880d9c42db8f326591_0;-><init>([Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;)V

    return-object v0
.end method

.method public newStorage(I)Ljava/lang/Object;
    .registers 3

    new-array v0, p1, [Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;

    return-object v0
.end method

.class public final Lchasm/gc/S4b42c662b0be7f667e4eec6ebd3e0627_0$Accessor;
.super Ljava/lang/Object;

# interfaces
.implements Lio/github/charlietap/chasm/runtime/compiled/gc/StructAccessor;


# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public newStruct()Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedStruct;
    .registers 2

    new-instance v0, Lchasm/gc/S4b42c662b0be7f667e4eec6ebd3e0627_0;

    invoke-direct {v0}, Lchasm/gc/S4b42c662b0be7f667e4eec6ebd3e0627_0;-><init>()V

    return-object v0
.end method

.method public readBits(Ljava/lang/Object;I)J
    .registers 5

    check-cast p1, Lchasm/gc/S4b42c662b0be7f667e4eec6ebd3e0627_0;

    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->fieldIndexError(I)Ljava/lang/IndexOutOfBoundsException;

    move-result-object v0

    throw v0
.end method

.method public readReference(Ljava/lang/Object;I)Ljava/lang/Object;
    .registers 4

    check-cast p1, Lchasm/gc/S4b42c662b0be7f667e4eec6ebd3e0627_0;

    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->fieldIndexError(I)Ljava/lang/IndexOutOfBoundsException;

    move-result-object v0

    throw v0
.end method

.method public writeBits(Ljava/lang/Object;IJ)V
    .registers 7

    check-cast p1, Lchasm/gc/S4b42c662b0be7f667e4eec6ebd3e0627_0;

    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->fieldIndexError(I)Ljava/lang/IndexOutOfBoundsException;

    move-result-object v0

    throw v0
.end method

.method public writeReference(Ljava/lang/Object;ILjava/lang/Object;)V
    .registers 5

    check-cast p1, Lchasm/gc/S4b42c662b0be7f667e4eec6ebd3e0627_0;

    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->fieldIndexError(I)Ljava/lang/IndexOutOfBoundsException;

    move-result-object v0

    throw v0
.end method

.class public final Lchasm/gc/S61c363bddcc3f2bdf003efba6c189cdb_0$Accessor;
.super Ljava/lang/Object;

# interfaces
.implements Lio/github/charlietap/chasm/runtime/compiled/gc/StructAccessor;


# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public newStruct()Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedStruct;
    .registers 2

    new-instance v0, Lchasm/gc/S61c363bddcc3f2bdf003efba6c189cdb_0;

    invoke-direct {v0}, Lchasm/gc/S61c363bddcc3f2bdf003efba6c189cdb_0;-><init>()V

    return-object v0
.end method

.method public readBits(Ljava/lang/Object;I)J
    .registers 5

    check-cast p1, Lchasm/gc/S61c363bddcc3f2bdf003efba6c189cdb_0;

    packed-switch p2, :pswitch_data_e

    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->fieldIndexError(I)Ljava/lang/IndexOutOfBoundsException;

    move-result-object v0

    throw v0

    :pswitch_a
    iget v0, p1, Lchasm/gc/S61c363bddcc3f2bdf003efba6c189cdb_0;->f0:I

    int-to-long v0, v0

    return-wide v0

    :pswitch_data_e
    .packed-switch 0x0
        :pswitch_a
    .end packed-switch
.end method

.method public readReference(Ljava/lang/Object;I)Ljava/lang/Object;
    .registers 4

    check-cast p1, Lchasm/gc/S61c363bddcc3f2bdf003efba6c189cdb_0;

    packed-switch p2, :pswitch_data_10

    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->fieldIndexError(I)Ljava/lang/IndexOutOfBoundsException;

    move-result-object v0

    throw v0

    :pswitch_a
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->numberFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    nop

    :pswitch_data_10
    .packed-switch 0x0
        :pswitch_a
    .end packed-switch
.end method

.method public writeBits(Ljava/lang/Object;IJ)V
    .registers 7

    check-cast p1, Lchasm/gc/S61c363bddcc3f2bdf003efba6c189cdb_0;

    packed-switch p2, :pswitch_data_e

    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->fieldIndexError(I)Ljava/lang/IndexOutOfBoundsException;

    move-result-object v0

    throw v0

    :pswitch_a
    long-to-int v0, p3

    iput v0, p1, Lchasm/gc/S61c363bddcc3f2bdf003efba6c189cdb_0;->f0:I

    return-void

    :pswitch_data_e
    .packed-switch 0x0
        :pswitch_a
    .end packed-switch
.end method

.method public writeReference(Ljava/lang/Object;ILjava/lang/Object;)V
    .registers 5

    check-cast p1, Lchasm/gc/S61c363bddcc3f2bdf003efba6c189cdb_0;

    packed-switch p2, :pswitch_data_10

    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->fieldIndexError(I)Ljava/lang/IndexOutOfBoundsException;

    move-result-object v0

    throw v0

    :pswitch_a
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->numberFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    nop

    :pswitch_data_10
    .packed-switch 0x0
        :pswitch_a
    .end packed-switch
.end method

.class public final Lchasm/gc/S9f72d2687ac2d87deef22760eae82777_0$Accessor;
.super Ljava/lang/Object;

# interfaces
.implements Lio/github/charlietap/chasm/runtime/compiled/gc/StructAccessor;


# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public newStruct()Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedStruct;
    .registers 2

    new-instance v0, Lchasm/gc/S9f72d2687ac2d87deef22760eae82777_0;

    invoke-direct {v0}, Lchasm/gc/S9f72d2687ac2d87deef22760eae82777_0;-><init>()V

    return-object v0
.end method

.method public readBits(Ljava/lang/Object;I)J
    .registers 5

    check-cast p1, Lchasm/gc/S9f72d2687ac2d87deef22760eae82777_0;

    packed-switch p2, :pswitch_data_1c

    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->fieldIndexError(I)Ljava/lang/IndexOutOfBoundsException;

    move-result-object v0

    throw v0

    :pswitch_a
    iget v0, p1, Lchasm/gc/S61c363bddcc3f2bdf003efba6c189cdb_0;->f0:I

    int-to-long v0, v0

    return-wide v0

    :pswitch_e
    iget v0, p1, Lchasm/gc/S9f72d2687ac2d87deef22760eae82777_0;->f1:F

    invoke-static {v0}, Ljava/lang/Float;->floatToRawIntBits(F)I

    move-result v0

    int-to-long v0, v0

    return-wide v0

    :pswitch_16
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->referenceFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    nop

    :pswitch_data_1c
    .packed-switch 0x0
        :pswitch_a
        :pswitch_e
        :pswitch_16
    .end packed-switch
.end method

.method public readReference(Ljava/lang/Object;I)Ljava/lang/Object;
    .registers 4

    check-cast p1, Lchasm/gc/S9f72d2687ac2d87deef22760eae82777_0;

    packed-switch p2, :pswitch_data_18

    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->fieldIndexError(I)Ljava/lang/IndexOutOfBoundsException;

    move-result-object v0

    throw v0

    :pswitch_a
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->numberFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_f
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->numberFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_14
    iget-object v0, p1, Lchasm/gc/S9f72d2687ac2d87deef22760eae82777_0;->f2:Ljava/lang/Object;

    return-object v0

    nop

    :pswitch_data_18
    .packed-switch 0x0
        :pswitch_a
        :pswitch_f
        :pswitch_14
    .end packed-switch
.end method

.method public writeBits(Ljava/lang/Object;IJ)V
    .registers 7

    check-cast p1, Lchasm/gc/S9f72d2687ac2d87deef22760eae82777_0;

    packed-switch p2, :pswitch_data_1c

    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->fieldIndexError(I)Ljava/lang/IndexOutOfBoundsException;

    move-result-object v0

    throw v0

    :pswitch_a
    long-to-int v0, p3

    iput v0, p1, Lchasm/gc/S61c363bddcc3f2bdf003efba6c189cdb_0;->f0:I

    return-void

    :pswitch_e
    long-to-int v0, p3

    invoke-static {v0}, Ljava/lang/Float;->intBitsToFloat(I)F

    move-result v0

    iput v0, p1, Lchasm/gc/S9f72d2687ac2d87deef22760eae82777_0;->f1:F

    return-void

    :pswitch_16
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->referenceFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    nop

    :pswitch_data_1c
    .packed-switch 0x0
        :pswitch_a
        :pswitch_e
        :pswitch_16
    .end packed-switch
.end method

.method public writeReference(Ljava/lang/Object;ILjava/lang/Object;)V
    .registers 5

    check-cast p1, Lchasm/gc/S9f72d2687ac2d87deef22760eae82777_0;

    packed-switch p2, :pswitch_data_1a

    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->fieldIndexError(I)Ljava/lang/IndexOutOfBoundsException;

    move-result-object v0

    throw v0

    :pswitch_a
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->numberFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_f
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->numberFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_14
    move-object v0, p3

    check-cast v0, Ljava/lang/Object;

    iput-object v0, p1, Lchasm/gc/S9f72d2687ac2d87deef22760eae82777_0;->f2:Ljava/lang/Object;

    return-void

    :pswitch_data_1a
    .packed-switch 0x0
        :pswitch_a
        :pswitch_f
        :pswitch_14
    .end packed-switch
.end method

.class public final Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0$Accessor;
.super Ljava/lang/Object;

# interfaces
.implements Lio/github/charlietap/chasm/runtime/compiled/gc/StructAccessor;


# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public newStruct()Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedStruct;
    .registers 2

    new-instance v0, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;

    invoke-direct {v0}, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;-><init>()V

    return-object v0
.end method

.method public readBits(Ljava/lang/Object;I)J
    .registers 5

    check-cast p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;

    packed-switch p2, :pswitch_data_38

    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->fieldIndexError(I)Ljava/lang/IndexOutOfBoundsException;

    move-result-object v0

    throw v0

    :pswitch_a
    iget v0, p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;->f0:I

    int-to-long v0, v0

    return-wide v0

    :pswitch_e
    iget-wide v0, p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;->f1:J

    return-wide v0

    :pswitch_11
    iget v0, p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;->f2:F

    invoke-static {v0}, Ljava/lang/Float;->floatToRawIntBits(F)I

    move-result v0

    int-to-long v0, v0

    return-wide v0

    :pswitch_19
    iget-wide v0, p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;->f3:D

    invoke-static {v0, v1}, Ljava/lang/Double;->doubleToRawLongBits(D)J

    move-result-wide v0

    return-wide v0

    :pswitch_20
    iget-byte v0, p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;->f4:B

    and-int/lit16 v0, v0, 0xff

    int-to-long v0, v0

    return-wide v0

    :pswitch_26
    iget-char v0, p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;->f5:C

    int-to-long v0, v0

    return-wide v0

    :pswitch_2a
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->referenceFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_2f
    iget v0, p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;->f7:I

    int-to-long v0, v0

    return-wide v0

    :pswitch_33
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->referenceFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_data_38
    .packed-switch 0x0
        :pswitch_a
        :pswitch_e
        :pswitch_11
        :pswitch_19
        :pswitch_20
        :pswitch_26
        :pswitch_2a
        :pswitch_2f
        :pswitch_33
    .end packed-switch
.end method

.method public readReference(Ljava/lang/Object;I)Ljava/lang/Object;
    .registers 4

    check-cast p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;

    packed-switch p2, :pswitch_data_34

    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->fieldIndexError(I)Ljava/lang/IndexOutOfBoundsException;

    move-result-object v0

    throw v0

    :pswitch_a
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->numberFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_f
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->numberFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_14
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->numberFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_19
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->numberFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_1e
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->numberFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_23
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->numberFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_28
    iget-object v0, p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;->f6:Ljava/lang/Object;

    return-object v0

    :pswitch_2b
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->numberFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_30
    iget-object v0, p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;->f8:Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;

    return-object v0

    nop

    :pswitch_data_34
    .packed-switch 0x0
        :pswitch_a
        :pswitch_f
        :pswitch_14
        :pswitch_19
        :pswitch_1e
        :pswitch_23
        :pswitch_28
        :pswitch_2b
        :pswitch_30
    .end packed-switch
.end method

.method public writeBits(Ljava/lang/Object;IJ)V
    .registers 7

    check-cast p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;

    packed-switch p2, :pswitch_data_36

    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->fieldIndexError(I)Ljava/lang/IndexOutOfBoundsException;

    move-result-object v0

    throw v0

    :pswitch_a
    long-to-int v0, p3

    iput v0, p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;->f0:I

    return-void

    :pswitch_e
    iput-wide p3, p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;->f1:J

    return-void

    :pswitch_11
    long-to-int v0, p3

    invoke-static {v0}, Ljava/lang/Float;->intBitsToFloat(I)F

    move-result v0

    iput v0, p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;->f2:F

    return-void

    :pswitch_19
    invoke-static {p3, p4}, Ljava/lang/Double;->longBitsToDouble(J)D

    move-result-wide v0

    iput-wide v0, p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;->f3:D

    return-void

    :pswitch_20
    long-to-int v0, p3

    iput-byte v0, p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;->f4:B

    return-void

    :pswitch_24
    long-to-int v0, p3

    iput-char v0, p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;->f5:C

    return-void

    :pswitch_28
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->referenceFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_2d
    long-to-int v0, p3

    iput v0, p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;->f7:I

    return-void

    :pswitch_31
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->referenceFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_data_36
    .packed-switch 0x0
        :pswitch_a
        :pswitch_e
        :pswitch_11
        :pswitch_19
        :pswitch_20
        :pswitch_24
        :pswitch_28
        :pswitch_2d
        :pswitch_31
    .end packed-switch
.end method

.method public writeReference(Ljava/lang/Object;ILjava/lang/Object;)V
    .registers 5

    check-cast p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;

    packed-switch p2, :pswitch_data_3a

    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->fieldIndexError(I)Ljava/lang/IndexOutOfBoundsException;

    move-result-object v0

    throw v0

    :pswitch_a
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->numberFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_f
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->numberFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_14
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->numberFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_19
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->numberFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_1e
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->numberFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_23
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->numberFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_28
    move-object v0, p3

    check-cast v0, Ljava/lang/Object;

    iput-object v0, p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;->f6:Ljava/lang/Object;

    return-void

    :pswitch_2e
    invoke-static {p2}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFields;->numberFieldError(I)Ljava/lang/IllegalArgumentException;

    move-result-object v0

    throw v0

    :pswitch_33
    move-object v0, p3

    check-cast v0, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;

    iput-object v0, p1, Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;->f8:Lchasm/gc/Sfbe3de40f765f78d9a17013eded91e72_0;

    return-void

    nop

    :pswitch_data_3a
    .packed-switch 0x0
        :pswitch_a
        :pswitch_f
        :pswitch_14
        :pswitch_19
        :pswitch_1e
        :pswitch_23
        :pswitch_28
        :pswitch_2e
        :pswitch_33
    .end packed-switch
.end method
