package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Importable
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.embedding.shapes._engine
import io.github.charlietap.chasm.embedding.shapes._function
import io.github.charlietap.chasm.embedding.shapes._global
import io.github.charlietap.chasm.embedding.shapes._instance
import io.github.charlietap.chasm.embedding.shapes._memory
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.embedding.shapes._table
import io.github.charlietap.chasm.embedding.shapes._tag
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

internal interface EngineHandles {

    fun function(address: Address.Function): Function

    fun memory(address: Address.Memory): Memory

    fun global(address: Address.Global): Global

    fun table(address: Address.Table): Table

    fun tag(address: Address.Tag): Tag

    fun importable(value: ExternalValue): Importable

    fun context(
        module: ModuleInstance,
        config: RuntimeConfig,
    ): InterpreterModuleContext

    fun interpreterInstance(instance: Instance): InterpreterInstance

    fun external(importable: Importable): ExternalValue

    fun functionAddress(function: Function): Address.Function

    fun memoryInstance(memory: Memory): MemoryInstance

    fun globalPayload(global: Global): GlobalPayload

    fun tablePayload(table: Table): TablePayload

    fun tagAddress(tag: Tag): Address.Tag
}

internal class InterpreterHandles(
    private val binding: EngineBinding,
    private val store: StoreRuntime,
    private val codec: ReferenceCodec,
) : EngineHandles {

    override fun function(address: Address.Function): Function {

        val runtime = store.liveStore()

        return _function(binding.engine, ExternalValue.Function(address), runtime.functions[address.address].functionType)
    }

    override fun memory(address: Address.Memory): Memory {

        val instance = store.liveStore().memories[address.address]
        val type = instance.type.copy(limits = instance.type.limits.copy())

        return _memory(binding.engine, MemoryPayload(ExternalValue.Memory(address), instance), type)
    }

    override fun global(address: Address.Global): Global {

        val instance = store.liveStore().globals[address.address]
        val referenceType = referenceType(instance.type.valueType)?.let(codec::resolve)
        val payload = GlobalPayload(ExternalValue.Global(address), instance, referenceType)

        return _global(binding.engine, payload, instance.type)
    }

    override fun table(address: Address.Table): Table {

        val instance = store.liveStore().tables[address.address]
        val payload = TablePayload(ExternalValue.Table(address), instance, codec.resolve(instance.type.referenceType))
        val type = instance.type.copy(limits = instance.type.limits.copy())

        return _table(binding.engine, payload, type)
    }

    override fun tag(address: Address.Tag): Tag {

        val runtime = store.liveStore()

        return _tag(binding.engine, ExternalValue.Tag(address), runtime.heap.tag(address).type)
    }

    override fun importable(value: ExternalValue): Importable {
        return when (value) {
            is ExternalValue.Function -> function(value.address)
            is ExternalValue.Memory -> memory(value.address)
            is ExternalValue.Global -> global(value.address)
            is ExternalValue.Table -> table(value.address)
            is ExternalValue.Tag -> tag(value.address)
        }
    }

    override fun context(
        module: ModuleInstance,
        config: RuntimeConfig,
    ): InterpreterModuleContext {

        val existing = module.compiledInstance as? InterpreterModuleContext
        if (existing != null) {
            return existing
        }

        val interpreterInstance = InterpreterInstance(module, config, this)
        val context = InterpreterModuleContext(_instance(binding.engine, interpreterInstance), interpreterInstance)
        module.compiledInstance = context

        return context
    }

    override fun interpreterInstance(instance: Instance): InterpreterInstance {

        require(instance._engine() === binding.engine) { "the instance belongs to a different store" }

        return instance._payload() as InterpreterInstance
    }

    override fun external(importable: Importable): ExternalValue {

        val payload = payload(importable)

        return if (payload is InstancePayload) {
            payload.external
        } else {
            payload as ExternalValue
        }
    }

    override fun functionAddress(function: Function): Address.Function {
        return (payload(function) as ExternalValue.Function).address
    }

    override fun memoryInstance(memory: Memory): MemoryInstance {

        val payload = payload(memory) as MemoryPayload
        store.liveStore()

        return payload.instance
    }

    override fun globalPayload(global: Global): GlobalPayload {

        val payload = payload(global) as GlobalPayload
        store.liveStore()

        return payload
    }

    override fun tablePayload(table: Table): TablePayload {

        val payload = payload(table) as TablePayload
        store.liveStore()

        return payload
    }

    override fun tagAddress(tag: Tag): Address.Tag {

        val address = (payload(tag) as ExternalValue.Tag).address
        store.liveStore()

        return address
    }

    private fun payload(importable: Importable): Any {

        require(importable._engine() === binding.engine) { "the handle belongs to a different store" }

        return importable._payload()
    }
}
