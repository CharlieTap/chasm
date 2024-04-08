package io.github.charlietap.chasm.executor.type.matching

import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.ast.type.StorageType

fun FieldTypeMatcher(
    type1: FieldType,
    type2: FieldType,
    context: TypeMatcherContext,
): Boolean = FieldTypeMatcher(
    type1 = type1,
    type2 = type2,
    context = context,
    storageTypeMatcher = ::StorageTypeMatcher,
)

fun FieldTypeMatcher(
    type1: FieldType,
    type2: FieldType,
    context: TypeMatcherContext,
    storageTypeMatcher: TypeMatcher<StorageType>,
): Boolean {

    val storageType1 = type1.storageType
    val storageType2 = type2.storageType

    val mutability1 = type1.mutability
    val mutability2 = type2.mutability

    return if (mutability1 == mutability2 && storageTypeMatcher(storageType1, storageType2, context)) {
        when (mutability1) {
            Mutability.Const -> true
            Mutability.Var -> storageTypeMatcher(storageType2, storageType1, context)
        }
    } else {
        false
    }
}
