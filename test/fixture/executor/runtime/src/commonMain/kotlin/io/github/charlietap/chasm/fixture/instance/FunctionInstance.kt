package io.github.charlietap.chasm.fixture.instance

import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.HostFunction
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.fixture.module.function
import io.github.charlietap.chasm.fixture.type.definedType

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
    function: Function = function(),
) = FunctionInstance.WasmFunction(
    type = type,
    module = module,
    function = function,
)
