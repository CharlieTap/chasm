package io.github.charlietap.chasm.type.rolling

import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.copy.DeepCopier
import io.github.charlietap.chasm.type.copy.SubTypeDeepCopier
import io.github.charlietap.chasm.type.rolling.substitution.SubTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.Substitution
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor

/*
    A DefinedType is a combination of a pointer to the root of a RecursiveType group
    and its index into that group.
    Unrolling effectively chases that pointer, yielding a specific SubType in the
    RecursiveType group, where all of its internal and external references to other
    RecursiveTypes have been replaced with ConcreteDefinedTypes.
 */

typealias DefinedTypeUnroller = (DefinedType) -> SubType

fun DefinedTypeUnroller(
    definedType: DefinedType,
): SubType = DefinedTypeUnroller(
    definedType = definedType,
    subTypeCopier = ::SubTypeDeepCopier,
    subTypeSubstitutor = ::SubTypeSubstitutor,
)

internal fun DefinedTypeUnroller(
    definedType: DefinedType,
    subTypeCopier: DeepCopier<SubType>,
    subTypeSubstitutor: TypeSubstitutor<SubType>,
): SubType {
    val closedSubType = definedType.recursiveType.subTypes[definedType.recursiveTypeIndex]
    val copy = subTypeCopier(closedSubType)
    val substitution = Substitution.RecursiveTypeIndexToDefinedType(definedType.recursiveType)
    return subTypeSubstitutor(copy, substitution)
}
