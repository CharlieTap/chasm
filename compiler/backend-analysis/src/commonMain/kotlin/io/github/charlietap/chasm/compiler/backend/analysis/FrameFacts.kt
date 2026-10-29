package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.compiler.backend.analysis.ext.localInitialValue
import io.github.charlietap.chasm.compiler.backend.analysis.ext.toType
import io.github.charlietap.chasm.compiler.backend.analysis.form.callSite
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType

class FrameFacts(
    val type: FunctionType,
    val frameSlots: Int,
    val localTypes: Array<ValueType?>,
    val firstTemporary: Int,
    val initialValues: LongArray,
)

fun frameFacts(
    function: CirFunction,
    declarations: ModuleIndexSpaces,
): FrameFacts {
    val type = declarations.functionType(function.type)
    val frame = function.frame
    val frameSlots = maxOf(frame.frameSlots, resultSlotsNeeded(function, declarations))
    val firstLocal = frame.activationHeaderSlot + 1
    val localTypes = arrayOfNulls<ValueType>(frameSlots)
    frame.locals.forEachIndexed { index, local -> localTypes[firstLocal + index] = local.toType() }
    val initialValues = LongArray(frameSlots)
    frame.locals.indices.forEach { index -> initialValues[firstLocal + index] = frame.localInitialValue(index) }
    return FrameFacts(type, frameSlots, localTypes, firstLocal + frame.locals.size, initialValues)
}

private fun resultSlotsNeeded(
    function: CirFunction,
    declarations: ModuleIndexSpaces,
): Int = function.instructions.maxOfOrNull { instruction ->
    val call = (instruction as? ControlInstruction)?.callSite(declarations) ?: return@maxOfOrNull 0
    call.resultBase + call.type.results.types.size
} ?: 0
