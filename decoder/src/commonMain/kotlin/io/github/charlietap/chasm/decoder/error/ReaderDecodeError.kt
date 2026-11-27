package io.github.charlietap.chasm.decoder.error

internal sealed interface ReaderDecodeError : WasmDecodeError {

    data class InvalidIntegerEncoding(val bitWidth: Int) : ReaderDecodeError

    data class SizeMismatch(
        val expectedSize: UInt,
        val actualSize: UInt,
    ) : ReaderDecodeError
}
