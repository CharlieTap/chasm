package io.github.charlietap.chasm.embedding.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.ChasmResult.Error
import io.github.charlietap.chasm.ChasmResult.Success
import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

fun writeTable(
    store: Store,
    address: Address.Table,
    elementIndex: Int,
    value: ReferenceValue,
): ChasmResult<Unit, ChasmError.ExecutionError> =
    internalWriteTable(
        store = store,
        address = address,
        elementIndex = elementIndex,
        value = value,
    )
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun internalWriteTable(
    store: Store,
    address: Address.Table,
    elementIndex: Int,
    value: ReferenceValue,
): Result<Unit, ModuleTrapError> = binding {
    val table = store.table(address).bind()
    table.elements[elementIndex] = value
}
