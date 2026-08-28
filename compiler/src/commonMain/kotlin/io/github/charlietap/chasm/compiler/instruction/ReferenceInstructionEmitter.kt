package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.reference.ReferenceInstructionDispatcher
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest

internal fun FunctionCompilationContext.emitReferenceInstruction(
    instruction: ReferenceInstruction,
) {
    emit(instruction, ::ReferenceInstructionDispatcher)
}

internal fun FunctionCompilationContext.emitRefNull(reference: Long, destinationSlot: Int) =
    emitReferenceInstruction(ReferenceInstruction.RefNullS(reference, destinationSlot))

internal fun FunctionCompilationContext.emitRefFunc(reference: Long, destinationSlot: Int) =
    emitReferenceInstruction(ReferenceInstruction.RefFuncS(reference, destinationSlot))

internal fun FunctionCompilationContext.emitRefIsNull(sourceSlot: Int, destinationSlot: Int) =
    emitReferenceInstruction(ReferenceInstruction.RefIsNullS(sourceSlot, destinationSlot))

internal fun FunctionCompilationContext.emitRefAsNonNull(sourceSlot: Int, destinationSlot: Int) =
    emitReferenceInstruction(ReferenceInstruction.RefAsNonNullS(sourceSlot, destinationSlot))

internal fun FunctionCompilationContext.emitRefEq(firstSlot: Int, secondSlot: Int, destinationSlot: Int) =
    emitReferenceInstruction(ReferenceInstruction.RefEqSs(firstSlot, secondSlot, destinationSlot))

internal fun FunctionCompilationContext.emitRefTest(
    sourceSlot: Int,
    destinationSlot: Int,
    typeTest: ReferenceTypeTest,
) = emitReferenceInstruction(ReferenceInstruction.RefTestS(sourceSlot, destinationSlot, typeTest))

internal fun FunctionCompilationContext.emitRefCast(
    sourceSlot: Int,
    destinationSlot: Int,
    typeTest: ReferenceTypeTest,
) = emitReferenceInstruction(ReferenceInstruction.RefCastS(sourceSlot, destinationSlot, typeTest))
