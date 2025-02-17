package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.type.rolling.DefinedTypeUnroller
import io.github.charlietap.chasm.type.rolling.substitution.ConcreteHeapTypeSubstitutor

interface TypeMatcherContext {

    val lookup: DefinedTypeLookup
    val reverseLookup: DefinedTypeReverseLookup
    val substitutor: ConcreteHeapTypeSubstitutor
    val unroller: DefinedTypeUnroller
}
