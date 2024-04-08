package io.github.charlietap.chasm.executor.instantiator.allocation.type

import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.type.rolling.DefinedTypeRoller
import io.github.charlietap.chasm.executor.type.rolling.DefinedTypeRollerImpl
import io.github.charlietap.chasm.executor.type.rolling.substitution.ConcreteHeapTypeSubstitutor
import io.github.charlietap.chasm.executor.type.rolling.substitution.DefinedTypeSubstitutorImpl
import io.github.charlietap.chasm.executor.type.rolling.substitution.TypeSubstitutor

fun TypeAllocatorImpl(
    recursiveTypes: List<RecursiveType>,
): List<DefinedType> =
    TypeAllocatorImpl(
        recursiveTypes = recursiveTypes,
        definedTypeRoller = ::DefinedTypeRollerImpl,
        definedTypeSubstitutor = ::DefinedTypeSubstitutorImpl,
    )

fun TypeAllocatorImpl(
    recursiveTypes: List<RecursiveType>,
    definedTypeRoller: DefinedTypeRoller,
    definedTypeSubstitutor: TypeSubstitutor<DefinedType>,
): List<DefinedType> = recursiveTypes.fold(emptyList()) { acc, recursiveType ->
    val size = acc.size

    val substitutor: ConcreteHeapTypeSubstitutor = { heapType ->
        when (heapType) {
            is ConcreteHeapType.TypeIndex -> {
                ConcreteHeapType.Defined(acc[heapType.index.index()])
            }
            else -> heapType
        }
    }

    acc + definedTypeRoller(size, recursiveType).map { definedType ->
        definedTypeSubstitutor(definedType, substitutor)
    }
}
