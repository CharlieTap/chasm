.method public static f7([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;II)Ljava/lang/Object;
    .registers 26

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    move/from16 v9, v17

    if-ltz v9, :cond_35

    const v1, 0x7fffffdf

    if-gt v9, v1, :cond_35

    shl-int/lit8 v1, v9, 0x0

    add-int/lit16 v1, v1, 0x20

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_21

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_21
    new-array v10, v9, [B

    const/16 v9, 0xa

    aget-object v11, v16, v9

    check-cast v11, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I8Array;

    invoke-direct {v12, v11, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/I8Array;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;[B)V

    move-object/from16 v18, v12

    move-object/from16 v12, v18

    return-object v12

    const/4 v12, 0x0

    return-object v12

    :cond_35
    invoke-static {}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->guestHeapExhausted()Ljava/lang/Void;

    const/4 v12, 0x0

    return-object v12
.end method
