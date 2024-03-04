@file:Suppress("UNUSED_PARAMETER")

package io.github.charlietap.chasm

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ChasmResult.Error
import io.github.charlietap.chasm.ChasmResult.Success
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.error.ChasmError.DecodeError
import io.github.charlietap.chasm.error.ChasmError.ExecutionError
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiator
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiatorImpl
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.executor.invoker.FunctionInvokerImpl
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.import.Import
import io.github.charlietap.chasm.import.ImportMatcher
import io.github.charlietap.chasm.import.ImportMatcherImpl

object Chasm

fun Chasm.module(sourceReader: SourceReader): ChasmResult<Module, DecodeError> {
    return module(sourceReader, WasmModuleDecoder())
}

fun Chasm.store() = Store()

fun Chasm.module(bytes: ByteArray): Module {
    TODO()
}

internal fun Chasm.module(
    reader: SourceReader,
    decoder: ModuleDecoder,
): ChasmResult<Module, DecodeError> {
    return decoder(reader)
        .mapError(::DecodeError)
        .fold(::Success, ::Error)
}

fun Chasm.instance(
    store: Store,
    module: Module,
    imports: List<Import>,
): ChasmResult<ModuleInstance, ExecutionError> {
    return instance(
        store = store,
        module = module,
        imports = imports,
        importMatcher = ::ImportMatcherImpl,
        instantiator = ::ModuleInstantiatorImpl,
    )
}

fun Chasm.instance(
    store: Store,
    module: Module,
    imports: List<Import>,
    importMatcher: ImportMatcher,
    instantiator: ModuleInstantiator,
): ChasmResult<ModuleInstance, ExecutionError> {
    val orderedImports = importMatcher(module, imports).component1()
        ?: return Error(ExecutionError(InstantiationError.MissingImport))
    return instantiator(store, module, orderedImports)
        .mapError(::ExecutionError)
        .fold(::Success, ::Error)
}

fun Chasm.invoke(
    store: Store,
    instance: ModuleInstance,
    name: String,
    args: List<ExecutionValue>,
): ChasmResult<List<ExecutionValue>, ExecutionError> = invoke(
    store = store,
    instance = instance,
    name = name,
    args = args,
    invoker = ::FunctionInvokerImpl,
)

fun Chasm.invoke(
    store: Store,
    instance: ModuleInstance,
    name: String,
    args: List<ExecutionValue>,
    invoker: FunctionInvoker,
): ChasmResult<List<ExecutionValue>, ExecutionError> {
    val extern = instance.exports.firstOrNull { export ->
        export.name.name == name
    }?.value
    val address = (extern as ExternalValue.Function).address

    return invoker(store, address, args)
        .mapError(::ExecutionError)
        .fold(::Success, ::Error)
}

fun Chasm.instance(sourceReader: SourceReader): ModuleInstance {
    TODO()
}

fun Chasm.memory(min: Int, max: Int): ExternalValue.Memory {
    // store alloc memory
    TODO()
}

fun Chasm.table(min: Int, max: Int): ExternalValue.Table {
    // store alloc memory
    TODO()
}

fun Chasm.global(min: Int, max: Int): ExternalValue.Global {
    // store alloc memory
    TODO()
}
