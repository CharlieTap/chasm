package io.github.charlietap.chasm.executor.instantiator.allocation.type

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.RecursiveType

typealias TypeAllocator = (List<RecursiveType>) -> List<DefinedType>
