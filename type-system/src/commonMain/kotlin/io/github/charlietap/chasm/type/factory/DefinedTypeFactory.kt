package io.github.charlietap.chasm.type.factory

import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.type.rolling.DefinedTypeRoller
import io.github.charlietap.chasm.type.rolling.DefinedTypeRollerImpl
import io.github.charlietap.chasm.type.rolling.substitution.ConcreteHeapTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.DefinedTypeSubstitutorImpl
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor

typealias DefinedTypeFactory = (List<RecursiveType>) -> List<DefinedType>

fun DefinedTypeFactory(
    recursiveTypes: List<RecursiveType>,
): List<DefinedType> =
    DefinedTypeFactory(
        recursiveTypes = recursiveTypes,
        definedTypeRoller = ::DefinedTypeRollerImpl,
        definedTypeSubstitutor = ::DefinedTypeSubstitutorImpl,
    )

internal fun DefinedTypeFactory(
    recursiveTypes: List<RecursiveType>,
    definedTypeRoller: DefinedTypeRoller,
    definedTypeSubstitutor: TypeSubstitutor<DefinedType>,
): List<DefinedType> = recursiveTypes.fold(emptyList()) { acc, recursiveType ->
    val size = acc.size

    val substitutor: ConcreteHeapTypeSubstitutor = { heapType ->
        when (heapType) {
            is ConcreteHeapType.TypeIndex -> {
                ConcreteHeapType.Defined(acc[heapType.index.idx.toInt()])
            }
            else -> heapType
        }
    }

    acc + definedTypeRoller(size, recursiveType).map { definedType ->
        definedTypeSubstitutor(definedType, substitutor)
    }
}
