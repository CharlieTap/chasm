package io.github.charlietap.chasm.fixture.runtime.instruction

import io.github.charlietap.chasm.fixture.runtime.instance.dataInstance
import io.github.charlietap.chasm.fixture.runtime.instance.elementInstance
import io.github.charlietap.chasm.fixture.runtime.type.referenceTypeTest
import io.github.charlietap.chasm.fixture.runtime.type.rtt
import io.github.charlietap.chasm.fixture.type.packedType
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest
import io.github.charlietap.chasm.type.PackedType

fun aggregateRuntimeInstruction(): AggregateInstruction = arrayCopyIiiRuntimeInstruction()

fun arrayCopyIiiRuntimeInstruction(
    elementsToCopy: Int = 0,
    sourceOffset: Int = 0,
    destinationOffset: Int = 0,
    sourceAddressSlot: Int = 0,
    destinationAddressSlot: Int = 0,
) = AggregateInstruction.ArrayCopyIii(
    elementsToCopy = elementsToCopy,
    sourceOffset = sourceOffset,
    destinationOffset = destinationOffset,
    sourceAddressSlot = sourceAddressSlot,
    destinationAddressSlot = destinationAddressSlot,
)

fun arrayCopyIisRuntimeInstruction(
    elementsToCopy: Int = 0,
    sourceOffset: Int = 0,
    destinationOffsetSlot: Int = 0,
    sourceAddressSlot: Int = 0,
    destinationAddressSlot: Int = 0,
) = AggregateInstruction.ArrayCopyIis(
    elementsToCopy = elementsToCopy,
    sourceOffset = sourceOffset,
    destinationOffsetSlot = destinationOffsetSlot,
    sourceAddressSlot = sourceAddressSlot,
    destinationAddressSlot = destinationAddressSlot,
)

fun arrayCopyIsiRuntimeInstruction(
    elementsToCopy: Int = 0,
    sourceOffsetSlot: Int = 0,
    destinationOffset: Int = 0,
    sourceAddressSlot: Int = 0,
    destinationAddressSlot: Int = 0,
) = AggregateInstruction.ArrayCopyIsi(
    elementsToCopy = elementsToCopy,
    sourceOffsetSlot = sourceOffsetSlot,
    destinationOffset = destinationOffset,
    sourceAddressSlot = sourceAddressSlot,
    destinationAddressSlot = destinationAddressSlot,
)

fun arrayCopyIssRuntimeInstruction(
    elementsToCopy: Int = 0,
    sourceOffsetSlot: Int = 0,
    destinationOffsetSlot: Int = 0,
    sourceAddressSlot: Int = 0,
    destinationAddressSlot: Int = 0,
) = AggregateInstruction.ArrayCopyIss(
    elementsToCopy = elementsToCopy,
    sourceOffsetSlot = sourceOffsetSlot,
    destinationOffsetSlot = destinationOffsetSlot,
    sourceAddressSlot = sourceAddressSlot,
    destinationAddressSlot = destinationAddressSlot,
)

fun arrayCopySiiRuntimeInstruction(
    elementsToCopySlot: Int = 0,
    sourceOffset: Int = 0,
    destinationOffset: Int = 0,
    sourceAddressSlot: Int = 0,
    destinationAddressSlot: Int = 0,
) = AggregateInstruction.ArrayCopySii(
    elementsToCopySlot = elementsToCopySlot,
    sourceOffset = sourceOffset,
    destinationOffset = destinationOffset,
    sourceAddressSlot = sourceAddressSlot,
    destinationAddressSlot = destinationAddressSlot,
)

fun arrayCopySisRuntimeInstruction(
    elementsToCopySlot: Int = 0,
    sourceOffset: Int = 0,
    destinationOffsetSlot: Int = 0,
    sourceAddressSlot: Int = 0,
    destinationAddressSlot: Int = 0,
) = AggregateInstruction.ArrayCopySis(
    elementsToCopySlot = elementsToCopySlot,
    sourceOffset = sourceOffset,
    destinationOffsetSlot = destinationOffsetSlot,
    sourceAddressSlot = sourceAddressSlot,
    destinationAddressSlot = destinationAddressSlot,
)

