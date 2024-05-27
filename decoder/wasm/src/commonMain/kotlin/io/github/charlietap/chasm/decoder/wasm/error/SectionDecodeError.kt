package io.github.charlietap.chasm.decoder.wasm.error

import kotlin.jvm.JvmInline

sealed interface SectionDecodeError : WasmDecodeError {
    @JvmInline
    value class UnknownSectionType(val type: UByte) : SectionDecodeError

    @JvmInline
    value class InvalidIndex(val encoded: UInt) : SectionDecodeError

    @JvmInline
    value class UnknownImportDescriptor(val descriptor: UByte) : SectionDecodeError

    @JvmInline
    value class UnknownExportDescriptor(val descriptor: UByte) : SectionDecodeError

    @JvmInline
    value class UnknownDataSegment(val segmentId: UInt) : SectionDecodeError

    @JvmInline
    value class UnknownElementSegment(val segmentId: UInt) : SectionDecodeError

    @JvmInline
    value class UnknownElementKind(val byte: Byte) : SectionDecodeError

    data object MultipleStartFunctions : SectionDecodeError

    data object DataCountMismatch : SectionDecodeError

    data object DataCountRequired : SectionDecodeError

    data object SectionSizeMismatch : SectionDecodeError

    data object TooManyLocals : SectionDecodeError
}
