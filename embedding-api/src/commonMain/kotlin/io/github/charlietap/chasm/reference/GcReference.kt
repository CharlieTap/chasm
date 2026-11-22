package io.github.charlietap.chasm.reference

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.engine.Engine
import kotlin.jvm.JvmField

sealed interface GcReference

sealed interface EqReference : GcReference

abstract class StructReference
    @InternalChasmApi
    constructor() : EqReference {

        @InternalChasmApi
        abstract fun engine(): Engine
    }

abstract class ArrayReference
    @InternalChasmApi
    constructor() : EqReference {

        @InternalChasmApi
        abstract fun engine(): Engine
    }

class I31Reference(value: Int) : EqReference {

    @JvmField
    val value: Int = value and I31_MASK

    override fun equals(other: Any?): Boolean {
        return other is I31Reference && other.value == value
    }

    override fun hashCode(): Int {
        return value
    }

    override fun toString(): String {
        return "I31Reference($value)"
    }

    private companion object {
        const val I31_MASK = 0x7FFFFFFF
    }
}

abstract class FunctionReference
    @InternalChasmApi
    constructor() : GcReference {

        @InternalChasmApi
        abstract fun engine(): Engine
    }

abstract class ExceptionReference
    @InternalChasmApi
    constructor() : GcReference {

        @InternalChasmApi
        abstract fun engine(): Engine
    }
