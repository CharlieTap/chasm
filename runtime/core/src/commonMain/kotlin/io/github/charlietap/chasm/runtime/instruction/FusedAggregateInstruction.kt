package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.RTT
import io.github.charlietap.chasm.type.StructType

sealed interface FusedAggregateInstruction : LinkedInstruction {

    data class ArrayCopyIii(
        val elementsToCopy: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayCopyIis(
        val elementsToCopy: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayCopyIsi(
        val elementsToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayCopyIss(
        val elementsToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayCopySii(
        val elementsToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayCopySis(
        val elementsToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayCopySsi(
        val elementsToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayCopySss(
        val elementsToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayFillIii(
        val elementsToFill: Int,
        val fillValue: Long,
        val arrayElementOffset: Int,
        val addressSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayFillIis(
        val elementsToFill: Int,
        val fillValue: Long,
        val arrayElementOffsetSlot: Int,
        val addressSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayFillIsi(
        val elementsToFill: Int,
        val fillValueSlot: Int,
        val arrayElementOffset: Int,
        val addressSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayFillIss(
        val elementsToFill: Int,
        val fillValueSlot: Int,
        val arrayElementOffsetSlot: Int,
        val addressSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayFillSii(
        val elementsToFillSlot: Int,
        val fillValue: Long,
        val arrayElementOffset: Int,
        val addressSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayFillSis(
        val elementsToFillSlot: Int,
        val fillValue: Long,
        val arrayElementOffsetSlot: Int,
        val addressSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayFillSsi(
        val elementsToFillSlot: Int,
        val fillValueSlot: Int,
        val arrayElementOffset: Int,
        val addressSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayFillSss(
        val elementsToFillSlot: Int,
        val fillValueSlot: Int,
        val arrayElementOffsetSlot: Int,
        val addressSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayGetI(
        val addressSlot: Int,
        val field: Int,
        val destinationSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayGetS(
        val addressSlot: Int,
        val fieldSlot: Int,
        val destinationSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayGetSignedI(
        val addressSlot: Int,
        val field: Int,
        val destinationSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayGetSignedS(
        val addressSlot: Int,
        val fieldSlot: Int,
        val destinationSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayGetUnsignedI(
        val addressSlot: Int,
        val field: Int,
        val destinationSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayGetUnsignedS(
        val addressSlot: Int,
        val fieldSlot: Int,
        val destinationSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayLenS(
        val addressSlot: Int,
        val destinationSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayNewIi(
        val size: Int,
        val value: Long,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
    ) : FusedAggregateInstruction

    data class ArrayNewIs(
        val size: Int,
        val valueSlot: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
    ) : FusedAggregateInstruction

    data class ArrayNewSi(
        val sizeSlot: Int,
        val value: Long,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
    ) : FusedAggregateInstruction

    data class ArrayNewSs(
        val sizeSlot: Int,
        val valueSlot: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
    ) : FusedAggregateInstruction

    data class ArrayNewDefaultI(
        val size: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val field: Long,
    ) : FusedAggregateInstruction

    data class ArrayNewDefaultS(
        val sizeSlot: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val field: Long,
    ) : FusedAggregateInstruction

    data class ArrayNewDataIi(
        val sourceOffset: Int,
        val arrayLength: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : FusedAggregateInstruction

    data class ArrayNewDataIs(
        val sourceOffset: Int,
        val arrayLengthSlot: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : FusedAggregateInstruction

    data class ArrayNewDataSi(
        val sourceOffsetSlot: Int,
        val arrayLength: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : FusedAggregateInstruction

    data class ArrayNewDataSs(
        val sourceOffsetSlot: Int,
        val arrayLengthSlot: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : FusedAggregateInstruction

    data class ArrayNewElementIi(
        val sourceOffset: Int,
        val arrayLength: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : FusedAggregateInstruction

    data class ArrayNewElementIs(
        val sourceOffset: Int,
        val arrayLengthSlot: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : FusedAggregateInstruction

    data class ArrayNewElementSi(
        val sourceOffsetSlot: Int,
        val arrayLength: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : FusedAggregateInstruction

    data class ArrayNewElementSs(
        val sourceOffsetSlot: Int,
        val arrayLengthSlot: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : FusedAggregateInstruction

    data class ArrayNewFixedS(
        val valueSlots: List<Int>,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val size: Int,
    ) : FusedAggregateInstruction

    data class ArraySetIi(
        val value: Long,
        val field: Int,
        val addressSlot: Int,
    ) : FusedAggregateInstruction

    data class ArraySetIs(
        val value: Long,
        val fieldSlot: Int,
        val addressSlot: Int,
    ) : FusedAggregateInstruction

    data class ArraySetSi(
        val valueSlot: Int,
        val field: Int,
        val addressSlot: Int,
    ) : FusedAggregateInstruction

    data class ArraySetSs(
        val valueSlot: Int,
        val fieldSlot: Int,
        val addressSlot: Int,
    ) : FusedAggregateInstruction

    data class ArrayInitDataIii(
        val elementsToCopy: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : FusedAggregateInstruction

    data class ArrayInitDataIis(
        val elementsToCopy: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : FusedAggregateInstruction

    data class ArrayInitDataIsi(
        val elementsToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : FusedAggregateInstruction

    data class ArrayInitDataIss(
        val elementsToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : FusedAggregateInstruction

    data class ArrayInitDataSii(
        val elementsToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : FusedAggregateInstruction

    data class ArrayInitDataSis(
        val elementsToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : FusedAggregateInstruction

    data class ArrayInitDataSsi(
        val elementsToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : FusedAggregateInstruction

    data class ArrayInitDataSss(
        val elementsToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : FusedAggregateInstruction

    data class ArrayInitElementIii(
        val elementsToCopy: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : FusedAggregateInstruction

    data class ArrayInitElementIis(
        val elementsToCopy: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : FusedAggregateInstruction

    data class ArrayInitElementIsi(
        val elementsToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : FusedAggregateInstruction

    data class ArrayInitElementIss(
        val elementsToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : FusedAggregateInstruction

    data class ArrayInitElementSii(
        val elementsToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : FusedAggregateInstruction

    data class ArrayInitElementSis(
        val elementsToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : FusedAggregateInstruction

    data class ArrayInitElementSsi(
        val elementsToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : FusedAggregateInstruction

    data class ArrayInitElementSss(
        val elementsToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : FusedAggregateInstruction

    data class RefI31I(
        val value: Int,
        val destinationSlot: Int,
    ) : FusedAggregateInstruction

    data class RefI31S(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : FusedAggregateInstruction

    data class I31GetSignedS(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : FusedAggregateInstruction

    data class I31GetUnsignedS(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : FusedAggregateInstruction

    data class AnyConvertExternS(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : FusedAggregateInstruction

    data class ExternConvertAnyS(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : FusedAggregateInstruction

    data class StructGetS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val fieldIndex: Int,
    ) : FusedAggregateInstruction

    data class StructGetSignedS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val fieldIndex: Int,
    ) : FusedAggregateInstruction

    data class StructGetUnsignedS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val fieldIndex: Int,
    ) : FusedAggregateInstruction

    data class StructNewS(
        val fieldSlots: List<Int>,
        val destinationSlot: Int,
        val rtt: RTT,
        val structType: StructType,
    ) : FusedAggregateInstruction

    data class StructNewDefaultS(
        val destinationSlot: Int,
        val rtt: RTT,
        val structType: StructType,
        val fields: LongArray,
    ) : FusedAggregateInstruction

    data class StructSetI(
        val value: Long,
        val addressSlot: Int,
        val fieldIndex: Int,
    ) : FusedAggregateInstruction

    data class StructSetS(
        val valueSlot: Int,
        val addressSlot: Int,
        val fieldIndex: Int,
    ) : FusedAggregateInstruction
}
