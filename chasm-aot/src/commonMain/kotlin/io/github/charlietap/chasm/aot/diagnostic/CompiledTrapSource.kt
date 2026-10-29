package io.github.charlietap.chasm.aot.diagnostic

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.declarations.ModuleNames
import io.github.charlietap.chasm.embedding.diagnostic.ModuleFrames
import io.github.charlietap.chasm.embedding.diagnostic.TrapSource
import io.github.charlietap.chasm.runtime.diagnostic.TrapSnapshot
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store

@OptIn(InternalChasmApi::class)
internal class CompiledTrapSource(
    override val instance: ModuleInstance,
    private val names: ModuleNames,
) : TrapSource, ModuleFrames {

    override val moduleName: String? get() = names.moduleName

    override fun frames(
        runtime: Store,
        snapshot: TrapSnapshot,
    ): ModuleFrames = this

    override fun functionName(
        functionIndex: Int,
    ): String? = names.trapFunctionName(functionIndex)
}
