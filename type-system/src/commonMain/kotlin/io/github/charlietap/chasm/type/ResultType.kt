package io.github.charlietap.chasm.type

import kotlin.jvm.JvmInline

@JvmInline
value class ResultType(val types: List<ValueType>) : Type
