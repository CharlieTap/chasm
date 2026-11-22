package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.runtime.function.WasmFunctionCallStrategy
import io.github.charlietap.chasm.runtime.host.RawHostFunction
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.type.FunctionType

sealed class FunctionInstance {

    abstract val rtt: RTT
    abstract val functionType: FunctionType
    abstract val parameterCount: Int
    abstract val resultCount: Int

    data class WasmFunction(
        override val rtt: RTT,
        override val functionType: FunctionType,
        val module: ModuleInstance,
        val callStrategy: WasmFunctionCallStrategy,
    ) : FunctionInstance() {
        override val parameterCount: Int = functionType.params.types.size

        override val resultCount: Int = functionType.results.types.size
    }

    data class HostFunction(
        override val rtt: RTT,
        override val functionType: FunctionType,
        val function: RawHostFunction,
    ) : FunctionInstance() {
        override val parameterCount: Int = functionType.params.types.size

        override val resultCount: Int = functionType.results.types.size
    }
}
