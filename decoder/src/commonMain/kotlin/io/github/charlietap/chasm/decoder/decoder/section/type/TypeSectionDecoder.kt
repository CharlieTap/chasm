package io.github.charlietap.chasm.decoder.decoder.section.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.recursive.RecursiveTypeDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.BinaryVectorLengthDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorLengthDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.section.TypeSection

internal fun TypeSectionDecoder(
    context: DecoderContext,
): Result<TypeSection, WasmDecodeError> =
    TypeSectionDecoder(
        context = context,
        vectorLengthDecoder = ::BinaryVectorLengthDecoder,
        recursiveTypeDecoder = ::RecursiveTypeDecoder,
    )

internal fun TypeSectionDecoder(
    context: DecoderContext,
    vectorLengthDecoder: VectorLengthDecoder = ::BinaryVectorLengthDecoder,
    recursiveTypeDecoder: Decoder<RecursiveType> = ::RecursiveTypeDecoder,
): Result<TypeSection, WasmDecodeError> = binding {
    val vectorLength = vectorLengthDecoder(context.reader).bind()

    val types = (0u..<vectorLength.length).map { idx ->
        val recursiveType = recursiveTypeDecoder(context).bind()
        Type(Index.TypeIndex(idx), recursiveType)
    }

    TypeSection(types)
}
