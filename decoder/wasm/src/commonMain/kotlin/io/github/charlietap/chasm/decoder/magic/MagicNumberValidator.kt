package io.github.charlietap.chasm.decoder.magic

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias MagicNumberValidator = (WasmBinaryReader) -> Result<Unit, WasmDecodeError>
