package io.github.charlietap.chasm.type.extremas

import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.HeapType

typealias BottomOf<T> = (T, List<DefinedType>) -> HeapType?
