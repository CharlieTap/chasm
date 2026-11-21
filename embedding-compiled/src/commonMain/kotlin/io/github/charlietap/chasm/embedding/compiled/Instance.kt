package io.github.charlietap.chasm.embedding.compiled

import io.github.charlietap.chasm.InternalChasmApi

class Instance
    @InternalChasmApi
    constructor(
        @property:InternalChasmApi val engine: CompiledEngine,
        @property:InternalChasmApi val payload: Any,
    )
