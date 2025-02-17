package io.github.charlietap.chasm.type.ir.matching

import io.github.charlietap.chasm.type.ir.rolling.DefinedTypeUnroller
import io.github.charlietap.chasm.type.ir.rolling.substitution.ConcreteHeapTypeSubstitutor

interface TypeMatcherContext {
    val lookup: DefinedTypeLookup
    val reverseLookup: DefinedTypeReverseLookup
    val substitutor: ConcreteHeapTypeSubstitutor
    val unroller: DefinedTypeUnroller
}
