@file:OptIn(ExperimentalChasmApi::class, InternalChasmApi::class)

package io.github.charlietap.chasm.aot.diagnostic

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.CompiledModule
import io.github.charlietap.chasm.aot.internal.FunctionTrapSites
import io.github.charlietap.chasm.declarations.ModuleNames
import io.github.charlietap.chasm.embedding.diagnostic.ModuleFrames
import io.github.charlietap.chasm.embedding.diagnostic.TrapSite
import io.github.charlietap.chasm.embedding.diagnostic.TrapSource
import io.github.charlietap.chasm.embedding.error.MemoryAccess
import io.github.charlietap.chasm.runtime.diagnostic.FailedMemoryAccess
import io.github.charlietap.chasm.runtime.diagnostic.TrapSnapshot
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store

internal class CompiledTrapSource(
    override val instance: ModuleInstance,
    private val names: ModuleNames,
    private val module: CompiledModule,
) : TrapSource {

    override fun frames(
        runtime: Store,
        snapshot: TrapSnapshot,
    ): ModuleFrames = CompiledModuleFrames(names, module, snapshot)
}

private class CompiledModuleFrames(
    private val names: ModuleNames,
    private val module: CompiledModule,
    private val snapshot: TrapSnapshot,
) : ModuleFrames {

    override val moduleName: String? get() = names.moduleName

    override fun functionName(
        functionIndex: Int,
    ): String? = names.trapFunctionName(functionIndex)

    override fun site(
        address: Int,
        functionIndex: Int,
        frameIndex: Int,
    ): TrapSite? {
        if (snapshot.entryIps[frameIndex] != TrapSnapshot.COMPILED_FRAME) return null
        val sites = module.trapSites?.function(functionIndex) ?: return null
        val position = snapshot.ips[frameIndex]
        val offset = sites.offset(position)
        if (offset < 0) return null
        return CompiledSite(offset, if (frameIndex == 0) memoryAccess(sites, position) else null)
    }

    private fun memoryAccess(
        sites: FunctionTrapSites,
        position: Int,
    ): MemoryAccess? {
        val access = snapshot.memoryAccess ?: return null
        val memoryIndex = sites.memoryIndex(position, access.source).takeIf { index -> index >= 0 } ?: access.memoryIndex
        if (memoryIndex < 0 || access.address + access.width <= access.memorySize) return null
        return MemoryAccess(kindOf(access.kind), memoryIndex, access.address, access.width, access.memorySize)
    }
}

private fun kindOf(kind: FailedMemoryAccess.Kind): MemoryAccess.Kind = when (kind) {
    FailedMemoryAccess.Kind.READ -> MemoryAccess.Kind.READ
    FailedMemoryAccess.Kind.WRITE -> MemoryAccess.Kind.WRITE
    FailedMemoryAccess.Kind.COPY -> MemoryAccess.Kind.COPY
    FailedMemoryAccess.Kind.FILL -> MemoryAccess.Kind.FILL
    FailedMemoryAccess.Kind.INIT -> MemoryAccess.Kind.INIT
}

private class CompiledSite(
    override val offset: Int,
    private val access: MemoryAccess?,
) : TrapSite {
    override fun memoryAccess(): MemoryAccess? = access
}
