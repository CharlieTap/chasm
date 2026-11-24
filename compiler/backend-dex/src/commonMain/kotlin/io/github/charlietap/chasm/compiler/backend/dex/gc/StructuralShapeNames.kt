package io.github.charlietap.chasm.compiler.backend.dex.gc

import io.github.charlietap.chasm.compiler.backend.analysis.gc.ShapeNames

internal class StructuralShapeNames : ShapeNames {

    override fun struct(
        key: String,
        index: Int,
    ): String {
        return "Lchasm/gc/S$key;"
    }

    override fun array(
        key: String,
        index: Int,
    ): String {
        return "Lchasm/gc/A$key;"
    }
}
