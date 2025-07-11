@file:JvmName("CustomSectionDecoderKt")

package io.github.charlietap.chasm.decoder.decoder.section.custom

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.NameData
import io.github.charlietap.chasm.ast.module.Uninterpreted
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.context.scope.NameScope
import io.github.charlietap.chasm.decoder.context.scope.Scope
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.name.NameValueDecoder
import io.github.charlietap.chasm.decoder.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.ext.trackBytes
import io.github.charlietap.chasm.decoder.section.CustomSection
import kotlin.jvm.JvmName

internal fun CustomSectionDecoder(
    context: DecoderContext,
): Result<CustomSection, WasmDecodeError> = CustomSectionDecoder(
    context = context,
    nameDataDecoder = ::NameDataDecoder,
    nameScope = ::NameScope,
    nameValueDecoder = ::NameValueDecoder,
)

internal inline fun CustomSectionDecoder(
    context: DecoderContext,
    crossinline nameDataDecoder: Decoder<NameData>,
    crossinline nameScope: Scope<UInt>,
    crossinline nameValueDecoder: Decoder<NameValue>,
) = binding {

    val (nameValue, bytesConsumed) = context.reader.trackBytes {
        nameValueDecoder(context).bind()
    }
    val payloadSize = context.sectionSize.size - bytesConsumed

    val custom = when {
        nameValue.name == NAME_SECTION && context.config.decodeNameSection -> {
            val scopedContext = nameScope(context, payloadSize).bind()
            nameDataDecoder(scopedContext).bind()
        }
        else -> {
            val payload = context.reader.ubytes(payloadSize).bind()
            if (payloadSize != payload.size.toUInt()) {
                Err(SectionDecodeError.SectionSizeMismatch).bind<Unit>()
            }

            Uninterpreted(nameValue, payload)
        }
    }

    CustomSection(custom)
}

internal const val NAME_SECTION: String = "name"
