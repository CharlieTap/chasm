package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.embedding.engine.Engine
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes._store

internal class EngineBinding {

    lateinit var engine: Engine
        private set

    lateinit var store: Store
        private set

    fun bind(engine: Engine) {
        this.engine = engine
        store = _store(engine)
    }
}
