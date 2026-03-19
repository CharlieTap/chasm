package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.TableType

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
): Boolean =
    // Tables are mutable, so their element reference type must match in both directions.
    referenceTypeMatcher(type1.referenceType, type2.referenceType, context) &&
        referenceTypeMatcher(type2.referenceType, type1.referenceType, context) &&
        limitsMatcher(type1.limits, type2.limits, context)
