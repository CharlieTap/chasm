package io.github.charlietap.chasm.decoder.decoder.type.limits

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.flatMap
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.SharedStatus
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun LimitsDecoder(
    context: DecoderContext,
): Result<Pair<Limits, SharedStatus>, WasmDecodeError> = binding {

    val (hasMaximum, sharedStatus) = context.reader
        .ubyte()
        .flatMap { byte ->
            when (byte) {
                LIMIT_NO_MAX -> Ok(false to SharedStatus.Unshared)
                LIMIT_MAX_UNSHARED -> Ok(true to SharedStatus.Unshared)
                LIMIT_MAX_SHARED -> Ok(true to SharedStatus.Shared)
                else -> Err(TypeDecodeError.UnknownLimitsFlag(byte))
            }
        }.bind()

    val minimum = context.reader.uint().bind()

    if (hasMaximum) {
        Limits(minimum, context.reader.uint().bind()) to sharedStatus
    } else {
        Limits(minimum) to sharedStatus
    }
}

internal const val LIMIT_NO_MAX: UByte = 0u
internal const val LIMIT_MAX_UNSHARED: UByte = 1u
internal const val LIMIT_MAX_SHARED: UByte = 3u
