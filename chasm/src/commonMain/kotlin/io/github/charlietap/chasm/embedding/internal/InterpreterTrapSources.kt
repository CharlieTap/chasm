package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.embedding.diagnostic.InterpreterTrapSource
import io.github.charlietap.chasm.embedding.diagnostic.TrapSource
import io.github.charlietap.chasm.embedding.shapes.DecodedModule
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

internal class InterpreterTrapSources : TrapSources {

    override fun trapSource(
        module: DecodedModule,
        instance: ModuleInstance,
    ): TrapSource? {
        return InterpreterTrapSource(instance, module)
    }
}
