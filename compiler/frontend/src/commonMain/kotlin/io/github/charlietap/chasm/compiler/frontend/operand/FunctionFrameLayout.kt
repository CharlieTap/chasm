package io.github.charlietap.chasm.compiler.frontend.operand

import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.compiler.frontend.emptyIntArray
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType

internal class FunctionFrameLayout(
    functionType: FunctionType,
    private val definedLocals: List<Local>,
    private val definedLocalStorage: DefinedLocalStorage,
) {

    val parameterTypes: List<ValueType> = functionType.params.types
    val resultTypes: List<ValueType> = functionType.results.types
    val localCount: Int = parameterTypes.size + definedLocals.size
    val interfaceSlotCount: Int = maxOf(parameterTypes.size, resultTypes.size)
    val activationHeaderSlot: Int = interfaceSlotCount
    val definedLocalSlotBase: Int = activationHeaderSlot + 1
    val temporarySlotBase: Int = definedLocalSlotBase + definedLocalStorage.slotCount
    val returnSlots: IntArray = if (resultTypes.isEmpty()) emptyIntArray else IntArray(resultTypes.size) { it }

    fun localTypes(): List<io.github.charlietap.chasm.cir.ValueType> = definedLocalStorage.types()

    fun initialisedLocalCount(): Int = definedLocalStorage.initialisedCount()

    fun localType(index: Int): ValueType = if (index < parameterTypes.size) {
        parameterTypes[index]
    } else {
        definedLocals[index - parameterTypes.size].type
    }

    fun localSlot(index: Int): Int = if (index < parameterTypes.size) {
        index
    } else {
        definedLocalSlotBase + definedLocalStorage.slot(index - parameterTypes.size)
    }
}
