package io.github.charlietap.chasm.decoder.wasm.decoder.section.function

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.BinaryTypeIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.FunctionSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

internal class BinaryFunctionSectionDecoder(
    private val vectorDecoder: VectorDecoder<Index.TypeIndex> = ::BinaryVectorDecoder,
    private val subDecoder: TypeIndexDecoder = ::BinaryTypeIndexDecoder,
) : FunctionSectionDecoder {
    override fun invoke(
        reader: WasmBinaryReader,
        size: SectionSize,
    ): Result<FunctionSection, WasmDecodeError> = binding {

        val indices = vectorDecoder(reader, subDecoder).bind()

        val builders = indices.vector.mapIndexed { index, typeIndex ->
            FunctionHeader(Index.FunctionIndex(index.toUInt()), typeIndex)
        }

        FunctionSection(builders)
    }
}
