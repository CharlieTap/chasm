package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.RecursiveType

sealed interface Substitution {

    val outputState: RecursiveType.State
    val concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor

    class TypeIndexToDefinedType(
        val types: List<DefinedType>,
    ) : Substitution {
        override val outputState: RecursiveType.State = RecursiveType.State.EXTERNAL_SUBSTITUTED
        override val concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor = TypeIndexToDefinedTypeSubstitutor(types)
    }

    class TypeIndexToRecursiveTypeIndex(
        lowerBound: Int,
        upperBound: Int,
    ) : Substitution {
        override val outputState: RecursiveType.State = RecursiveType.State.INTERNAL_SUBSTITUTED
        override val concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor =
            TypeIndexToRecursiveTypeIndexSubsitutor(lowerBound, upperBound)
    }

    class RecursiveTypeIndexToDefinedType(
        recursiveType: RecursiveType,
    ) : Substitution {
        override val outputState: RecursiveType.State = RecursiveType.State.UNROLLED
        override val concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor =
            RecursiveTypeIndexToDefinedTypeSubstitutor(recursiveType)
    }
}
