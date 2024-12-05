package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.StructType

fun CompositeTypeMatcher(
    type1: CompositeType,
    type2: CompositeType,
    context: TypeMatcherContext,
): Boolean = CompositeTypeMatcher(
    type1 = type1,
    type2 = type2,
    context = context,
    arrayTypeMatcher = ::ArrayTypeMatcher,
    functionTypeMatcher = ::FunctionTypeMatcher,
    structTypeMatcher = ::StructTypeMatcher,
)

internal fun CompositeTypeMatcher(
    type1: CompositeType,
    type2: CompositeType,
    context: TypeMatcherContext,
    arrayTypeMatcher: TypeMatcher<ArrayType>,
    functionTypeMatcher: TypeMatcher<FunctionType>,
    structTypeMatcher: TypeMatcher<StructType>,
): Boolean = when(type1) {
    is CompositeType.Array if type2 is CompositeType.Array -> arrayTypeMatcher(type1.arrayType, type2.arrayType, context)
    is CompositeType.Function if type2 is CompositeType.Function -> functionTypeMatcher(
        type1.functionType,
        type2.functionType,
        context,
    )
    is CompositeType.Struct if type2 is CompositeType.Struct -> structTypeMatcher(type1.structType, type2.structType, context)
    else -> false
}
