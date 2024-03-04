package io.github.charlietap.chasm.decoder.section.code

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.CodeSection
import io.github.charlietap.chasm.section.SectionSize

class BinaryCodeSectionDecoder(
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
