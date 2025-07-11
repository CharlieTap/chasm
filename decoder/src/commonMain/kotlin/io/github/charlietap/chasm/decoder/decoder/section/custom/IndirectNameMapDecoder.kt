package io.github.charlietap.chasm.decoder.decoder.section.custom

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.IndirectNameAssociation
import io.github.charlietap.chasm.ast.module.IndirectNameMap
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun IndirectNameMapDecoder(
    context: DecoderContext,
): Result<IndirectNameMap, WasmDecodeError> = IndirectNameMapDecoder(
    context = context,
    indirectNameAssociationDecoder = ::IndirectNameAssociationDecoder,
    vectorDecoder = ::VectorDecoder,
)

internal inline fun IndirectNameMapDecoder(
    context: DecoderContext,
    noinline indirectNameAssociationDecoder: Decoder<IndirectNameAssociation>,
    crossinline vectorDecoder: VectorDecoder<IndirectNameAssociation>,
) = binding {
    vectorDecoder(context, indirectNameAssociationDecoder).bind().vector
}
