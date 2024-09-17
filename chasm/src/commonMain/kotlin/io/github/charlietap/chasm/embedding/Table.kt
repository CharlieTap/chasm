package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.TableType
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.embedding.transform.Mapper
import io.github.charlietap.chasm.embedding.transform.ReferenceValueMapper
import io.github.charlietap.chasm.embedding.transform.TableTypeMapper
import io.github.charlietap.chasm.executor.instantiator.allocation.table.TableAllocator
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.ast.type.TableType as InternalTableType
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue as InternalReferenceValue

fun table(
    store: Store,
    type: TableType,
    value: Value.Reference,
): Table = table(
    store = store,
    type = type,
    value = value,
    allocator = ::TableAllocator,
    tableTypeMapper = TableTypeMapper.instance,
    referenceValueMapper = ReferenceValueMapper.instance,
)

internal fun table(
    store: Store,
    type: TableType,
    value: Value.Reference,
    allocator: TableAllocator,
    tableTypeMapper: Mapper<TableType, InternalTableType>,
    referenceValueMapper: Mapper<Value.Reference, InternalReferenceValue>,
): Table {
    val tableType = tableTypeMapper.map(type)
    val referenceValue = referenceValueMapper.map(value)
    return Table(ExternalValue.Table(allocator(store.store, tableType, referenceValue)))
}
