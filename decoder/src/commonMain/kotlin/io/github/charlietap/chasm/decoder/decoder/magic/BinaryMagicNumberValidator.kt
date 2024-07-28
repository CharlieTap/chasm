package io.github.charlietap.chasm.decoder.decoder.magic

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.flatMap
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.ext.magicNumber
import io.github.charlietap.chasm.decoder.reader.WasmBinaryReader

internal fun BinaryMagicNumberValidator(
    reader: WasmBinaryReader,
): Result<Unit, WasmDecodeError> = reader.ubytes(4u).flatMap(UByteArray::magicNumber)
