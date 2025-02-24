package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.executor.runtime.function.Function
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.executor.runtime.instance.HostFunction as HostFunctionImpl

sealed class FunctionInstance {

    abstract val type: DefinedType
    abstract val functionType: FunctionType

    data class WasmFunction(
        override val type: DefinedType,
        override val functionType: FunctionType,
        val module: ModuleInstance,
        var function: Function,
    ) : FunctionInstance()

    data class HostFunction(
        override val type: DefinedType,
        override val functionType: FunctionType,
        val function: HostFunctionImpl,
    ) : FunctionInstance()
}