fun arrayCopySsiRuntimeInstruction(
    elementsToCopySlot: Int = 0,
    sourceOffsetSlot: Int = 0,
    destinationOffset: Int = 0,
    sourceAddressSlot: Int = 0,
    destinationAddressSlot: Int = 0,
) = AggregateInstruction.ArrayCopySsi(
    elementsToCopySlot = elementsToCopySlot,
    sourceOffsetSlot = sourceOffsetSlot,
    destinationOffset = destinationOffset,
    sourceAddressSlot = sourceAddressSlot,
    destinationAddressSlot = destinationAddressSlot,
)

fun arrayCopySssRuntimeInstruction(
    elementsToCopySlot: Int = 0,
    sourceOffsetSlot: Int = 0,
    destinationOffsetSlot: Int = 0,
    sourceAddressSlot: Int = 0,
    destinationAddressSlot: Int = 0,
) = AggregateInstruction.ArrayCopySss(
    elementsToCopySlot = elementsToCopySlot,
    sourceOffsetSlot = sourceOffsetSlot,
    destinationOffsetSlot = destinationOffsetSlot,
    sourceAddressSlot = sourceAddressSlot,
    destinationAddressSlot = destinationAddressSlot,
)

fun arrayFillIiiRuntimeInstruction(
    elementsToFill: Int = 0,
    fillValue: Long = 0L,
    arrayElementOffset: Int = 0,
    addressSlot: Int = 0,
) = AggregateInstruction.ArrayFillIii(
    elementsToFill = elementsToFill,
    fillValue = fillValue,
    arrayElementOffset = arrayElementOffset,
    addressSlot = addressSlot,
)

fun arrayFillIisRuntimeInstruction(
    elementsToFill: Int = 0,
    fillValue: Long = 0L,
    arrayElementOffsetSlot: Int = 0,
    addressSlot: Int = 0,
) = AggregateInstruction.ArrayFillIis(
    elementsToFill = elementsToFill,
    fillValue = fillValue,
    arrayElementOffsetSlot = arrayElementOffsetSlot,
    addressSlot = addressSlot,
)

fun arrayFillIsiRuntimeInstruction(
    elementsToFill: Int = 0,
    fillValueSlot: Int = 0,
    arrayElementOffset: Int = 0,
    addressSlot: Int = 0,
) = AggregateInstruction.ArrayFillIsi(
    elementsToFill = elementsToFill,
    fillValueSlot = fillValueSlot,
    arrayElementOffset = arrayElementOffset,
    addressSlot = addressSlot,
)

fun arrayFillIssRuntimeInstruction(
    elementsToFill: Int = 0,
    fillValueSlot: Int = 0,
    arrayElementOffsetSlot: Int = 0,
    addressSlot: Int = 0,
) = AggregateInstruction.ArrayFillIss(
    elementsToFill = elementsToFill,
    fillValueSlot = fillValueSlot,
    arrayElementOffsetSlot = arrayElementOffsetSlot,
    addressSlot = addressSlot,
)

fun arrayFillSiiRuntimeInstruction(
    elementsToFillSlot: Int = 0,
    fillValue: Long = 0L,
    arrayElementOffset: Int = 0,
    addressSlot: Int = 0,
) = AggregateInstruction.ArrayFillSii(
    elementsToFillSlot = elementsToFillSlot,
    fillValue = fillValue,
    arrayElementOffset = arrayElementOffset,
    addressSlot = addressSlot,
)

fun arrayFillSisRuntimeInstruction(
    elementsToFillSlot: Int = 0,
    fillValue: Long = 0L,
    arrayElementOffsetSlot: Int = 0,
    addressSlot: Int = 0,
) = AggregateInstruction.ArrayFillSis(
    elementsToFillSlot = elementsToFillSlot,
    fillValue = fillValue,
    arrayElementOffsetSlot = arrayElementOffsetSlot,
    addressSlot = addressSlot,
)

fun arrayFillSsiRuntimeInstruction(
    elementsToFillSlot: Int = 0,
    fillValueSlot: Int = 0,
    arrayElementOffset: Int = 0,
    addressSlot: Int = 0,
) = AggregateInstruction.ArrayFillSsi(
    elementsToFillSlot = elementsToFillSlot,
    fillValueSlot = fillValueSlot,
    arrayElementOffset = arrayElementOffset,
    addressSlot = addressSlot,
)

