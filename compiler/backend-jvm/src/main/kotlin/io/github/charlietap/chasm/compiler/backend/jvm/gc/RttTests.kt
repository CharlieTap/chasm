package io.github.charlietap.chasm.compiler.backend.jvm.gc

import io.github.charlietap.chasm.compiler.backend.abi.COMPILED_ARRAY
import io.github.charlietap.chasm.compiler.backend.abi.COMPILED_STRUCT
import io.github.charlietap.chasm.compiler.backend.analysis.gc.resolveDefinedType
import io.github.charlietap.chasm.compiler.backend.analysis.gc.subType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.SubType

internal data class RttTest(
    val baseClass: String,
    val depth: Int,
    val final: Boolean,
)

internal class RttTests(
    private val types: List<DefinedType>,
) {
    private val tests = types.map { type ->
        val subType = type.subType()
        val baseClass = if (subType.compositeType is CompositeType.Array) COMPILED_ARRAY else COMPILED_STRUCT
        RttTest(baseClass, depth(type), subType is SubType.Final)
    }

    fun test(index: Int): RttTest {
        return tests[index]
    }

    private fun depth(type: DefinedType): Int {

        var depth = 0
        var current = type
        while (true) {
            val supertype = current.subType().superTypes.singleOrNull() ?: return depth
            current = resolveDefinedType(supertype, types, current)
            depth++
        }
    }
}
