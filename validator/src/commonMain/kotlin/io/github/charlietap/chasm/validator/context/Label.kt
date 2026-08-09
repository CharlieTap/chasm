package io.github.charlietap.chasm.validator.context

import io.github.charlietap.chasm.type.ResultType

internal enum class LabelKind {
    Function,
    Block,
    Loop,
    IfThen,
    IfElse,
    TryTable,
}

internal data class Label(
    var kind: LabelKind,
    val inputs: ResultType,
    val outputs: ResultType,
    val operandsDepth: Int,
    val localChangesDepth: Int,
    var unreachable: Boolean,
) {
    companion object {
        internal val DEFAULT = Label(
            kind = LabelKind.Function,
            inputs = ResultType(emptyList()),
            outputs = ResultType(emptyList()),
            operandsDepth = 0,
            localChangesDepth = 0,
            unreachable = false,
        )
    }
}
