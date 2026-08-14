package io.github.charlietap.chasm.vm

interface SuspendingWasmVirtualMachine : WasmVirtualMachine {

    suspend fun moduleDecodeSuspending(
        binary: ByteArray,
    ): WasmVirtualMachine.Result<Module>

    suspend fun moduleInstantiateSuspending(
        store: Store,
        module: Module,
        imports: List<Import>,
    ): WasmVirtualMachine.Result<Instance>
}
