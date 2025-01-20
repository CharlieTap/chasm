package io.github.charlietap.chasm.executor.invoker.ext

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance

inline fun ModuleInstance.definedType(
    index: Index.TypeIndex,
): DefinedType = try {
    types[index.idx.toInt()]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.FunctionTypeLookupFailed(index.idx.toInt()))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.FunctionTypeLookupFailed(index.idx.toInt()))
}
