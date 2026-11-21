package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.compiled.CompiledEngine
import io.github.charlietap.chasm.embedding.compiled.Export
import io.github.charlietap.chasm.embedding.compiled.Exports
import io.github.charlietap.chasm.embedding.compiled.Function
import io.github.charlietap.chasm.embedding.compiled.Global
import io.github.charlietap.chasm.embedding.compiled.HostGcFieldInfo
import io.github.charlietap.chasm.embedding.compiled.HostGcType
import io.github.charlietap.chasm.embedding.compiled.Import
import io.github.charlietap.chasm.embedding.compiled.Importable
import io.github.charlietap.chasm.embedding.compiled.Instance
import io.github.charlietap.chasm.embedding.compiled.Memory
import io.github.charlietap.chasm.embedding.compiled.Module
import io.github.charlietap.chasm.embedding.compiled.PreparedFunction
import io.github.charlietap.chasm.embedding.compiled.Store
import io.github.charlietap.chasm.embedding.compiled.Table
import io.github.charlietap.chasm.embedding.compiled.Tag
import io.github.charlietap.chasm.embedding.compiled.host.HostFunction
import io.github.charlietap.chasm.embedding.compiled.host.HostModuleInstance
import io.github.charlietap.chasm.embedding.compiled.value.ExecutionValue
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.error.InvocationError
import io.github.charlietap.chasm.embedding.internal.externalValue
import io.github.charlietap.chasm.embedding.internal.importable
import io.github.charlietap.chasm.embedding.internal.storeDropped
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.host.HostMemory
import io.github.charlietap.chasm.host.readNullTerminatedUtf8String
import io.github.charlietap.chasm.host.readUtf8String
import io.github.charlietap.chasm.host.writeUtf8String
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedObjects
import io.github.charlietap.chasm.runtime.exception.HostRaisedWasmException
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.embedding.addFuel as classicAddFuel
import io.github.charlietap.chasm.embedding.dropInstance as classicDropInstance
import io.github.charlietap.chasm.embedding.dropStore as classicDropStore
import io.github.charlietap.chasm.embedding.function as classicFunction
import io.github.charlietap.chasm.embedding.global as classicGlobal
import io.github.charlietap.chasm.embedding.interrupt as classicInterrupt
import io.github.charlietap.chasm.embedding.memory as classicMemory
import io.github.charlietap.chasm.embedding.remainingFuel as classicRemainingFuel
import io.github.charlietap.chasm.embedding.resetFuel as classicResetFuel
import io.github.charlietap.chasm.embedding.shapes.Import as ClassicImport
import io.github.charlietap.chasm.embedding.shapes.Instance as ClassicInstance
import io.github.charlietap.chasm.embedding.shapes.Module as ClassicModule
import io.github.charlietap.chasm.embedding.shapes.Store as ClassicStore
import io.github.charlietap.chasm.embedding.store as classicStore
import io.github.charlietap.chasm.embedding.table as classicTable
import io.github.charlietap.chasm.embedding.tag as classicTag
import io.github.charlietap.chasm.host.HostGcType as RuntimeGcType

@InternalChasmApi
fun _store(config: StoreConfig): Store {
    return JitEngine(classicStore(config)).store
}

