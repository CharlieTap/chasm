package io.github.charlietap.chasm.validator.context

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.type.ResultType

data class Label(
    val instruction: ControlInstruction?,
    val inputs: ResultType,
    val outputs: ResultType,
    val operandsDepth: Int,
    var unreachable: Boolean,
) {
    companion object {
        internal val DEFAULT = Label(
            instruction = null,
            inputs = ResultType(emptyList()),
            outputs = ResultType(emptyList()),
            operandsDepth = 0,
            unreachable = false,
        )
    }
}
