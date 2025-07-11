package io.github.charlietap.chasm.decoder.decoder.section.custom

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.NameAssociation
import io.github.charlietap.chasm.ast.module.NameMap
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun NameMapDecoder(
    context: DecoderContext,
): Result<NameMap, WasmDecodeError> = NameMapDecoder(
    context = context,
    nameAssociationDecoder = ::NameAssociationDecoder,
    vectorDecoder = ::VectorDecoder,
)

internal inline fun NameMapDecoder(
    context: DecoderContext,
    noinline nameAssociationDecoder: Decoder<NameAssociation>,
    crossinline vectorDecoder: VectorDecoder<NameAssociation>,
) = binding {
    vectorDecoder(context, nameAssociationDecoder).bind().vector
}
