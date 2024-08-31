package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.embedding.shapes.HostFunction
import io.github.charlietap.chasm.embedding.shapes.HostFunctionFactory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Value

fun hostFunction(
    hostFunction: Store.(List<Value>) -> List<Value>,
): HostFunctionFactory {
    return { store: Store ->
        HostFunction { params ->
            hostFunction(store, params)
        }
    }
}
