@file:JvmName("CustomSectionDecoderKt")

package io.github.charlietap.chasm.decoder.wasm.decoder.section.custom

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Custom
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.decoder.value.name.NameValueDecoder
import io.github.charlietap.chasm.decoder.wasm.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.ext.trackBytes
import io.github.charlietap.chasm.decoder.wasm.section.CustomSection
import kotlin.jvm.JvmName

internal fun CustomSectionDecoder(
    context: DecoderContext,
): Result<CustomSection, WasmDecodeError> = CustomSectionDecoder(
    context = context,
    nameValueDecoder = ::NameValueDecoder,
)

internal fun CustomSectionDecoder(
    context: DecoderContext,
    nameValueDecoder: Decoder<NameValue>,
) = binding {
    val (nameResult, bytesConsumed) = context.reader.trackBytes {
        nameValueDecoder(context)
    }

    val nameValue = nameResult.bind()
    val payloadSize = context.sectionSize.size - bytesConsumed
    val payload = context.reader.ubytes(payloadSize).bind()

    if (payloadSize != payload.size.toUInt()) {
        Err(SectionDecodeError.SectionSizeMismatch).bind<Unit>()
    }

    CustomSection(Custom(nameValue, payload))
}
