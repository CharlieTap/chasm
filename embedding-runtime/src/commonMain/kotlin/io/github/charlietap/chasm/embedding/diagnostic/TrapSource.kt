package io.github.charlietap.chasm.embedding.diagnostic

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.error.MemoryAccess
import io.github.charlietap.chasm.runtime.diagnostic.TrapSnapshot
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

@InternalChasmApi
interface TrapSource {

    val instance: ModuleInstance

    fun frames(
        runtime: RuntimeStore,
        snapshot: TrapSnapshot,
    ): ModuleFrames?
}

@InternalChasmApi
interface ModuleFrames {

    val moduleName: String?

    fun functionName(
        functionIndex: Int,
    ): String?

    fun site(
        address: Int,
        functionIndex: Int,
        frameIndex: Int,
    ): TrapSite? = null
}

@InternalChasmApi
interface TrapSite {

    val offset: Int

    fun memoryAccess(): MemoryAccess? = null
}
