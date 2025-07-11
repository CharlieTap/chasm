package io.github.charlietap.chasm.decoder.decoder.section.custom

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.NameAssociation
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.name.NameValueDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun NameAssociationDecoder(
    context: DecoderContext,
): Result<NameAssociation, WasmDecodeError> = NameAssociationDecoder(
    context = context,
    nameValueDecoder = ::NameValueDecoder,
)

internal inline fun NameAssociationDecoder(
    context: DecoderContext,
    crossinline nameValueDecoder: Decoder<NameValue>,
) = binding {

    val index = context.reader.uint().bind()
    val name = nameValueDecoder(context).bind()

    NameAssociation(
        idx = index,
        name = name,
    )
}
