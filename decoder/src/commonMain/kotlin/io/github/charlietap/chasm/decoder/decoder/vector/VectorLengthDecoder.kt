package io.github.charlietap.chasm.decoder.decoder.vector

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.reader.WasmBinaryReader

internal typealias VectorLengthDecoder = (WasmBinaryReader) -> Result<VectorLength, WasmDecodeError>
