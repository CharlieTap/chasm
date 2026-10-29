
package io.github.charlietap.chasm.runtime.compiled

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.invoker.compiled.CompiledFunctionInstallation
import io.github.charlietap.chasm.executor.invoker.compiled.installCompiledFunctions
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
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
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.ReferenceType

fun ModuleCodeInstallation(
    install: (CodeInstallation) -> Unit,
    store: Store,
    definedFunctions: Int,
    instance: ModuleInstance,
    runtimeTypes: RuntimeTypeMap,
): Result<Unit, ModuleTrapError> {
    val installation = Installation(store, definedFunctions, instance, runtimeTypes)
    try {
        install(installation)
    } catch (failure: IllegalArgumentException) {
        return Err(InstantiationError.CompiledCodeRejected(failure.message ?: "compiled code does not fit the module"))
    } catch (failure: IndexOutOfBoundsException) {
        return Err(InstantiationError.CompiledCodeRejected(failure.message ?: "compiled code does not fit the module"))
    }
    val missing = installation.missing()
    if (missing != null) return Err(InstantiationError.CompiledCodeRejected("no code for function $missing"))
    installCompiledFunctions(store, installation.installations())
    return Ok(Unit)
}

private class Installation(
    private val store: Store,
    definedFunctions: Int,
    override val instance: ModuleInstance,
    private val runtimeTypes: RuntimeTypeMap,
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

    override fun runtimeType(
        index: Int,
    ): Int = runtimeTypes[index].value

    override fun tagAddress(
        index: Int,
    ): Int = instance.tagAddresses[index].address

    override fun functionReference(
        index: Int,
    ): Long = ReferenceValue.Function(Address.Function(instance.functionAddresses[index].address)).toLong()

    override fun typeTest(
        nullable: Boolean,
        heapType: Int,
    ): Any {
        val heap: HeapType = if (heapType >= 0) ConcreteHeapType.TypeIndex(heapType) else ABSTRACT_HEAP_TYPES[-heapType - 1]
        val type = if (nullable) ReferenceType.RefNull(heap) else ReferenceType.Ref(heap)
        return ReferenceTypeTest.from(type, runtimeTypes)
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

    fun missing(): Int? = entries.indexOfFirst { it == null }.takeIf { it >= 0 }?.plus(importedFunctions)

    fun installations(): List<CompiledFunctionInstallation> = entries.mapIndexed { defined, entry ->
        CompiledFunctionInstallation(function(defined + importedFunctions) as FunctionInstance.WasmFunction, checkNotNull(entry))
    }
}

private val ABSTRACT_HEAP_TYPES: List<HeapType> = listOf(
    AbstractHeapType.Func,
    AbstractHeapType.NoFunc,
    AbstractHeapType.Extern,
    AbstractHeapType.NoExtern,
    AbstractHeapType.Any,
    AbstractHeapType.Eq,
    AbstractHeapType.I31,
    AbstractHeapType.Struct,
    AbstractHeapType.Array,
    AbstractHeapType.None,
    AbstractHeapType.Exception,
    AbstractHeapType.NoException,
)
