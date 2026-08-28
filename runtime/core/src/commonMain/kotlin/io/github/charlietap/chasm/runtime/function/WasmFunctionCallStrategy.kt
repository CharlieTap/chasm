package io.github.charlietap.chasm.runtime.function

/** Precomputed activation metadata for a compiled Wasm function. */
class WasmFunctionCallStrategy(
    val interfaceSlotCount: Int,
    var entryIp: Int = -1,
    var frameSlots: Int = 0,
    var localInitialization: LocalInitialization = LocalInitialization.None,
) {
    val isInstalled: Boolean
        get() = entryIp >= 0
}
