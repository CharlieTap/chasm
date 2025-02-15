package io.github.charlietap.chasm.ir.type

data class Limits(var min: UInt, val max: UInt? = null) : Type
