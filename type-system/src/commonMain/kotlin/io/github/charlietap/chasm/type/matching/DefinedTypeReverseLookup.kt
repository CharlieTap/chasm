package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.DefinedType

typealias DefinedTypeReverseLookup = (DefinedType) -> Index.TypeIndex
