package io.github.charlietap.chasm.decoder.wasm.decoder.magic

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.flatMap
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.ext.magicNumber
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryMagicNumberValidator(
    reader: WasmBinaryReader,
): Result<Unit, WasmDecodeError> = reader.ubytes(4u).flatMap(UByteArray::magicNumber)
