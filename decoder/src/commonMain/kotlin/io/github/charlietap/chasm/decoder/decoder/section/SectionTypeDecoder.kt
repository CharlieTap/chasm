package io.github.charlietap.chasm.decoder.decoder.section

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.flatMap
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.ext.sectionType
import io.github.charlietap.chasm.decoder.section.SectionType

internal fun SectionTypeDecoder(
    context: DecoderContext,
): Result<SectionType, WasmDecodeError> = context.reader.ubyte().flatMap(UByte::sectionType)
