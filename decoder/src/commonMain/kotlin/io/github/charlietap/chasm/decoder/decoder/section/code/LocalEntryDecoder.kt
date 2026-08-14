package io.github.charlietap.chasm.decoder.decoder.section.code

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.CodeBodyDecoderContext
import io.github.charlietap.chasm.decoder.decoder.CodeBodyDecoder
import io.github.charlietap.chasm.decoder.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.type.ValueType

internal fun LocalEntryDecoder(
    context: CodeBodyDecoderContext,
): Result<LocalEntry, WasmDecodeError> =
    LocalEntryDecoder(
        context = context,
        valueTypeDecoder = ::ValueTypeDecoder,
    )

internal inline fun LocalEntryDecoder(
    context: CodeBodyDecoderContext,
    crossinline valueTypeDecoder: CodeBodyDecoder<ValueType>,
): Result<LocalEntry, WasmDecodeError> = binding {

    val count = context.reader.uint()
    val valueType = valueTypeDecoder(context).bind()

    LocalEntry(count, valueType)
}
