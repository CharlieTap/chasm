package io.github.charlietap.chasm.vm

import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.vm.WasmVirtualMachine.Result
import io.github.charlietap.chasm.vm.WasmVirtualMachine.Result.Ok
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

actual fun suspendingVirtualMachineFactory(dispatcher: CoroutineDispatcher): SuspendingWasmVirtualMachine {
    return DispatchedSuspendingWasmVirtualMachine(dispatcher)
}

private class DispatchedSuspendingWasmVirtualMachine(
    private val dispatcher: CoroutineDispatcher,
) : SuspendingWasmVirtualMachine,
    WasmVirtualMachine by NonJsVirtualMachine {

    override suspend fun moduleDecodeSuspending(binary: ByteArray): Result<Module> {
        val module = withContext(dispatcher) { module(binary) }.expect("Failed to decode module")
        return Ok(Module(module))
    }

    override suspend fun moduleInstantiateSuspending(
        store: Store,
        module: Module,
        imports: List<Import>,
    ): Result<Instance> {
        val mappedImports = imports.map(ImportMapper::from)
        val instance = withContext(dispatcher) {
            instance(store.reference, module.reference, mappedImports)
        }.expect("Failed to instantiate module")
        return Ok(Instance(instance))
    }
}
