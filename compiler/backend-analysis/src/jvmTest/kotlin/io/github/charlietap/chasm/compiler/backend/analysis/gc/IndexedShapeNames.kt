package io.github.charlietap.chasm.compiler.backend.analysis.gc

internal class IndexedShapeNames : ShapeNames {

    override fun struct(
        key: String,
        index: Int,
    ): String {
        return "Lshape/Struct$index;"
    }

    override fun array(
        key: String,
        index: Int,
    ): String {
        return "Lshape/Array$index;"
    }
}
