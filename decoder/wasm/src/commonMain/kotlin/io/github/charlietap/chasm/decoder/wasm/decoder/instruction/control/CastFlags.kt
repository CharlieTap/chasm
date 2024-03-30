package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.control

data class CastFlags(val src: Nullability, val dst: Nullability)

enum class Nullability {
    NULL,
    NON_NULL,
}
