package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.CopySlotDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.CopySlotsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32ConstDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64ConstDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ConstDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ConstDispatcher
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal fun FunctionCompilationContext.emitCopy(
    sourceSlot: Int,
    destinationSlot: Int,
) {
    if (sourceSlot == destinationSlot) return
    appendCopy(sourceSlot, destinationSlot)
}

internal fun FunctionCompilationContext.emitCopies(
    sourceSlots: IntArray,
    destinationSlots: IntArray,
) {
    check(sourceSlots.size == destinationSlots.size)
    var changed = false
    for (index in sourceSlots.indices) {
        if (sourceSlots[index] != destinationSlots[index]) {
            changed = true
            break
        }
    }
    if (!changed) return
    if (sourceSlots.size == 1) {
        emit(CopySlotDispatcher(sourceSlots[0], destinationSlots[0])) {
            AdminInstruction.CopySlot(sourceSlots[0], destinationSlots[0])
        }
        return
    }
    val instruction = AdminInstruction.CopySlots(
        sourceSlots = sourceSlots,
        destinationSlots = destinationSlots,
    )
    emit(instruction, ::CopySlotsDispatcher)
}

internal fun FunctionCompilationContext.emitI32Constant(value: Int, destinationSlot: Int) {
    val instruction = NumericInstruction.I32ConstS(value, destinationSlot)
    emit(instruction, ::I32ConstDispatcher)
}

internal fun FunctionCompilationContext.emitI64Constant(value: Long, destinationSlot: Int) {
    val instruction = NumericInstruction.I64ConstS(value, destinationSlot)
    emit(instruction, ::I64ConstDispatcher)
}

internal fun FunctionCompilationContext.emitF32Constant(bits: Int, destinationSlot: Int) {
    val instruction = NumericInstruction.F32ConstS(bits, destinationSlot)
    emit(instruction, ::F32ConstDispatcher)
}

internal fun FunctionCompilationContext.emitF64Constant(bits: Long, destinationSlot: Int) {
    val instruction = NumericInstruction.F64ConstS(bits, destinationSlot)
    emit(instruction, ::F64ConstDispatcher)
}
