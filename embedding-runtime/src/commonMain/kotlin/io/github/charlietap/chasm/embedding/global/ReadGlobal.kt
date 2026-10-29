package io.github.charlietap.chasm.embedding.global

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.ext.global
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.toExecutionValue
import io.github.charlietap.chasm.runtime.value.ExecutionValue

fun readGlobal(
    store: Store,
    global: Global,
): ChasmResult<ExecutionValue, ChasmError.ExecutionError> =
    internalReadGlobal(
        store = store,
        global = global,
    ).mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun internalReadGlobal(
    store: Store,
    global: Global,
): Result<ExecutionValue, ModuleTrapError> = runCatching {
    val global = store.global(global)
    global.value.toExecutionValue(global.type.valueType)
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        else -> throw e
    }
}
