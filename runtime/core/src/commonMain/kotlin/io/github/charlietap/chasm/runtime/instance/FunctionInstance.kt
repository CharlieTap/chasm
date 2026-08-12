package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.runtime.function.Function
import io.github.charlietap.chasm.runtime.function.WasmFunctionCallPlan
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.runtime.instance.HostFunction as HostFunctionImpl

sealed class FunctionInstance {

    abstract val rtt: RTT
    abstract val functionType: FunctionType

    data class WasmFunction(
        override val rtt: RTT,
        override val functionType: FunctionType,
        val module: ModuleInstance,
        var function: Function,
        val callPlan: WasmFunctionCallPlan = WasmFunctionCallPlan(
            params = functionType.params.types.size,
            results = functionType.results.types.size,
            interfaceSlots = maxOf(functionType.params.types.size, functionType.results.types.size),
            module = module,
            locals = function.locals.copyOf(),
        ).apply {
            install(
                entryIp = function.body.entryIp,
                frameSlots = function.frameSlots,
            )
        },
    ) : FunctionInstance()

    data class HostFunction(
        override val rtt: RTT,
        override val functionType: FunctionType,
        val function: HostFunctionImpl,
    ) : FunctionInstance()
}
