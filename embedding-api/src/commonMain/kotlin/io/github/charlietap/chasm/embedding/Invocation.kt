package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InvocationError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Exports
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.PreparedFunction
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.host.UnsafeHostApi

fun exports(instance: Instance): Exports {
    return instance.engine.exports(instance)
}

fun prepareFunction(
    instance: Instance,
    name: String,
): ChasmResult<PreparedFunction, ExecutionError.Rejected> {
    return instance.engine.prepareFunction(instance, name)
}

fun invoke(
    instance: Instance,
    name: String,
    args: List<ExecutionValue> = emptyList(),
): ChasmResult<List<ExecutionValue>, InvocationError> {
    return when (val prepared = prepareFunction(instance, name)) {
        is ChasmResult.Success -> prepared.result(args)
        is ChasmResult.Error -> prepared
    }
}

operator fun PreparedFunction.invoke(args: List<ExecutionValue> = emptyList()): ChasmResult<List<ExecutionValue>, InvocationError> {
    return engine.invoke(this, args)
}

@ExperimentalChasmApi
fun PreparedFunction.invokeUnboxed(
    words: LongArray,
    references: Array<Any?>,
): ChasmResult<Unit, InvocationError> {
    return engine.invokeUnboxed(this, words, references)
}

@ExperimentalChasmApi
@UnsafeHostApi
fun PreparedFunction.unsafeInvokeUnboxed(
    words: LongArray,
    references: Array<Any?>,
): ChasmResult<Unit, InvocationError> {
    return engine.unsafeInvokeUnboxed(this, words, references)
}
