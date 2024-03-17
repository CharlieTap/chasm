package io.github.charlietap.chasm.decoder.wasm.decoder.section.function

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.FunctionSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

internal fun interface FunctionSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<FunctionSection, WasmDecodeError>
