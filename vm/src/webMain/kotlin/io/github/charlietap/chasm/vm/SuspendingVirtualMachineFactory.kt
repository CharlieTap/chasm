package io.github.charlietap.chasm.vm

import kotlinx.coroutines.CoroutineDispatcher

actual fun suspendingVirtualMachineFactory(
    dispatcher: CoroutineDispatcher,
): SuspendingWasmVirtualMachine = SerialSuspendingWasmVirtualMachine(virtualMachineFactory())

private class SerialSuspendingWasmVirtualMachine(
    delegate: WasmVirtualMachine,
) : SuspendingWasmVirtualMachine,
    WasmVirtualMachine by delegate {

    private val virtualMachine = delegate

    override suspend fun moduleDecodeSuspending(
        binary: ByteArray,
    ): WasmVirtualMachine.Result<Module> = virtualMachine.moduleDecode(binary)

    override suspend fun moduleInstantiateSuspending(
        store: Store,
        module: Module,
        imports: List<Import>,
    ): WasmVirtualMachine.Result<Instance> = virtualMachine.moduleInstantiate(store, module, imports)
}