fun arrayFillSssRuntimeInstruction(
    elementsToFillSlot: Int = 0,
    fillValueSlot: Int = 0,
    arrayElementOffsetSlot: Int = 0,
    addressSlot: Int = 0,
) = AggregateInstruction.ArrayFillSss(
    elementsToFillSlot = elementsToFillSlot,
    fillValueSlot = fillValueSlot,
    arrayElementOffsetSlot = arrayElementOffsetSlot,
    addressSlot = addressSlot,
)

fun arrayGetIRuntimeInstruction(
    addressSlot: Int = 0,
    field: Int = 0,
    destinationSlot: Int = 0,
) = AggregateInstruction.ArrayGetI(
    addressSlot = addressSlot,
    field = field,
    destinationSlot = destinationSlot,
)

fun arrayGetSRuntimeInstruction(
    addressSlot: Int = 0,
    fieldSlot: Int = 0,
    destinationSlot: Int = 0,
) = AggregateInstruction.ArrayGetS(
    addressSlot = addressSlot,
    fieldSlot = fieldSlot,
    destinationSlot = destinationSlot,
)

fun arrayGetSignedIRuntimeInstruction(
    addressSlot: Int = 0,
    field: Int = 0,
    destinationSlot: Int = 0,
    packedType: PackedType = packedType(),
) = AggregateInstruction.ArrayGetSignedI(
    addressSlot = addressSlot,
    field = field,
    destinationSlot = destinationSlot,
    packedType = packedType,
)

fun arrayGetSignedSRuntimeInstruction(
    addressSlot: Int = 0,
    fieldSlot: Int = 0,
    destinationSlot: Int = 0,
    packedType: PackedType = packedType(),
) = AggregateInstruction.ArrayGetSignedS(
    addressSlot = addressSlot,
    fieldSlot = fieldSlot,
    destinationSlot = destinationSlot,
    packedType = packedType,
)

fun arrayGetUnsignedIRuntimeInstruction(
    addressSlot: Int = 0,
    field: Int = 0,
    destinationSlot: Int = 0,
    packedType: PackedType = packedType(),
) = AggregateInstruction.ArrayGetUnsignedI(
    addressSlot = addressSlot,
    field = field,
    destinationSlot = destinationSlot,
    packedType = packedType,
)

fun arrayGetUnsignedSRuntimeInstruction(
    addressSlot: Int = 0,
    fieldSlot: Int = 0,
    destinationSlot: Int = 0,
    packedType: PackedType = packedType(),
) = AggregateInstruction.ArrayGetUnsignedS(
    addressSlot = addressSlot,
    fieldSlot = fieldSlot,
    destinationSlot = destinationSlot,
    packedType = packedType,
)

fun arrayLenSRuntimeInstruction(
    addressSlot: Int = 0,
    destinationSlot: Int = 0,
) = AggregateInstruction.ArrayLenS(
    addressSlot = addressSlot,
    destinationSlot = destinationSlot,
)

fun arrayNewIiRuntimeInstruction(
    size: Int = 0,
    value: Long = 0L,
    destinationSlot: Int = 0,
    rtt: RTT = rtt(),
) = AggregateInstruction.ArrayNewIi(
    size = size,
    value = value,
    destinationSlot = destinationSlot,
    rtt = rtt,
)

fun arrayNewIsRuntimeInstruction(
    size: Int = 0,
    valueSlot: Int = 0,
    destinationSlot: Int = 0,
    rtt: RTT = rtt(),
) = AggregateInstruction.ArrayNewIs(
    size = size,
    valueSlot = valueSlot,
    destinationSlot = destinationSlot,
    rtt = rtt,
)

fun arrayNewSiRuntimeInstruction(
    sizeSlot: Int = 0,
    value: Long = 0L,
    destinationSlot: Int = 0,
    rtt: RTT = rtt(),
) = AggregateInstruction.ArrayNewSi(
    sizeSlot = sizeSlot,
    value = value,
    destinationSlot = destinationSlot,
    rtt = rtt,
)

fun arrayNewSsRuntimeInstruction(
    sizeSlot: Int = 0,
    valueSlot: Int = 0,
    destinationSlot: Int = 0,
    rtt: RTT = rtt(),
) = AggregateInstruction.ArrayNewSs(
    sizeSlot = sizeSlot,
    valueSlot = valueSlot,
    destinationSlot = destinationSlot,
    rtt = rtt,
)

