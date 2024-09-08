package io.github.charlietap.chasm.decoder.decoder.type.tag

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.TagType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun TagTypeDecoder(
    context: DecoderContext,
): Result<TagType, WasmDecodeError> =
    TagTypeDecoder(
        context = context,
        attributeDecoder = ::AttributeDecoder,
        typeIndexDecoder = ::TypeIndexDecoder,
    )

internal fun TagTypeDecoder(
    context: DecoderContext,
    attributeDecoder: Decoder<TagType.Attribute>,
    typeIndexDecoder: Decoder<Index.TypeIndex>,
): Result<TagType, WasmDecodeError> = binding {

    val attribute = attributeDecoder(context).bind()
    val index = typeIndexDecoder(context).bind()

    TagType(attribute, index)
}
