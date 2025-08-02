package io.github.charlietap.chasm.decoder.decoder.type.limits

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.map
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.reader.WasmBinaryReader
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.SharedStatus

internal fun LimitsDecoder(
    context: DecoderContext,
): Result<Triple<Limits, SharedStatus, AddressType>, WasmDecodeError> = binding {

    val (hasMaximum, sharedStatus, addressType) = context.reader
        .ubyte()
        .flatMap { byte ->
            when (byte) {
                LIMIT_NO_MAX_UNSHARED_I32 -> Ok(Triple(false, SharedStatus.Unshared, AddressType.I32))
                LIMIT_MAX_UNSHARED_I32 -> Ok(Triple(true, SharedStatus.Unshared, AddressType.I32))
                LIMIT_NO_MAX_SHARED_I32 -> Err(TypeDecodeError.UnboundedSharedLimits)
                LIMIT_MAX_SHARED_I32 -> Ok(Triple(true, SharedStatus.Shared, AddressType.I32))
                LIMIT_NO_MAX_UNSHARED_I64 -> Ok(Triple(false, SharedStatus.Unshared, AddressType.I64))
                LIMIT_MAX_UNSHARED_I64 -> Ok(Triple(true, SharedStatus.Unshared, AddressType.I64))
                LIMIT_NO_MAX_SHARED_I64 -> Err(TypeDecodeError.UnboundedSharedLimits)
                LIMIT_MAX_SHARED_I64 -> Ok(Triple(true, SharedStatus.Shared, AddressType.I64))
                else -> Err(TypeDecodeError.UnknownLimitsFlag(byte))
            }
        }.bind()

    val scalarReader: (WasmBinaryReader) -> Result<ULong, WasmDecodeError> = when (addressType) {
        AddressType.I32 -> { reader ->
            reader.uint().map(UInt::toULong)
        }
        AddressType.I64 -> { reader ->
            reader.ulong()
        }
    }

    val minimum = scalarReader(context.reader).bind()
    if (hasMaximum) {
        val limits = Limits(minimum, scalarReader(context.reader).bind())
        Triple(limits, sharedStatus, addressType)
    } else {
        Triple(Limits(minimum), sharedStatus, addressType)
    }
}

internal const val LIMIT_NO_MAX_UNSHARED_I32: UByte = 0u
internal const val LIMIT_MAX_UNSHARED_I32: UByte = 1u
internal const val LIMIT_NO_MAX_SHARED_I32: UByte = 2u // Shared memory with no max is not permitted
internal const val LIMIT_MAX_SHARED_I32: UByte = 3u
internal const val LIMIT_NO_MAX_UNSHARED_I64: UByte = 4u
internal const val LIMIT_MAX_UNSHARED_I64: UByte = 5u
internal const val LIMIT_NO_MAX_SHARED_I64: UByte = 6u // Shared memory with no max is not permitted
internal const val LIMIT_MAX_SHARED_I64: UByte = 7u
