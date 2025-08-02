package io.github.charlietap.chasm.type

data class Limits(var min: ULong, val max: ULong? = null) : Type
