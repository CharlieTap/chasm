package io.github.charlietap.chasm.ast.value

import io.github.charlietap.chasm.InternalChasmApi
import kotlin.jvm.JvmInline

@InternalChasmApi
@JvmInline
value class NameValue(val name: String) : Value
