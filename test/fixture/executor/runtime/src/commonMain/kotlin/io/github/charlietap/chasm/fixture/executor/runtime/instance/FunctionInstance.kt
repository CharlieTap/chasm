package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.executor.runtime.function.Function
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.HostFunction
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.ir.type.definedType
import io.github.charlietap.chasm.fixture.ir.type.functionType
import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.FunctionType

fun functionInstance(): FunctionInstance = hostFunctionInstance()

fun hostFunctionInstance(
    type: DefinedType = definedType(),
    functionType: FunctionType = functionType(),
    function: HostFunction = { emptyList() },
) = FunctionInstance.HostFunction(
    type = type,
    functionType = functionType,
    function = function,
)

fun wasmFunctionInstance(
    type: DefinedType = definedType(),
    functionType: FunctionType = functionType(),
    module: ModuleInstance = moduleInstance(),
    function: Function = runtimeFunction(),
) = FunctionInstance.WasmFunction(
    type = type,
    functionType = functionType,
    module = module,
    function = function,
)
