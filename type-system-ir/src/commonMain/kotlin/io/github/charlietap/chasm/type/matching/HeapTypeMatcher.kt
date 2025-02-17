package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.ir.type.AbstractHeapType
import io.github.charlietap.chasm.ir.type.CompositeType
import io.github.charlietap.chasm.ir.type.ConcreteHeapType
import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.HeapType
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

fun HeapTypeMatcher(
    type1: HeapType,
    type2: HeapType,
    context: TypeMatcherContext,
): Boolean =
    HeapTypeMatcher(
        type1 = type1,
        type2 = type2,
        context = context,
        abstractHeapTypeMatcher = ::AbstractHeapTypeMatcher,
        definedTypeExpander = ::DefinedTypeExpander,
        definedTypeMatcher = ::DefinedTypeMatcher,
    )

internal fun HeapTypeMatcher(
    type1: HeapType,
    type2: HeapType,
    context: TypeMatcherContext,
    abstractHeapTypeMatcher: TypeMatcher<AbstractHeapType>,
    definedTypeExpander: DefinedTypeExpander,
    definedTypeMatcher: TypeMatcher<DefinedType>,
): Boolean = when {
    type1 is AbstractHeapType.None -> HeapTypeMatcher(type2, AbstractHeapType.Any, context)
    type1 is AbstractHeapType.NoFunc -> HeapTypeMatcher(type2, AbstractHeapType.Func, context)
    type1 is AbstractHeapType.NoException -> HeapTypeMatcher(type2, AbstractHeapType.Exception, context)
    type1 is AbstractHeapType.NoExtern -> HeapTypeMatcher(type2, AbstractHeapType.Extern, context)
    type1 is AbstractHeapType.Bottom -> true
    type1 is AbstractHeapType && type2 is AbstractHeapType -> abstractHeapTypeMatcher(type1, type2, context)
    type1 is ConcreteHeapType.TypeIndex -> {
        val definedType1 = context.lookup(type1.index)
        if (definedType1 != null) {
            HeapTypeMatcher(ConcreteHeapType.Defined(definedType1), type2, context)
        } else {
            false
        }
    }
    type2 is ConcreteHeapType.TypeIndex -> {
        val definedType2 = context.lookup(type2.index)
        if (definedType2 != null) {
            HeapTypeMatcher(type1, ConcreteHeapType.Defined(definedType2), context)
        } else {
            false
        }
    }
    type1 is ConcreteHeapType.Defined && type2 is ConcreteHeapType.Defined -> definedTypeMatcher(
        type1.definedType,
        type2.definedType,
        context,
    )
    type1 is ConcreteHeapType.Defined && type2 !is ConcreteHeapType.Defined -> CompositeTypeHeapTypeMatcher(
        definedTypeExpander(type1.definedType),
        type2,
    )
    else -> type1 == type2
}

private fun CompositeTypeHeapTypeMatcher(
    compositeType: CompositeType,
    heapType: HeapType,
): Boolean = when (compositeType) {
    is CompositeType.Struct -> {
        when (heapType) {
            AbstractHeapType.Any,
            AbstractHeapType.Eq,
            AbstractHeapType.Struct,
            -> true
            else -> false
        }
    }
    is CompositeType.Array -> {
        when (heapType) {
            AbstractHeapType.Any,
            AbstractHeapType.Eq,
            AbstractHeapType.Array,
            -> true
            else -> false
        }
    }
    is CompositeType.Function -> heapType == AbstractHeapType.Func
}
