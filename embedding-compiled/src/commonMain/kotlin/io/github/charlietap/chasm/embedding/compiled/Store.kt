package io.github.charlietap.chasm.embedding.compiled

import io.github.charlietap.chasm.InternalChasmApi

class Store
    @InternalChasmApi
    constructor(
        @property:InternalChasmApi val engine: CompiledEngine,
    )
