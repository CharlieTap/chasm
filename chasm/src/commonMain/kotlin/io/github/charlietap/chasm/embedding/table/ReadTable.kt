package io.github.charlietap.chasm.embedding.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.embedding.transform.BidirectionalMapper
import io.github.charlietap.chasm.embedding.transform.ReferenceValueMapper
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

fun readTable(
    store: Store,
    table: Table,
    elementIndex: Int,
): ChasmResult<Value.Reference, ChasmError.ExecutionError> =
    internalReadTable(
        store = store,
        table = table,
        elementIndex = elementIndex,
        referenceValueMapper = ReferenceValueMapper.instance,
    )
        .mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun internalReadTable(
    store: Store,
    table: Table,
    elementIndex: Int,
    referenceValueMapper: BidirectionalMapper<Value.Reference, ReferenceValue>,
): Result<Value.Reference, ModuleTrapError> = binding {
    val table = store.store.table(table.reference.address).bind()
    referenceValueMapper.bimap(table.element(elementIndex).bind())
}
