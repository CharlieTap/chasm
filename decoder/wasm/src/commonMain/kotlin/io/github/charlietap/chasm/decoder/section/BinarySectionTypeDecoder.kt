package io.github.charlietap.chasm.decoder.section

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.flatMap
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.ext.sectionType
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.SectionType

fun BinarySectionTypeDecoder(
    reader: WasmBinaryReader,
): Result<SectionType, WasmDecodeError> = reader.ubyte().flatMap(UByte::sectionType)
