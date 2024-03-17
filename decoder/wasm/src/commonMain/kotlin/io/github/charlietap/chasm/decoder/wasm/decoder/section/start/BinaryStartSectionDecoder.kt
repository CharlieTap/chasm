package io.github.charlietap.chasm.decoder.wasm.decoder.section.start

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.StartFunction
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.BinaryFunctionIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.FunctionIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import io.github.charlietap.chasm.decoder.wasm.section.StartSection

internal class BinaryStartSectionDecoder(
    private val functionIndexDecoder: FunctionIndexDecoder = ::BinaryFunctionIndexDecoder,
) : StartSectionDecoder {
    override fun invoke(
        reader: WasmBinaryReader,
        size: SectionSize,
    ): Result<StartSection, WasmDecodeError> = binding {

        val functionIndex = functionIndexDecoder(reader).bind()

        StartSection(StartFunction(functionIndex))
    }
}
