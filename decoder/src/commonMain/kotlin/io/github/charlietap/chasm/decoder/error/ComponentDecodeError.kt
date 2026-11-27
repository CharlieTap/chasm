package io.github.charlietap.chasm.decoder.error

internal sealed interface ComponentDecodeError : WasmDecodeError {

    data class InvalidLayer(val layer: UByteArray) : ComponentDecodeError

    data class UnknownSection(val id: UByte) : ComponentDecodeError

    data class InvalidOptionalTag(val tag: UByte) : ComponentDecodeError

    data class VectorTooLarge(val size: UInt) : ComponentDecodeError

    data class UnknownCoreSort(val opcode: UByte) : ComponentDecodeError

    data class UnknownSort(val opcode: UByte) : ComponentDecodeError

    data class InvalidSortTarget(val opcode: UByte) : ComponentDecodeError

    data class UnknownNameAttribute(val opcode: UByte) : ComponentDecodeError

    data class UnknownExternalAttribute(val opcode: UByte) : ComponentDecodeError

    data class UnknownAlias(val opcode: UByte) : ComponentDecodeError

    data class InvalidOuterAliasSort(val opcode: UByte) : ComponentDecodeError

    data class InvalidInstanceAliasSort(val opcode: UByte) : ComponentDecodeError

    data class InvalidCoreInstanceAliasSort(val opcode: UByte) : ComponentDecodeError

    data class UnknownCoreInstanceExpression(val opcode: UByte) : ComponentDecodeError

    data class UnknownInstanceExpression(val opcode: UByte) : ComponentDecodeError

    data class InvalidMarker(
        val expected: UByte,
        val actual: UByte,
    ) : ComponentDecodeError
}