fun arrayNewDefaultIRuntimeInstruction(
    size: Int = 0,
    destinationSlot: Int = 0,
    rtt: RTT = rtt(),
    field: Long = 0L,
) = AggregateInstruction.ArrayNewDefaultI(
    size = size,
    destinationSlot = destinationSlot,
    rtt = rtt,
    field = field,
)

fun arrayNewDefaultSRuntimeInstruction(
    sizeSlot: Int = 0,
    destinationSlot: Int = 0,
    rtt: RTT = rtt(),
    field: Long = 0L,
) = AggregateInstruction.ArrayNewDefaultS(
    sizeSlot = sizeSlot,
    destinationSlot = destinationSlot,
    rtt = rtt,
    field = field,
)

fun arrayNewDataIiRuntimeInstruction(
    sourceOffset: Int = 0,
    arrayLength: Int = 0,
    destinationSlot: Int = 0,
    rtt: RTT = rtt(),
    dataInstance: DataInstance = dataInstance(),
    fieldWidthInBytes: Int = 0,
) = AggregateInstruction.ArrayNewDataIi(
    sourceOffset = sourceOffset,
    arrayLength = arrayLength,
    destinationSlot = destinationSlot,
    rtt = rtt,
    dataInstance = dataInstance,
    fieldWidthInBytes = fieldWidthInBytes,
)

fun arrayNewDataIsRuntimeInstruction(
    sourceOffset: Int = 0,
    arrayLengthSlot: Int = 0,
    destinationSlot: Int = 0,
    rtt: RTT = rtt(),
    dataInstance: DataInstance = dataInstance(),
    fieldWidthInBytes: Int = 0,
) = AggregateInstruction.ArrayNewDataIs(
    sourceOffset = sourceOffset,
    arrayLengthSlot = arrayLengthSlot,
    destinationSlot = destinationSlot,
    rtt = rtt,
    dataInstance = dataInstance,
    fieldWidthInBytes = fieldWidthInBytes,
)

fun arrayNewDataSiRuntimeInstruction(
    sourceOffsetSlot: Int = 0,
    arrayLength: Int = 0,
    destinationSlot: Int = 0,
    rtt: RTT = rtt(),
    dataInstance: DataInstance = dataInstance(),
    fieldWidthInBytes: Int = 0,
) = AggregateInstruction.ArrayNewDataSi(
    sourceOffsetSlot = sourceOffsetSlot,
    arrayLength = arrayLength,
    destinationSlot = destinationSlot,
    rtt = rtt,
    dataInstance = dataInstance,
    fieldWidthInBytes = fieldWidthInBytes,
)

fun arrayNewDataSsRuntimeInstruction(
    sourceOffsetSlot: Int = 0,
    arrayLengthSlot: Int = 0,
    destinationSlot: Int = 0,
    rtt: RTT = rtt(),
    dataInstance: DataInstance = dataInstance(),
    fieldWidthInBytes: Int = 0,
) = AggregateInstruction.ArrayNewDataSs(
    sourceOffsetSlot = sourceOffsetSlot,
    arrayLengthSlot = arrayLengthSlot,
    destinationSlot = destinationSlot,
    rtt = rtt,
    dataInstance = dataInstance,
    fieldWidthInBytes = fieldWidthInBytes,
)

fun arrayNewElementIiRuntimeInstruction(
    sourceOffset: Int = 0,
    arrayLength: Int = 0,
    destinationSlot: Int = 0,
    rtt: RTT = rtt(),
    elementInstance: ElementInstance = elementInstance(),
) = AggregateInstruction.ArrayNewElementIi(
    sourceOffset = sourceOffset,
    arrayLength = arrayLength,
    destinationSlot = destinationSlot,
    rtt = rtt,
    elementInstance = elementInstance,
)

fun arrayNewElementIsRuntimeInstruction(
    sourceOffset: Int = 0,
    arrayLengthSlot: Int = 0,
    destinationSlot: Int = 0,
    rtt: RTT = rtt(),
    elementInstance: ElementInstance = elementInstance(),
) = AggregateInstruction.ArrayNewElementIs(
    sourceOffset = sourceOffset,
    arrayLengthSlot = arrayLengthSlot,
    destinationSlot = destinationSlot,
    rtt = rtt,
    elementInstance = elementInstance,
)

