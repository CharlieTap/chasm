package io.github.charlietap.chasm.executor.type.matching

import io.github.charlietap.chasm.executor.type.rolling.substitution.ConcreteHeapTypeSubstitutor

interface TypeMatcherContext {

    fun lookup(): DefinedTypeLookup

    fun substitution(): ConcreteHeapTypeSubstitutor
}
