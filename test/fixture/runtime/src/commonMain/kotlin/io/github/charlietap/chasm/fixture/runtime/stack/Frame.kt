package io.github.charlietap.chasm.fixture.runtime.stack

import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.program.EXIT_IP
import io.github.charlietap.chasm.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.runtime.stack.NO_RESULT_SLOT_BASE

fun frame(
    arity: Int = 0,
    handlerDepth: Int = 0,
    valueDepth: Int = 0,
    instance: ModuleInstance = moduleInstance(),
    previousFramePointer: Int = 0,
    resultSlotBase: Int = NO_RESULT_SLOT_BASE,
    returnIp: Int = EXIT_IP,
) = ActivationFrame(
    arity = arity,
    handlerDepth = handlerDepth,
    valueDepth = valueDepth,
    instance = instance,
    previousFramePointer = previousFramePointer,
    resultSlotBase = resultSlotBase,
    returnIp = returnIp,
)
