package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.engine.Engine
import io.github.charlietap.chasm.type.FunctionType

class PreparedFunction internal constructor(
    val type: FunctionType,
    @PublishedApi internal val engine: Engine,
    @PublishedApi internal val payload: Any,
) {

    val slots: Int = maxOf(type.params.types.size, type.results.types.size)
}

@InternalChasmApi
fun _preparedFunction(
    type: FunctionType,
    engine: Engine,
    payload: Any,
): PreparedFunction {
    return PreparedFunction(type, engine, payload)
}

@InternalChasmApi
inline fun PreparedFunction._engine(): Engine {
    return engine
}

@InternalChasmApi
inline fun PreparedFunction._payload(): Any {
    return payload
}
