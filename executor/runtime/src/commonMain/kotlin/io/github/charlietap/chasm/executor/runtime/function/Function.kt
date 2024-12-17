package io.github.charlietap.chasm.executor.runtime.function

import io.github.charlietap.chasm.ast.module.Index.FunctionIndex
import io.github.charlietap.chasm.ast.module.Index.TypeIndex
import io.github.charlietap.chasm.ast.module.Local

data class Function(
    val idx: FunctionIndex,
    val typeIndex: TypeIndex,
    val locals: List<Local>,
    val body: Expression,
) {
    companion object {
        val TEMP = Function(
            idx = FunctionIndex(0u),
            typeIndex = TypeIndex(0u),
            locals = emptyList(),
            body = Expression.EMPTY,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Function

        if (idx != other.idx) return false
        if (typeIndex != other.typeIndex) return false
        if (locals != other.locals) return false
        if (!(body.instructions contentEquals other.body.instructions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = idx.hashCode()
        result = 31 * result + typeIndex.hashCode()
        result = 31 * result + locals.hashCode()
        result = 31 * result + body.hashCode()
        return result
    }
}
