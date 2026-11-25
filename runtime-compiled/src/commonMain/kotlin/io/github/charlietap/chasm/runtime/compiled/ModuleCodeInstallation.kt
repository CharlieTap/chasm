
package io.github.charlietap.chasm.runtime.compiled

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.executor.invoker.compiled.CompiledFunctionInstallation
import io.github.charlietap.chasm.executor.invoker.compiled.installCompiledFunctions
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedStore
import io.github.charlietap.chasm.runtime.compiled.gc.Rtt
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.function.CompiledFunction
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap
import io.github.charlietap.chasm.runtime.value.ReferenceValue

@InternalChasmApi
fun ModuleCodeInstallation(
    install: (CodeInstallation) -> Unit,
    store: Store,
    definedFunctions: Int,
    instance: ModuleInstance,
    runtimeTypes: RuntimeTypeMap,
    rtts: List<Rtt>,
): Result<Unit, ModuleTrapError> {
    val installation = Installation(store, definedFunctions, instance, runtimeTypes, rtts)
    try {
        install(installation)
    } catch (failure: IllegalArgumentException) {
        return Err(InstantiationError.CompiledCodeRejected(failure.message ?: "compiled code does not fit the module"))
    } catch (failure: IndexOutOfBoundsException) {
        return Err(InstantiationError.CompiledCodeRejected(failure.message ?: "compiled code does not fit the module"))
    } catch (failure: InvocationException) {
        return Err(failure.error)
    }
    val missing = installation.missing()
    if (missing != null) return Err(InstantiationError.CompiledCodeRejected("no code for function $missing"))
    installCompiledFunctions(store, installation.installations())
    installation.publishManagedEntries()
    return Ok(Unit)
}

private class Installation(
    private val store: Store,
    definedFunctions: Int,
    override val instance: ModuleInstance,
    private val runtimeTypes: RuntimeTypeMap,
    private val rtts: List<Rtt>,
) : CodeInstallation() {

    private val importedFunctions = instance.functionAddresses.size - definedFunctions
    private val entries = arrayOfNulls<CompiledFunction>(definedFunctions)

    override fun function(
        index: Int,
    ): FunctionInstance = store.functions[instance.functionAddresses[index].address]

    override fun memory(
        index: Int,
    ): MemoryInstance = store.memories[instance.memAddresses[index].address]

    override fun table(
        index: Int,
    ): TableInstance = store.tables[instance.tableAddresses[index].address]

    override fun global(
        index: Int,
    ): GlobalInstance = store.globals[instance.globalAddresses[index].address]

    override fun element(
        index: Int,
    ): ElementInstance = store.elements[instance.elemAddresses[index].address]

    override fun data(
        index: Int,
    ): DataInstance = store.data[instance.dataAddresses[index].address]

    override fun referenceGlobal(
        index: Int,
    ): Any = managedStore().referenceGlobal(global(index))

    override fun referenceTable(
        index: Int,
    ): Any = managedStore().referenceTable(table(index))

    override fun referenceSegment(
        index: Int,
    ): Any = managedStore().referenceSegment(element(index))

    override fun runtimeType(
        index: Int,
    ): Int = runtimeTypes[index].value

    override fun tagAddress(
        index: Int,
    ): Int = instance.tagAddresses[index].address

    override fun functionReference(
        index: Int,
    ): Long = ReferenceValue.Function(Address.Function(instance.functionAddresses[index].address)).toLong()

    override fun managedFunction(index: Int): Any {
        return managedStore().functions.at(instance.functionAddresses[index].address)
    }

    override fun rtt(index: Int): Any {
        return rtts[index]
    }

    override fun install(
        index: Int,
        entry: CompiledFunction,
    ) {
        val defined = index - importedFunctions
        require(defined in entries.indices) { "function $index is not defined by the module" }
        require(entries[defined] == null) { "function $index installed twice" }
        entries[defined] = entry
    }

    private fun managedStore(): ManagedStore = checkNotNull(store.referenceStorage as? ManagedStore) { "store has no managed references" }

    fun missing(): Int? = entries.indexOfFirst { it == null }.takeIf { it >= 0 }?.plus(importedFunctions)

    fun publishManagedEntries() {

        val functions = (store.referenceStorage as? ManagedStore)?.functions ?: return

        entries.forEachIndexed { defined, entry ->
            val address = instance.functionAddresses[defined + importedFunctions].address
            functions.publish(address, checkNotNull(entry))
        }
    }

    fun installations(): List<CompiledFunctionInstallation> = entries.mapIndexed { defined, entry ->
        CompiledFunctionInstallation(function(defined + importedFunctions) as FunctionInstance.WasmFunction, checkNotNull(entry))
    }
}
