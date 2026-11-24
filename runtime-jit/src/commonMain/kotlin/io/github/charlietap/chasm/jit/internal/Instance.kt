package io.github.charlietap.chasm.jit.internal

import com.github.michaelbull.result.getError
import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.error.Trap
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.internal.InstantiatedModule
import io.github.charlietap.chasm.embedding.internal.StoreRuntime
import io.github.charlietap.chasm.embedding.managed.ManagedInstances
import io.github.charlietap.chasm.embedding.managed.ManagedInstantiator
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.DecodedModule
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.executor.instantiator.compat.CompatibilityChecker
import io.github.charlietap.chasm.jit.internal.CompileError.Stage
import io.github.charlietap.chasm.runtime.compiled.CodeInstallation
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedStore
import io.github.charlietap.chasm.runtime.error.InvocationError

internal class JitInstantiator(
    private val store: StoreRuntime,
    private val managedStore: ManagedStore,
    private val shapeTypes: ShapeClassTypes,
    private val instances: ManagedInstances,
    private val compatibilityChecker: CompatibilityChecker,
) : ManagedInstantiator {

    private val shapes = StoreShapes(CodeInstallation::class.java.classLoader!!, managedStore.engine)

    override suspend fun instantiate(
        module: Module,
        imports: List<Import>,
    ): ChasmResult<InstantiatedModule, InstantiationError> {

        val decoded = module._payload() as? DecodedModule ?: return ChasmResult.Error(ExecutionError.Unsupported(UNSUPPORTED_MODULE))
        val config = store.liveStore().config

        val incompatibility = compatibilityChecker(decoded.declarations).getError()
        if (incompatibility != null) {
            return ChasmResult.Error(ExecutionError.Unsupported(incompatibility.toString()))
        }

        val compileConfig = CompileConfig(config.limits.cpu is CpuLimits.Metered, config.interrupts, config.stacktraces, shapes)
        val compilation = when (val compiled = compile(decoded, compileConfig)) {
            is CompileResult.Success -> compiled.compilation
            is CompileResult.Error -> return ChasmResult.Error(ExecutionError.Unsupported(compiled.error.message()))
        }
        val installation = JitModuleInstallation(decoded, compilation, shapes, managedStore, shapeTypes, instances, config.stacktraces)

        return try {
            store.instantiate(managedStore.engine, imports, installation)
        } catch (failure: LinkageError) {
            ChasmResult.Error(ExecutionError.Unsupported(CompileError(failure.toString(), Stage.LOADING).message()))
        } catch (_: OutOfMemoryError) {
            val message = InvocationError.GuestHeapOutOfMemory.toString()
            ChasmResult.Error(ExecutionError.Trapped(Trap(TrapReason.GUEST_HEAP_EXHAUSTED, message, null, null)))
        }
    }

    private companion object {
        const val UNSUPPORTED_MODULE = "a chasm-jit store only instantiates modules decoded by module()"
    }
}
