package io.github.charlietap.chasm.ast.type

data class Limits(var min: UInt, val max: UInt? = null) : Type
