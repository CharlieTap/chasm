package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.InternalChasmApi
import kotlin.jvm.JvmField

@InternalChasmApi
abstract class BindingEntry(
    @JvmField val binding: Array<Any?>,
    @JvmField val id: Int,
) : ObjectEntry()
