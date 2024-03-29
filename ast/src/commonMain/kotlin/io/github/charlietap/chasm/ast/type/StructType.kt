package io.github.charlietap.chasm.ast.type

import kotlin.jvm.JvmInline

@JvmInline
value class StructType(val fields: List<FieldType>) : Type
