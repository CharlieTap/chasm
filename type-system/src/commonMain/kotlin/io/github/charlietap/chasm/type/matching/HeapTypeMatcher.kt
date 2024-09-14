package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpanderImpl

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
        definedTypeExpander = ::DefinedTypeExpanderImpl,
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
    type1 is ConcreteHeapType.TypeIndex && type2 !is ConcreteHeapType.TypeIndex -> {
        HeapTypeMatcher(ConcreteHeapType.Defined(context.lookup()(type1.index)), type2, context)
    }
    type2 is ConcreteHeapType.TypeIndex && type1 !is ConcreteHeapType.TypeIndex -> {
        HeapTypeMatcher(type1, ConcreteHeapType.Defined(context.lookup()(type2.index)), context)
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
    type1 !is ConcreteHeapType.Defined && type2 is ConcreteHeapType.Defined -> CompositeTypeHeapTypeMatcher(
        definedTypeExpander(type2.definedType),
        type1,
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
