package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.embedding.engine.Engine
import io.github.charlietap.chasm.reference.FunctionReference
import io.github.charlietap.chasm.runtime.function.CompiledFunction
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.store.Store
import kotlin.jvm.JvmField

class ManagedFunction(
    @JvmField val instance: FunctionInstance,
    @JvmField val address: Int,
    @JvmField val rtt: Int,
    @JvmField val owningEngine: Engine,
) : FunctionReference() {

    @JvmField
    var entry: CompiledFunction? = null

    override fun engine(): Engine {
        return owningEngine
    }
}

class ManagedFunctions(
    private val store: Store,
    private val managedStore: ManagedStore,
) {

    private var functions = arrayOfNulls<ManagedFunction>(0)

    fun at(address: Int): ManagedFunction {

        if (address >= functions.size) {
            functions = functions.copyOf(maxOf(address + 1, functions.size * 2))
        }

        val existing = functions[address]
        if (existing != null) {
            return existing
        }

        val created = create(address)
        functions[address] = created

        return created
    }

    fun publish(
        address: Int,
        entry: CompiledFunction,
    ) {
        functions.getOrNull(address)?.entry = entry
    }

    fun owns(function: ManagedFunction): Boolean {
        return function.address < functions.size && functions[function.address] === function
    }

    private fun create(address: Int): ManagedFunction {

        val instance = store.functions[address]

        return ManagedFunction(instance, address, instance.rtt.value, managedStore.engine).apply {
            entry = (instance as? FunctionInstance.WasmFunction)?.callStrategy?.compiledEntry
        }
    }
}