fun arrayNewElementSiRuntimeInstruction(
    sourceOffsetSlot: Int = 0,
    arrayLength: Int = 0,
    destinationSlot: Int = 0,
    rtt: RTT = rtt(),
    elementInstance: ElementInstance = elementInstance(),
) = AggregateInstruction.ArrayNewElementSi(
    sourceOffsetSlot = sourceOffsetSlot,
    arrayLength = arrayLength,
    destinationSlot = destinationSlot,
    rtt = rtt,
    elementInstance = elementInstance,
)

fun arrayNewElementSsRuntimeInstruction(
    sourceOffsetSlot: Int = 0,
    arrayLengthSlot: Int = 0,
    destinationSlot: Int = 0,
    rtt: RTT = rtt(),
    elementInstance: ElementInstance = elementInstance(),
) = AggregateInstruction.ArrayNewElementSs(
    sourceOffsetSlot = sourceOffsetSlot,
    arrayLengthSlot = arrayLengthSlot,
    destinationSlot = destinationSlot,
    rtt = rtt,
    elementInstance = elementInstance,
)

fun arrayNewFixedSRuntimeInstruction(
    firstElementSlot: Int = 0,
    destinationSlot: Int = 0,
    rtt: RTT = rtt(),
    size: Int = 0,
) = AggregateInstruction.ArrayNewFixedS(
    firstElementSlot = firstElementSlot,
    destinationSlot = destinationSlot,
    rtt = rtt,
    size = size,
)

fun arraySetIiRuntimeInstruction(
    value: Long = 0L,
    field: Int = 0,
    addressSlot: Int = 0,
) = AggregateInstruction.ArraySetIi(
    value = value,
    field = field,
    addressSlot = addressSlot,
)

fun arraySetIsRuntimeInstruction(
    value: Long = 0L,
    fieldSlot: Int = 0,
    addressSlot: Int = 0,
) = AggregateInstruction.ArraySetIs(
    value = value,
    fieldSlot = fieldSlot,
    addressSlot = addressSlot,
)

fun arraySetSiRuntimeInstruction(
    valueSlot: Int = 0,
    field: Int = 0,
    addressSlot: Int = 0,
) = AggregateInstruction.ArraySetSi(
    valueSlot = valueSlot,
    field = field,
    addressSlot = addressSlot,
)

fun arraySetSsRuntimeInstruction(
    valueSlot: Int = 0,
    fieldSlot: Int = 0,
    addressSlot: Int = 0,
) = AggregateInstruction.ArraySetSs(
    valueSlot = valueSlot,
    fieldSlot = fieldSlot,
    addressSlot = addressSlot,
)

fun arrayInitDataIiiRuntimeInstruction(
    elementsToCopy: Int = 0,
    sourceOffset: Int = 0,
    destinationOffset: Int = 0,
    addressSlot: Int = 0,
    dataInstance: DataInstance = dataInstance(),
    fieldWidthInBytes: Int = 0,
) = AggregateInstruction.ArrayInitDataIii(
    elementsToCopy = elementsToCopy,
    sourceOffset = sourceOffset,
    destinationOffset = destinationOffset,
    addressSlot = addressSlot,
    dataInstance = dataInstance,
    fieldWidthInBytes = fieldWidthInBytes,
)

fun arrayInitDataIisRuntimeInstruction(
    elementsToCopy: Int = 0,
    sourceOffset: Int = 0,
    destinationOffsetSlot: Int = 0,
    addressSlot: Int = 0,
    dataInstance: DataInstance = dataInstance(),
    fieldWidthInBytes: Int = 0,
) = AggregateInstruction.ArrayInitDataIis(
    elementsToCopy = elementsToCopy,
    sourceOffset = sourceOffset,
    destinationOffsetSlot = destinationOffsetSlot,
    addressSlot = addressSlot,
    dataInstance = dataInstance,
    fieldWidthInBytes = fieldWidthInBytes,
)

fun arrayInitDataIsiRuntimeInstruction(
    elementsToCopy: Int = 0,
    sourceOffsetSlot: Int = 0,
    destinationOffset: Int = 0,
    addressSlot: Int = 0,
    dataInstance: DataInstance = dataInstance(),
    fieldWidthInBytes: Int = 0,
) = AggregateInstruction.ArrayInitDataIsi(
    elementsToCopy = elementsToCopy,
    sourceOffsetSlot = sourceOffsetSlot,
    destinationOffset = destinationOffset,
    addressSlot = addressSlot,
    dataInstance = dataInstance,
    fieldWidthInBytes = fieldWidthInBytes,
)

