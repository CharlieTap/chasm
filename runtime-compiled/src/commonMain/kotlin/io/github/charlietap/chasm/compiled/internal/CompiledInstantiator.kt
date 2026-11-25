package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.embedding.compiled.internal.ManagedInstances
import io.github.charlietap.chasm.embedding.compiled.internal.ManagedInstantiator
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.error.Trap
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.internal.InstantiatedModule
import io.github.charlietap.chasm.embedding.internal.StoreRuntime
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedStore
import io.github.charlietap.chasm.runtime.compiled.gc.RttRegistry
import io.github.charlietap.chasm.runtime.error.InvocationError

internal class CompiledInstantiator(
    private val first: CompiledModulePayload,
    private val store: StoreRuntime,
    private val managedStore: ManagedStore,
    private val registry: RttRegistry,
    private val instances: ManagedInstances,
    private val names: StoreModuleNames,
) : ManagedInstantiator {

    override suspend fun instantiate(
        module: Module,
        imports: List<Import>,
    ): ChasmResult<InstantiatedModule, InstantiationError> {

        val payload = module._payload() as? CompiledModulePayload
            ?: return ChasmResult.Error(ExecutionError.Unsupported(UNSUPPORTED_MODULE))

        val difference = SettingsDifference(payload.config, first.config).describe()
        if (difference != null) {
            return ChasmResult.Error(ExecutionError.Unlinkable("the module was compiled with $difference"))
        }

        if (!names.claim(payload)) {
            val name = payload.resourceRoot.replace('/', '.')
            val message = "the store already holds another module compiled as $name; give each module of a store its own className"
            return ChasmResult.Error(ExecutionError.Unlinkable(message))
        }

        var installed = false
        try {
            val result = install(payload, imports)
            installed = result is ChasmResult.Success
            return result
        } finally {
            if (!installed) {
                names.release(payload)
            }
        }
    }

    private suspend fun install(
        payload: CompiledModulePayload,
        imports: List<Import>,
    ): ChasmResult<InstantiatedModule, InstantiationError> {
        return try {
            payload.eagerClasses.initialize()
            store.instantiate(managedStore.engine, imports, CompiledModuleInstallation(payload, managedStore, registry, instances))
        } catch (failure: LinkageError) {
            ChasmResult.Error(ExecutionError.Unsupported("the compiled module does not link against this runtime: $failure"))
        } catch (_: OutOfMemoryError) {
            val message = InvocationError.GuestHeapOutOfMemory.toString()
            ChasmResult.Error(ExecutionError.Trapped(Trap(TrapReason.GUEST_HEAP_EXHAUSTED, message, null, null)))
        }
    }

    private companion object {
        const val UNSUPPORTED_MODULE = "a compiled store only instantiates modules loaded by runtime-compiled"
    }
}
