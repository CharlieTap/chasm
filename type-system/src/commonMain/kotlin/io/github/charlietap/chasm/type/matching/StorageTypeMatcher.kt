package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType

fun StorageTypeMatcher(
    type1: StorageType,
    type2: StorageType,
    context: TypeMatcherContext,
): Boolean = StorageTypeMatcher(
    type1 = type1,
    type2 = type2,
    context = context,
    packedTypeMatcher = ::PackedTypeMatcher,
    valueTypeMatcher = ::ValueTypeMatcher,
)

internal fun StorageTypeMatcher(
    type1: StorageType,
    type2: StorageType,
    context: TypeMatcherContext,
    packedTypeMatcher: TypeMatcher<PackedType>,
    valueTypeMatcher: TypeMatcher<ValueType>,
): Boolean = when {
    type1 is StorageType.Packed && type2 is StorageType.Packed -> packedTypeMatcher(type1.type, type2.type, context)
    type1 is StorageType.Value && type2 is StorageType.Value -> valueTypeMatcher(type1.type, type2.type, context)
    else -> false
}
