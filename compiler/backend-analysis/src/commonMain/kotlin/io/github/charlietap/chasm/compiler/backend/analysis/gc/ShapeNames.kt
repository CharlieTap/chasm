package io.github.charlietap.chasm.compiler.backend.analysis.gc

interface ShapeNames {

    fun struct(
        key: String,
        index: Int,
    ): String

    fun array(
        key: String,
        index: Int,
    ): String
}
