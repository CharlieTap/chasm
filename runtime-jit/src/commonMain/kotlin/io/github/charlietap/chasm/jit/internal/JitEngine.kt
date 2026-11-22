package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.LinearMemoryConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.compiled.ManagedEngine
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.error.InvocationError
import io.github.charlietap.chasm.embedding.internal.StoreRuntime
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.DecodedModule
import io.github.charlietap.chasm.embedding.shapes.Export
import io.github.charlietap.chasm.embedding.shapes.Exports
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Importable
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.PreparedFunction
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.embedding.shapes._engine
import io.github.charlietap.chasm.embedding.shapes._exports
import io.github.charlietap.chasm.embedding.shapes._function
import io.github.charlietap.chasm.embedding.shapes._global
import io.github.charlietap.chasm.embedding.shapes._instance
import io.github.charlietap.chasm.embedding.shapes._memory
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.embedding.shapes._preparedFunction
import io.github.charlietap.chasm.embedding.shapes._store
import io.github.charlietap.chasm.embedding.shapes._table
import io.github.charlietap.chasm.embedding.shapes._tag
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.executor.instantiator.compat.CompatibilityChecker
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostGcFieldInfo
import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.reference.FunctionReference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedObjects
import io.github.charlietap.chasm.runtime.compiled.gc.ReferenceGlobal
import io.github.charlietap.chasm.runtime.compiled.gc.ReferenceTable
import io.github.charlietap.chasm.runtime.exception.HostRaisedWasmException
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.host.HostMemory
import io.github.charlietap.chasm.runtime.host.readNullTerminatedUtf8String
import io.github.charlietap.chasm.runtime.host.readUtf8String
import io.github.charlietap.chasm.runtime.host.writeUtf8String
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.runtime.error.InvocationError as RuntimeInvocationError
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

@InternalChasmApi
fun _jitStore(config: StoreConfig): Store {
    return JitEngine(StoreRuntime(RuntimeStore(config = config))).store
}

