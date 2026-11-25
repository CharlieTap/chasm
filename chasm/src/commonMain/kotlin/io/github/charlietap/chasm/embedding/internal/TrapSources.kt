package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.embedding.diagnostic.TrapSource
import io.github.charlietap.chasm.embedding.shapes.DecodedModule
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

internal interface TrapSources {

    fun trapSource(
        module: DecodedModule,
        instance: ModuleInstance,
    ): TrapSource?
}
