package io.github.charlietap.chasm.decoder.decoder.section.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.section.TypeSection
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory

internal fun TypeSectionDecoder(
    context: DecoderContext,
): Result<TypeSection, WasmDecodeError> =
    TypeSectionDecoder(
        context = context,
        definedTypeFactory = ::DefinedTypeFactory,
        typeDecoder = ::TypeDecoder,
        vectorDecoder = ::VectorDecoder,
    )

internal inline fun TypeSectionDecoder(
    context: DecoderContext,
    crossinline definedTypeFactory: DefinedTypeFactory,
    noinline typeDecoder: Decoder<Type>,
    crossinline vectorDecoder: VectorDecoder<Type>,
): Result<TypeSection, WasmDecodeError> = binding {

    val types = vectorDecoder(context, typeDecoder).bind().vector
    val definedTypes = definedTypeFactory(types.map(Type::recursiveType))

    context.types += types
    context.definedTypes += definedTypes

    TypeSection(types, definedTypes)
}
