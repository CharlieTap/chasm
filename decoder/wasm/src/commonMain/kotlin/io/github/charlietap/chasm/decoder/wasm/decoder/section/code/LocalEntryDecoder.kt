package io.github.charlietap.chasm.decoder.wasm.decoder.section.code

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal fun LocalEntryDecoder(
    context: DecoderContext,
): Result<LocalEntry, WasmDecodeError> =
    LocalEntryDecoder(
        context = context,
        valueTypeDecoder = ::ValueTypeDecoder,
    )

internal fun LocalEntryDecoder(
    context: DecoderContext,
    valueTypeDecoder: Decoder<ValueType>,
): Result<LocalEntry, WasmDecodeError> = binding {

    val count = context.reader.uint().bind()
    val valueType = valueTypeDecoder(context).bind()

    LocalEntry(count, valueType)
}
