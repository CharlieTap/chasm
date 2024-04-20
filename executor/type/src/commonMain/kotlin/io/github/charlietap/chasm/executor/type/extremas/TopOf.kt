package io.github.charlietap.chasm.executor.type.extremas

import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance

typealias TopOf<T> = (T, ModuleInstance) -> HeapType?
