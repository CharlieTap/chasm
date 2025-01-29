package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.executor.runtime.encoder.ReferenceValueEncoder
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

fun ReferenceValue.toLong(): Long = ReferenceValueEncoder(this)
