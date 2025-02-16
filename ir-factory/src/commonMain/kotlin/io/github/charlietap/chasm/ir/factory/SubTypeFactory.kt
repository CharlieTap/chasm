package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.ir.type.CompositeType as IRCompositeType
import io.github.charlietap.chasm.ir.type.HeapType as IRHeapType
import io.github.charlietap.chasm.ir.type.SubType as IRSubType

internal fun SubTypeFactory(
    subType: SubType,
): IRSubType = SubTypeFactory(
    subType = subType,
    compositeTypeFactory = ::CompositeTypeFactory,
    heapTypeFactory = ::HeapTypeFactory,
)

internal inline fun SubTypeFactory(
    subType: SubType,
    compositeTypeFactory: IRFactory<CompositeType, IRCompositeType>,
    heapTypeFactory: IRFactory<HeapType, IRHeapType>,
): IRSubType {
    return when (subType) {
        is SubType.Open -> IRSubType.Open(
            superTypes = subType.superTypes.map(heapTypeFactory),
            compositeType = compositeTypeFactory(subType.compositeType),
        )

        is SubType.Final -> IRSubType.Final(
            superTypes = subType.superTypes.map(heapTypeFactory),
            compositeType = compositeTypeFactory(subType.compositeType),
        )
    }
}
