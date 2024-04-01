package io.github.charlietap.chasm.executor.type.rolling

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.RecursiveType

typealias DefinedTypeRoller = (Int, RecursiveType) -> List<DefinedType>
