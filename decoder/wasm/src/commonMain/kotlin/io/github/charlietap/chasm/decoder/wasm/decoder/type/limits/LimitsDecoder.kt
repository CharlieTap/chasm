package io.github.charlietap.chasm.decoder.wasm.decoder.type.limits

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.flatMap
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal fun LimitsDecoder(
    context: DecoderContext,
): Result<Limits, WasmDecodeError> = binding {

    val hasMaximum = context.reader.ubyte().flatMap { byte ->
        when (byte) {
            0.toUByte() -> Ok(false)
            1.toUByte() -> Ok(true)
            else -> Err(TypeDecodeError.UnknownLimitsFlag(byte))
        }
    }.bind()

    val minimum = context.reader.uint().bind()

    if (hasMaximum) {
        Limits(minimum, context.reader.uint().bind())
    } else {
        Limits(minimum)
    }
}
