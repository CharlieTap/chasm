package io.github.charlietap.chasm.tools.compilerbaseline

import io.github.charlietap.chasm.executor.invoker.dispatch.admin.CopySlotSequenceDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.CopySlotsDispatcher
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

internal class DispatchSupplements {

    private val sequentialCopies = CopySlotSequenceDispatcher(intArrayOf(0, 1), intArrayOf(2, 3)).javaClass
    private val simultaneousCopies = CopySlotsDispatcher(intArrayOf(0, 1), intArrayOf(2, 3)).javaClass

    fun immediates(
        instruction: LinkedInstruction,
        dispatchable: DispatchableInstruction,
    ): List<Pair<String, JsonElement>> = buildList {
        if (instruction is AdminInstruction.CopySlots && instruction.sourceSlots.size > 1) {
            add("copies" to JsonPrimitive(copies(dispatchable)))
        }
        resultDestinationSlot(dispatchable)?.let { slot -> add("result_destination_slot" to JsonPrimitive(slot)) }
        if (instruction is ControlInstruction.HostCall) {
            add("result_slot_base" to JsonPrimitive(capturedInt(dispatchable, "resultSlotBase")))
        }
    }

    private fun copies(dispatchable: DispatchableInstruction): String = when (dispatchable.javaClass) {
        sequentialCopies -> "sequential"
        simultaneousCopies -> "simultaneous"
        else -> error("unrecognised copy dispatcher ${dispatchable.javaClass.name}")
    }

    private fun resultDestinationSlot(dispatchable: DispatchableInstruction): Int? = dispatchable.javaClass.methods
        .firstOrNull { method -> method.name == "getResultDestinationSlot" && method.parameterCount == 0 }
        ?.let { method ->
            method.isAccessible = true
            method.invoke(dispatchable) as Int
        }

    private fun capturedInt(dispatchable: DispatchableInstruction, name: String): Int {
        var type: Class<*>? = dispatchable.javaClass
        while (type != null) {
            type.declaredFields.firstOrNull { field -> field.name == "$$name" || field.name == "$$name\$inlined" }?.let { field ->
                field.isAccessible = true
                return field.getInt(dispatchable)
            }
            type = type.superclass
        }
        error("${dispatchable.javaClass.name} does not capture $name")
    }
}
