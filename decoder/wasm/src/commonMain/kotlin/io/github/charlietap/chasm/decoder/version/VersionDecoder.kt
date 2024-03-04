package io.github.charlietap.chasm.decoder.version

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias VersionDecoder = (WasmBinaryReader) -> Result<Version, WasmDecodeError>
