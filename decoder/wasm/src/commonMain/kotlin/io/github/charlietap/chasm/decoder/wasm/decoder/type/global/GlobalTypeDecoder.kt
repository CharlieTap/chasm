package io.github.charlietap.chasm.decoder.wasm.decoder.type.global

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.decoder.type.MutabilityDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal fun GlobalTypeDecoder(
    context: DecoderContext,
): Result<GlobalType, WasmDecodeError> = GlobalTypeDecoder(
    context = context,
    valueTypeDecoder = ::ValueTypeDecoder,
    mutabilityDecoder = ::MutabilityDecoder,
)

internal fun GlobalTypeDecoder(
    context: DecoderContext,
    valueTypeDecoder: Decoder<ValueType>,
    mutabilityDecoder: Decoder<Mutability>,
): Result<GlobalType, WasmDecodeError> = binding {
    val valueType = valueTypeDecoder(context).bind()
    val mutability = mutabilityDecoder(context).bind()
    GlobalType(valueType, mutability)
}
