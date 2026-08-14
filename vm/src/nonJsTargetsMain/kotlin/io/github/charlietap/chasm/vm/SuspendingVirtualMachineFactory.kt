package io.github.charlietap.chasm.vm

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.coroutines.internal._coroutineParallelTaskExecutor
import io.github.charlietap.chasm.embedding.internal._instance
import io.github.charlietap.chasm.embedding.internal._module
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.vm.WasmVirtualMachine.Result
import io.github.charlietap.chasm.vm.WasmVirtualMachine.Result.Ok
import kotlinx.coroutines.CoroutineDispatcher

@OptIn(InternalChasmApi::class)
actual fun suspendingVirtualMachineFactory(
    dispatcher: CoroutineDispatcher,
): SuspendingWasmVirtualMachine =
    ParallelSuspendingWasmVirtualMachine(_coroutineParallelTaskExecutor(dispatcher))

@OptIn(InternalChasmApi::class)
private class ParallelSuspendingWasmVirtualMachine(
    private val taskExecutor: ParallelTaskExecutor,
) : SuspendingWasmVirtualMachine,
    WasmVirtualMachine by NonJsVirtualMachine {

    override suspend fun moduleDecodeSuspending(
        binary: ByteArray,
    ): Result<Module> {
        val module = _module(binary, ModuleConfig(), taskExecutor).expect("Failed to decode module")
        return Ok(Module(module))
    }

    override suspend fun moduleInstantiateSuspending(
        store: Store,
        module: Module,
        imports: List<Import>,
    ): Result<Instance> {
        val mappedImports = imports.map(ImportMapper::from)
        val instance = _instance(
            store = store.reference,
            module = module.reference,
            imports = mappedImports,
            config = RuntimeConfig(),
            taskExecutor = taskExecutor,
        ).expect("Failed to instantiate module")
        return Ok(Instance(instance))
    }
}
