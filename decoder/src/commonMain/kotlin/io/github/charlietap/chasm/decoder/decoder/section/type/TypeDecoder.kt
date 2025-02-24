package io.github.charlietap.chasm.decoder.decoder.section.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.recursive.RecursiveTypeDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.type.RecursiveType

internal fun TypeDecoder(
    context: DecoderContext,
): Result<Type, WasmDecodeError> =
    TypeDecoder(
        context = context,
        typeDecoder = ::RecursiveTypeDecoder,
    )

internal inline fun TypeDecoder(
    context: DecoderContext,
    crossinline typeDecoder: Decoder<RecursiveType>,
): Result<Type, WasmDecodeError> = binding {

    val type = typeDecoder(context).bind()

    val index = context.index
    val typeIndex = Index.TypeIndex(index.toUInt())

    Type(typeIndex, type)
}
