package io.github.charlietap.chasm.type.ir.matching

import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.type.DefinedType

typealias DefinedTypeLookup = (Index.TypeIndex) -> DefinedType?
