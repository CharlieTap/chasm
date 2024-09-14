package io.github.charlietap.chasm.decoder.decoder.section.tag

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Tag
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.section.TagSection

internal fun TagSectionDecoder(
    context: DecoderContext,
): Result<TagSection, WasmDecodeError> =
    TagSectionDecoder(
        context = context,
        tagDecoder = ::TagDecoder,
        vectorDecoder = ::VectorDecoder,
    )

internal fun TagSectionDecoder(
    context: DecoderContext,
    tagDecoder: Decoder<Tag>,
    vectorDecoder: VectorDecoder<Tag> = ::VectorDecoder,
): Result<TagSection, WasmDecodeError> = binding {

    val tags = vectorDecoder(context, tagDecoder).bind().vector.mapIndexed { idx, tag ->
        tag.copy(
            index = Index.TagIndex(idx.toUInt()),
        )
    }

    TagSection(tags)
}
