package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.GlobalType
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.embedding.transform.GlobalTypeMapper
import io.github.charlietap.chasm.embedding.transform.Mapper
import io.github.charlietap.chasm.embedding.transform.ValueMapper
import io.github.charlietap.chasm.executor.instantiator.allocation.global.GlobalAllocator
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.ast.type.GlobalType as InternalGlobalType

fun global(
    store: Store,
    type: GlobalType,
    value: Value,
): Global = global(
    store = store,
    type = type,
    value = value,
    allocator = ::GlobalAllocator,
    globalTypeMapper = GlobalTypeMapper.instance,
    valueMapper = ValueMapper.instance,
)

internal fun global(
    store: Store,
    type: GlobalType,
    value: Value,
    allocator: GlobalAllocator,
    globalTypeMapper: Mapper<GlobalType, InternalGlobalType>,
    valueMapper: Mapper<Value, ExecutionValue>,
): Global {
    val globalType = globalTypeMapper.map(type)
    val executionValue = valueMapper.map(value)
    return Global(ExternalValue.Global(allocator(store.store, globalType, executionValue)))
}