internal class JitEngine(
    private val storeRuntime: StoreRuntime,
) : ManagedEngine,
    JitHandles {

    private val runtime = storeRuntime.liveStore()

    private val managedStore = managedStore(runtime).also { created -> created.engine = this }

    private val objects: ManagedObjects
        get() {
            requireLiveStore()
            return managedStore.objects
        }

    private val values = LaneValues()

    private var exceptionTags = arrayOfNulls<Tag>(0)

    val store = _store(this)

    private val resources = JitHostResources(store)

    private val instantiator = JitInstantiator(storeRuntime, managedStore, this, ::CompatibilityChecker)

    override fun dropStore() {

        requireLiveStore()

        storeRuntime.drop()
        managedStore.drop()
    }

    override fun dropInstance(instance: Instance) {

        val jit = jit(instance)

        requireLiveStore()
        check(!jit.module.deallocated) { "the instance has already been dropped" }

        storeRuntime.dropInstance(jit.module)
        jit.prepared = emptyMap()
    }

    override fun addFuel(fuel: Long) {

        requireLiveStore()
        require(fuel >= 0) { "negative fuel $fuel" }

        storeRuntime.addFuel(fuel)
    }

    override fun resetFuel() {

        requireLiveStore()

        storeRuntime.resetFuel()
    }

    override fun remainingFuel(): Long {

        requireLiveStore()

        return storeRuntime.remainingFuel()
    }

    override fun interrupt(): Boolean {

        requireLiveStore()

        return storeRuntime.interrupt()
    }

    override fun collect() {

        requireLiveStore()

        Runtime.getRuntime().gc()
    }

    override fun function(
        type: FunctionType,
        function: HostFunction,
    ): Function {

        requireLiveStore()

        return _function(this, storeRuntime.function(type, CompiledHostFunction(type, function, this, resources)), type)
    }

    override fun memory(type: MemoryType): Memory {

        requireLiveStore()

        return memoryAt(storeRuntime.memory(type, LinearMemoryConfig()).address)
    }

    override fun global(
        type: GlobalType,
        value: ExecutionValue,
    ): Global {

        requireLiveStore()

        val reference = type.valueType as? ValueType.Reference
        if (reference == null) {
            return globalAt(storeRuntime.global(type, values.number(type.valueType, value)).address)
        }

        val initial = values.reference(value)

        require(managedStore.checks.check(reference.referenceType, null).accepts(initial)) { "the value does not match the global's type" }

        val global = globalAt(storeRuntime.global(type, NULL_REFERENCE_WORD).address)
        managedStore.objects.globals.unsafeWriteGlobalReference(referenceGlobal(global), initial)

        return global
    }

    override fun table(
        type: TableType,
        initial: Any?,
    ): Table {

        requireLiveStore()

        val table = tableAt(storeRuntime.table(type, NULL_REFERENCE_WORD).address)
        val storage = referenceTable(table)

        managedStore.objects.tables.fillTable(storage, 0, initial, managedStore.objects.tables.sizeTable(storage))

        return table
    }

    override fun tag(type: TagType): Tag {

        requireLiveStore()

        return _tag(this, storeRuntime.tag(type), type)
    }

    override fun functionReference(function: Function): FunctionReference {

        require(function._engine() === this) { "the function belongs to a different store" }
        requireLiveStore()

        return managedStore.functions.at((function._payload() as ExternalValue.Function).address.address)
    }

    override suspend fun instance(
        module: Module,
        imports: List<Import>,
        config: RuntimeConfig,
    ): ChasmResult<Instance, InstantiationError> {

        requireLiveStore()

        val decoded = module._payload() as? DecodedModule ?: return ChasmResult.Error(ExecutionError.Unsupported(UNSUPPORTED_MODULE))

        imports.forEach { import ->
            require(import.value._engine() === this) { "the import ${import.moduleName}.${import.entityName} belongs to a different store" }
        }

        return when (val result = instantiator.instantiate(decoded, imports, config)) {
            is ChasmResult.Success -> {
                val instance = instanceOf(result.result.instance)
                prepareExports(jit(instance))
                ChasmResult.Success(instance)
            }
            is ChasmResult.Error -> result
        }
    }

    private fun prepareExports(jit: JitInstance) {

        jit.prepared = buildMap(jit.module.exports.size) {
            jit.module.exports.forEach { export ->
                val value = export.value
                if (value is ExternalValue.Function) {
                    val preparation = ObjectCall.prepare(storeRuntime, jit.module, jit.config, value.address, managedStore)
                    if (preparation is ChasmResult.Success) {
                        put(export.name.name, _preparedFunction(preparation.result.type, this@JitEngine, preparation.result))
                    }
                }
            }
        }
    }

    override fun exports(instance: Instance): Exports {

        val jit = jit(instance)

        requireLiveStore()
        check(!jit.module.deallocated) { "the instance has been dropped" }

        val cached = jit.exports
        if (cached != null) {
            return cached
        }

        val exports = _exports(jit.module.exports.map { export -> Export(export.name.name, importable(export.value)) })
        jit.exports = exports

        return exports
    }

    override fun prepareFunction(
        instance: Instance,
        name: String,
    ): ChasmResult<PreparedFunction, ExecutionError.Rejected> {

        val jit = jit(instance)
        if (storeRuntime.dropped || jit.module.deallocated) {
            return ChasmResult.Error(ExecutionError.Rejected("the instance or its store has been dropped"))
        }

        val cached = jit.prepared[name]
        if (cached != null) {
            return ChasmResult.Success(cached)
        }

        val export = jit.module.exports.firstOrNull { candidate -> candidate.name.name == name }
        val address = (export?.value as? ExternalValue.Function)?.address
            ?: return ChasmResult.Error(ExecutionError.Rejected(RuntimeInvocationError.FunctionNotFound(name).toString()))

        return when (val prepared = ObjectCall.prepare(storeRuntime, jit.module, jit.config, address, managedStore)) {
            is ChasmResult.Success -> ChasmResult.Success(_preparedFunction(prepared.result.type, this, prepared.result))
            is ChasmResult.Error -> prepared
        }
    }

    override fun invoke(
        function: PreparedFunction,
        args: List<ExecutionValue>,
    ): ChasmResult<List<ExecutionValue>, InvocationError> {

        val words = LongArray(function.slots)
        val references = arrayOfNulls<Any>(function.slots)
        val rejection = values.write(function.type.params.types, args, words, references)
        if (rejection != null) {
            return ChasmResult.Error(ExecutionError.Rejected(rejection))
        }

        return when (val result = call(function).call(words, references)) {
            is ChasmResult.Success -> ChasmResult.Success(values.read(function.type.results.types, words, references))
            is ChasmResult.Error -> result
        }
    }

    override fun invokeUnboxed(
        function: PreparedFunction,
        words: LongArray,
        references: Array<Any?>,
    ): ChasmResult<Unit, InvocationError> {
        return call(function).call(words, references)
    }

    override fun unsafeInvokeUnboxed(
        function: PreparedFunction,
        words: LongArray,
        references: Array<Any?>,
    ): ChasmResult<Unit, InvocationError> {
        return call(function).unsafeCall(words, references)
    }

    override fun sizeMemory(memory: Memory): Int {
        return memoryInstance(memory).type.limits.min.toInt()
    }

    override fun memoryByteSize(memory: Memory): Int {
        return linear(memory).byteSize
    }

    @OptIn(UnsafeHostApi::class)
    override fun unsafeBorrowMemory(memory: Memory): Any? {
        return memoryInstance(memory).unsafeBorrowStorage()
    }

    override fun growMemory(
        memory: Memory,
        pagesToAdd: Int,
    ): Int {
        return memoryInstance(memory).grow(pagesToAdd)
    }

    override fun readByte(
        memory: Memory,
        pointer: Int,
    ): Byte {
        return linear(memory).readI8(pointer)
    }

    override fun readShort(
        memory: Memory,
        pointer: Int,
    ): Short {
        return linear(memory).readI16(pointer)
    }

    override fun readInt(
        memory: Memory,
        pointer: Int,
    ): Int {
        return linear(memory).readI32(pointer)
    }

    override fun readLong(
        memory: Memory,
        pointer: Int,
    ): Long {
        return linear(memory).readI64(pointer)
    }

    override fun readFloat(
        memory: Memory,
        pointer: Int,
    ): Float {
        return linear(memory).readF32(pointer)
    }

    override fun readDouble(
        memory: Memory,
        pointer: Int,
    ): Double {
        return linear(memory).readF64(pointer)
    }

    override fun readBytes(
        memory: Memory,
        buffer: ByteArray,
        memoryPointer: Int,
        bytesToRead: Int,
        bufferPointer: Int,
    ): ByteArray {
        return linear(memory).read(buffer, memoryPointer, bytesToRead, bufferPointer)
    }

    override fun readUtf8String(
        memory: Memory,
        pointer: Int,
        stringLengthInBytes: Int,
    ): String {
        return linear(memory).readUtf8String(pointer, stringLengthInBytes)
    }

    override fun readNullTerminatedUtf8String(
        memory: Memory,
        pointer: Int,
    ): String {
        return linear(memory).readNullTerminatedUtf8String(pointer)
    }

    override fun writeByte(
        memory: Memory,
        pointer: Int,
        value: Byte,
    ) {
        linear(memory).writeI8(pointer, value)
    }

    override fun writeShort(
        memory: Memory,
        pointer: Int,
        value: Short,
    ) {
        linear(memory).writeI16(pointer, value)
    }

    override fun writeInt(
        memory: Memory,
        pointer: Int,
        value: Int,
    ) {
        linear(memory).writeI32(pointer, value)
    }

    override fun writeLong(
        memory: Memory,
        pointer: Int,
        value: Long,
    ) {
        linear(memory).writeI64(pointer, value)
    }

    override fun writeFloat(
        memory: Memory,
        pointer: Int,
        value: Float,
    ) {
        linear(memory).writeF32(pointer, value)
    }

    override fun writeDouble(
        memory: Memory,
        pointer: Int,
        value: Double,
    ) {
        linear(memory).writeF64(pointer, value)
    }

    override fun writeBytes(
        memory: Memory,
        pointer: Int,
        buffer: ByteArray,
        bufferPointer: Int,
        bytesToWrite: Int,
    ) {
        linear(memory).write(pointer, buffer, bufferPointer, bytesToWrite)
    }

    override fun writeUtf8String(
        memory: Memory,
        pointer: Int,
        string: String,
    ) {
        linear(memory).writeUtf8String(pointer, string)
    }

    override fun fillMemory(
        memory: Memory,
        pointer: Int,
        value: Byte,
        bytesToFill: Int,
    ) {
        linear(memory).fill(pointer, value, bytesToFill)
    }

    override fun copyMemory(
        source: Memory,
        sourcePointer: Int,
        destination: Memory,
        destinationPointer: Int,
        bytesToCopy: Int,
    ) {
        linear(destination).copy(sourcePointer, destinationPointer, bytesToCopy, linear(source))
    }

    override fun moveMemory(
        source: Memory,
        sourcePointer: Int,
        destination: Memory,
        destinationPointer: Int,
        bytesToMove: Int,
    ) {
        linear(destination).move(sourcePointer, destinationPointer, bytesToMove, linear(source))
    }

    override fun readGlobal(global: Global): Long {
        return managedStore.objects.globals.readGlobal(globalInstance(global))
    }

    override fun readGlobalReference(global: Global): Any? {
        return managedStore.objects.globals.readGlobalReference(referenceGlobal(global))
    }

    override fun writeGlobal(
        global: Global,
        value: Long,
    ) {
        managedStore.objects.globals.writeGlobal(globalInstance(global), value)
    }

    override fun writeGlobalReference(
        global: Global,
        value: Any?,
    ) {
        managedStore.objects.globals.writeGlobalReference(referenceGlobal(global), value)
    }

    override fun unsafeWriteGlobalReference(
        global: Global,
        value: Any?,
    ) {
        managedStore.objects.globals.unsafeWriteGlobalReference(referenceGlobal(global), value)
    }

    override fun sizeTable(table: Table): Int {
        return managedStore.objects.tables.sizeTable(referenceTable(table))
    }

    override fun growTable(
        table: Table,
        elementsToAdd: Int,
        initial: Any?,
    ): Int {
        return managedStore.objects.tables.growTable(referenceTable(table), elementsToAdd, initial)
    }

    override fun readTable(
        table: Table,
        index: Int,
    ): Any? {
        return managedStore.objects.tables.readTable(referenceTable(table), index)
    }

    override fun writeTable(
        table: Table,
        index: Int,
        value: Any?,
    ) {
        managedStore.objects.tables.writeTable(referenceTable(table), index, value)
    }

    override fun readTableElements(
        table: Table,
        buffer: Array<Any?>,
        elementIndex: Int,
        elementsToRead: Int,
        bufferIndex: Int,
    ): Array<Any?> {
        return managedStore.objects.tables.readTable(referenceTable(table), buffer, elementIndex, elementsToRead, bufferIndex)
    }

    override fun writeTableElements(
        table: Table,
        elementIndex: Int,
        buffer: Array<Any?>,
        bufferIndex: Int,
        elementsToWrite: Int,
    ) {
        managedStore.objects.tables.writeTable(referenceTable(table), elementIndex, buffer, bufferIndex, elementsToWrite)
    }

    override fun fillTable(
        table: Table,
        elementIndex: Int,
        value: Any?,
        elementsToFill: Int,
    ) {
        managedStore.objects.tables.fillTable(referenceTable(table), elementIndex, value, elementsToFill)
    }

    override fun copyTable(
        source: Table,
        sourceElementIndex: Int,
        destination: Table,
        destinationElementIndex: Int,
        elementsToCopy: Int,
    ) {
        managedStore.objects.tables.copyTable(referenceTable(source), sourceElementIndex, referenceTable(destination), destinationElementIndex, elementsToCopy)
    }

    override fun unsafeWriteTable(
        table: Table,
        index: Int,
        value: Any?,
    ) {
        managedStore.objects.tables.unsafeWriteTable(referenceTable(table), index, value)
    }

    override fun unsafeBorrowElements(table: Table): Array<Any?> {
        return managedStore.objects.tables.borrowElements(referenceTable(table))
    }

    override fun structType(struct: StructReference): HostGcType {
        return objects.structs.structType(struct)
    }

    override fun readStructField(
        struct: StructReference,
        index: Int,
    ): Long {
        return objects.structs.readStructField(struct, index)
    }

    override fun readStructReference(
        struct: StructReference,
        index: Int,
    ): Any? {
        return objects.structs.readStructReference(struct, index)
    }

    override fun writeStructField(
        struct: StructReference,
        index: Int,
        value: Long,
    ) {
        objects.structs.writeStructField(struct, index, value)
    }

    override fun writeStructReference(
        struct: StructReference,
        index: Int,
        value: Any?,
    ) {
        objects.structs.writeStructReference(struct, index, value)
    }

    override fun unsafeWriteStructReference(
        struct: StructReference,
        index: Int,
        value: Any?,
    ) {
        objects.structs.unsafeWriteStructReference(struct, index, value)
    }

    override fun arrayType(array: ArrayReference): HostGcType {
        return objects.arrays.arrayType(array)
    }

    override fun arrayLength(array: ArrayReference): Int {
        return objects.arrays.arrayLength(array)
    }

    override fun readArrayElement(
        array: ArrayReference,
        index: Int,
    ): Long {
        return objects.arrays.readArrayElement(array, index)
    }

    override fun readArrayReference(
        array: ArrayReference,
        index: Int,
    ): Any? {
        return objects.arrays.readArrayReference(array, index)
    }

    override fun writeArrayElement(
        array: ArrayReference,
        index: Int,
        value: Long,
    ) {
        objects.arrays.writeArrayElement(array, index, value)
    }

    override fun writeArrayReference(
        array: ArrayReference,
        index: Int,
        value: Any?,
    ) {
        objects.arrays.writeArrayReference(array, index, value)
    }

    override fun unsafeWriteArrayReference(
        array: ArrayReference,
        index: Int,
        value: Any?,
    ) {
        objects.arrays.unsafeWriteArrayReference(array, index, value)
    }

    override fun readArrayElements(
        array: ArrayReference,
        offset: Int,
        destination: LongArray,
        destinationOffset: Int,
        count: Int,
    ) {
        objects.arrays.readArrayElements(array, offset, destination, destinationOffset, count)
    }

    override fun writeArrayElements(
        array: ArrayReference,
        offset: Int,
        source: LongArray,
        sourceOffset: Int,
        count: Int,
    ) {
        objects.arrays.writeArrayElements(array, offset, source, sourceOffset, count)
    }

    override fun fillArray(
        array: ArrayReference,
        offset: Int,
        count: Int,
        value: Long,
    ) {
        objects.arrays.fillArray(array, offset, count, value)
    }

    override fun copyArray(
        source: ArrayReference,
        sourceOffset: Int,
        destination: ArrayReference,
        destinationOffset: Int,
        count: Int,
    ) {
        objects.arrays.copyArray(source, sourceOffset, destination, destinationOffset, count)
    }

    override fun <T : Any> unsafeBorrow(
        array: ArrayReference,
        storage: Class<T>,
    ): T {
        return objects.arrays.borrow(array, storage)
    }

    override fun runtimeType(
        instance: Instance,
        typeIndex: Int,
    ): HostGcType {
        return runtime.heap.runtimeType(jit(instance).module, typeIndex)
    }

    override fun structFieldCount(type: HostGcType): Int {
        return runtime.heap.structFieldCount(type)
    }

    override fun structFieldInfo(
        type: HostGcType,
        fieldIndex: Int,
    ): HostGcFieldInfo {
        return runtime.heap.structFieldInfo(type, fieldIndex)
    }

    override fun arrayElementInfo(type: HostGcType): HostGcFieldInfo {
        return runtime.heap.arrayElementInfo(type)
    }

    override fun isSubtype(
        actual: HostGcType,
        expected: HostGcType,
    ): Boolean {
        return runtime.heap.isSubtype(actual, expected)
    }

    override fun allocateStruct(
        type: HostGcType,
        fields: LongArray,
        references: Array<Any?>,
    ): StructReference {
        return objects.structs.allocateStruct(type, fields, references)
    }

    override fun unsafeAllocateStruct(
        type: HostGcType,
        fields: LongArray,
        references: Array<Any?>,
    ): StructReference {
        return objects.structs.unsafeAllocateStruct(type, fields, references)
    }

    override fun allocateNumericArray(
        type: HostGcType,
        length: Int,
        initialValue: Long,
    ): ArrayReference {
        return objects.arrays.allocateArray(type, length, initialValue)
    }

    override fun allocateReferenceArray(
        type: HostGcType,
        length: Int,
        initialReference: Any?,
    ): ArrayReference {
        return objects.arrays.allocateArray(type, length, initialReference)
    }

    override fun allocateNumericArray(
        type: HostGcType,
        elements: LongArray,
        elementOffset: Int,
        elementCount: Int,
    ): ArrayReference {
        return objects.arrays.allocateArray(type, elements, elementOffset, elementCount)
    }

    override fun allocateReferenceArray(
        type: HostGcType,
        elements: Array<Any?>,
        elementOffset: Int,
        elementCount: Int,
    ): ArrayReference {
        return objects.arrays.allocateArray(type, elements, elementOffset, elementCount)
    }

    override fun unsafeAllocateArray(
        type: HostGcType,
        storage: Any,
    ): ArrayReference {
        return objects.arrays.unsafeAllocateArray(type, storage)
    }

    override fun createException(
        tag: Tag,
        words: LongArray,
        references: Array<Any?>,
    ): ExceptionReference {
        return objects.exceptions.createException(tagAddress(tag), words, references)
    }

    override fun exceptionTag(exception: ExceptionReference): Tag {

        val address = objects.exceptions.exceptionTag(exception)
        val cached = exceptionTags.getOrNull(address.address)
        if (cached != null) {
            return cached
        }

        val tag = tagAt(address)
        var tags = exceptionTags
        if (address.address >= tags.size) {
            tags = tags.copyOf(maxOf(address.address + 1, tags.size * 2))
        }
        tags[address.address] = tag
        exceptionTags = tags

        return tag
    }

    override fun payloadSize(exception: ExceptionReference): Int {
        return objects.exceptions.payloadSize(exception)
    }

    override fun readPayload(
        exception: ExceptionReference,
        index: Int,
    ): Long {
        return objects.exceptions.readPayload(exception, index)
    }

    override fun readPayloadReference(
        exception: ExceptionReference,
        index: Int,
    ): Any? {
        return objects.exceptions.readPayloadReference(exception, index)
    }

    override fun raise(exception: ExceptionReference): Nothing {
        objects.exceptions.raise(exception)
    }

    override fun raisePending(): Nothing {

        check(managedStore.pending.hasPending) { "no exception is pending" }

        throw HostRaisedWasmException.instance
    }

    override fun takePending(): ExceptionReference {
        return managedStore.pending.take()
    }

    override fun hasPending(): Boolean {
        return managedStore.pending.hasPending
    }

    override fun installed(
        module: ModuleInstance,
        config: RuntimeConfig,
    ): Instance {

        val instance = _instance(this, JitInstance(module, config, this))
        module.compiledInstance = JitModuleContext(instance)

        return instance
    }

    override fun instanceOf(module: ModuleInstance): Instance {
        return contextOf(module).instance
    }

    override fun contextOf(module: ModuleInstance): HostModuleInstance {
        return checkNotNull(module.compiledInstance as? JitModuleContext) { "the module was not instantiated by this engine" }
    }

    override fun memoryAt(address: Address.Memory): Memory {

        val instance = runtime.memories[address.address]
        val type = instance.type.copy(limits = instance.type.limits.copy())

        return _memory(this, JitMemory(address, instance), type)
    }

    override fun tableAt(address: Address.Table): Table {

        val instance = runtime.tables[address.address]
        val type = instance.type.copy(limits = instance.type.limits.copy())

        return _table(this, JitTable(address, managedStore.referenceTable(instance)), type)
    }

    override fun globalAt(address: Address.Global): Global {

        val instance = runtime.globals[address.address]
        val payload = when (instance.type.valueType) {
            is ValueType.Reference -> ReferenceJitGlobal(address, managedStore.referenceGlobal(instance))
            else -> NumberJitGlobal(address, instance)
        }

        return _global(this, payload, instance.type)
    }

    override fun tagAt(address: Address.Tag): Tag {
        return _tag(this, ExternalValue.Tag(address), runtime.heap.tag(address).type)
    }

    private fun importable(value: ExternalValue): Importable {
        return when (value) {
            is ExternalValue.Function -> _function(this, value, runtime.functions[value.address.address].functionType)
            is ExternalValue.Memory -> memoryAt(value.address)
            is ExternalValue.Global -> globalAt(value.address)
            is ExternalValue.Table -> tableAt(value.address)
            is ExternalValue.Tag -> tagAt(value.address)
        }
    }

    private fun jit(instance: Instance): JitInstance {

        require(instance._engine() === this) { "the instance belongs to a different store" }

        return instance._payload() as JitInstance
    }

    private fun call(function: PreparedFunction): ObjectCall {

        require(function._engine() === this) { "the function belongs to a different store" }

        return function._payload() as ObjectCall
    }

    private fun memoryInstance(memory: Memory): MemoryInstance {

        require(memory._engine() === this) { "the memory belongs to a different store" }
        requireLiveStore()

        return (memory._payload() as JitMemory).instance
    }

    private fun linear(memory: Memory): HostMemory {
        return memoryInstance(memory).data
    }

    private fun referenceTable(table: Table): ReferenceTable {

        val storage = jitTable(table).storage
        check(!storage.released) { "the table's instance has been dropped" }

        return storage
    }

    private fun jitTable(table: Table): JitTable {

        require(table._engine() === this) { "the table belongs to a different store" }
        requireLiveStore()

        return table._payload() as JitTable
    }

    private fun referenceGlobal(global: Global): ReferenceGlobal {

        val payload = jitGlobal(global)
        require(payload is ReferenceJitGlobal) { "the global holds a number" }
        check(!payload.storage.released) { "the global's instance has been dropped" }

        return payload.storage
    }

    private fun globalInstance(global: Global): GlobalInstance {
        return when (val payload = jitGlobal(global)) {
            is NumberJitGlobal -> payload.instance
            is ReferenceJitGlobal -> payload.storage.instance
        }
    }

    private fun jitGlobal(global: Global): JitGlobal {

        require(global._engine() === this) { "the global belongs to a different store" }
        requireLiveStore()

        return global._payload() as JitGlobal
    }

    private fun tagAddress(tag: Tag): Address.Tag {

        require(tag._engine() === this) { "the tag belongs to a different store" }

        return (tag._payload() as ExternalValue.Tag).address
    }

    private fun requireLiveStore() {
        check(!storeRuntime.dropped) { "the store has been dropped" }
    }

    private companion object {
        val NULL_REFERENCE_WORD = ReferenceValue.Null(AbstractHeapType.None).toLongFromBoxed()
        const val UNSUPPORTED_MODULE = "a chasm-jit store only instantiates modules decoded by module()"
    }
}
