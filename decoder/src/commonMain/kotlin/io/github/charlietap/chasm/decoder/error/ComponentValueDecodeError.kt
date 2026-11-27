package io.github.charlietap.chasm.decoder.error

internal sealed interface ComponentValueDecodeError : WasmDecodeError {

    data class InvalidBoolean(val opcode: UByte) : ComponentValueDecodeError

    data object NonCanonicalNan : ComponentValueDecodeError

    data class InvalidUnicode(val bytes: UByteArray) : ComponentValueDecodeError

    data class UnsupportedValueType(val type: String) : ComponentValueDecodeError
}
