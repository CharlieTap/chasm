package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal fun CastFlagsDecoder(
    context: DecoderContext,
): Result<CastFlags, WasmDecodeError> = binding {
    when (val flagsByte = context.reader.ubyte().bind()) {
        NON_NULL_BOTH_CAST_FLAGS -> CastFlags(Nullability.NON_NULL, Nullability.NON_NULL)
        NULL_TO_NON_NULL_CAST_FLAGS -> CastFlags(Nullability.NULL, Nullability.NON_NULL)
        NON_NULL_TO_NULL_CAST_FLAGS -> CastFlags(Nullability.NON_NULL, Nullability.NULL)
        NULL_BOTH_CAST_FLAGS -> CastFlags(Nullability.NULL, Nullability.NULL)
        else -> Err(InstructionDecodeError.InvalidCastFlags(flagsByte)).bind<CastFlags>()
    }
}

internal const val NON_NULL_BOTH_CAST_FLAGS: UByte = 0x00u
internal const val NULL_TO_NON_NULL_CAST_FLAGS: UByte = 0x01u
internal const val NON_NULL_TO_NULL_CAST_FLAGS: UByte = 0x02u
internal const val NULL_BOTH_CAST_FLAGS: UByte = 0x03u
