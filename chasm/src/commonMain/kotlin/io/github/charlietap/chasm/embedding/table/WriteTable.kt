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
import io.github.charlietap.chasm.embedding.transform.Mapper
import io.github.charlietap.chasm.embedding.transform.ReferenceValueMapper
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

fun writeTable(
    store: Store,
    table: Table,
    elementIndex: Int,
    value: Value.Reference,
): ChasmResult<Unit, ChasmError.ExecutionError> =
    internalWriteTable(
        store = store,
        table = table,
        elementIndex = elementIndex,
        value = value,
        referenceValueMapper = ReferenceValueMapper.instance,
    )
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun internalWriteTable(
    store: Store,
    table: Table,
    elementIndex: Int,
    value: Value.Reference,
    referenceValueMapper: Mapper<Value.Reference, ReferenceValue>,
): Result<Unit, ModuleTrapError> = binding {
    val table = store.store.table(table.reference.address).bind()
    table.elements[elementIndex] = referenceValueMapper.map(value)
}
