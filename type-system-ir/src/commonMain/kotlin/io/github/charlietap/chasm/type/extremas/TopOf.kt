package io.github.charlietap.chasm.type.extremas

import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.HeapType

typealias TopOf<T> = (T, List<DefinedType>) -> HeapType?
