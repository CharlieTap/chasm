package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.type.rolling.DefinedTypeUnroller
import io.github.charlietap.chasm.type.rolling.substitution.Substitution

interface TypeMatcherContext {

    val lookup: DefinedTypeLookup
    val reverseLookup: DefinedTypeReverseLookup
    val substitution: Substitution.TypeIndexToDefinedType
    val unroller: DefinedTypeUnroller
}
