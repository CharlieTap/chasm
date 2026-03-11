package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.RTT
import io.github.charlietap.chasm.type.StructType

sealed interface AggregateSuperInstruction : LinkedInstruction {

    data class ArrayCopyIii(
        val elementsToCopy: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayCopyIis(
        val elementsToCopy: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayCopyIsi(
        val elementsToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayCopyIss(
        val elementsToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayCopySii(
        val elementsToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayCopySis(
        val elementsToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayCopySsi(
        val elementsToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayCopySss(
        val elementsToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val sourceAddressSlot: Int,
        val destinationAddressSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayFillIii(
        val elementsToFill: Int,
        val fillValue: Long,
        val arrayElementOffset: Int,
        val addressSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayFillIis(
        val elementsToFill: Int,
        val fillValue: Long,
        val arrayElementOffsetSlot: Int,
        val addressSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayFillIsi(
        val elementsToFill: Int,
        val fillValueSlot: Int,
        val arrayElementOffset: Int,
        val addressSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayFillIss(
        val elementsToFill: Int,
        val fillValueSlot: Int,
        val arrayElementOffsetSlot: Int,
        val addressSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayFillSii(
        val elementsToFillSlot: Int,
        val fillValue: Long,
        val arrayElementOffset: Int,
        val addressSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayFillSis(
        val elementsToFillSlot: Int,
        val fillValue: Long,
        val arrayElementOffsetSlot: Int,
        val addressSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayFillSsi(
        val elementsToFillSlot: Int,
        val fillValueSlot: Int,
        val arrayElementOffset: Int,
        val addressSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayFillSss(
        val elementsToFillSlot: Int,
        val fillValueSlot: Int,
        val arrayElementOffsetSlot: Int,
        val addressSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayGetI(
        val addressSlot: Int,
        val field: Int,
        val destinationSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayGetS(
        val addressSlot: Int,
        val fieldSlot: Int,
        val destinationSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayGetSignedI(
        val addressSlot: Int,
        val field: Int,
        val destinationSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayGetSignedS(
        val addressSlot: Int,
        val fieldSlot: Int,
        val destinationSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayGetUnsignedI(
        val addressSlot: Int,
        val field: Int,
        val destinationSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayGetUnsignedS(
        val addressSlot: Int,
        val fieldSlot: Int,
        val destinationSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayLenS(
        val addressSlot: Int,
        val destinationSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayNewIi(
        val size: Int,
        val value: Long,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
    ) : AggregateSuperInstruction

    data class ArrayNewIs(
        val size: Int,
        val valueSlot: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
    ) : AggregateSuperInstruction

    data class ArrayNewSi(
        val sizeSlot: Int,
        val value: Long,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
    ) : AggregateSuperInstruction

    data class ArrayNewSs(
        val sizeSlot: Int,
        val valueSlot: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
    ) : AggregateSuperInstruction

    data class ArrayNewDefaultI(
        val size: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val field: Long,
    ) : AggregateSuperInstruction

    data class ArrayNewDefaultS(
        val sizeSlot: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val field: Long,
    ) : AggregateSuperInstruction

    data class ArrayNewDataIi(
        val sourceOffset: Int,
        val arrayLength: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : AggregateSuperInstruction

    data class ArrayNewDataIs(
        val sourceOffset: Int,
        val arrayLengthSlot: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : AggregateSuperInstruction

    data class ArrayNewDataSi(
        val sourceOffsetSlot: Int,
        val arrayLength: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : AggregateSuperInstruction

    data class ArrayNewDataSs(
        val sourceOffsetSlot: Int,
        val arrayLengthSlot: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : AggregateSuperInstruction

    data class ArrayNewElementIi(
        val sourceOffset: Int,
        val arrayLength: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : AggregateSuperInstruction

    data class ArrayNewElementIs(
        val sourceOffset: Int,
        val arrayLengthSlot: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : AggregateSuperInstruction

    data class ArrayNewElementSi(
        val sourceOffsetSlot: Int,
        val arrayLength: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : AggregateSuperInstruction

    data class ArrayNewElementSs(
        val sourceOffsetSlot: Int,
        val arrayLengthSlot: Int,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : AggregateSuperInstruction

    data class ArrayNewFixedS(
        val valueSlots: List<Int>,
        val destinationSlot: Int,
        val rtt: RTT,
        val arrayType: ArrayType,
        val size: Int,
    ) : AggregateSuperInstruction

    data class ArraySetIi(
        val value: Long,
        val field: Int,
        val addressSlot: Int,
    ) : AggregateSuperInstruction

    data class ArraySetIs(
        val value: Long,
        val fieldSlot: Int,
        val addressSlot: Int,
    ) : AggregateSuperInstruction

    data class ArraySetSi(
        val valueSlot: Int,
        val field: Int,
        val addressSlot: Int,
    ) : AggregateSuperInstruction

    data class ArraySetSs(
        val valueSlot: Int,
        val fieldSlot: Int,
        val addressSlot: Int,
    ) : AggregateSuperInstruction

    data class ArrayInitDataIii(
        val elementsToCopy: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : AggregateSuperInstruction

    data class ArrayInitDataIis(
        val elementsToCopy: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : AggregateSuperInstruction

    data class ArrayInitDataIsi(
        val elementsToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : AggregateSuperInstruction

    data class ArrayInitDataIss(
        val elementsToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : AggregateSuperInstruction

    data class ArrayInitDataSii(
        val elementsToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : AggregateSuperInstruction

    data class ArrayInitDataSis(
        val elementsToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : AggregateSuperInstruction

    data class ArrayInitDataSsi(
        val elementsToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : AggregateSuperInstruction

    data class ArrayInitDataSss(
        val elementsToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
        val fieldWidthInBytes: Int,
    ) : AggregateSuperInstruction

    data class ArrayInitElementIii(
        val elementsToCopy: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : AggregateSuperInstruction

    data class ArrayInitElementIis(
        val elementsToCopy: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : AggregateSuperInstruction

    data class ArrayInitElementIsi(
        val elementsToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : AggregateSuperInstruction

    data class ArrayInitElementIss(
        val elementsToCopy: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : AggregateSuperInstruction

    data class ArrayInitElementSii(
        val elementsToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : AggregateSuperInstruction

    data class ArrayInitElementSis(
        val elementsToCopySlot: Int,
        val sourceOffset: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : AggregateSuperInstruction

    data class ArrayInitElementSsi(
        val elementsToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffset: Int,
        val addressSlot: Int,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : AggregateSuperInstruction

    data class ArrayInitElementSss(
        val elementsToCopySlot: Int,
        val sourceOffsetSlot: Int,
        val destinationOffsetSlot: Int,
        val addressSlot: Int,
        val elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
    ) : AggregateSuperInstruction

    data class RefI31I(
        val value: Int,
        val destinationSlot: Int,
    ) : AggregateSuperInstruction

    data class RefI31S(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : AggregateSuperInstruction

    data class I31GetSignedS(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : AggregateSuperInstruction

    data class I31GetUnsignedS(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : AggregateSuperInstruction

    data class AnyConvertExternS(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : AggregateSuperInstruction

    data class ExternConvertAnyS(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : AggregateSuperInstruction

    data class StructGetS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val fieldIndex: Int,
    ) : AggregateSuperInstruction

    data class StructGetSignedS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val fieldIndex: Int,
    ) : AggregateSuperInstruction

    data class StructGetUnsignedS(
        val addressSlot: Int,
        val destinationSlot: Int,
        val fieldIndex: Int,
    ) : AggregateSuperInstruction

    data class StructNewS(
        val fieldSlots: List<Int>,
        val destinationSlot: Int,
        val rtt: RTT,
        val structType: StructType,
    ) : AggregateSuperInstruction

    data class StructNewDefaultS(
        val destinationSlot: Int,
        val rtt: RTT,
        val structType: StructType,
        val fields: LongArray,
    ) : AggregateSuperInstruction

    data class StructSetI(
        val value: Long,
        val addressSlot: Int,
        val fieldIndex: Int,
    ) : AggregateSuperInstruction

    data class StructSetS(
        val valueSlot: Int,
        val addressSlot: Int,
        val fieldIndex: Int,
    ) : AggregateSuperInstruction
}
