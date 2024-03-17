package io.github.charlietap.chasm.decoder.wasm.decoder.version

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.flatMap
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.ext.version
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryVersionDecoder(
    reader: WasmBinaryReader,
): Result<Version, WasmDecodeError> = reader.ubytes(4u).flatMap(UByteArray::version)
