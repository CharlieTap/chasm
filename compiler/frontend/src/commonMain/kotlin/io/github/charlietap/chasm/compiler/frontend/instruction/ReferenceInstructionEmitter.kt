package io.github.charlietap.chasm.compiler.frontend.instruction

import io.github.charlietap.chasm.cir.FunctionIndex
import io.github.charlietap.chasm.cir.HeapType
import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilationContext
import io.github.charlietap.chasm.cir.ReferenceType as CirReferenceType

internal fun FunctionCompilationContext.emitRefNull(
    heapType: HeapType,
    destinationSlot: Int,
) =
    emit(ReferenceInstruction.RefNullS(heapType, destinationSlot))

internal fun FunctionCompilationContext.emitRefFunc(
    function: FunctionIndex,
    destinationSlot: Int,
) =
    emit(ReferenceInstruction.RefFuncS(function, destinationSlot))

internal fun FunctionCompilationContext.emitRefIsNull(
    sourceSlot: Int,
    destinationSlot: Int,
) =
    emit(ReferenceInstruction.RefIsNullS(sourceSlot, destinationSlot))

internal fun FunctionCompilationContext.emitRefAsNonNull(
    sourceSlot: Int,
    destinationSlot: Int,
) =
    emit(ReferenceInstruction.RefAsNonNullS(sourceSlot, destinationSlot))

internal fun FunctionCompilationContext.emitRefEq(
    firstSlot: Int,
    secondSlot: Int,
    destinationSlot: Int,
) =
    emit(ReferenceInstruction.RefEqSs(firstSlot, secondSlot, destinationSlot))

internal fun FunctionCompilationContext.emitRefTest(
    sourceSlot: Int,
    destinationSlot: Int,
    typeTest: CirReferenceType,
) = emit(ReferenceInstruction.RefTestS(sourceSlot, destinationSlot, typeTest))

internal fun FunctionCompilationContext.emitRefCast(
    sourceSlot: Int,
    destinationSlot: Int,
    typeTest: CirReferenceType,
) = emit(ReferenceInstruction.RefCastS(sourceSlot, destinationSlot, typeTest))
