package io.github.charlietap.chasm.decoder.wasm.decoder.type.recursive

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryRecursiveTypeDecoder(
    reader: WasmBinaryReader,
): Result<RecursiveType, WasmDecodeError> =
    BinaryRecursiveTypeDecoder(
        reader = reader,
        subTypeDecoder = ::BinarySubTypeDecoder,
        vectorDecoder = ::BinaryVectorDecoder,
    )

internal fun BinaryRecursiveTypeDecoder(
    reader: WasmBinaryReader,
    subTypeDecoder: SubTypeDecoder,
    vectorDecoder: VectorDecoder<SubType>,
): Result<RecursiveType, WasmDecodeError> = binding {
    when (reader.peek().ubyte().bind()) {
        MULTIPLE_SUBTYPES_RECURSIVE_TYPE -> {
            reader.ubyte().bind() // consume byte
            val subTypes = vectorDecoder(reader, subTypeDecoder).bind()
            RecursiveType(subTypes.vector)
        }
        else -> {
            val subType = subTypeDecoder(reader).bind()
            RecursiveType(listOf(subType))
        }
    }
}

internal const val MULTIPLE_SUBTYPES_RECURSIVE_TYPE: UByte = 0x4Eu
