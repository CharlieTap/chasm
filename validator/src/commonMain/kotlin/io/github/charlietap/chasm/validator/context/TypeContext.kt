package io.github.charlietap.chasm.validator.context

internal interface TypeContext {
    val limitsMaximum: UInt
}

internal data class TypeContextImpl(
    override val limitsMaximum: UInt = UInt.MAX_VALUE,
) : TypeContext
