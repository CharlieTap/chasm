package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.compiler.backend.analysis.ext.localInitialValue
import io.github.charlietap.chasm.compiler.backend.analysis.ext.toType
import io.github.charlietap.chasm.compiler.backend.analysis.form.InstructionForms
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
    forms: InstructionForms,
): FrameFacts {
    val function = forms.function
    val type = forms.declarations.functionType(function.type)
    val frame = function.frame
    val frameSlots = maxOf(frame.frameSlots, resultSlotsNeeded(forms))
    val firstLocal = frame.activationHeaderSlot + 1
    val localTypes = arrayOfNulls<ValueType>(frameSlots)
    frame.locals.forEachIndexed { index, local -> localTypes[firstLocal + index] = local.toType() }
    val initialValues = LongArray(frameSlots)
    frame.locals.indices.forEach { index -> initialValues[firstLocal + index] = frame.localInitialValue(index) }
    return FrameFacts(type, frameSlots, localTypes, firstLocal + frame.locals.size, initialValues)
}

private fun resultSlotsNeeded(
    forms: InstructionForms,
): Int {
    var needed = 0
    for (position in 0 until forms.size) {
        val call = forms.callSite(position) ?: continue
        needed = maxOf(needed, call.resultBase + call.type.results.types.size)
    }
    return needed
}
