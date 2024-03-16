package io.github.charlietap.chasm.decoder.section.code

import io.github.charlietap.chasm.ast.type.ValueType

data class LocalEntry(val count: UInt, val type: ValueType)
