package io.github.charlietap.chasm.decoder.wasm.decoder.version

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias VersionDecoder = (WasmBinaryReader) -> Result<Version, WasmDecodeError>