fun arrayInitDataIssRuntimeInstruction(
    elementsToCopy: Int = 0,
    sourceOffsetSlot: Int = 0,
    destinationOffsetSlot: Int = 0,
    addressSlot: Int = 0,
    dataInstance: DataInstance = dataInstance(),
    fieldWidthInBytes: Int = 0,
) = AggregateInstruction.ArrayInitDataIss(
    elementsToCopy = elementsToCopy,
    sourceOffsetSlot = sourceOffsetSlot,
    destinationOffsetSlot = destinationOffsetSlot,
    addressSlot = addressSlot,
    dataInstance = dataInstance,
    fieldWidthInBytes = fieldWidthInBytes,
)

fun arrayInitDataSiiRuntimeInstruction(
    elementsToCopySlot: Int = 0,
    sourceOffset: Int = 0,
    destinationOffset: Int = 0,
    addressSlot: Int = 0,
    dataInstance: DataInstance = dataInstance(),
    fieldWidthInBytes: Int = 0,
) = AggregateInstruction.ArrayInitDataSii(
    elementsToCopySlot = elementsToCopySlot,
    sourceOffset = sourceOffset,
    destinationOffset = destinationOffset,
    addressSlot = addressSlot,
    dataInstance = dataInstance,
    fieldWidthInBytes = fieldWidthInBytes,
)

fun arrayInitDataSisRuntimeInstruction(
    elementsToCopySlot: Int = 0,
    sourceOffset: Int = 0,
    destinationOffsetSlot: Int = 0,
    addressSlot: Int = 0,
    dataInstance: DataInstance = dataInstance(),
    fieldWidthInBytes: Int = 0,
) = AggregateInstruction.ArrayInitDataSis(
    elementsToCopySlot = elementsToCopySlot,
    sourceOffset = sourceOffset,
    destinationOffsetSlot = destinationOffsetSlot,
    addressSlot = addressSlot,
    dataInstance = dataInstance,
    fieldWidthInBytes = fieldWidthInBytes,
)

fun arrayInitDataSsiRuntimeInstruction(
    elementsToCopySlot: Int = 0,
    sourceOffsetSlot: Int = 0,
    destinationOffset: Int = 0,
    addressSlot: Int = 0,
    dataInstance: DataInstance = dataInstance(),
    fieldWidthInBytes: Int = 0,
) = AggregateInstruction.ArrayInitDataSsi(
    elementsToCopySlot = elementsToCopySlot,
    sourceOffsetSlot = sourceOffsetSlot,
    destinationOffset = destinationOffset,
    addressSlot = addressSlot,
    dataInstance = dataInstance,
    fieldWidthInBytes = fieldWidthInBytes,
)

fun arrayInitDataSssRuntimeInstruction(
    elementsToCopySlot: Int = 0,
    sourceOffsetSlot: Int = 0,
    destinationOffsetSlot: Int = 0,
    addressSlot: Int = 0,
    dataInstance: DataInstance = dataInstance(),
    fieldWidthInBytes: Int = 0,
) = AggregateInstruction.ArrayInitDataSss(
    elementsToCopySlot = elementsToCopySlot,
    sourceOffsetSlot = sourceOffsetSlot,
    destinationOffsetSlot = destinationOffsetSlot,
    addressSlot = addressSlot,
    dataInstance = dataInstance,
    fieldWidthInBytes = fieldWidthInBytes,
)

fun arrayInitElementIiiRuntimeInstruction(
    elementsToCopy: Int = 0,
    sourceOffset: Int = 0,
    destinationOffset: Int = 0,
    addressSlot: Int = 0,
    elementInstance: ElementInstance = elementInstance(),
) = AggregateInstruction.ArrayInitElementIii(
    elementsToCopy = elementsToCopy,
    sourceOffset = sourceOffset,
    destinationOffset = destinationOffset,
    addressSlot = addressSlot,
    elementInstance = elementInstance,
)

fun arrayInitElementIisRuntimeInstruction(
    elementsToCopy: Int = 0,
    sourceOffset: Int = 0,
    destinationOffsetSlot: Int = 0,
    addressSlot: Int = 0,
    elementInstance: ElementInstance = elementInstance(),
) = AggregateInstruction.ArrayInitElementIis(
    elementsToCopy = elementsToCopy,
    sourceOffset = sourceOffset,
    destinationOffsetSlot = destinationOffsetSlot,
    addressSlot = addressSlot,
    elementInstance = elementInstance,
)

