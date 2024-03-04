package io.github.charlietap.chasm.decoder.type.limits

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.flatMap
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.error.TypeDecodeError
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryLimitsDecoder(
    reader: WasmBinaryReader,
): Result<Limits, WasmDecodeError> = binding {

    val hasMaximum = reader.ubyte().flatMap { byte ->
        when (byte) {
            0.toUByte() -> Ok(false)
            1.toUByte() -> Ok(true)
            else -> Err(TypeDecodeError.UnknownLimitsFlag(byte))
        }
    }.bind()

    val minimum = reader.uint().bind()

    if (hasMaximum) {
        Limits(minimum, reader.uint().bind())
    } else {
        Limits(minimum)
    }
}
