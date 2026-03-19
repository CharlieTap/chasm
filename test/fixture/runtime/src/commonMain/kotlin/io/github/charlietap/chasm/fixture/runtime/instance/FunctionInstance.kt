package io.github.charlietap.chasm.fixture.runtime.instance

import io.github.charlietap.chasm.fixture.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.rtt
import io.github.charlietap.chasm.runtime.function.Function
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.HostFunction
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.RTT

fun functionInstance(): FunctionInstance = hostFunctionInstance()

fun hostFunctionInstance(
    rtt: RTT = rtt(),
    functionType: FunctionType = functionType(),
    function: HostFunction = { emptyList() },
) = FunctionInstance.HostFunction(
    rtt = rtt,
    functionType = functionType,
    function = function,
)

fun wasmFunctionInstance(
    rtt: RTT = rtt(),
    functionType: FunctionType = functionType(),
    module: ModuleInstance = moduleInstance(),
    function: Function = runtimeFunction(),
) = FunctionInstance.WasmFunction(
    rtt = rtt,
    functionType = functionType,
    module = module,
    function = function,
)
