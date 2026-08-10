package io.github.charlietap.chasm.runtime.function

import io.github.charlietap.chasm.runtime.instance.ModuleInstance

class WasmFunctionCallPlan(
    val entryIp: Int,
    val frameSlots: Int,
    val params: Int,
    val results: Int,
    val interfaceSlots: Int,
    val module: ModuleInstance,
    val locals: LongArray,
)
