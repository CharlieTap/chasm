package io.github.charlietap.chasm.decoder.wasm.decoder.type.recursive

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal fun RecursiveTypeDecoder(
    context: DecoderContext,
): Result<RecursiveType, WasmDecodeError> =
    RecursiveTypeDecoder(
        context = context,
        subTypeDecoder = ::SubTypeDecoder,
        vectorDecoder = ::VectorDecoder,
    )

internal fun RecursiveTypeDecoder(
    context: DecoderContext,
    subTypeDecoder: Decoder<SubType>,
    vectorDecoder: VectorDecoder<SubType>,
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
