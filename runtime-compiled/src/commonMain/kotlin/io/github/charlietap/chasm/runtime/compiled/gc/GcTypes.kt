package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
interface GcTypes {

    val layouts: ShapeLayouts

    fun type(value: Any): Int

    fun drop()
}
