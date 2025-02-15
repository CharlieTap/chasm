package io.github.charlietap.chasm.ir.type

import kotlin.jvm.JvmInline

@JvmInline
value class StructType(val fields: List<FieldType>) : Type
