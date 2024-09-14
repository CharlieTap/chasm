package io.github.charlietap.chasm.decoder.error

import kotlin.jvm.JvmInline

sealed interface TypeDecodeError : WasmDecodeError {
    @JvmInline
    value class InvalidNumberType(val encoded: UByte) : TypeDecodeError

    @JvmInline
    value class InvalidVectorType(val encoded: UByte) : TypeDecodeError

    @JvmInline
    value class InvalidHeapType(val encoded: UByte) : TypeDecodeError

    @JvmInline
    value class InvalidPackedType(val encoded: UByte) : TypeDecodeError

    @JvmInline
    value class InvalidStorageType(val encoded: UByte) : TypeDecodeError

    @JvmInline
    value class InvalidCompositeType(val encoded: UByte) : TypeDecodeError

    @JvmInline
    value class InvalidReferenceType(val encoded: UByte) : TypeDecodeError

    @JvmInline
    value class InvalidValueType(val encoded: UByte) : TypeDecodeError

    @JvmInline
    value class InvalidFunctionType(val encoded: UByte) : TypeDecodeError

    @JvmInline
    value class UnknownLimitsFlag(val encoded: UByte) : TypeDecodeError

    @JvmInline
    value class UnknownMutabilityFlag(val encoded: UByte) : TypeDecodeError

    data object InvalidTagType : TypeDecodeError
}
