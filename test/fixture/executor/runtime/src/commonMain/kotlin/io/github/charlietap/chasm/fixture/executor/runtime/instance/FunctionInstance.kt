package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.runtime.function.Function
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.HostFunction
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.fixture.ast.type.definedType
import io.github.charlietap.chasm.fixture.executor.runtime.function.runtimeFunction

fun hostFunctionInstance(
    type: DefinedType = definedType(),
    function: HostFunction = { emptyList() },
) = FunctionInstance.HostFunction(
    type = type,
    function = function,
)

fun wasmFunctionInstance(
    type: DefinedType = definedType(),
    module: ModuleInstance = moduleInstance(),
    function: Function = runtimeFunction(),
) = FunctionInstance.WasmFunction(
    type = type,
    module = module,
    function = function,
)
