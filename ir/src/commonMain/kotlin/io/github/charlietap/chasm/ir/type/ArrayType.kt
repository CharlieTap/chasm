package io.github.charlietap.chasm.ir.type

import kotlin.jvm.JvmInline

@JvmInline
value class ArrayType(val fieldType: FieldType) : Type
