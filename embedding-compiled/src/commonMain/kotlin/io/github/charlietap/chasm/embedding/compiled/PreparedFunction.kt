package io.github.charlietap.chasm.embedding.compiled

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.compiled.value.ExecutionValue
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InvocationError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.type.FunctionType

class PreparedFunction
    @InternalChasmApi
    constructor(
        val type: FunctionType,
        @property:InternalChasmApi val engine: CompiledEngine,
        @property:InternalChasmApi val payload: Any,
    ) {

        val slots: Int = maxOf(type.params.types.size, type.results.types.size)
    }

fun prepareFunction(
    store: Store,
    instance: Instance,
    name: String,
): ChasmResult<PreparedFunction, ExecutionError.Rejected> {
    return store.engine.prepareFunction(instance, name)
}

fun invoke(
    store: Store,
    instance: Instance,
    name: String,
    args: List<ExecutionValue> = emptyList(),
): ChasmResult<List<ExecutionValue>, InvocationError<ExceptionReference>> {
    return when (val prepared = prepareFunction(store, instance, name)) {
        is ChasmResult.Success -> prepared.result(args)
        is ChasmResult.Error -> prepared
    }
}

operator fun PreparedFunction.invoke(args: List<ExecutionValue> = emptyList()): ChasmResult<List<ExecutionValue>, InvocationError<ExceptionReference>> {
    return engine.invoke(this, args)
}

@ExperimentalChasmApi
fun PreparedFunction.invokeUnboxed(
    words: LongArray,
    references: Array<Any?>,
): ChasmResult<Unit, InvocationError<ExceptionReference>> {
    return engine.invokeUnboxed(this, words, references)
}

@ExperimentalChasmApi
@UnsafeHostApi
fun PreparedFunction.unsafeInvokeUnboxed(
    words: LongArray,
    references: Array<Any?>,
): ChasmResult<Unit, InvocationError<ExceptionReference>> {
    return engine.unsafeInvokeUnboxed(this, words, references)
}
