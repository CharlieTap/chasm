package io.github.charlietap.chasm.decoder.decoder.type.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.limits.LimitsDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun MemoryTypeDecoder(
    context: DecoderContext,
): Result<MemoryType, WasmDecodeError> = MemoryTypeDecoder(
    context = context,
    limitsDecoder = ::LimitsDecoder,
)

internal fun MemoryTypeDecoder(
    context: DecoderContext,
    limitsDecoder: Decoder<Limits>,
): Result<MemoryType, WasmDecodeError> = binding {
    val limits = limitsDecoder(context).bind()
    MemoryType(limits)
}
