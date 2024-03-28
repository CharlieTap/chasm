package io.github.charlietap.chasm.decoder.wasm.decoder.section.global

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.SubDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.GlobalSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

internal class BinaryGlobalSectionDecoder(
    private val vectorDecoder: VectorDecoder<Global> = ::BinaryVectorDecoder,
    private val globalDecoder: GlobalDecoder = ::BinaryGlobalDecoder,
) : GlobalSectionDecoder {

    override fun invoke(
        reader: WasmBinaryReader,
        size: SectionSize,
    ): Result<GlobalSection, WasmDecodeError> = binding {

        var index = 0u

        val subDecoder: SubDecoder<Global> = { reader ->
            val globalIndex = Index.GlobalIndex(index).also {
                index++
            }
            globalDecoder(reader, globalIndex)
        }

        val globals = vectorDecoder(reader, subDecoder).bind()

        GlobalSection(globals.vector)
    }
}
