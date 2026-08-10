package io.github.charlietap.chasm.runtime.function

import io.github.charlietap.chasm.ast.module.Index.FunctionIndex
import io.github.charlietap.chasm.ast.module.Index.TypeIndex
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionTest {

    @Test
    fun equalFunctionsHaveEqualHashCodes() {
        val first = function()
        val second = function()

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
    }
}

private fun function() = Function(
    idx = FunctionIndex(1u),
    typeIndex = TypeIndex(2u),
    locals = longArrayOf(3),
    body = Expression(4),
    frameSlots = 5,
    returnSlots = intArrayOf(6),
)
