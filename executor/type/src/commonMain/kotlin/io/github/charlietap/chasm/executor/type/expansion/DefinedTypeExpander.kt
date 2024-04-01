package io.github.charlietap.chasm.executor.type.expansion

import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.DefinedType

typealias DefinedTypeExpander = (DefinedType) -> CompositeType
