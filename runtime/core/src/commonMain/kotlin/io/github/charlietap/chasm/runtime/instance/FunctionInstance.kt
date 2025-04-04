package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.runtime.function.Function
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.RTT
import io.github.charlietap.chasm.runtime.instance.HostFunction as HostFunctionImpl

sealed class FunctionInstance {

    abstract val type: DefinedType
    abstract val rtt: RTT
    abstract val functionType: FunctionType

    data class WasmFunction(
        override val type: DefinedType,
        override val rtt: RTT,
        override val functionType: FunctionType,
        val module: ModuleInstance,
        var function: Function,
    ) : FunctionInstance()

    data class HostFunction(
        override val type: DefinedType,
        override val rtt: RTT,
        override val functionType: FunctionType,
        val function: HostFunctionImpl,
    ) : FunctionInstance()
}
