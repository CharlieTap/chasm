package io.github.charlietap.chasm.decoder.section.export

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias ExportDecoder = (WasmBinaryReader) -> Result<Export, WasmDecodeError>
