package io.github.charlietap.chasm.validator.context

internal interface GlobalContext {
    val expressionsMustBeConstant: Boolean
}

internal data class GlobalContextImpl(
    override val expressionsMustBeConstant: Boolean = false,
) : GlobalContext
