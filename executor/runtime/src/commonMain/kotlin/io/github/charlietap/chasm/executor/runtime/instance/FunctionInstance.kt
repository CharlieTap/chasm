package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.runtime.function.Function
import io.github.charlietap.chasm.executor.runtime.instance.HostFunction as HostFunctionImpl

sealed class FunctionInstance {

    abstract val type: DefinedType

    data class WasmFunction(
        override val type: DefinedType,
        val module: ModuleInstance,
        val function: Function,
    ) : FunctionInstance()

    data class HostFunction(
        override val type: DefinedType,
        val function: HostFunctionImpl,
    ) : FunctionInstance()
}
