package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.runtime.store.Store

sealed class Importable protected constructor(internal val store: Store)
