package io.github.charlietap.chasm.runtime.function

import io.github.charlietap.chasm.runtime.instance.ModuleInstance

class WasmFunctionCallPlan(
    val params: Int,
    val results: Int,
    val interfaceSlots: Int,
    val module: ModuleInstance,
    val locals: LongArray,
) {
    private var installed = false

    val isInstalled: Boolean
        get() = installed

    var entryIp: Int = 0
        private set

    var frameSlots: Int = 0
        private set

    fun install(
        entryIp: Int,
        frameSlots: Int,
    ) {
        check(!installed) { "Wasm function call plan is already installed" }
        this.entryIp = entryIp
        this.frameSlots = frameSlots
        installed = true
    }
}
