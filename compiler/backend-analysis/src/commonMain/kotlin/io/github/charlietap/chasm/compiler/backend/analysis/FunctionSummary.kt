package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.cir.ReferenceInstruction

class FunctionSummary internal constructor(
    internal val requestsTailCall: Boolean,
    internal val usesExceptions: Boolean,
    internal val mayGrowMemory: Boolean,
    internal val mayCollect: Boolean,
    internal val mayUnwind: Boolean,
    internal val catchesExceptionReference: Boolean,
    internal val directCallees: IntArray,
    internal val indirectCallTypes: IntArray,
    internal val bridgedFunctions: IntArray,
    internal val referencedFunctions: IntArray,
)

fun functionSummary(
    function: CirFunction,
    declarations: ModuleIndexSpaces,
): FunctionSummary {

    var requestsTailCall = false
    var raisesException = false
    var mayGrowMemory = false
    var mayCollect = false
    var mayUnwind = false
    val callees = IntCollector()
    val indirectTypes = IntCollector()
    val bridged = IntCollector()
    val referenced = IntCollector()

    for (instruction in function.instructions) {

        if (instruction.isTailCall()) requestsTailCall = true
        if (instruction.raisesException()) {
            raisesException = true
            mayUnwind = true
        }

        when (instruction) {
            is ControlInstruction -> {
                val callee = instruction.directCallee()
                when {
                    callee != null -> callees.add(callee)
                    instruction.callsUnknownFunction() -> {
                        mayGrowMemory = true
                        mayCollect = true
                        mayUnwind = true
                    }
                    instruction.allocatesGuestObject() -> mayCollect = true
                }
                when (instruction) {
                    is ControlInstruction.Call -> if (declarations.isImported(instruction.function)) bridged.add(instruction.function.index)
                    is ControlInstruction.ReturnCall -> bridged.add(instruction.function.index)
                    else -> Unit
                }
                instruction.indirectCallTypeIndex()?.let { type -> indirectTypes.add(type.index) }
            }
            is MemoryInstruction -> if (instruction.growsMemory()) mayGrowMemory = true
            is AggregateInstruction -> if (instruction.allocatesGuestObject()) mayCollect = true
            is ReferenceInstruction.RefFuncS -> referenced.add(instruction.function.index)
            else -> Unit
        }
    }

    return FunctionSummary(
        requestsTailCall = requestsTailCall,
        usesExceptions = function.exceptions != null || raisesException,
        mayGrowMemory = mayGrowMemory,
        mayCollect = mayCollect,
        mayUnwind = mayUnwind,
        catchesExceptionReference = function.exceptions?.regions.orEmpty().any { region -> region.catches.any { handler -> handler.exceptionReference } },
        directCallees = callees.toArray(),
        indirectCallTypes = indirectTypes.toArray(),
        bridgedFunctions = bridged.toArray(),
        referencedFunctions = referenced.toArray(),
    )
}

private fun ControlInstruction.directCallee(): Int? {
    return when (this) {
        is ControlInstruction.Call -> function.index
        is ControlInstruction.ReturnCall -> function.index
        else -> null
    }
}

private fun ControlInstruction.callsUnknownFunction(): Boolean {
    return this is ControlInstruction.CallIndirectI ||
        this is ControlInstruction.CallIndirectS || this is ControlInstruction.CallRefS ||
        this is ControlInstruction.ReturnCallIndirectI || this is ControlInstruction.ReturnCallIndirectS ||
        this is ControlInstruction.ReturnCallRefS
}

private fun MemoryInstruction.growsMemory(): Boolean {
    return this is MemoryInstruction.MemoryGrowI || this is MemoryInstruction.MemoryGrowS
}

private class IntCollector {
    private var values = EMPTY
    private var size = 0

    fun add(value: Int) {
        if (size == values.size) values = values.copyOf(maxOf(INITIAL_CAPACITY, size * 2))
        values[size++] = value
    }

    fun toArray(): IntArray {
        return if (size == 0) EMPTY else values.copyOf(size)
    }

    private companion object {
        val EMPTY = IntArray(0)
        const val INITIAL_CAPACITY = 4
    }
}
