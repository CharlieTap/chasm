package io.github.charlietap.chasm.ast.type

import kotlin.jvm.JvmInline

@JvmInline
value class ArrayType(val field: FieldType) : Type
