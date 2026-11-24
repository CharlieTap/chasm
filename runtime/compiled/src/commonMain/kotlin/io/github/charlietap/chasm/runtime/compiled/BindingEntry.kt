package io.github.charlietap.chasm.runtime.compiled

import kotlin.jvm.JvmField

abstract class BindingEntry(
    @JvmField val binding: Array<Any?>,
    @JvmField val id: Int,
) : ObjectEntry()
