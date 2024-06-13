package io.github.charlietap.chasm.type.extremas

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.HeapType

typealias BottomOf<T> = (T, List<DefinedType>) -> HeapType?
