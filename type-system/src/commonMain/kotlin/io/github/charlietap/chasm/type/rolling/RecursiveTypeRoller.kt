package io.github.charlietap.chasm.type.rolling

import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.type.rolling.substitution.ConcreteHeapTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.RecursiveTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor

internal typealias RecursiveTypeRoller = (Int, RecursiveType) -> RecursiveType

internal fun RecursiveTypeRoller(
    index: Int,
    recursiveType: RecursiveType,
): RecursiveType =
    RecursiveTypeRoller(
        index = index,
        recursiveType = recursiveType,
        recursiveTypeSubstitutor = ::RecursiveTypeSubstitutor,
    )

internal fun RecursiveTypeRoller(
    index: Int,
    recursiveType: RecursiveType,
    recursiveTypeSubstitutor: TypeSubstitutor<RecursiveType>,
): RecursiveType {
    val recursiveIndexUpperBound = index + recursiveType.subTypes.size
    val substitutor: ConcreteHeapTypeSubstitutor = { heapType ->
        when (heapType) {
            is ConcreteHeapType.TypeIndex -> {
                val typeIndex = heapType.index.idx.toInt()
                if (typeIndex in index..<recursiveIndexUpperBound) {
                    ConcreteHeapType.RecursiveTypeIndex(typeIndex - index)
                } else {
                    heapType
                }
            }
            else -> heapType
        }
    }
    return recursiveTypeSubstitutor(recursiveType, substitutor)
}
