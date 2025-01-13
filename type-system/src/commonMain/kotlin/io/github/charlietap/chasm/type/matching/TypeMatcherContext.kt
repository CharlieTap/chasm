package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.type.rolling.substitution.ConcreteHeapTypeSubstitutor

interface TypeMatcherContext {

    val lookup: DefinedTypeLookup

    val substitutor: ConcreteHeapTypeSubstitutor
}
