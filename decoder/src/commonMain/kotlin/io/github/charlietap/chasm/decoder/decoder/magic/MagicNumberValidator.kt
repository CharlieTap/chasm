package io.github.charlietap.chasm.decoder.decoder.magic

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.reader.WasmBinaryReader

internal typealias MagicNumberValidator = (WasmBinaryReader) -> Result<Unit, WasmDecodeError>
