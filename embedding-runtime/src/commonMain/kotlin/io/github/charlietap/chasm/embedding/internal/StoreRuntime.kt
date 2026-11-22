package io.github.charlietap.chasm.embedding.internal

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.LinearMemoryConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.diagnostic.Instantiation
import io.github.charlietap.chasm.embedding.diagnostic.TrapSource
import io.github.charlietap.chasm.embedding.diagnostic.TrapSourceRegistry
import io.github.charlietap.chasm.embedding.diagnostic.executionTrap
import io.github.charlietap.chasm.embedding.diagnostic.instantiationError
import io.github.charlietap.chasm.embedding.diagnostic.invocationError
import io.github.charlietap.chasm.embedding.engine.Engine
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.error.Trap
import io.github.charlietap.chasm.embedding.ext.withStoreSettings
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Importable
import io.github.charlietap.chasm.embedding.shapes._engine
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.data
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.global
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.host.RawHostFunction
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.resource.allocation.GlobalAllocator
import io.github.charlietap.chasm.runtime.resource.allocation.HostFunctionAllocator
import io.github.charlietap.chasm.runtime.resource.allocation.MemoryAllocator
import io.github.charlietap.chasm.runtime.resource.allocation.TableAllocator
import io.github.charlietap.chasm.runtime.resource.allocation.TagAllocator
import io.github.charlietap.chasm.runtime.resource.drop.MemoryInstanceDropper
import io.github.charlietap.chasm.runtime.store.Fuel
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.ext.definedType
import io.github.charlietap.chasm.embedding.error.InvocationError as EmbeddingInvocationError
import io.github.charlietap.chasm.runtime.instance.Import as RuntimeImport
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

@InternalChasmApi
data class InstantiatedModule(
    val config: RuntimeConfig,
    val instance: ModuleInstance,
)

@InternalChasmApi
class StoreRuntime(
    private val store: RuntimeStore,
) {

    var dropped: Boolean = false
        private set

    private val diagnostics = TrapSourceRegistry()

    fun liveStore(): RuntimeStore {

        check(!dropped) { InvocationError.StoreDropped.toString() }

        return store
    }

    internal fun runtime(): RuntimeStore {
        return store
    }

    fun addFuel(amount: Long) {

        val fuel = meteredFuel()
        require(amount >= 0L) { "Fuel added must be non-negative" }

        fuel.remaining += amount.coerceAtMost(Long.MAX_VALUE - fuel.remaining)
    }

    fun resetFuel() {
        meteredFuel().remaining = 0L
    }

    fun remainingFuel(): Long {
        return meteredFuel().remaining
    }

    fun interrupt(): Boolean {

        val interrupt = store.interrupt
        check(interrupt.enabled) { "Interrupts are not enabled for this store" }

        if (interrupt.depth == 0) {
            return false
        }

        interrupt.requested = true

        return true
    }

    fun function(
        type: FunctionType,
        function: RawHostFunction,
    ): ExternalValue.Function {
        return HostFunctionAllocator(liveStore(), type, function)
    }

    fun memory(
        type: MemoryType,
        config: LinearMemoryConfig,
    ): ExternalValue.Memory {
        return ExternalValue.Memory(MemoryAllocator(liveStore(), type, config))
    }

    fun global(
        type: GlobalType,
        value: Long,
    ): ExternalValue.Global {
        return ExternalValue.Global(GlobalAllocator(liveStore(), type, value))
    }

    fun table(
        type: TableType,
        value: Long,
    ): ExternalValue.Table {
        return ExternalValue.Table(TableAllocator(liveStore(), type, value))
    }

    fun tag(type: TagType): ExternalValue.Tag {

        val runtime = liveStore()
        val rtt = runtime.heap.registerRuntimeType(type.functionType.definedType())

        return ExternalValue.Tag(TagAllocator(runtime, rtt, type))
    }

    suspend fun instantiate(
        engine: Engine,
        imports: List<Import>,
        config: RuntimeConfig,
        installation: ModuleInstallation,
    ): ChasmResult<InstantiatedModule, InstantiationError> {

        val runtime = liveStore()
        require(imports.all { import -> import.value._engine() === engine }) { "an import belongs to a different store" }

        if (runtime.invocationDepth == 0) {
            runtime.heap.clearPendingException()
        }

        val firstFunctionAddress = runtime.functions.size
        val effectiveConfig = config.withStoreSettings(runtime.config)
        val runtimeImports = imports.map { import ->
            RuntimeImport(import.moduleName, import.entityName, installation.external(import.value))
        }

        return installation.instantiate(effectiveConfig, runtime, runtimeImports).fold(
            success = { instance ->

                if (effectiveConfig.debugInfo) {
                    diagnostics.register(installation.trapSource(instance))
                }

                ChasmResult.Success(InstantiatedModule(effectiveConfig, instance))
            },
            failure = { error ->
                val instantiation = Instantiation(firstFunctionAddress, installation::trapSource)
                ChasmResult.Error(instantiationError(this, error, instantiation, installation::thrown))
            },
        )
    }

    fun invocationError(
        error: InvocationError,
        thrown: () -> ExceptionReference,
    ): EmbeddingInvocationError {
        return invocationError(this, error, thrown)
    }

    fun trap(error: InvocationError): Trap {
        return executionTrap(this, error)
    }

    fun register(source: TrapSource?) {
        diagnostics.register(source)
    }

    fun trapSource(instance: ModuleInstance): TrapSource? {
        return diagnostics.find(instance)
    }

    fun dropInstance(instance: ModuleInstance) {

        val runtime = liveStore()
        diagnostics.unregister(instance)

        instance.dataAddresses.forEach { address ->
            runtime.data(address).bytes = ubyteArrayOf()
        }
        instance.dataAddresses.clear()

        instance.elemAddresses.forEach { address ->
            val element = runtime.element(address)
            element.elements = longArrayOf()
            runtime.referenceStorage?.release(element)
        }
        instance.elemAddresses.clear()

        instance.exports.clear()
        instance.functionAddresses.clear()

        instance.globalAddresses.forEach { address ->
            val global = runtime.global(address)
            global.value = 0L
            runtime.referenceStorage?.release(global)
        }
        instance.globalAddresses.clear()

        instance.memAddresses.clear()

        instance.tableAddresses.forEach { address ->
            val table = runtime.table(address)
            table.elements = longArrayOf()
            runtime.referenceStorage?.release(table)
        }
        instance.tableAddresses.clear()

        instance.tagAddresses.clear()
        instance.deallocated = true
    }

    fun drop() {

        dropped = true
        diagnostics.clear()

        store.data.forEach { data ->
            data.bytes = ubyteArrayOf()
        }
        store.data.clear()

        store.elements.forEach { element ->
            element.elements = longArrayOf()
        }
        store.elements.clear()

        store.functions.clear()

        store.globals.forEach { global ->
            global.value = 0L
        }
        store.globals.clear()

        store.memories.forEach { memory ->
            MemoryInstanceDropper(memory)
        }
        store.memories.clear()

        store.tables.forEach { table ->
            table.elements = longArrayOf()
        }
        store.tables.clear()

        store.heap.drop()
        store.referenceStorage = null
    }

    private fun meteredFuel(): Fuel {

        val fuel = liveStore().fuel
        check(fuel.metered) { "Fuel metering is not enabled for this store" }

        return fuel
    }
}
