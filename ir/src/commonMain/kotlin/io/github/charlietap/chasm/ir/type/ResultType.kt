package io.github.charlietap.chasm.ir.type

import kotlin.jvm.JvmInline

@JvmInline
value class ResultType(val types: List<ValueType>) : Type
