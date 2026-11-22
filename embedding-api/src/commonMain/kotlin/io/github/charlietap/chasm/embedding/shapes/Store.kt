package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.engine.Engine

class Store internal constructor(
    @PublishedApi internal val engine: Engine,
)

@InternalChasmApi
fun _store(engine: Engine): Store {
    return Store(engine)
}

@InternalChasmApi
inline fun Store._engine(): Engine {
    return engine
}
