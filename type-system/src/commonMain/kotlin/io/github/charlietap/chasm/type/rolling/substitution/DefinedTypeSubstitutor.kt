package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.RecursiveType

fun DefinedTypeSubstitutor(
    definedType: DefinedType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): DefinedType =
    DefinedTypeSubstitutor(
        definedType = definedType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        recursiveTypeSubstitutor = ::RecursiveTypeSubstitutor,
    )

internal fun DefinedTypeSubstitutor(
    definedType: DefinedType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
    recursiveTypeSubstitutor: TypeSubstitutor<RecursiveType>,
): DefinedType = definedType.apply {
    recursiveType = recursiveTypeSubstitutor(definedType.recursiveType, concreteHeapTypeSubstitutor)
}
