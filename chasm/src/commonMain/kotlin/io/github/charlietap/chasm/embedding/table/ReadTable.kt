package io.github.charlietap.chasm.embedding.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.ChasmResult.Error
import io.github.charlietap.chasm.ChasmResult.Success
import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.executor.runtime.error.ModuleRuntimeError
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

fun readTable(
    store: Store,
    address: Address.Table,
    elementIndex: Int,
): ChasmResult<ReferenceValue, ChasmError.ExecutionError> =
    internalReadTable(
        store = store,
        address = address,
        elementIndex = elementIndex,
    )
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun internalReadTable(
    store: Store,
    address: Address.Table,
    elementIndex: Int,
): Result<ReferenceValue, ModuleRuntimeError> = binding {
    val table = store.table(address).bind()
    table.element(elementIndex).bind()
}
