package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.embedding.engine.Engine
import io.github.charlietap.chasm.reference.FunctionReference

internal class InterpreterFunctionReference(
    private val binding: EngineBinding,
    val address: Int,
) : FunctionReference() {

    override fun engine(): Engine {
        return binding.engine
    }

    override fun equals(other: Any?): Boolean {
        return other is InterpreterFunctionReference && other.binding === binding && other.address == address
    }

    override fun hashCode(): Int {
        return address
    }
}
