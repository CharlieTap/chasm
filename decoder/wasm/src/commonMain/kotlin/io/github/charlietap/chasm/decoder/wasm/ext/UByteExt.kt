package io.github.charlietap.chasm.decoder.wasm.ext

import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.decoder.wasm.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.wasm.section.SectionType

internal fun UByte.sectionType() = SectionType.entries.firstOrNull { type ->
    equals(type.id)
}.toResultOr { SectionDecodeError.UnknownSectionType(this) }
