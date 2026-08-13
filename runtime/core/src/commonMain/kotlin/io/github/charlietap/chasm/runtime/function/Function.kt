package io.github.charlietap.chasm.runtime.function

import io.github.charlietap.chasm.ast.module.Index.FunctionIndex
import io.github.charlietap.chasm.ast.module.Index.TypeIndex

data class Function(
    val idx: FunctionIndex,
    val typeIndex: TypeIndex,
    val locals: LongArray,
    var body: Expression,
    val frameSlots: Int,
    val returnSlots: IntArray,
    var collectGarbageAfterInvocation: Boolean = false,
) {
    companion object {
        val TEMP = Function(
            idx = FunctionIndex(0u),
            typeIndex = TypeIndex(0u),
            locals = longArrayOf(),
            body = Expression.EMPTY,
            frameSlots = 0,
            returnSlots = intArrayOf(),
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Function

        if (idx != other.idx) return false
        if (typeIndex != other.typeIndex) return false
        if (!(locals contentEquals other.locals)) return false
        if (body != other.body) return false
        if (frameSlots != other.frameSlots) return false
        if (!returnSlots.contentEquals(other.returnSlots)) return false
        if (collectGarbageAfterInvocation != other.collectGarbageAfterInvocation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = idx.hashCode()
        result = 31 * result + typeIndex.hashCode()
        result = 31 * result + locals.contentHashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + frameSlots
        result = 31 * result + returnSlots.contentHashCode()
        result = 31 * result + collectGarbageAfterInvocation.hashCode()
        return result
    }
}
