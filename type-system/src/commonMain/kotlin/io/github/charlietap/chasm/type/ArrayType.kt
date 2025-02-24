package io.github.charlietap.chasm.type

import kotlin.jvm.JvmInline

@JvmInline
value class ArrayType(val fieldType: FieldType) : Type