internal class JitEngine(
    private val classic: ClassicStore,
) : CompiledEngine,
    JitHandles {

    private val runtime = classic.store

    private val bridge = managedBridge(runtime).also { bridge -> bridge.engine = this }

    private val objects: ManagedObjects
        get() {
            live()
            return bridge.objects
        }

    private val values = LaneValues()

    val store = Store(this)

    private val resources = JitHostResources(store)

    private val instantiator = JitInstantiator(classic, bridge, this)

    override fun dropStore() {

        live()

        classicDropStore(classic).orThrow()
    }

    override fun dropInstance(instance: Instance) {

        val jit = jit(instance)

        live()
        check(!jit.module.deallocated) { "the instance has already been dropped" }

        classicDropInstance(classic, jit.embedding).orThrow()
    }

    override fun addFuel(fuel: Long) {

        live()
        require(fuel >= 0) { "negative fuel $fuel" }

        classicAddFuel(classic, fuel).orThrow()
    }

    override fun resetFuel() {

        live()

        classicResetFuel(classic).orThrow()
    }

    override fun remainingFuel(): Long {

        live()

        return classicRemainingFuel(classic).orThrow()
    }

    override fun interrupt(): Boolean {

        live()

        return classicInterrupt(classic).orThrow()
    }

    override fun collect() {

        live()

        Runtime.getRuntime().gc()
    }

    override fun function(
        type: FunctionType,
        function: HostFunction,
    ): Function {

        live()

        return Function(this, externalValue(classicFunction(classic, type, CompiledHostFunction(type, function, this, resources))))
    }

    override fun memory(type: MemoryType): Memory {

        live()

        return Memory(this, externalValue(classicMemory(classic, type)))
    }

    override fun global(
        type: GlobalType,
        value: ExecutionValue,
    ): Global {

        live()

        val reference = type.valueType as? ValueType.Reference
        if (reference == null) {
            return Global(this, externalValue(classicGlobal(classic, type, values.number(type.valueType, value))))
        }

        val initial = values.reference(value)

        require(bridge.checks.check(reference.referenceType, null).accepts(initial)) { "the value does not match the global's type" }

        val global = Global(this, externalValue(classicGlobal(classic, type, NULL_REFERENCE)))
        objects.globals.unsafeWriteGlobalReference(globalInstance(global), initial)

        return global
    }

    override fun table(
        type: TableType,
        initial: Any?,
    ): Table {

        live()

        val table = Table(this, externalValue(classicTable(classic, type, NULL_REFERENCE)))
        val instance = tableInstance(table)

        objects.tables.fillTable(instance, 0, initial, objects.tables.sizeTable(instance))

        return table
    }

    override fun tag(type: TagType): Tag {

        live()

        return Tag(this, externalValue(classicTag(classic, type)))
    }

    override suspend fun instance(
        module: Module,
        imports: List<Import>,
        config: RuntimeConfig,
    ): ChasmResult<Instance, InstantiationError<ExceptionReference>> {

        live()

        val classicImports = imports.map { import ->
            require(import.value.engine === this) { "the import ${import.moduleName}.${import.entityName} belongs to a different store" }
            ClassicImport(import.moduleName, import.entityName, importable(classic, import.value.payload as ExternalValue))
        }

        return when (val result = instantiator.instantiate(module.payload as ClassicModule, classicImports, config)) {
            is ChasmResult.Success -> ChasmResult.Success(instanceOf(result.result.instance))
            is ChasmResult.Error -> result
        }
    }

    override fun exports(instance: Instance): Exports {

        val jit = jit(instance)

        live()
        check(!jit.module.deallocated) { "the instance has been dropped" }

        val cached = jit.exports
        if (cached != null) {
            return cached
        }

        val exports = Exports(jit.module.exports.map { export -> Export(export.name.name, importable(export.value)) })
        jit.exports = exports

        return exports
    }

    override fun prepareFunction(
        instance: Instance,
        name: String,
    ): ChasmResult<PreparedFunction, ExecutionError.Rejected> {

        val jit = jit(instance)
        if (storeDropped(classic) || jit.module.deallocated) {
            return ChasmResult.Error(ExecutionError.Rejected("the instance or its store has been dropped"))
        }

        val cached = jit.prepared[name]
        if (cached != null) {
            return ChasmResult.Success(cached)
        }

        return when (val prepared = ObjectCall.prepare(classic, jit.embedding, name, bridge)) {
            is ChasmResult.Success -> {
                val function = PreparedFunction(prepared.result.type, this, prepared.result)
                ChasmResult.Success(jit.prepared.putIfAbsent(name, function) ?: function)
            }
            is ChasmResult.Error -> prepared
        }
    }

    override fun invoke(
        function: PreparedFunction,
        args: List<ExecutionValue>,
    ): ChasmResult<List<ExecutionValue>, InvocationError<ExceptionReference>> {

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
    ): ChasmResult<Unit, InvocationError<ExceptionReference>> {
        return call(function).call(words, references)
    }

    override fun unsafeInvokeUnboxed(
        function: PreparedFunction,
        words: LongArray,
        references: Array<Any?>,
    ): ChasmResult<Unit, InvocationError<ExceptionReference>> {
        return call(function).unsafeCall(words, references)
    }

    override fun sizeMemory(memory: Memory): Int {
        return memoryInstance(memory).type.limits.min.toInt()
    }

    override fun memoryByteSize(memory: Memory): Int {
        return linear(memory).byteSize
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
        return objects.globals.readGlobal(globalInstance(global))
    }

    override fun readGlobalReference(global: Global): Any? {
        return objects.globals.readGlobalReference(globalInstance(global))
    }

    override fun writeGlobal(
        global: Global,
        value: Long,
    ) {
        objects.globals.writeGlobal(globalInstance(global), value)
    }

    override fun writeGlobalReference(
        global: Global,
        value: Any?,
    ) {
        objects.globals.writeGlobalReference(globalInstance(global), value)
    }

    override fun unsafeWriteGlobalReference(
        global: Global,
        value: Any?,
    ) {
        objects.globals.unsafeWriteGlobalReference(globalInstance(global), value)
    }

    override fun sizeTable(table: Table): Int {
        return objects.tables.sizeTable(tableInstance(table))
    }

    override fun growTable(
        table: Table,
        elementsToAdd: Int,
        initial: Any?,
    ): Int {
        return objects.tables.growTable(tableInstance(table), elementsToAdd, initial)
    }

    override fun readTable(
        table: Table,
        index: Int,
    ): Any? {
        return objects.tables.readTable(tableInstance(table), index)
    }

    override fun writeTable(
        table: Table,
        index: Int,
        value: Any?,
    ) {
        objects.tables.writeTable(tableInstance(table), index, value)
    }

    override fun readTable(
        table: Table,
        buffer: Array<Any?>,
        elementIndex: Int,
        elementsToRead: Int,
        bufferIndex: Int,
    ): Array<Any?> {
        return objects.tables.readTable(tableInstance(table), buffer, elementIndex, elementsToRead, bufferIndex)
    }

    override fun writeTable(
        table: Table,
        elementIndex: Int,
        buffer: Array<Any?>,
        bufferIndex: Int,
        elementsToWrite: Int,
    ) {
        objects.tables.writeTable(tableInstance(table), elementIndex, buffer, bufferIndex, elementsToWrite)
    }

    override fun fillTable(
        table: Table,
        elementIndex: Int,
        value: Any?,
        elementsToFill: Int,
    ) {
        objects.tables.fillTable(tableInstance(table), elementIndex, value, elementsToFill)
    }

    override fun copyTable(
        source: Table,
        sourceElementIndex: Int,
        destination: Table,
        destinationElementIndex: Int,
        elementsToCopy: Int,
    ) {
        objects.tables.copyTable(tableInstance(source), sourceElementIndex, tableInstance(destination), destinationElementIndex, elementsToCopy)
    }

    override fun unsafeWriteTable(
        table: Table,
        index: Int,
        value: Any?,
    ) {
        objects.tables.unsafeWriteTable(tableInstance(table), index, value)
    }

    override fun unsafeBorrowElements(table: Table): Array<Any?> {
        return objects.tables.borrowElements(tableInstance(table))
    }

    override fun structType(struct: StructReference): HostGcType {
        return HostGcType(objects.structs.structType(struct).id)
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
        return HostGcType(objects.arrays.arrayType(array).id)
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
        return HostGcType(runtime.heap.runtimeType(jit(instance).module, typeIndex).id)
    }

    override fun structFieldCount(type: HostGcType): Int {
        return runtime.heap.structFieldCount(RuntimeGcType(type.id))
    }

    override fun structFieldInfo(
        type: HostGcType,
        fieldIndex: Int,
    ): HostGcFieldInfo {
        return HostGcFieldInfo(runtime.heap.structFieldInfo(RuntimeGcType(type.id), fieldIndex).encoding)
    }

    override fun arrayElementInfo(type: HostGcType): HostGcFieldInfo {
        return HostGcFieldInfo(runtime.heap.arrayElementInfo(RuntimeGcType(type.id)).encoding)
    }

    override fun isSubtype(
        actual: HostGcType,
        expected: HostGcType,
    ): Boolean {
        return runtime.heap.isSubtype(RuntimeGcType(actual.id), RuntimeGcType(expected.id))
    }

    override fun allocateStruct(
        type: HostGcType,
        fields: LongArray,
        references: Array<Any?>,
    ): StructReference {
        return objects.structs.allocateStruct(RuntimeGcType(type.id), fields, references)
    }

    override fun unsafeAllocateStruct(
        type: HostGcType,
        fields: LongArray,
        references: Array<Any?>,
    ): StructReference {
        return objects.structs.unsafeAllocateStruct(RuntimeGcType(type.id), fields, references)
    }

    override fun allocateArray(
        type: HostGcType,
        length: Int,
        initialValue: Long,
    ): ArrayReference {
        return objects.arrays.allocateArray(RuntimeGcType(type.id), length, initialValue)
    }

    override fun allocateArray(
        type: HostGcType,
        length: Int,
        initialReference: Any?,
    ): ArrayReference {
        return objects.arrays.allocateArray(RuntimeGcType(type.id), length, initialReference)
    }

    override fun allocateArray(
        type: HostGcType,
        elements: LongArray,
        elementOffset: Int,
        elementCount: Int,
    ): ArrayReference {
        return objects.arrays.allocateArray(RuntimeGcType(type.id), elements, elementOffset, elementCount)
    }

    override fun allocateArray(
        type: HostGcType,
        elements: Array<Any?>,
        elementOffset: Int,
        elementCount: Int,
    ): ArrayReference {
        return objects.arrays.allocateArray(RuntimeGcType(type.id), elements, elementOffset, elementCount)
    }

    override fun unsafeAllocateArray(
        type: HostGcType,
        storage: Any,
    ): ArrayReference {
        return objects.arrays.unsafeAllocateArray(RuntimeGcType(type.id), storage)
    }

    override fun createException(
        tag: Tag,
        words: LongArray,
        references: Array<Any?>,
    ): ExceptionReference {
        return objects.exceptions.createException(tagAddress(tag), words, references)
    }

    override fun exceptionTag(exception: ExceptionReference): Tag {
        return Tag(this, ExternalValue.Tag(objects.exceptions.exceptionTag(exception)))
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

        check(runtime.heap.hasPending) { "no exception is pending" }

        throw HostRaisedWasmException.instance
    }

    override fun takePending(): ExceptionReference {

        check(runtime.heap.hasPending) { "no exception is pending" }

        return bridge.take()
    }

    override fun hasPending(): Boolean {
        return runtime.heap.hasPending
    }

    override fun installed(
        module: ModuleInstance,
        embedding: ClassicInstance,
    ): Instance {

        val instance = Instance(this, JitInstance(module, embedding, this))
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
        return Memory(this, ExternalValue.Memory(address))
    }

    override fun tableAt(address: Address.Table): Table {
        return Table(this, ExternalValue.Table(address))
    }

    override fun globalAt(address: Address.Global): Global {
        return Global(this, ExternalValue.Global(address))
    }

    override fun tagAt(address: Address.Tag): Tag {
        return Tag(this, ExternalValue.Tag(address))
    }

    private fun importable(value: ExternalValue): Importable {
        return when (value) {
            is ExternalValue.Function -> Function(this, value)
            is ExternalValue.Memory -> Memory(this, value)
            is ExternalValue.Global -> Global(this, value)
            is ExternalValue.Table -> Table(this, value)
            is ExternalValue.Tag -> Tag(this, value)
        }
    }

    private fun jit(instance: Instance): JitInstance {

        require(instance.engine === this) { "the instance belongs to a different store" }

        return instance.payload as JitInstance
    }

    private fun call(function: PreparedFunction): ObjectCall {

        require(function.engine === this) { "the function belongs to a different store" }

        return function.payload as ObjectCall
    }

    private fun memoryInstance(memory: Memory): MemoryInstance {

        require(memory.engine === this) { "the memory belongs to a different store" }
        live()

        return runtime.memories[(memory.payload as ExternalValue.Memory).address.address]
    }

    private fun linear(memory: Memory): HostMemory {
        return memoryInstance(memory).data
    }

    private fun tableInstance(table: Table): TableInstance {

        require(table.engine === this) { "the table belongs to a different store" }
        live()

        return runtime.tables[(table.payload as ExternalValue.Table).address.address]
    }

    private fun globalInstance(global: Global): GlobalInstance {

        require(global.engine === this) { "the global belongs to a different store" }
        live()

        return runtime.globals[(global.payload as ExternalValue.Global).address.address]
    }

    private fun tagAddress(tag: Tag): Address.Tag {

        require(tag.engine === this) { "the tag belongs to a different store" }

        return (tag.payload as ExternalValue.Tag).address
    }

    private fun live() {
        check(!storeDropped(classic)) { "the store has been dropped" }
    }

    private companion object {
        val NULL_REFERENCE = ReferenceValue.Null(AbstractHeapType.None)
    }
}

private fun <T> ChasmResult<T, *>.orThrow(): T {
    return when (this) {
        is ChasmResult.Success -> result
        is ChasmResult.Error -> throw IllegalStateException(error.toString())
    }
}
