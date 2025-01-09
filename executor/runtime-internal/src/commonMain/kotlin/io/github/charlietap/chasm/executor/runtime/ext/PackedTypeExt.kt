package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.ast.type.PackedType
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

fun PackedType.default(): ExecutionValue = NumberValue.I32(0)
