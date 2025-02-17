package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.ir.type.Limits
import io.github.charlietap.chasm.ir.type.ReferenceType
import io.github.charlietap.chasm.ir.type.TableType

fun TableTypeMatcher(
    type1: TableType,
    type2: TableType,
    context: TypeMatcherContext,
): Boolean = TableTypeMatcher(
    type1 = type1,
    type2 = type2,
    context = context,
    limitsMatcher = ::LimitsMatcher,
    referenceTypeMatcher = ::ReferenceTypeMatcher,
)

internal fun TableTypeMatcher(
    type1: TableType,
    type2: TableType,
    context: TypeMatcherContext,
    limitsMatcher: TypeMatcher<Limits>,
    referenceTypeMatcher: TypeMatcher<ReferenceType>,
): Boolean = referenceTypeMatcher(type1.referenceType, type2.referenceType, context) &&
    limitsMatcher(type1.limits, type2.limits, context)
