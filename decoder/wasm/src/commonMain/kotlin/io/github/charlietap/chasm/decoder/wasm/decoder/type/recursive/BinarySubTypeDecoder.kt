package io.github.charlietap.chasm.decoder.wasm.decoder.type.recursive

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.BinaryTypeIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.composite.BinaryCompositeTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.composite.CompositeTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinarySubTypeDecoder(
    reader: WasmBinaryReader,
): Result<SubType, WasmDecodeError> =
    BinarySubTypeDecoder(
        reader = reader,
        typeIndexDecoder = ::BinaryTypeIndexDecoder,
        vectorDecoder = ::BinaryVectorDecoder,
        compositeTypeDecoder = ::BinaryCompositeTypeDecoder,
    )

internal fun BinarySubTypeDecoder(
    reader: WasmBinaryReader,
    typeIndexDecoder: TypeIndexDecoder,
    vectorDecoder: VectorDecoder<Index.TypeIndex>,
    compositeTypeDecoder: CompositeTypeDecoder,
): Result<SubType, WasmDecodeError> = binding {
    when (reader.peek().ubyte().bind()) {
        OPEN_SUB_TYPE -> {
            reader.ubyte().bind() // consume byte
            val typeIndices = vectorDecoder(reader, typeIndexDecoder).bind()
            val heapTypes = typeIndices.vector.map(ConcreteHeapType::TypeIndex)
            val compositeType = compositeTypeDecoder(reader).bind()
            SubType.Open(heapTypes, compositeType)
        }
        FINAL_SUB_TYPE -> {
            reader.ubyte().bind() // consume byte
            val typeIndices = vectorDecoder(reader, typeIndexDecoder).bind()
            val heapTypes = typeIndices.vector.map(ConcreteHeapType::TypeIndex)
            val compositeType = compositeTypeDecoder(reader).bind()
            SubType.Final(heapTypes, compositeType)
        }
        else -> {
            val compositeType = compositeTypeDecoder(reader).bind()
            SubType.Final(emptyList(), compositeType)
        }
    }
}

internal const val OPEN_SUB_TYPE: UByte = 0x50u
internal const val FINAL_SUB_TYPE: UByte = 0x4Fu
