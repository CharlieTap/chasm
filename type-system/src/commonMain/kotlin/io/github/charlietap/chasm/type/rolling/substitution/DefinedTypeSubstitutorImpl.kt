package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.RecursiveType

fun DefinedTypeSubstitutorImpl(
    definedType: DefinedType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): DefinedType =
    DefinedTypeSubstitutorImpl(
        definedType = definedType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        recursiveTypeSubstitutor = ::RecursiveTypeSubstitutorImpl,
    )

internal fun DefinedTypeSubstitutorImpl(
    definedType: DefinedType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
    recursiveTypeSubstitutor: TypeSubstitutor<RecursiveType>,
): DefinedType = definedType.copy(
    recursiveTypeSubstitutor(definedType.recursiveType, concreteHeapTypeSubstitutor),
)
