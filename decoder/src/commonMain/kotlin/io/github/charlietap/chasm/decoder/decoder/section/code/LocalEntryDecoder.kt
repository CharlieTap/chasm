package io.github.charlietap.chasm.decoder.decoder.section.code

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun LocalEntryDecoder(
    context: DecoderContext,
): Result<LocalEntry, WasmDecodeError> =
    LocalEntryDecoder(
        context = context,
        valueTypeDecoder = ::ValueTypeDecoder,
    )

internal inline fun LocalEntryDecoder(
    context: DecoderContext,
    crossinline valueTypeDecoder: Decoder<ValueType>,
): Result<LocalEntry, WasmDecodeError> = binding {

    val count = context.reader.uint().bind()
    val valueType = valueTypeDecoder(context).bind()

    LocalEntry(count, valueType)
}
