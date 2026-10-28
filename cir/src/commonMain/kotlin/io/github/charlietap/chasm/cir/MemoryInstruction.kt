package io.github.charlietap.chasm.cir

sealed class MemoryInstruction : Instruction() {

    @CirTag("memory.i32.load.i")
    data class I32LoadI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 460
        }
    }

    @CirTag("memory.i32.load.s")
    data class I32LoadS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 461
        }
    }

    @CirTag("memory.i64.load.i")
    data class I64LoadI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 462
        }
    }

    @CirTag("memory.i64.load.s")
    data class I64LoadS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 463
        }
    }

    @CirTag("memory.f32.load.i")
    data class F32LoadI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 464
        }
    }

    @CirTag("memory.f32.load.s")
    data class F32LoadS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 465
        }
    }

    @CirTag("memory.f64.load.i")
    data class F64LoadI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 466
        }
    }

    @CirTag("memory.f64.load.s")
    data class F64LoadS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 467
        }
    }

    @CirTag("memory.i32.load8_s.i")
    data class I32Load8SI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 468
        }
    }

    @CirTag("memory.i32.load8_s.s")
    data class I32Load8SS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 469
        }
    }

    @CirTag("memory.i32.load8_u.i")
    data class I32Load8UI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 470
        }
    }

    @CirTag("memory.i32.load8_u.s")
    data class I32Load8US(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 471
        }
    }

    @CirTag("memory.i32.load16_s.i")
    data class I32Load16SI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 472
        }
    }

    @CirTag("memory.i32.load16_s.s")
    data class I32Load16SS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 473
        }
    }

    @CirTag("memory.i32.load16_u.i")
    data class I32Load16UI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 474
        }
    }

    @CirTag("memory.i32.load16_u.s")
    data class I32Load16US(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 475
        }
    }

    @CirTag("memory.i64.load8_s.i")
    data class I64Load8SI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 476
        }
    }

    @CirTag("memory.i64.load8_s.s")
    data class I64Load8SS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 477
        }
    }

    @CirTag("memory.i64.load8_u.i")
    data class I64Load8UI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 478
        }
    }

    @CirTag("memory.i64.load8_u.s")
    data class I64Load8US(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 479
        }
    }

    @CirTag("memory.i64.load16_s.i")
    data class I64Load16SI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 480
        }
    }

    @CirTag("memory.i64.load16_s.s")
    data class I64Load16SS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 481
        }
    }

    @CirTag("memory.i64.load16_u.i")
    data class I64Load16UI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 482
        }
    }

    @CirTag("memory.i64.load16_u.s")
    data class I64Load16US(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 483
        }
    }

    @CirTag("memory.i64.load32_s.i")
    data class I64Load32SI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 484
        }
    }

    @CirTag("memory.i64.load32_s.s")
    data class I64Load32SS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 485
        }
    }

    @CirTag("memory.i64.load32_u.i")
    data class I64Load32UI(
        val address: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 486
        }
    }

    @CirTag("memory.i64.load32_u.s")
    data class I64Load32US(
        val addressSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 487
        }
    }

    @CirTag("memory.size")
    data class MemorySizeS(
        val destinationSlot: Int,
        val memory: MemoryIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 488
        }
    }

    @CirTag("memory.grow.i")
    data class MemoryGrowI(
        val pagesToAdd: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 489
        }
    }

    @CirTag("memory.grow.s")
    data class MemoryGrowS(
        val pagesToAddSlot: Int,
        val destinationSlot: Int,
        val memory: MemoryIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 490
        }
    }

    @CirTag("memory.init.iii")
    data class MemoryInitIii(
        val bytesToCopy: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val memory: MemoryIndex,
        val data: DataIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 491
        }
    }

    @CirTag("memory.init.iis")
    data class MemoryInitIis(
        val bytesToCopy: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val memory: MemoryIndex,
        val data: DataIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 492
        }
    }

    @CirTag("memory.init.isi")
    data class MemoryInitIsi(
        val bytesToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val memory: MemoryIndex,
        val data: DataIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 493
        }
    }

    @CirTag("memory.init.iss")
    data class MemoryInitIss(
        val bytesToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val memory: MemoryIndex,
        val data: DataIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 494
        }
    }

    @CirTag("memory.init.sii")
    data class MemoryInitSii(
        val bytesToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val memory: MemoryIndex,
        val data: DataIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 495
        }
    }

    @CirTag("memory.init.sis")
    data class MemoryInitSis(
        val bytesToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val memory: MemoryIndex,
        val data: DataIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 496
        }
    }

    @CirTag("memory.init.ssi")
    data class MemoryInitSsi(
        val bytesToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val memory: MemoryIndex,
        val data: DataIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 497
        }
    }

    @CirTag("memory.init.sss")
    data class MemoryInitSss(
        val bytesToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val memory: MemoryIndex,
        val data: DataIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 498
        }
    }

    @CirTag("memory.copy.iii")
    data class MemoryCopyIii(
        val bytesToCopy: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val srcMemory: MemoryIndex,
        val dstMemory: MemoryIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 499
        }
    }

    @CirTag("memory.copy.iis")
    data class MemoryCopyIis(
        val bytesToCopy: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val srcMemory: MemoryIndex,
        val dstMemory: MemoryIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 500
        }
    }

    @CirTag("memory.copy.isi")
    data class MemoryCopyIsi(
        val bytesToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val srcMemory: MemoryIndex,
        val dstMemory: MemoryIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 501
        }
    }

    @CirTag("memory.copy.iss")
    data class MemoryCopyIss(
        val bytesToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val srcMemory: MemoryIndex,
        val dstMemory: MemoryIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 502
        }
    }

    @CirTag("memory.copy.sii")
    data class MemoryCopySii(
        val bytesToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val srcMemory: MemoryIndex,
        val dstMemory: MemoryIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 503
        }
    }

    @CirTag("memory.copy.sis")
    data class MemoryCopySis(
        val bytesToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val srcMemory: MemoryIndex,
        val dstMemory: MemoryIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 504
        }
    }

    @CirTag("memory.copy.ssi")
    data class MemoryCopySsi(
        val bytesToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val srcMemory: MemoryIndex,
        val dstMemory: MemoryIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 505
        }
    }

    @CirTag("memory.copy.sss")
    data class MemoryCopySss(
        val bytesToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val srcMemory: MemoryIndex,
        val dstMemory: MemoryIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 506
        }
    }

    @CirTag("memory.fill.iii")
    data class MemoryFillIii(
        val bytesToFill: Int,
        val fillValue: Int,
        val offset: Int,
        val memory: MemoryIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 507
        }
    }

    @CirTag("memory.fill.iis")
    data class MemoryFillIis(
        val bytesToFill: Int,
        val fillValue: Int,
        val offsetSlot: Int,
        val memory: MemoryIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 508
        }
    }

    @CirTag("memory.fill.isi")
    data class MemoryFillIsi(
        val bytesToFill: Int,
        val fillValueSlot: Int,
        val offset: Int,
        val memory: MemoryIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 509
        }
    }

    @CirTag("memory.fill.iss")
    data class MemoryFillIss(
        val bytesToFill: Int,
        val fillValueSlot: Int,
        val offsetSlot: Int,
        val memory: MemoryIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 510
        }
    }

    @CirTag("memory.fill.sii")
    data class MemoryFillSii(
        val bytesToFillSlot: Int,
        val fillValue: Int,
        val offset: Int,
        val memory: MemoryIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 511
        }
    }

    @CirTag("memory.fill.sis")
    data class MemoryFillSis(
        val bytesToFillSlot: Int,
        val fillValue: Int,
        val offsetSlot: Int,
        val memory: MemoryIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 512
        }
    }

    @CirTag("memory.fill.ssi")
    data class MemoryFillSsi(
        val bytesToFillSlot: Int,
        val fillValueSlot: Int,
        val offset: Int,
        val memory: MemoryIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 513
        }
    }

    @CirTag("memory.fill.sss")
    data class MemoryFillSss(
        val bytesToFillSlot: Int,
        val fillValueSlot: Int,
        val offsetSlot: Int,
        val memory: MemoryIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 514
        }
    }

    @CirTag("memory.i32.store.ii")
    data class I32StoreIi(
        val value: Int,
        val address: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 515
        }
    }

    @CirTag("memory.i32.store.is")
    data class I32StoreIs(
        val value: Int,
        val addressSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 516
        }
    }

    @CirTag("memory.i32.store.si")
    data class I32StoreSi(
        val valueSlot: Int,
        val address: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 517
        }
    }

    @CirTag("memory.i32.store.ss")
    data class I32StoreSs(
        val valueSlot: Int,
        val addressSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 518
        }
    }

    @CirTag("memory.i64.store.ii")
    data class I64StoreIi(
        val value: Long,
        val address: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 519
        }
    }

    @CirTag("memory.i64.store.is")
    data class I64StoreIs(
        val value: Long,
        val addressSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 520
        }
    }

    @CirTag("memory.i64.store.si")
    data class I64StoreSi(
        val valueSlot: Int,
        val address: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 521
        }
    }

    @CirTag("memory.i64.store.ss")
    data class I64StoreSs(
        val valueSlot: Int,
        val addressSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 522
        }
    }

    @CirTag("memory.f32.store.ii")
    data class F32StoreIi(
        val value: F32,
        val address: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 523
        }
    }

    @CirTag("memory.f32.store.is")
    data class F32StoreIs(
        val value: F32,
        val addressSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 524
        }
    }

    @CirTag("memory.f32.store.si")
    data class F32StoreSi(
        val valueSlot: Int,
        val address: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 525
        }
    }

    @CirTag("memory.f32.store.ss")
    data class F32StoreSs(
        val valueSlot: Int,
        val addressSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 526
        }
    }

    @CirTag("memory.f64.store.ii")
    data class F64StoreIi(
        val value: F64,
        val address: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 527
        }
    }

    @CirTag("memory.f64.store.is")
    data class F64StoreIs(
        val value: F64,
        val addressSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 528
        }
    }

    @CirTag("memory.f64.store.si")
    data class F64StoreSi(
        val valueSlot: Int,
        val address: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 529
        }
    }

    @CirTag("memory.f64.store.ss")
    data class F64StoreSs(
        val valueSlot: Int,
        val addressSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 530
        }
    }

    @CirTag("memory.i32.store8.ii")
    data class I32Store8Ii(
        val value: Int,
        val address: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 531
        }
    }

    @CirTag("memory.i32.store8.is")
    data class I32Store8Is(
        val value: Int,
        val addressSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 532
        }
    }

    @CirTag("memory.i32.store8.si")
    data class I32Store8Si(
        val valueSlot: Int,
        val address: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 533
        }
    }

    @CirTag("memory.i32.store8.ss")
    data class I32Store8Ss(
        val valueSlot: Int,
        val addressSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 534
        }
    }

    @CirTag("memory.i32.store16.ii")
    data class I32Store16Ii(
        val value: Int,
        val address: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 535
        }
    }

    @CirTag("memory.i32.store16.is")
    data class I32Store16Is(
        val value: Int,
        val addressSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 536
        }
    }

    @CirTag("memory.i32.store16.si")
    data class I32Store16Si(
        val valueSlot: Int,
        val address: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 537
        }
    }

    @CirTag("memory.i32.store16.ss")
    data class I32Store16Ss(
        val valueSlot: Int,
        val addressSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 538
        }
    }

    @CirTag("memory.i64.store8.ii")
    data class I64Store8Ii(
        val value: Long,
        val address: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 539
        }
    }

    @CirTag("memory.i64.store8.is")
    data class I64Store8Is(
        val value: Long,
        val addressSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 540
        }
    }

    @CirTag("memory.i64.store8.si")
    data class I64Store8Si(
        val valueSlot: Int,
        val address: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 541
        }
    }

    @CirTag("memory.i64.store8.ss")
    data class I64Store8Ss(
        val valueSlot: Int,
        val addressSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 542
        }
    }

    @CirTag("memory.i64.store16.ii")
    data class I64Store16Ii(
        val value: Long,
        val address: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 543
        }
    }

    @CirTag("memory.i64.store16.is")
    data class I64Store16Is(
        val value: Long,
        val addressSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 544
        }
    }

    @CirTag("memory.i64.store16.si")
    data class I64Store16Si(
        val valueSlot: Int,
        val address: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 545
        }
    }

    @CirTag("memory.i64.store16.ss")
    data class I64Store16Ss(
        val valueSlot: Int,
        val addressSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 546
        }
    }

    @CirTag("memory.i64.store32.ii")
    data class I64Store32Ii(
        val value: Long,
        val address: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 547
        }
    }

    @CirTag("memory.i64.store32.is")
    data class I64Store32Is(
        val value: Long,
        val addressSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 548
        }
    }

    @CirTag("memory.i64.store32.si")
    data class I64Store32Si(
        val valueSlot: Int,
        val address: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 549
        }
    }

    @CirTag("memory.i64.store32.ss")
    data class I64Store32Ss(
        val valueSlot: Int,
        val addressSlot: Int,
        val memory: MemoryIndex,
        val memArg: MemArg,
    ) : MemoryInstruction() {
        init {
            opcode = 550
        }
    }

    @CirTag("memory.data_drop")
    data class DataDrop(
        val data: DataIndex,
    ) : MemoryInstruction() {
        init {
            opcode = 551
        }
    }
}
