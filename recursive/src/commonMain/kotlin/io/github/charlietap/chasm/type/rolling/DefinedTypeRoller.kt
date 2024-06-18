package io.github.charlietap.chasm.type.rolling

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.RecursiveType

typealias DefinedTypeRoller = (Int, RecursiveType) -> List<DefinedType>
