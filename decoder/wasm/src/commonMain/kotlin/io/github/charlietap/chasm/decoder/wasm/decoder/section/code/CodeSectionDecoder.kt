package io.github.charlietap.chasm.decoder.wasm.decoder.section.code

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.CodeSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

internal fun interface CodeSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<CodeSection, WasmDecodeError>
