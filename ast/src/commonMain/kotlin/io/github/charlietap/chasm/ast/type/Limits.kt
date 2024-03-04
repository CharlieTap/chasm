package io.github.charlietap.chasm.ast.type

data class Limits(val min: UInt, val max: UInt? = null) : Type
