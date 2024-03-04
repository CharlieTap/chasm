package io.github.charlietap.chasm.ext

import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.error.SectionDecodeError
import io.github.charlietap.chasm.section.SectionType

fun UByte.sectionType() = SectionType.entries.firstOrNull { type ->
    equals(type.id)
}.toResultOr { SectionDecodeError.UnknownSectionType(this) }
