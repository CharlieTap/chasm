package io.github.charlietap.chasm.script.decoder

internal enum class WasmLayer {
    Core,
    Component,
}

internal typealias WasmLayerDecoder = (ByteArray) -> WasmLayer?

internal fun WasmLayerDecoder(bytes: ByteArray): WasmLayer? {
    if (bytes.size < WASM_PREAMBLE_SIZE || !bytes.hasWasmMagic()) {
        return null
    }

    val layer = (bytes[LAYER_OFFSET].toInt() and BYTE_MASK) or
        ((bytes[LAYER_OFFSET + 1].toInt() and BYTE_MASK) shl 8)

    return when (layer) {
        CORE_LAYER -> WasmLayer.Core
        COMPONENT_LAYER -> WasmLayer.Component
        else -> null
    }
}

private fun ByteArray.hasWasmMagic(): Boolean {
    return this[0] == WASM_MAGIC_0 &&
        this[1] == WASM_MAGIC_1 &&
        this[2] == WASM_MAGIC_2 &&
        this[3] == WASM_MAGIC_3
}

private const val WASM_PREAMBLE_SIZE = 8
private const val LAYER_OFFSET = 6
private const val BYTE_MASK = 0xFF
private const val CORE_LAYER = 0
private const val COMPONENT_LAYER = 1
private const val WASM_MAGIC_0: Byte = 0x00
private const val WASM_MAGIC_1: Byte = 0x61
private const val WASM_MAGIC_2: Byte = 0x73
private const val WASM_MAGIC_3: Byte = 0x6D
