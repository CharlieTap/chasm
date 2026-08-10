package io.github.charlietap.chasm.compiler.operand

import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.compiler.emptyIntArray
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType

internal class FunctionFrameLayout(
    functionType: FunctionType,
    private val definedLocals: List<Local>,
) {

    val parameterTypes: List<ValueType> = functionType.params.types
    val resultTypes: List<ValueType> = functionType.results.types
    val localCount: Int = parameterTypes.size + definedLocals.size
    val interfaceSlots: Int = maxOf(parameterTypes.size, resultTypes.size)
    val temporarySlotBase: Int = interfaceSlots + definedLocals.size
    val returnSlots: IntArray = if (resultTypes.isEmpty()) emptyIntArray else IntArray(resultTypes.size) { it }

    fun localType(index: Int): ValueType = if (index < parameterTypes.size) {
        parameterTypes[index]
    } else {
        definedLocals[index - parameterTypes.size].type
    }

    fun localSlot(index: Int): Int = if (index < parameterTypes.size) {
        index
    } else {
        interfaceSlots + index - parameterTypes.size
    }
}
