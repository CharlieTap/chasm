package io.github.charlietap.chasm.decoder.decoder.section.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.section.TypeSection

internal fun TypeSectionDecoder(
    context: DecoderContext,
): Result<TypeSection, WasmDecodeError> =
    TypeSectionDecoder(
        context = context,
        vectorDecoder = ::VectorDecoder,
        typeDecoder = ::TypeDecoder,
    )

internal inline fun TypeSectionDecoder(
    context: DecoderContext,
    crossinline vectorDecoder: VectorDecoder<Type>,
    noinline typeDecoder: Decoder<Type>,
): Result<TypeSection, WasmDecodeError> = binding {

    val types = vectorDecoder(context, typeDecoder).bind().vector

    context.types += types
    TypeSection(types)
}
