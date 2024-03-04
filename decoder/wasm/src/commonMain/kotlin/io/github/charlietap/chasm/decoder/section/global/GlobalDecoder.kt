package io.github.charlietap.chasm.decoder.section.global

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias GlobalDecoder = (WasmBinaryReader, Index.GlobalIndex) -> Result<Global, WasmDecodeError>