fun arrayInitElementIsiRuntimeInstruction(
    elementsToCopy: Int = 0,
    sourceOffsetSlot: Int = 0,
    destinationOffset: Int = 0,
    addressSlot: Int = 0,
    elementInstance: ElementInstance = elementInstance(),
) = AggregateInstruction.ArrayInitElementIsi(
    elementsToCopy = elementsToCopy,
    sourceOffsetSlot = sourceOffsetSlot,
    destinationOffset = destinationOffset,
    addressSlot = addressSlot,
    elementInstance = elementInstance,
)

fun arrayInitElementIssRuntimeInstruction(
    elementsToCopy: Int = 0,
    sourceOffsetSlot: Int = 0,
    destinationOffsetSlot: Int = 0,
    addressSlot: Int = 0,
    elementInstance: ElementInstance = elementInstance(),
) = AggregateInstruction.ArrayInitElementIss(
    elementsToCopy = elementsToCopy,
    sourceOffsetSlot = sourceOffsetSlot,
    destinationOffsetSlot = destinationOffsetSlot,
    addressSlot = addressSlot,
    elementInstance = elementInstance,
)

fun arrayInitElementSiiRuntimeInstruction(
    elementsToCopySlot: Int = 0,
    sourceOffset: Int = 0,
    destinationOffset: Int = 0,
    addressSlot: Int = 0,
    elementInstance: ElementInstance = elementInstance(),
) = AggregateInstruction.ArrayInitElementSii(
    elementsToCopySlot = elementsToCopySlot,
    sourceOffset = sourceOffset,
    destinationOffset = destinationOffset,
    addressSlot = addressSlot,
    elementInstance = elementInstance,
)

fun arrayInitElementSisRuntimeInstruction(
    elementsToCopySlot: Int = 0,
    sourceOffset: Int = 0,
    destinationOffsetSlot: Int = 0,
    addressSlot: Int = 0,
    elementInstance: ElementInstance = elementInstance(),
) = AggregateInstruction.ArrayInitElementSis(
    elementsToCopySlot = elementsToCopySlot,
    sourceOffset = sourceOffset,
    destinationOffsetSlot = destinationOffsetSlot,
    addressSlot = addressSlot,
    elementInstance = elementInstance,
)

fun arrayInitElementSsiRuntimeInstruction(
    elementsToCopySlot: Int = 0,
    sourceOffsetSlot: Int = 0,
    destinationOffset: Int = 0,
    addressSlot: Int = 0,
    elementInstance: ElementInstance = elementInstance(),
) = AggregateInstruction.ArrayInitElementSsi(
    elementsToCopySlot = elementsToCopySlot,
    sourceOffsetSlot = sourceOffsetSlot,
    destinationOffset = destinationOffset,
    addressSlot = addressSlot,
    elementInstance = elementInstance,
)

fun arrayInitElementSssRuntimeInstruction(
    elementsToCopySlot: Int = 0,
    sourceOffsetSlot: Int = 0,
    destinationOffsetSlot: Int = 0,
    addressSlot: Int = 0,
    elementInstance: ElementInstance = elementInstance(),
) = AggregateInstruction.ArrayInitElementSss(
    elementsToCopySlot = elementsToCopySlot,
    sourceOffsetSlot = sourceOffsetSlot,
    destinationOffsetSlot = destinationOffsetSlot,
    addressSlot = addressSlot,
    elementInstance = elementInstance,
)

fun refI31IRuntimeInstruction(
    value: Int = 0,
    destinationSlot: Int = 0,
) = AggregateInstruction.RefI31I(
    value = value,
    destinationSlot = destinationSlot,
)

fun refI31SRuntimeInstruction(
    valueSlot: Int = 0,
    destinationSlot: Int = 0,
) = AggregateInstruction.RefI31S(
    valueSlot = valueSlot,
    destinationSlot = destinationSlot,
)

fun i31GetSignedSRuntimeInstruction(
    valueSlot: Int = 0,
    destinationSlot: Int = 0,
) = AggregateInstruction.I31GetSignedS(
    valueSlot = valueSlot,
    destinationSlot = destinationSlot,
)

