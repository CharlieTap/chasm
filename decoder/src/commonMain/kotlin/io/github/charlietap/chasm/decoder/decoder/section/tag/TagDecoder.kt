package io.github.charlietap.chasm.decoder.decoder.section.tag

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Tag
import io.github.charlietap.chasm.ast.type.TagType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.tag.TagTypeDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun TagDecoder(
    context: DecoderContext,
): Result<Tag, WasmDecodeError> =
    TagDecoder(
        context = context,
        typeDecoder = ::TagTypeDecoder,
    )

internal fun TagDecoder(
    context: DecoderContext,
    typeDecoder: Decoder<TagType>,
): Result<Tag, WasmDecodeError> = binding {

    val type = typeDecoder(context).bind()

    Tag(Index.TagIndex(0u), type)
}
