package io.github.charlietap.chasm.decoder.decoder.type.tag

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.ext.functionType

internal fun TagTypeDecoder(
    context: DecoderContext,
): Result<TagType, WasmDecodeError> =
    TagTypeDecoder(
        context = context,
        attributeDecoder = ::AttributeDecoder,
        typeIndexDecoder = ::TypeIndexDecoder,
    )

internal inline fun TagTypeDecoder(
    context: DecoderContext,
    crossinline attributeDecoder: Decoder<TagType.Attribute>,
    crossinline typeIndexDecoder: Decoder<Index.TypeIndex>,
): Result<TagType, WasmDecodeError> = binding {

    val attribute = attributeDecoder(context).bind()
    val index = typeIndexDecoder(context).bind()

    val type = context.definedTypes[index.idx.toInt()]
    val functionType = type.functionType() ?: Err(TypeDecodeError.InvalidTagType).bind()

    TagType(attribute, type, functionType)
}
