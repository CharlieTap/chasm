package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.type.FunctionType

sealed class FunctionInstance {

    abstract val type: FunctionType

    data class WasmFunction(
        override val type: FunctionType,
        val module: ModuleInstance,
        val function: Function,
    ) : FunctionInstance()

    data class HostFunction(
        override val type: FunctionType,
        val function: kotlin.Function<*>,
    ) : FunctionInstance()
}
