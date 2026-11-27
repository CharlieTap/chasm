package io.github.charlietap.chasm.decoder.error

internal sealed interface ComponentCanonicalDecodeError : WasmDecodeError {

    data class UnknownDefinition(val opcode: UByte) : ComponentCanonicalDecodeError

    data class UnknownOption(val opcode: UByte) : ComponentCanonicalDecodeError

    data class InvalidAsyncFlag(val opcode: UByte) : ComponentCanonicalDecodeError

    data class InvalidCancellableFlag(val opcode: UByte) : ComponentCanonicalDecodeError

    data class InvalidSharedFlag(val opcode: UByte) : ComponentCanonicalDecodeError
}
