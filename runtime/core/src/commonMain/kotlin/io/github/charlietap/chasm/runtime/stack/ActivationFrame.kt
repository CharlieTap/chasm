package io.github.charlietap.chasm.runtime.stack

import io.github.charlietap.chasm.runtime.instance.ModuleInstance

data class ActivationFrame(
    val arity: Int,
    val handlerDepth: Int,
    val valueDepth: Int,
    val instance: ModuleInstance,
    val previousFramePointer: Int = 0,
    val resultSlotBase: Int = NO_RESULT_SLOT_BASE,
    val returnIp: Int,
)

const val NO_RESULT_SLOT_BASE = -1
