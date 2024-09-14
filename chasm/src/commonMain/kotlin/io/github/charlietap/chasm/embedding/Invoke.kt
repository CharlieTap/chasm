package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.embedding.transform.BidirectionalMapper
import io.github.charlietap.chasm.embedding.transform.ValueMapper
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.executor.invoker.FunctionInvokerImpl
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

fun invoke(
    store: Store,
    instance: Instance,
    name: String,
    args: List<Value> = emptyList(),
): ChasmResult<List<Value>, ChasmError.ExecutionError> = invoke(
    store = store,
    instance = instance,
    name = name,
    args = args,
    invoker = ::FunctionInvokerImpl,
    valueMapper = ValueMapper.instance,
)

internal fun invoke(
    store: Store,
    instance: Instance,
    name: String,
    args: List<Value>,
    invoker: FunctionInvoker,
    valueMapper: BidirectionalMapper<Value, ExecutionValue>,
): ChasmResult<List<Value>, ChasmError.ExecutionError> {

    val extern = instance.instance.exports.firstOrNull { export ->
        export.name.name == name
    }?.value
    val address = (extern as? ExternalValue.Function)?.address ?: return Error(
        ChasmError.ExecutionError(InvocationError.FunctionNotFound(name).toString()),
    )
    val arguments = args.map(valueMapper::map)

    return invoker(store.store, address, arguments)
        .map { values -> values.map(valueMapper::bimap) }
        .mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)
}
