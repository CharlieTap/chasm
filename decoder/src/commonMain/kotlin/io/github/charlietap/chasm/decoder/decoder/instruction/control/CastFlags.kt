package io.github.charlietap.chasm.decoder.decoder.instruction.control

internal data class CastFlags(val src: Nullability, val dst: Nullability)

internal enum class Nullability {
    NULL,
    NON_NULL,
}
