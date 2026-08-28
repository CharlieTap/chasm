package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.runtime.function.WasmFunctionCallStrategy
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.host.HostFunction as HostCallback

sealed class FunctionInstance {

    abstract val rtt: RTT
    abstract val functionType: FunctionType

    data class WasmFunction(
        override val rtt: RTT,
        override val functionType: FunctionType,
        val module: ModuleInstance,
        val callStrategy: WasmFunctionCallStrategy,
    ) : FunctionInstance()

    data class HostFunction(
        override val rtt: RTT,
        override val functionType: FunctionType,
        val function: HostCallback,
    ) : FunctionInstance()
}
