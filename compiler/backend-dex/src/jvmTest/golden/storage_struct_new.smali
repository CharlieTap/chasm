.method public static f0([Ljava/lang/Object;Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;IIJLjava/lang/Object;)Ljava/lang/Object;
    .registers 36

    move-object/from16 v14, p1

    move/from16 v13, p2

    move-object/from16 v16, p0

    move/from16 v17, p3

    move-wide/from16 v19, p4

    move-object/from16 v21, p6

    move/from16 v22, v17

    move-wide/from16 v23, v19

    move-object/from16 v25, v21

    const/16 v1, 0x20

    iget-object v2, v14, Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;->allocation:Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;

    iget v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    sub-int/2addr v0, v1

    iput v0, v2, Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;->remaining:I

    if-gez v0, :cond_20

    invoke-static {v14, v1}, Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;->refillAllocation(Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;I)V

    :cond_20
    const/4 v9, 0x3

    aget-object v10, v16, v9

    check-cast v10, Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;

    new-instance v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R2__I_II_RR;

    invoke-direct {v12, v10}, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R2__I_II_RR;-><init>(Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;)V

    move/from16 v8, v22

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I1_R0__I;->i0:I

    move-wide/from16 v2, v23

    long-to-int v8, v2

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;->i1:I

    const/16 v15, 0x20

    ushr-long/2addr v2, v15

    long-to-int v8, v2

    iput v8, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R0__I_II;->i2:I

    move-object/from16 v10, v25

    iput-object v10, v12, Lio/github/charlietap/chasm/runtime/compiled/gc/storage/Struct_I3_R2__I_II_RR;->r0:Ljava/lang/Object;

    move-object/from16 v18, v12

    move-object/from16 v12, v18

    return-object v12

    const/4 v12, 0x0

    return-object v12
.end method
