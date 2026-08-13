package io.github.charlietap.chasm.vm

class PreparedFunction internal constructor(
    private val invocation: (List<WasmVirtualMachine.Value>) -> WasmVirtualMachine.Result<List<WasmVirtualMachine.Value>>,
) {
    operator fun invoke(
        args: List<WasmVirtualMachine.Value> = emptyList(),
    ): WasmVirtualMachine.Result<List<WasmVirtualMachine.Value>> = invocation(args)
}
