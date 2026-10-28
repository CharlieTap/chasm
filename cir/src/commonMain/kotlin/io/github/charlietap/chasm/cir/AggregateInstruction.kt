package io.github.charlietap.chasm.cir

sealed class AggregateInstruction : Instruction() {

    @CirTag("aggregate.array.copy.iii")
    data class ArrayCopyIii(
        val elementsToCopy: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 552
        }
    }

    @CirTag("aggregate.array.copy.iis")
    data class ArrayCopyIis(
        val elementsToCopy: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 553
        }
    }

    @CirTag("aggregate.array.copy.isi")
    data class ArrayCopyIsi(
        val elementsToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 554
        }
    }

    @CirTag("aggregate.array.copy.iss")
    data class ArrayCopyIss(
        val elementsToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 555
        }
    }

    @CirTag("aggregate.array.copy.sii")
    data class ArrayCopySii(
        val elementsToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 556
        }
    }

    @CirTag("aggregate.array.copy.sis")
    data class ArrayCopySis(
        val elementsToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 557
        }
    }

    @CirTag("aggregate.array.copy.ssi")
    data class ArrayCopySsi(
        val elementsToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 558
        }
    }

    @CirTag("aggregate.array.copy.sss")
    data class ArrayCopySss(
        val elementsToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 559
        }
    }

    @CirTag("aggregate.array.fill.iii")
    data class ArrayFillIii(
        val elementsToFill: Int,
        val fillValue: Long,
        val arrayElementOffset: Int,
        val addressSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 560
        }
    }

    @CirTag("aggregate.array.fill.iis")
    data class ArrayFillIis(
        val elementsToFill: Int,
        val fillValue: Long,
        val arrayElementOffsetSlot: Int,
        val addressSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 561
        }
    }

    @CirTag("aggregate.array.fill.isi")
    data class ArrayFillIsi(
        val elementsToFill: Int,
        val fillValueSlot: Int,
        val arrayElementOffset: Int,
        val addressSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 562
        }
    }

    @CirTag("aggregate.array.fill.iss")
    data class ArrayFillIss(
        val elementsToFill: Int,
        val fillValueSlot: Int,
        val arrayElementOffsetSlot: Int,
        val addressSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 563
        }
    }

    @CirTag("aggregate.array.fill.sii")
    data class ArrayFillSii(
        val elementsToFillSlot: Int,
        val fillValue: Long,
        val arrayElementOffset: Int,
        val addressSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 564
        }
    }

    @CirTag("aggregate.array.fill.sis")
    data class ArrayFillSis(
        val elementsToFillSlot: Int,
        val fillValue: Long,
        val arrayElementOffsetSlot: Int,
        val addressSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 565
        }
    }

    @CirTag("aggregate.array.fill.ssi")
    data class ArrayFillSsi(
        val elementsToFillSlot: Int,
        val fillValueSlot: Int,
        val arrayElementOffset: Int,
        val addressSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 566
        }
    }

    @CirTag("aggregate.array.fill.sss")
    data class ArrayFillSss(
        val elementsToFillSlot: Int,
        val fillValueSlot: Int,
        val arrayElementOffsetSlot: Int,
        val addressSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 567
        }
    }

    @CirTag("aggregate.array.get.i")
    data class ArrayGetI(
        val addressSlot: Int,
        val field: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 568
        }
    }

    @CirTag("aggregate.array.get.s")
    data class ArrayGetS(
        val addressSlot: Int,
        val fieldSlot: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 569
        }
    }

    @CirTag("aggregate.array.get_signed.i")
    data class ArrayGetSignedI(
        val addressSlot: Int,
        val field: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
        val packedType: PackedType,
    ) : AggregateInstruction() {
        init {
            opcode = 570
        }
    }

    @CirTag("aggregate.array.get_signed.s")
    data class ArrayGetSignedS(
        val addressSlot: Int,
        val fieldSlot: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
        val packedType: PackedType,
    ) : AggregateInstruction() {
        init {
            opcode = 571
        }
    }

    @CirTag("aggregate.array.get_unsigned.i")
    data class ArrayGetUnsignedI(
        val addressSlot: Int,
        val field: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
        val packedType: PackedType,
    ) : AggregateInstruction() {
        init {
            opcode = 572
        }
    }

    @CirTag("aggregate.array.get_unsigned.s")
    data class ArrayGetUnsignedS(
        val addressSlot: Int,
        val fieldSlot: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
        val packedType: PackedType,
    ) : AggregateInstruction() {
        init {
            opcode = 573
        }
    }

    @CirTag("aggregate.array.len")
    data class ArrayLenS(
        val addressSlot: Int,
        val destinationSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 574
        }
    }

    @CirTag("aggregate.array.new.ii")
    data class ArrayNewIi(
        val size: Int,
        val value: Long,
        val destinationSlot: Int,
        val type: TypeIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 575
        }
    }

    @CirTag("aggregate.array.new.is")
    data class ArrayNewIs(
        val size: Int,
        val valueSlot: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 576
        }
    }

    @CirTag("aggregate.array.new.si")
    data class ArrayNewSi(
        val sizeSlot: Int,
        val value: Long,
        val destinationSlot: Int,
        val type: TypeIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 577
        }
    }

    @CirTag("aggregate.array.new.ss")
    data class ArrayNewSs(
        val sizeSlot: Int,
        val valueSlot: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 578
        }
    }

    @CirTag("aggregate.array.new_default.i")
    data class ArrayNewDefaultI(
        val size: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 579
        }
    }

    @CirTag("aggregate.array.new_default.s")
    data class ArrayNewDefaultS(
        val sizeSlot: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 580
        }
    }

    @CirTag("aggregate.array.new_data.ii")
    data class ArrayNewDataIi(
        val sourceOffset: Int,
        val arrayLength: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
        val data: DataIndex,
        val fieldWidthInBytes: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 581
        }
    }

    @CirTag("aggregate.array.new_data.is")
    data class ArrayNewDataIs(
        val sourceOffset: Int,
        val arrayLengthSlot: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
        val data: DataIndex,
        val fieldWidthInBytes: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 582
        }
    }

    @CirTag("aggregate.array.new_data.si")
    data class ArrayNewDataSi(
        val sourceOffsetSlot: Int,
        val arrayLength: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
        val data: DataIndex,
        val fieldWidthInBytes: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 583
        }
    }

    @CirTag("aggregate.array.new_data.ss")
    data class ArrayNewDataSs(
        val sourceOffsetSlot: Int,
        val arrayLengthSlot: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
        val data: DataIndex,
        val fieldWidthInBytes: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 584
        }
    }

    @CirTag("aggregate.array.new_element.ii")
    data class ArrayNewElementIi(
        val sourceOffset: Int,
        val arrayLength: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
        val element: ElementIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 585
        }
    }

    @CirTag("aggregate.array.new_element.is")
    data class ArrayNewElementIs(
        val sourceOffset: Int,
        val arrayLengthSlot: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
        val element: ElementIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 586
        }
    }

    @CirTag("aggregate.array.new_element.si")
    data class ArrayNewElementSi(
        val sourceOffsetSlot: Int,
        val arrayLength: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
        val element: ElementIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 587
        }
    }

    @CirTag("aggregate.array.new_element.ss")
    data class ArrayNewElementSs(
        val sourceOffsetSlot: Int,
        val arrayLengthSlot: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
        val element: ElementIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 588
        }
    }

    @CirTag("aggregate.array.new_fixed")
    data class ArrayNewFixedS(
        val firstElementSlot: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
        val size: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 589
        }
    }

    @CirTag("aggregate.array.set.ii")
    data class ArraySetIi(
        val value: Long,
        val field: Int,
        val addressSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 590
        }
    }

    @CirTag("aggregate.array.set.is")
    data class ArraySetIs(
        val value: Long,
        val fieldSlot: Int,
        val addressSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 591
        }
    }

    @CirTag("aggregate.array.set.si")
    data class ArraySetSi(
        val valueSlot: Int,
        val field: Int,
        val addressSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 592
        }
    }

    @CirTag("aggregate.array.set.ss")
    data class ArraySetSs(
        val valueSlot: Int,
        val fieldSlot: Int,
        val addressSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 593
        }
    }

    @CirTag("aggregate.array.init_data.iii")
    data class ArrayInitDataIii(
        val elementsToCopy: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val data: DataIndex,
        val fieldWidthInBytes: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 594
        }
    }

    @CirTag("aggregate.array.init_data.iis")
    data class ArrayInitDataIis(
        val elementsToCopy: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val data: DataIndex,
        val fieldWidthInBytes: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 595
        }
    }

    @CirTag("aggregate.array.init_data.isi")
    data class ArrayInitDataIsi(
        val elementsToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val data: DataIndex,
        val fieldWidthInBytes: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 596
        }
    }

    @CirTag("aggregate.array.init_data.iss")
    data class ArrayInitDataIss(
        val elementsToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val data: DataIndex,
        val fieldWidthInBytes: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 597
        }
    }

    @CirTag("aggregate.array.init_data.sii")
    data class ArrayInitDataSii(
        val elementsToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val data: DataIndex,
        val fieldWidthInBytes: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 598
        }
    }

    @CirTag("aggregate.array.init_data.sis")
    data class ArrayInitDataSis(
        val elementsToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val data: DataIndex,
        val fieldWidthInBytes: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 599
        }
    }

    @CirTag("aggregate.array.init_data.ssi")
    data class ArrayInitDataSsi(
        val elementsToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val data: DataIndex,
        val fieldWidthInBytes: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 600
        }
    }

    @CirTag("aggregate.array.init_data.sss")
    data class ArrayInitDataSss(
        val elementsToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val data: DataIndex,
        val fieldWidthInBytes: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 601
        }
    }

    @CirTag("aggregate.array.init_element.iii")
    data class ArrayInitElementIii(
        val elementsToCopy: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val element: ElementIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 602
        }
    }

    @CirTag("aggregate.array.init_element.iis")
    data class ArrayInitElementIis(
        val elementsToCopy: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val element: ElementIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 603
        }
    }

    @CirTag("aggregate.array.init_element.isi")
    data class ArrayInitElementIsi(
        val elementsToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val element: ElementIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 604
        }
    }

    @CirTag("aggregate.array.init_element.iss")
    data class ArrayInitElementIss(
        val elementsToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val element: ElementIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 605
        }
    }

    @CirTag("aggregate.array.init_element.sii")
    data class ArrayInitElementSii(
        val elementsToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val element: ElementIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 606
        }
    }

    @CirTag("aggregate.array.init_element.sis")
    data class ArrayInitElementSis(
        val elementsToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val element: ElementIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 607
        }
    }

    @CirTag("aggregate.array.init_element.ssi")
    data class ArrayInitElementSsi(
        val elementsToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val element: ElementIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 608
        }
    }

    @CirTag("aggregate.array.init_element.sss")
    data class ArrayInitElementSss(
        val elementsToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val element: ElementIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 609
        }
    }

    @CirTag("aggregate.ref_i31.i")
    data class RefI31I(
        val value: Int,
        val destinationSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 610
        }
    }

    @CirTag("aggregate.ref_i31.s")
    data class RefI31S(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 611
        }
    }

    @CirTag("aggregate.i31.get_signed")
    data class I31GetSignedS(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 612
        }
    }

    @CirTag("aggregate.i31.get_unsigned")
    data class I31GetUnsignedS(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 613
        }
    }

    @CirTag("aggregate.any.convert_extern")
    data class AnyConvertExternS(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 614
        }
    }

    @CirTag("aggregate.extern.convert_any")
    data class ExternConvertAnyS(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 615
        }
    }

    @CirTag("aggregate.struct.get")
    data class StructGetS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
        val fieldIndex: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 616
        }
    }

    @CirTag("aggregate.struct.get_signed")
    data class StructGetSignedS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
        val fieldIndex: Int,
        val packedType: PackedType,
    ) : AggregateInstruction() {
        init {
            opcode = 617
        }
    }

    @CirTag("aggregate.struct.get_unsigned")
    data class StructGetUnsignedS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
        val fieldIndex: Int,
        val packedType: PackedType,
    ) : AggregateInstruction() {
        init {
            opcode = 618
        }
    }

    @CirTag("aggregate.ref_cast_struct_get")
    data class RefCastStructGetS(
        val referenceSlot: Int,
        val destinationSlot: Int,
        val typeTest: ReferenceType,
        val type: TypeIndex,
        val fieldIndex: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 619
        }
    }

    @CirTag("aggregate.struct_get_struct_get")
    data class StructGetStructGetS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val firstType: TypeIndex,
        val firstFieldIndex: Int,
        val secondType: TypeIndex,
        val secondFieldIndex: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 620
        }
    }

    @CirTag("aggregate.local_set_struct_get")
    data class LocalSetStructGetS(
        val sourceSlot: Int,
        val localSlot: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
        val fieldIndex: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 621
        }
    }

    @CirTag("aggregate.struct.new")
    data class StructNewS(
        val firstFieldSlot: Int,
        val destinationSlot: Int,
        val type: TypeIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 622
        }
    }

    @CirTag("aggregate.struct.new_default")
    data class StructNewDefaultS(
        val destinationSlot: Int,
        val type: TypeIndex,
    ) : AggregateInstruction() {
        init {
            opcode = 623
        }
    }

    @CirTag("aggregate.struct.set.i")
    data class StructSetI(
        val value: Long,
        val addressSlot: Int,
        val fieldIndex: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 624
        }
    }

    @CirTag("aggregate.struct.set.s")
    data class StructSetS(
        val valueSlot: Int,
        val addressSlot: Int,
        val fieldIndex: Int,
    ) : AggregateInstruction() {
        init {
            opcode = 625
        }
    }
}
