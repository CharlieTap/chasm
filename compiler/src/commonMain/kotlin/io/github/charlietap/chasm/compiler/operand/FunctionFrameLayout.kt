package io.github.charlietap.chasm.compiler.operand

import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.compiler.emptyIntArray
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
    val localInitialValues: LongArray = definedLocalStorage.initialValues
    val returnSlots: IntArray = if (resultTypes.isEmpty()) emptyIntArray else IntArray(resultTypes.size) { it }

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

    fun hasLocalSlot(index: Int): Boolean =
        index < parameterTypes.size || definedLocalStorage.hasSlot(index - parameterTypes.size)
}
