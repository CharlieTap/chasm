package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.engine.Engine

class Instance internal constructor(
    @PublishedApi internal val engine: Engine,
    @PublishedApi internal val payload: Any,
)

@InternalChasmApi
fun _instance(
    engine: Engine,
    payload: Any,
): Instance {
    return Instance(engine, payload)
}

@InternalChasmApi
inline fun Instance._engine(): Engine {
    return engine
}

@InternalChasmApi
inline fun Instance._payload(): Any {
    return payload
}
