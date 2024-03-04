package io.github.charlietap.chasm.decoder.section.element

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias ElementKindDecoder = (WasmBinaryReader) -> Result<ElementKind, WasmDecodeError>
