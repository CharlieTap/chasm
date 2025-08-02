package io.github.charlietap.chasm.decoder.decoder.type.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.limits.LimitsDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.SharedStatus

internal fun MemoryTypeDecoder(
    context: DecoderContext,
): Result<MemoryType, WasmDecodeError> = MemoryTypeDecoder(
    context = context,
    limitsDecoder = ::LimitsDecoder,
)

internal inline fun MemoryTypeDecoder(
    context: DecoderContext,
    crossinline limitsDecoder: Decoder<Triple<Limits, SharedStatus, AddressType>>,
): Result<MemoryType, WasmDecodeError> = binding {
    val (limits, shared, addressType) = limitsDecoder(context).bind()
    MemoryType(addressType, limits, shared)
}
