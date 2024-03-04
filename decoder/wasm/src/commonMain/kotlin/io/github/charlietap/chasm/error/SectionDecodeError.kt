package io.github.charlietap.chasm.error

import io.github.charlietap.chasm.section.SectionType
import kotlin.jvm.JvmInline

sealed interface SectionDecodeError : WasmDecodeError {
    @JvmInline
    value class UnknownSectionType(val type: UByte) : SectionDecodeError

    data class InvalidSection(val type: SectionType, val position: Long) : SectionDecodeError

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
}
