package io.github.charlietap.chasm.decoder.wasm.decoder.section.code

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.CodeSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

internal class BinaryCodeSectionDecoder(
    private val codeEntryDecoder: CodeEntryDecoder = ::BinaryCodeEntryDecoder,
    private val vectorDecoder: VectorDecoder<CodeEntry> = ::BinaryVectorDecoder,
) : CodeSectionDecoder {
    override fun invoke(
        reader: WasmBinaryReader,
        size: SectionSize,
    ): Result<CodeSection, WasmDecodeError> = binding {

        val indices = vectorDecoder(reader, codeEntryDecoder).bind()

        val bodies = indices.vector.mapIndexed { index, codeEntry ->
            FunctionBody(Index.FunctionIndex(index.toUInt()), codeEntry.locals, codeEntry.body)
        }

        CodeSection(bodies)
    }
}
