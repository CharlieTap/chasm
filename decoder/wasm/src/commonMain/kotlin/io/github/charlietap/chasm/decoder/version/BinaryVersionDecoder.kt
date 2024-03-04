package io.github.charlietap.chasm.decoder.version

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.flatMap
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.ext.version
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryVersionDecoder(
    reader: WasmBinaryReader,
): Result<Version, WasmDecodeError> = reader.ubytes(4u).flatMap(UByteArray::version)
