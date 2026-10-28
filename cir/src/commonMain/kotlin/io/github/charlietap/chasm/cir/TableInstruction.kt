package io.github.charlietap.chasm.cir

sealed class TableInstruction : Instruction() {

    @CirTag("table.copy.iii")
    data class TableCopyIii(
        val elementsToCopy: Int,
        val srcOffset: Int,
        val dstOffset: Int,
        val srcTable: TableIndex,
        val destTable: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 626
        }
    }

    @CirTag("table.copy.iis")
    data class TableCopyIis(
        val elementsToCopy: Int,
        val srcOffset: Int,
        val dstOffsetSlot: Int,
        val srcTable: TableIndex,
        val destTable: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 627
        }
    }

    @CirTag("table.copy.isi")
    data class TableCopyIsi(
        val elementsToCopy: Int,
        val srcOffsetSlot: Int,
        val dstOffset: Int,
        val srcTable: TableIndex,
        val destTable: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 628
        }
    }

    @CirTag("table.copy.iss")
    data class TableCopyIss(
        val elementsToCopy: Int,
        val srcOffsetSlot: Int,
        val dstOffsetSlot: Int,
        val srcTable: TableIndex,
        val destTable: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 629
        }
    }

    @CirTag("table.copy.sii")
    data class TableCopySii(
        val elementsToCopySlot: Int,
        val srcOffset: Int,
        val dstOffset: Int,
        val srcTable: TableIndex,
        val destTable: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 630
        }
    }

    @CirTag("table.copy.sis")
    data class TableCopySis(
        val elementsToCopySlot: Int,
        val srcOffset: Int,
        val dstOffsetSlot: Int,
        val srcTable: TableIndex,
        val destTable: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 631
        }
    }

    @CirTag("table.copy.ssi")
    data class TableCopySsi(
        val elementsToCopySlot: Int,
        val srcOffsetSlot: Int,
        val dstOffset: Int,
        val srcTable: TableIndex,
        val destTable: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 632
        }
    }

    @CirTag("table.copy.sss")
    data class TableCopySss(
        val elementsToCopySlot: Int,
        val srcOffsetSlot: Int,
        val dstOffsetSlot: Int,
        val srcTable: TableIndex,
        val destTable: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 633
        }
    }

    @CirTag("table.fill.isi")
    data class TableFillIsi(
        val elementsToFill: Int,
        val fillValueSlot: Int,
        val tableOffset: Int,
        val table: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 634
        }
    }

    @CirTag("table.fill.iss")
    data class TableFillIss(
        val elementsToFill: Int,
        val fillValueSlot: Int,
        val tableOffsetSlot: Int,
        val table: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 635
        }
    }

    @CirTag("table.fill.ssi")
    data class TableFillSsi(
        val elementsToFillSlot: Int,
        val fillValueSlot: Int,
        val tableOffset: Int,
        val table: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 636
        }
    }

    @CirTag("table.fill.sss")
    data class TableFillSss(
        val elementsToFillSlot: Int,
        val fillValueSlot: Int,
        val tableOffsetSlot: Int,
        val table: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 637
        }
    }

    @CirTag("table.grow.is")
    data class TableGrowIs(
        val elementsToAdd: Int,
        val referenceValueSlot: Int,
        val destinationSlot: Int,
        val table: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 638
        }
    }

    @CirTag("table.grow.ss")
    data class TableGrowSs(
        val elementsToAddSlot: Int,
        val referenceValueSlot: Int,
        val destinationSlot: Int,
        val table: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 639
        }
    }

    @CirTag("table.init.iii")
    data class TableInitIii(
        val elementsToInitialise: Int,
        val segmentOffset: Int,
        val tableOffset: Int,
        val element: ElementIndex,
        val table: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 640
        }
    }

    @CirTag("table.init.iis")
    data class TableInitIis(
        val elementsToInitialise: Int,
        val segmentOffset: Int,
        val tableOffsetSlot: Int,
        val element: ElementIndex,
        val table: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 641
        }
    }

    @CirTag("table.init.isi")
    data class TableInitIsi(
        val elementsToInitialise: Int,
        val segmentOffsetSlot: Int,
        val tableOffset: Int,
        val element: ElementIndex,
        val table: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 642
        }
    }

    @CirTag("table.init.iss")
    data class TableInitIss(
        val elementsToInitialise: Int,
        val segmentOffsetSlot: Int,
        val tableOffsetSlot: Int,
        val element: ElementIndex,
        val table: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 643
        }
    }

    @CirTag("table.init.sii")
    data class TableInitSii(
        val elementsToInitialiseSlot: Int,
        val segmentOffset: Int,
        val tableOffset: Int,
        val element: ElementIndex,
        val table: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 644
        }
    }

    @CirTag("table.init.sis")
    data class TableInitSis(
        val elementsToInitialiseSlot: Int,
        val segmentOffset: Int,
        val tableOffsetSlot: Int,
        val element: ElementIndex,
        val table: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 645
        }
    }

    @CirTag("table.init.ssi")
    data class TableInitSsi(
        val elementsToInitialiseSlot: Int,
        val segmentOffsetSlot: Int,
        val tableOffset: Int,
        val element: ElementIndex,
        val table: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 646
        }
    }

    @CirTag("table.init.sss")
    data class TableInitSss(
        val elementsToInitialiseSlot: Int,
        val segmentOffsetSlot: Int,
        val tableOffsetSlot: Int,
        val element: ElementIndex,
        val table: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 647
        }
    }

    @CirTag("table.get.i")
    data class TableGetI(
        val elementIndex: Int,
        val destinationSlot: Int,
        val table: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 648
        }
    }

    @CirTag("table.get.s")
    data class TableGetS(
        val elementIndexSlot: Int,
        val destinationSlot: Int,
        val table: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 649
        }
    }

    @CirTag("table.set.si")
    data class TableSetSi(
        val valueSlot: Int,
        val elementIndex: Int,
        val table: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 650
        }
    }

    @CirTag("table.set.ss")
    data class TableSetSs(
        val valueSlot: Int,
        val elementIndexSlot: Int,
        val table: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 651
        }
    }

    @CirTag("table.size")
    data class TableSizeS(
        val destinationSlot: Int,
        val table: TableIndex,
    ) : TableInstruction() {
        init {
            opcode = 652
        }
    }

    @CirTag("table.elem_drop")
    data class ElemDrop(
        val element: ElementIndex,
    ) : TableInstruction() {
        init {
            opcode = 653
        }
    }
}
