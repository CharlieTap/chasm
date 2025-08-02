package io.github.charlietap.chasm.validator.context

internal interface TypeContext {
    val limitsMaximum: ULong
}

internal data class TypeContextImpl(
    override val limitsMaximum: ULong = ULong.MAX_VALUE,
) : TypeContext