fun i31GetUnsignedSRuntimeInstruction(
    valueSlot: Int = 0,
    destinationSlot: Int = 0,
) = AggregateInstruction.I31GetUnsignedS(
    valueSlot = valueSlot,
    destinationSlot = destinationSlot,
)

fun anyConvertExternSRuntimeInstruction(
    valueSlot: Int = 0,
    destinationSlot: Int = 0,
) = AggregateInstruction.AnyConvertExternS(
    valueSlot = valueSlot,
    destinationSlot = destinationSlot,
)

fun externConvertAnySRuntimeInstruction(
    valueSlot: Int = 0,
    destinationSlot: Int = 0,
) = AggregateInstruction.ExternConvertAnyS(
    valueSlot = valueSlot,
    destinationSlot = destinationSlot,
)

fun structGetSRuntimeInstruction(
    addressSlot: Int = 0,
    destinationSlot: Int = 0,
    fieldIndex: Int = 0,
) = AggregateInstruction.StructGetS(
    addressSlot = addressSlot,
    destinationSlot = destinationSlot,
    fieldIndex = fieldIndex,
)

fun structGetSignedSRuntimeInstruction(
    addressSlot: Int = 0,
    destinationSlot: Int = 0,
    fieldIndex: Int = 0,
    packedType: PackedType = packedType(),
) = AggregateInstruction.StructGetSignedS(
    addressSlot = addressSlot,
    destinationSlot = destinationSlot,
    fieldIndex = fieldIndex,
    packedType = packedType,
)

fun structGetUnsignedSRuntimeInstruction(
    addressSlot: Int = 0,
    destinationSlot: Int = 0,
    fieldIndex: Int = 0,
    packedType: PackedType = packedType(),
) = AggregateInstruction.StructGetUnsignedS(
    addressSlot = addressSlot,
    destinationSlot = destinationSlot,
    fieldIndex = fieldIndex,
    packedType = packedType,
)

fun refCastStructGetSRuntimeInstruction(
    referenceSlot: Int = 0,
    destinationSlot: Int = 0,
    typeTest: ReferenceTypeTest = referenceTypeTest(),
    fieldIndex: Int = 0,
) = AggregateInstruction.RefCastStructGetS(
    referenceSlot = referenceSlot,
    destinationSlot = destinationSlot,
    typeTest = typeTest,
    fieldIndex = fieldIndex,
)

fun structGetStructGetSRuntimeInstruction(
    addressSlot: Int = 0,
    destinationSlot: Int = 0,
    firstFieldIndex: Int = 0,
    secondFieldIndex: Int = 0,
) = AggregateInstruction.StructGetStructGetS(
    addressSlot = addressSlot,
    destinationSlot = destinationSlot,
    firstFieldIndex = firstFieldIndex,
    secondFieldIndex = secondFieldIndex,
)

fun localSetStructGetSRuntimeInstruction(
    sourceSlot: Int = 0,
    localSlot: Int = 0,
    destinationSlot: Int = 0,
    fieldIndex: Int = 0,
) = AggregateInstruction.LocalSetStructGetS(
    sourceSlot = sourceSlot,
    localSlot = localSlot,
    destinationSlot = destinationSlot,
    fieldIndex = fieldIndex,
)

fun structNewSRuntimeInstruction(
    firstFieldSlot: Int = 0,
    destinationSlot: Int = 0,
    rtt: RTT = rtt(),
) = AggregateInstruction.StructNewS(
    firstFieldSlot = firstFieldSlot,
    destinationSlot = destinationSlot,
    rtt = rtt,
)

fun structNewDefaultSRuntimeInstruction(
    destinationSlot: Int = 0,
    rtt: RTT = rtt(),
    fields: LongArray = longArrayOf(),
) = AggregateInstruction.StructNewDefaultS(
    destinationSlot = destinationSlot,
    rtt = rtt,
    fields = fields,
)

fun structSetIRuntimeInstruction(
    value: Long = 0L,
    addressSlot: Int = 0,
    fieldIndex: Int = 0,
) = AggregateInstruction.StructSetI(
    value = value,
    addressSlot = addressSlot,
    fieldIndex = fieldIndex,
)

fun structSetSRuntimeInstruction(
    valueSlot: Int = 0,
    addressSlot: Int = 0,
    fieldIndex: Int = 0,
) = AggregateInstruction.StructSetS(
    valueSlot = valueSlot,
    addressSlot = addressSlot,
    fieldIndex = fieldIndex,
)
