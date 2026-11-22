package io.github.charlietap.chasm.embedding.internal

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.LinearMemoryConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.engine.Engine
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.error.InvocationError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Export
import io.github.charlietap.chasm.embedding.shapes.Exports
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.PreparedFunction
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.embedding.shapes._exports
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.embedding.shapes._preparedFunction
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.embedding.value.VectorValue
import io.github.charlietap.chasm.executor.invoker.RawFunctionInvoker
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostGcFieldInfo
import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.reference.FunctionReference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.host.HostMemory
import io.github.charlietap.chasm.runtime.host.readNullTerminatedUtf8String
import io.github.charlietap.chasm.runtime.host.readUtf8String
import io.github.charlietap.chasm.runtime.host.withScope
import io.github.charlietap.chasm.runtime.host.writeUtf8String
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.resource.gc.GarbageCollector
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.runtime.error.InvocationError as RuntimeInvocationError

@InternalChasmApi
class InterpreterEngine internal constructor(
    private val storeRuntime: StoreRuntime,
    private val installer: ModuleInstaller,
    private val binding: EngineBinding,
    private val heap: WasmHeap,
    private val heldReferences: HeldReferences,
    private val codec: ReferenceCodec,
    private val handles: EngineHandles,
    private val values: ValueBoxing,
    private val hostCalls: HostCallAdapters,
    private val globals: GlobalAccess,
    private val tables: TableAccess,
    private val structs: StructAccess,
    private val arrays: ArrayAccess,
    private val exceptions: ExceptionAccess,
) : Engine {

    val store: Store
        get() = binding.store

    override fun dropStore() {

        storeRuntime.liveStore()

        heldReferences.clear()
        storeRuntime.drop()
    }

    override fun dropInstance(instance: Instance) {

        val interpreterInstance = handles.interpreterInstance(instance)

        storeRuntime.liveStore()
        check(!interpreterInstance.module.deallocated) { "the instance has already been dropped" }

        storeRuntime.dropInstance(interpreterInstance.module)
        interpreterInstance.prepared.clear()
        interpreterInstance.exports = null
    }

    override fun addFuel(fuel: Long) {
        storeRuntime.addFuel(fuel)
    }

    override fun resetFuel() {
        storeRuntime.resetFuel()
    }

    override fun remainingFuel(): Long {
        return storeRuntime.remainingFuel()
    }

    override fun interrupt(): Boolean {

        storeRuntime.liveStore()

        return storeRuntime.interrupt()
    }

    override fun collect() {
        GarbageCollector(storeRuntime.liveStore()).getOrElse(::throwAccessorFailure)
    }

    override fun function(
        type: FunctionType,
        function: HostFunction,
    ): Function {

        storeRuntime.liveStore()

        return handles.function(storeRuntime.function(type, hostCalls.adapt(type, function)).address)
    }

    override fun memory(type: MemoryType): Memory {
        return memory(type, LinearMemoryConfig())
    }

    fun memory(
        type: MemoryType,
        config: LinearMemoryConfig,
    ): Memory {
        return handles.memory(storeRuntime.memory(type, config).address)
    }

    override fun global(
        type: GlobalType,
        value: ExecutionValue,
    ): Global {

        storeRuntime.liveStore()
        require(matchesType(type.valueType, value)) { "the value does not match the global's type" }

        return withAccessorFailures {
            heap.withScope {
                handles.global(storeRuntime.global(type, word(type.valueType, value)).address)
            }
        }
    }

    override fun table(
        type: TableType,
        initial: Any?,
    ): Table {

        storeRuntime.liveStore()

        return withAccessorFailures {
            heap.withScope {
                handles.table(storeRuntime.table(type, codec.encode(codec.resolve(type.referenceType), initial)).address)
            }
        }
    }

    override fun tag(type: TagType): Tag {
        return handles.tag(storeRuntime.tag(type).address)
    }

    override fun functionReference(function: Function): FunctionReference {

        val address = handles.functionAddress(function)
        storeRuntime.liveStore()

        return InterpreterFunctionReference(binding, address.address)
    }

    override suspend fun instance(
        module: Module,
        imports: List<Import>,
        config: RuntimeConfig,
    ): ChasmResult<Instance, InstantiationError> {

        val runtime = storeRuntime.liveStore()
        installer.rejection(module, runtime.config)?.let { reason ->
            return ChasmResult.Error(ExecutionError.Unsupported(reason))
        }

        val result = storeRuntime.instantiate(
            engine = this,
            imports = imports,
            config = installer.config(module, config),
            installation = InterpreterModuleInstallation(module, installer, handles, heldReferences, heap),
        )

        return when (result) {
            is ChasmResult.Success -> ChasmResult.Success(handles.context(result.result.instance, result.result.config).instance)
            is ChasmResult.Error -> result
        }
    }

    override fun exports(instance: Instance): Exports {

        val interpreterInstance = handles.interpreterInstance(instance)

        storeRuntime.liveStore()
        check(!interpreterInstance.module.deallocated) { "the instance has been dropped" }

        val cached = interpreterInstance.exports
        if (cached != null) {
            return cached
        }

        val exports = _exports(
            interpreterInstance.module.exports.map { export -> Export(export.name.name, handles.importable(export.value)) },
        )
        interpreterInstance.exports = exports

        return exports
    }

    override fun prepareFunction(
        instance: Instance,
        name: String,
    ): ChasmResult<PreparedFunction, ExecutionError.Rejected> {

        val interpreterInstance = handles.interpreterInstance(instance)
        val rejection = rejection(interpreterInstance)
        if (rejection != null) {
            return ChasmResult.Error(ExecutionError.Rejected(rejection))
        }

        val cached = interpreterInstance.prepared[name]
        if (cached != null) {
            return ChasmResult.Success(cached)
        }

        val export = interpreterInstance.module.exports.firstOrNull { export -> export.name.name == name }
        val address = (export?.value as? ExternalValue.Function)?.address
            ?: return ChasmResult.Error(ExecutionError.Rejected(RuntimeInvocationError.FunctionNotFound(name).toString()))

        val function = storeRuntime.liveStore().functions[address.address]
        val type = function.functionType
        if (usesVector(type)) {
            return ChasmResult.Error(ExecutionError.Rejected("$name uses v128, which cannot cross the interpreter's embedding boundary"))
        }

        val call = PreparedCall(
            owner = this,
            instance = interpreterInstance,
            function = function,
            parameterSlots = codec.slots(type.params.types),
            resultSlots = codec.slots(type.results.types),
        )
        val prepared = _preparedFunction(type, this, call)
        interpreterInstance.prepared[name] = prepared

        return ChasmResult.Success(prepared)
    }

    override fun invoke(
        function: PreparedFunction,
        args: List<ExecutionValue>,
    ): ChasmResult<List<ExecutionValue>, InvocationError> {

        val mismatch = argumentMismatch(function.type, args)
        if (mismatch != null) {
            return ChasmResult.Error(ExecutionError.Rejected(mismatch))
        }

        val call = preparedCall(function)
        val words = LongArray(function.slots)
        val references = if (call.usesReferenceLane) {
            arrayOfNulls<Any>(function.slots)
        } else {
            NO_REFERENCES
        }
        val rejection = values.lower(args, words, references)
        if (rejection != null) {
            return ChasmResult.Error(ExecutionError.Rejected(rejection))
        }

        return when (val result = invokeUnboxed(function, words, references)) {
            is ChasmResult.Success -> ChasmResult.Success(values.raise(function.type.results.types, words, references))
            is ChasmResult.Error -> result
        }
    }

    override fun invokeUnboxed(
        function: PreparedFunction,
        words: LongArray,
        references: Array<Any?>,
    ): ChasmResult<Unit, InvocationError> {

        val call = preparedCall(function)
        val rejection = rejection(call, function.slots, words, references)
        if (rejection != null) {
            return ChasmResult.Error(ExecutionError.Rejected(rejection))
        }

        if (call.parameterSlots.isEmpty) {
            return execute(call, words, references)
        }

        val marker = heap.beginScope(0)
        try {

            val failure = encodeArguments(call, words, references)
            if (failure != null) {
                return failure
            }

            return execute(call, words, references)
        } finally {
            heap.endScope(marker)
        }
    }

    override fun unsafeInvokeUnboxed(
        function: PreparedFunction,
        words: LongArray,
        references: Array<Any?>,
    ): ChasmResult<Unit, InvocationError> {
        return invokeUnboxed(function, words, references)
    }

    override fun sizeMemory(memory: Memory): Int {
        return handles.memoryInstance(memory).type.limits.min.toInt()
    }

    override fun memoryByteSize(memory: Memory): Int {
        return linear(memory).byteSize
    }

    @OptIn(UnsafeHostApi::class)
    override fun unsafeBorrowMemory(memory: Memory): Any? {
        return handles.memoryInstance(memory).unsafeBorrowStorage()
    }

    override fun growMemory(
        memory: Memory,
        pagesToAdd: Int,
    ): Int {
        return handles.memoryInstance(memory).grow(pagesToAdd)
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
        return globals.readGlobal(global)
    }

    override fun readGlobalReference(global: Global): Any? {
        return globals.readGlobalReference(global)
    }

    override fun writeGlobal(
        global: Global,
        value: Long,
    ) {
        globals.writeGlobal(global, value)
    }

    override fun writeGlobalReference(
        global: Global,
        value: Any?,
    ) {
        globals.writeGlobalReference(global, value)
    }

    override fun unsafeWriteGlobalReference(
        global: Global,
        value: Any?,
    ) {
        globals.writeGlobalReference(global, value)
    }

    override fun sizeTable(table: Table): Int {
        return tables.sizeTable(table)
    }

    override fun growTable(
        table: Table,
        elementsToAdd: Int,
        initial: Any?,
    ): Int {
        return tables.growTable(table, elementsToAdd, initial)
    }

    override fun readTable(
        table: Table,
        index: Int,
    ): Any? {
        return tables.readTable(table, index)
    }

    override fun writeTable(
        table: Table,
        index: Int,
        value: Any?,
    ) {
        tables.writeTable(table, index, value)
    }

    override fun readTableElements(
        table: Table,
        buffer: Array<Any?>,
        elementIndex: Int,
        elementsToRead: Int,
        bufferIndex: Int,
    ): Array<Any?> {
        return tables.readTableElements(table, buffer, elementIndex, elementsToRead, bufferIndex)
    }

    override fun writeTableElements(
        table: Table,
        elementIndex: Int,
        buffer: Array<Any?>,
        bufferIndex: Int,
        elementsToWrite: Int,
    ) {
        tables.writeTableElements(table, elementIndex, buffer, bufferIndex, elementsToWrite)
    }

    override fun fillTable(
        table: Table,
        elementIndex: Int,
        value: Any?,
        elementsToFill: Int,
    ) {
        tables.fillTable(table, elementIndex, value, elementsToFill)
    }

    override fun copyTable(
        source: Table,
        sourceElementIndex: Int,
        destination: Table,
        destinationElementIndex: Int,
        elementsToCopy: Int,
    ) {
        tables.copyTable(source, sourceElementIndex, destination, destinationElementIndex, elementsToCopy)
    }

    override fun unsafeWriteTable(
        table: Table,
        index: Int,
        value: Any?,
    ) {
        tables.writeTable(table, index, value)
    }

    override fun structType(struct: StructReference): HostGcType {
        return structs.structType(struct)
    }

    override fun readStructField(
        struct: StructReference,
        index: Int,
    ): Long {
        return structs.readStructField(struct, index)
    }

    override fun readStructReference(
        struct: StructReference,
        index: Int,
    ): Any? {
        return structs.readStructReference(struct, index)
    }

    override fun writeStructField(
        struct: StructReference,
        index: Int,
        value: Long,
    ) {
        structs.writeStructField(struct, index, value)
    }

    override fun writeStructReference(
        struct: StructReference,
        index: Int,
        value: Any?,
    ) {
        structs.writeStructReference(struct, index, value)
    }

    override fun unsafeWriteStructReference(
        struct: StructReference,
        index: Int,
        value: Any?,
    ) {
        structs.writeStructReference(struct, index, value)
    }

    override fun arrayType(array: ArrayReference): HostGcType {
        return arrays.arrayType(array)
    }

    override fun arrayLength(array: ArrayReference): Int {
        return arrays.arrayLength(array)
    }

    override fun readArrayElement(
        array: ArrayReference,
        index: Int,
    ): Long {
        return arrays.readArrayElement(array, index)
    }

    override fun readArrayReference(
        array: ArrayReference,
        index: Int,
    ): Any? {
        return arrays.readArrayReference(array, index)
    }

    override fun writeArrayElement(
        array: ArrayReference,
        index: Int,
        value: Long,
    ) {
        arrays.writeArrayElement(array, index, value)
    }

    override fun writeArrayReference(
        array: ArrayReference,
        index: Int,
        value: Any?,
    ) {
        arrays.writeArrayReference(array, index, value)
    }

    override fun unsafeWriteArrayReference(
        array: ArrayReference,
        index: Int,
        value: Any?,
    ) {
        arrays.writeArrayReference(array, index, value)
    }

    override fun readArrayElements(
        array: ArrayReference,
        offset: Int,
        destination: LongArray,
        destinationOffset: Int,
        count: Int,
    ) {
        arrays.readArrayElements(array, offset, destination, destinationOffset, count)
    }

    override fun writeArrayElements(
        array: ArrayReference,
        offset: Int,
        source: LongArray,
        sourceOffset: Int,
        count: Int,
    ) {
        arrays.writeArrayElements(array, offset, source, sourceOffset, count)
    }

    override fun fillArray(
        array: ArrayReference,
        offset: Int,
        count: Int,
        value: Long,
    ) {
        arrays.fillArray(array, offset, count, value)
    }

    override fun copyArray(
        source: ArrayReference,
        sourceOffset: Int,
        destination: ArrayReference,
        destinationOffset: Int,
        count: Int,
    ) {
        arrays.copyArray(source, sourceOffset, destination, destinationOffset, count)
    }

    override fun runtimeType(
        instance: Instance,
        typeIndex: Int,
    ): HostGcType {

        val interpreterInstance = handles.interpreterInstance(instance)
        storeRuntime.liveStore()

        return heap.runtimeType(interpreterInstance.module, typeIndex)
    }

    override fun structFieldCount(type: HostGcType): Int {

        storeRuntime.liveStore()
        require(heap.isStructType(type)) { "$type is not a struct type" }

        return heap.structFieldCount(type)
    }

    override fun structFieldInfo(
        type: HostGcType,
        fieldIndex: Int,
    ): HostGcFieldInfo {

        storeRuntime.liveStore()
        require(heap.isStructType(type)) { "$type is not a struct type" }

        return heap.structFieldInfo(type, fieldIndex)
    }

    override fun arrayElementInfo(type: HostGcType): HostGcFieldInfo {

        storeRuntime.liveStore()
        require(heap.isArrayType(type)) { "$type is not an array type" }

        return heap.arrayElementInfo(type)
    }

    override fun isSubtype(
        actual: HostGcType,
        expected: HostGcType,
    ): Boolean {

        storeRuntime.liveStore()

        return heap.isSubtype(actual, expected)
    }

    override fun allocateStruct(
        type: HostGcType,
        fields: LongArray,
        references: Array<Any?>,
    ): StructReference {

        storeRuntime.liveStore()

        return structs.allocateStruct(type, fields, references)
    }

    override fun unsafeAllocateStruct(
        type: HostGcType,
        fields: LongArray,
        references: Array<Any?>,
    ): StructReference {
        return allocateStruct(type, fields, references)
    }

    override fun allocateNumericArray(
        type: HostGcType,
        length: Int,
        initialValue: Long,
    ): ArrayReference {

        storeRuntime.liveStore()

        return arrays.allocateNumericArray(type, length, initialValue)
    }

    override fun allocateReferenceArray(
        type: HostGcType,
        length: Int,
        initialReference: Any?,
    ): ArrayReference {

        storeRuntime.liveStore()

        return arrays.allocateReferenceArray(type, length, initialReference)
    }

    override fun allocateNumericArray(
        type: HostGcType,
        elements: LongArray,
        elementOffset: Int,
        elementCount: Int,
    ): ArrayReference {

        storeRuntime.liveStore()

        return arrays.allocateNumericArray(type, elements, elementOffset, elementCount)
    }

    override fun allocateReferenceArray(
        type: HostGcType,
        elements: Array<Any?>,
        elementOffset: Int,
        elementCount: Int,
    ): ArrayReference {

        storeRuntime.liveStore()

        return arrays.allocateReferenceArray(type, elements, elementOffset, elementCount)
    }

    override fun createException(
        tag: Tag,
        words: LongArray,
        references: Array<Any?>,
    ): ExceptionReference {
        return exceptions.createException(tag, words, references)
    }

    override fun exceptionTag(exception: ExceptionReference): Tag {
        return exceptions.exceptionTag(exception)
    }

    override fun payloadSize(exception: ExceptionReference): Int {
        return exceptions.payloadSize(exception)
    }

    override fun readPayload(
        exception: ExceptionReference,
        index: Int,
    ): Long {
        return exceptions.readPayload(exception, index)
    }

    override fun readPayloadReference(
        exception: ExceptionReference,
        index: Int,
    ): Any? {
        return exceptions.readPayloadReference(exception, index)
    }

    override fun raise(exception: ExceptionReference): Nothing {
        exceptions.raise(exception)
    }

    override fun raisePending(): Nothing {

        storeRuntime.liveStore()

        exceptions.raisePending()
    }

    override fun takePending(): ExceptionReference {

        storeRuntime.liveStore()

        return exceptions.takePending()
    }

    override fun hasPending(): Boolean {

        storeRuntime.liveStore()

        return exceptions.hasPending()
    }

    private fun execute(
        call: PreparedCall,
        words: LongArray,
        references: Array<Any?>,
    ): ChasmResult<Unit, InvocationError> {

        val instance = call.instance
        val result = try {
            RawFunctionInvoker(instance.config, storeRuntime.liveStore(), instance.module, call.function, words)
        } catch (exception: InvocationException) {
            Err(exception.error)
        }

        return result.fold(
            success = { _ ->
                decodeResults(call, words, references)
                SUCCESS
            },
            failure = { error ->
                ChasmResult.Error(storeRuntime.invocationError(error) { pendingException() })
            },
        )
    }

    private fun encodeArguments(
        call: PreparedCall,
        words: LongArray,
        references: Array<Any?>,
    ): ChasmResult<Unit, InvocationError>? {

        val valueIndices = call.parameterSlots.valueIndices
        val types = call.parameterSlots.types

        for (position in valueIndices.indices) {

            val index = valueIndices[position]

            try {
                words[index] = codec.encode(types[position], references[index])
            } catch (exception: IllegalArgumentException) {
                return ChasmResult.Error(ExecutionError.Rejected("argument $index: ${exception.message}"))
            } catch (exception: InvocationException) {
                return ChasmResult.Error(storeRuntime.invocationError(exception.error) { pendingException() })
            }
        }

        return null
    }

    private fun decodeResults(
        call: PreparedCall,
        words: LongArray,
        references: Array<Any?>,
    ) {
        for (index in call.resultSlots.valueIndices) {
            references[index] = codec.decode(words[index])
        }
    }

    private fun preparedCall(function: PreparedFunction): PreparedCall {

        val call = function._payload() as? PreparedCall
        require(call != null && call.owner === this) { "the function belongs to a different store" }

        return call
    }

    private fun rejection(instance: InterpreterInstance): String? {
        return when {
            storeRuntime.dropped -> RuntimeInvocationError.StoreDropped.toString()
            instance.module.deallocated -> RuntimeInvocationError.InvocationOfADeinstantiatedInstance.toString()
            else -> null
        }
    }

    private fun rejection(
        call: PreparedCall,
        slots: Int,
        words: LongArray,
        references: Array<Any?>,
    ): String? {

        val lifetime = rejection(call.instance)
        if (lifetime != null) {
            return lifetime
        }

        return when {
            words.size < slots -> "the word lane holds ${words.size} slots but the function needs $slots"
            call.usesReferenceLane && references.size < slots -> "the reference lane holds ${references.size} slots but the function needs $slots"
            else -> null
        }
    }

    private fun word(
        type: ValueType,
        value: ExecutionValue,
    ): Long {
        return when (value) {
            is NumberValue<*> -> values.word(value)
            is ReferenceValue -> codec.encode(codec.resolve(requireNotNull(referenceType(type))), values.reference(value))
            is VectorValue -> throw IllegalArgumentException("a v128 global cannot be created from the embedding")
        }
    }

    private fun pendingException(): ExceptionReference {
        return heldReferences.exception(heap.takePendingExceptionReference())
    }

    private fun linear(memory: Memory): HostMemory {
        return handles.memoryInstance(memory).data
    }

    private companion object {
        val SUCCESS: ChasmResult<Unit, Nothing> = ChasmResult.Success(Unit)
        val NO_REFERENCES = arrayOfNulls<Any>(0)
    }
}
