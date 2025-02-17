package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.type.DefinedType

typealias DefinedTypeReverseLookup = (DefinedType) -> Index.TypeIndex
