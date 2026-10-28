package io.github.charlietap.chasm.compiler.frontend.instruction

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.NumericInstruction
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilationContext

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
        emit(AdminInstruction.CopySlot(sourceSlots[0], destinationSlots[0]))
        return
    }
    val instruction = AdminInstruction.CopySlots(
        sourceSlots = sourceSlots,
        destinationSlots = destinationSlots,
    )
    emit(instruction)
}

internal fun FunctionCompilationContext.emitI32Constant(
    value: Int,
    destinationSlot: Int,
) {
    val instruction = NumericInstruction.I32ConstS(value, destinationSlot)
    emit(instruction)
}

internal fun FunctionCompilationContext.emitI64Constant(
    value: Long,
    destinationSlot: Int,
) {
    val instruction = NumericInstruction.I64ConstS(value, destinationSlot)
    emit(instruction)
}

internal fun FunctionCompilationContext.emitF32Constant(
    bits: Int,
    destinationSlot: Int,
) {
    val instruction = NumericInstruction.F32ConstS(bits, destinationSlot)
    emit(instruction)
}

internal fun FunctionCompilationContext.emitF64Constant(
    bits: Long,
    destinationSlot: Int,
) {
    val instruction = NumericInstruction.F64ConstS(bits, destinationSlot)
    emit(instruction)
}

internal fun FunctionCompilationContext.emitCheckpoint() {
    emit(AdminInstruction.Checkpoint)
    program.markCheckpoint()
}
