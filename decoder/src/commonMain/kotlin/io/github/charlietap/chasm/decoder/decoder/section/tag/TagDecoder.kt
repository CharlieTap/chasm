package io.github.charlietap.chasm.decoder.decoder.section.tag

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Tag
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.tag.TagTypeDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.type.TagType

internal fun TagDecoder(
    context: DecoderContext,
): Result<Tag, WasmDecodeError> =
    TagDecoder(
        context = context,
        typeDecoder = ::TagTypeDecoder,
    )

internal inline fun TagDecoder(
    context: DecoderContext,
    crossinline typeDecoder: Decoder<TagType>,
): Result<Tag, WasmDecodeError> = binding {

    val type = typeDecoder(context).bind()

    val tagImportCount = context.imports.count { it.descriptor is Import.Descriptor.Tag }
    val index = tagImportCount + context.index
    val tagIndex = Index.TagIndex(index.toUInt())

    Tag(tagIndex, type)
}
