package io.github.charlietap.chasm.decoder.decoder.section.code

import io.github.charlietap.chasm.ast.type.ValueType

internal data class LocalEntry(val count: UInt, val type: ValueType)
