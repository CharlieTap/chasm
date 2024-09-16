package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.RecursiveType

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
): DefinedType = definedType.copy(
    recursiveTypeSubstitutor(definedType.recursiveType, concreteHeapTypeSubstitutor),
)
