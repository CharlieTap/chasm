package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ir.type.HeapType as IRHeapType
import io.github.charlietap.chasm.ir.type.ReferenceType as IRReferenceType

internal fun ReferenceTypeFactory(
    referenceType: ReferenceType,
): IRReferenceType = ReferenceTypeFactory(
    referenceType = referenceType,
    heapTypeFactory = ::HeapTypeFactory,
)

internal inline fun ReferenceTypeFactory(
    referenceType: ReferenceType,
    heapTypeFactory: IRFactory<HeapType, IRHeapType>,
): IRReferenceType {
    return when (referenceType) {
        is ReferenceType.Ref -> IRReferenceType.Ref(
            heapType = heapTypeFactory(referenceType.heapType),
        )

        is ReferenceType.RefNull -> IRReferenceType.RefNull(
            heapType = heapTypeFactory(referenceType.heapType),
        )
    }
}
