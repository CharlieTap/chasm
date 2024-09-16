package io.github.charlietap.chasm.type.rolling

import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.type.rolling.substitution.ConcreteHeapTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.RecursiveTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor

internal typealias RecursiveTypeUnroller = (RecursiveType) -> RecursiveType

internal fun RecursiveTypeUnroller(
    recursiveType: RecursiveType,
): RecursiveType =
    RecursiveTypeUnroller(
        recursiveType = recursiveType,
        recursiveTypeSubstitutor = ::RecursiveTypeSubstitutor,
    )

internal fun RecursiveTypeUnroller(
    recursiveType: RecursiveType,
    recursiveTypeSubstitutor: TypeSubstitutor<RecursiveType>,
): RecursiveType {
    val substitutor: ConcreteHeapTypeSubstitutor = { heapType ->
        when (heapType) {
            is ConcreteHeapType.RecursiveTypeIndex -> ConcreteHeapType.Defined(
                DefinedType(recursiveType, heapType.index),
            )
            else -> heapType
        }
    }
    return recursiveTypeSubstitutor(recursiveType, substitutor)
}
