package io.github.charlietap.chasm.runtime.compiled.gc

interface GcTypes {

    val layouts: ShapeLayouts

    fun type(value: Any): Int

    fun drop()
}
