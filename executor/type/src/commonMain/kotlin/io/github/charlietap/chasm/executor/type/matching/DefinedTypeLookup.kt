package io.github.charlietap.chasm.executor.type.matching

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.DefinedType

typealias DefinedTypeLookup = (Index.TypeIndex) -> DefinedType
