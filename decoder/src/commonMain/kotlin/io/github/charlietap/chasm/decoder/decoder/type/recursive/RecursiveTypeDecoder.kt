package io.github.charlietap.chasm.decoder.decoder.type.recursive

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun RecursiveTypeDecoder(
    context: DecoderContext,
): Result<RecursiveType, WasmDecodeError> =
    RecursiveTypeDecoder(
        context = context,
        subTypeDecoder = ::SubTypeDecoder,
        vectorDecoder = ::VectorDecoder,
    )

internal inline fun RecursiveTypeDecoder(
    context: DecoderContext,
    noinline subTypeDecoder: Decoder<SubType>,
    crossinline vectorDecoder: VectorDecoder<SubType>,
): Result<RecursiveType, WasmDecodeError> = binding {
    when (context.reader.peek().ubyte().bind()) {
        MULTIPLE_SUBTYPES_RECURSIVE_TYPE -> {
            context.reader.ubyte().bind() // consume byte
            val subTypes = vectorDecoder(context, subTypeDecoder).bind()
            RecursiveType(subTypes.vector)
        }
        else -> {
            val subType = subTypeDecoder(context).bind()
            RecursiveType(listOf(subType))
        }
    }
}

internal const val MULTIPLE_SUBTYPES_RECURSIVE_TYPE: UByte = 0x4Eu
