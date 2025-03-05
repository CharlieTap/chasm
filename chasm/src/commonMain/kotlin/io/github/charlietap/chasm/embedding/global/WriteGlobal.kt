package io.github.charlietap.chasm.embedding.global

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.global
import io.github.charlietap.chasm.executor.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.value.ExecutionValue

fun writeGlobal(
    store: Store,
    global: Global,
    value: ExecutionValue,
): ChasmResult<Unit, ChasmError.ExecutionError> =
    internalWriteGlobal(
        store = store,
        global = global,
        value = value,
    ).mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun internalWriteGlobal(
    store: Store,
    global: Global,
    value: ExecutionValue,
): Result<Unit, ModuleTrapError> = binding {
    val global = store.store.global(global.reference.address)
    global.value = value.toLongFromBoxed()
}
