package io.github.charlietap.chasm.decoder.decoder.section.custom

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.IndirectNameAssociation
import io.github.charlietap.chasm.ast.module.NameMap
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun IndirectNameAssociationDecoder(
    context: DecoderContext,
): Result<IndirectNameAssociation, WasmDecodeError> = IndirectNameAssociationDecoder(
    context = context,
    nameMapDecoder = ::NameMapDecoder,
)

internal inline fun IndirectNameAssociationDecoder(
    context: DecoderContext,
    crossinline nameMapDecoder: Decoder<NameMap>,
) = binding {

    val index = context.reader.uint().bind()
    val nameMap = nameMapDecoder(context).bind()

    IndirectNameAssociation(
        idx = index,
        nameMap = nameMap,
    )
}
