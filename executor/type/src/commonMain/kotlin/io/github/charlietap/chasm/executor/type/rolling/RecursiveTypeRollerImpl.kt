package io.github.charlietap.chasm.executor.type.rolling

import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.executor.type.rolling.substitution.ConcreteHeapTypeSubstitutor
import io.github.charlietap.chasm.executor.type.rolling.substitution.RecursiveTypeSubstitutorImpl
import io.github.charlietap.chasm.executor.type.rolling.substitution.TypeSubstitutor

fun RecursiveTypeRollerImpl(
    index: Int,
    recursiveType: RecursiveType,
): RecursiveType =
    RecursiveTypeRollerImpl(
        index = index,
        recursiveType = recursiveType,
        recursiveTypeSubstitutor = ::RecursiveTypeSubstitutorImpl,
    )

internal fun RecursiveTypeRollerImpl(
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
