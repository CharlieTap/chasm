@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.aot.internal

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.error.Trap
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.internal.InstantiatedModule
import io.github.charlietap.chasm.embedding.internal.StoreRuntime
import io.github.charlietap.chasm.embedding.managed.ManagedInstances
import io.github.charlietap.chasm.embedding.managed.ManagedInstantiator
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedStore
import io.github.charlietap.chasm.runtime.compiled.gc.RttRegistry
import io.github.charlietap.chasm.runtime.error.InvocationError

internal class AotInstantiator(
    private val first: AotModule,
    private val store: StoreRuntime,
    private val managedStore: ManagedStore,
    private val registry: RttRegistry,
    private val instances: ManagedInstances,
) : ManagedInstantiator {

    override suspend fun instantiate(
        module: Module,
        imports: List<Import>,
    ): ChasmResult<InstantiatedModule, InstantiationError> {

        val payload = module._payload() as? AotModule
            ?: return ChasmResult.Error(ExecutionError.Unsupported(UNSUPPORTED_MODULE))

        val difference = SettingsDifference(payload.config, first.config).describe()
        if (difference != null) {
            return ChasmResult.Error(ExecutionError.Unlinkable("the module was compiled with $difference"))
        }

        return try {
            store.instantiate(managedStore.engine, imports, AotModuleInstallation(payload, managedStore, registry, instances))
        } catch (_: OutOfMemoryError) {
            val message = InvocationError.GuestHeapOutOfMemory.toString()
            ChasmResult.Error(ExecutionError.Trapped(Trap(TrapReason.GUEST_HEAP_EXHAUSTED, message, null, null)))
        }
    }

    private companion object {
        const val UNSUPPORTED_MODULE = "an AOT store only instantiates modules loaded by module(CompiledModule)"
    }
}
