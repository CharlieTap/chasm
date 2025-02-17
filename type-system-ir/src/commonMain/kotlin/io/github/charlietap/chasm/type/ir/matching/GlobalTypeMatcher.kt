package io.github.charlietap.chasm.type.ir.matching

import io.github.charlietap.chasm.ir.type.GlobalType
import io.github.charlietap.chasm.ir.type.Mutability
import io.github.charlietap.chasm.ir.type.ValueType

fun GlobalTypeMatcher(
    type1: GlobalType,
    type2: GlobalType,
    context: TypeMatcherContext,
): Boolean = GlobalTypeMatcher(
    type1 = type1,
    type2 = type2,
    context = context,
    valueTypeMatcher = ::ValueTypeMatcher,
)

internal fun GlobalTypeMatcher(
    type1: GlobalType,
    type2: GlobalType,
    context: TypeMatcherContext,
    valueTypeMatcher: TypeMatcher<ValueType>,
): Boolean {
    return type1.mutability == type2.mutability &&
        valueTypeMatcher(type1.valueType, type2.valueType, context) &&
        when (type1.mutability) {
            Mutability.Const -> true
            Mutability.Var -> valueTypeMatcher(type2.valueType, type1.valueType, context)
        }
}
