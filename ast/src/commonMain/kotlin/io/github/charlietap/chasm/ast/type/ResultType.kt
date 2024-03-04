package io.github.charlietap.chasm.ast.type

import kotlin.jvm.JvmInline

@JvmInline
value class ResultType(val types: List<ValueType>) : Type
