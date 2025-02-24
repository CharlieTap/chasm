package io.github.charlietap.chasm.decoder.decoder.type.global

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.MutabilityDecoder
import io.github.charlietap.chasm.decoder.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.ValueType

internal fun GlobalTypeDecoder(
    context: DecoderContext,
): Result<GlobalType, WasmDecodeError> = GlobalTypeDecoder(
    context = context,
    valueTypeDecoder = ::ValueTypeDecoder,
    mutabilityDecoder = ::MutabilityDecoder,
)

internal inline fun GlobalTypeDecoder(
    context: DecoderContext,
    crossinline valueTypeDecoder: Decoder<ValueType>,
    crossinline mutabilityDecoder: Decoder<Mutability>,
): Result<GlobalType, WasmDecodeError> = binding {
    val valueType = valueTypeDecoder(context).bind()
    val mutability = mutabilityDecoder(context).bind()
    GlobalType(valueType, mutability)
}
