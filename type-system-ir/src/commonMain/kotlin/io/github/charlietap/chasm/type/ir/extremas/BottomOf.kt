package io.github.charlietap.chasm.type.ir.extremas

import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.HeapType

typealias BottomOf<T> = (T, List<DefinedType>) -> HeapType?
