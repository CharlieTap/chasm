package io.github.charlietap.chasm.embedding.global

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.ext.global
import io.github.charlietap.chasm.embedding.internal.throwAccessorFailure
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.toExecutionValue
import io.github.charlietap.chasm.runtime.value.ExecutionValue

fun readGlobal(
    store: Store,
    global: Global,
): ExecutionValue {
    return internalReadGlobal(
        store = store,
        global = global,
    ).getOrElse(::throwAccessorFailure)
}

internal fun internalReadGlobal(
    store: Store,
    global: Global,
): Result<ExecutionValue, ModuleTrapError> = runCatching {
    val instance = store.global(global)
    instance.rawValue.toExecutionValue(instance.type.valueType)
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        else -> throw e
    }
}
