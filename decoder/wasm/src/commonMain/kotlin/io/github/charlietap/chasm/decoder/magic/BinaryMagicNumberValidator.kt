package io.github.charlietap.chasm.decoder.magic

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.flatMap
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.ext.magicNumber
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryMagicNumberValidator(
    reader: WasmBinaryReader,
): Result<Unit, WasmDecodeError> = reader.ubytes(4u).flatMap(UByteArray::magicNumber)
