package io.github.charlietap.chasm.decoder.section.code

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias LocalDecoder = (WasmBinaryReader) -> Result<Local, WasmDecodeError>
