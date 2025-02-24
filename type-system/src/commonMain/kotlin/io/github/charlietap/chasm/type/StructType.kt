package io.github.charlietap.chasm.type

import kotlin.jvm.JvmInline

@JvmInline
value class StructType(val fields: List<FieldType>) : Type
