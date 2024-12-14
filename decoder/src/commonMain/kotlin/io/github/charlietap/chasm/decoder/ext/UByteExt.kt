package io.github.charlietap.chasm.decoder.ext

import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.decoder.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.section.SectionType

internal fun UByte.sectionType() = SectionType.entries
    .firstOrNull { type ->
        equals(type.id)
    }.toResultOr { SectionDecodeError.UnknownSectionType(this) }
