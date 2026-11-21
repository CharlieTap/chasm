package io.github.charlietap.chasm.embedding.compiled

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.compiled.host.HostFunction
import io.github.charlietap.chasm.embedding.compiled.value.ExecutionValue
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.error.InvocationError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType

@InternalChasmApi
interface CompiledEngine {

    fun dropStore()

    fun dropInstance(instance: Instance)

    fun addFuel(fuel: Long)

    fun resetFuel()

    fun remainingFuel(): Long

    fun interrupt(): Boolean

    fun collect()

    fun function(
        type: FunctionType,
        function: HostFunction,
    ): Function

    fun memory(type: MemoryType): Memory

    fun global(
        type: GlobalType,
        value: ExecutionValue,
    ): Global

    fun table(
        type: TableType,
        initial: Any?,
    ): Table

    fun tag(type: TagType): Tag

    suspend fun instance(
        module: Module,
        imports: List<Import>,
        config: RuntimeConfig,
    ): ChasmResult<Instance, InstantiationError<ExceptionReference>>

    fun exports(instance: Instance): Exports

    fun prepareFunction(
        instance: Instance,
        name: String,
    ): ChasmResult<PreparedFunction, ExecutionError.Rejected>

    fun invoke(
        function: PreparedFunction,
        args: List<ExecutionValue>,
    ): ChasmResult<List<ExecutionValue>, InvocationError<ExceptionReference>>

    fun invokeUnboxed(
        function: PreparedFunction,
        words: LongArray,
        references: Array<Any?>,
    ): ChasmResult<Unit, InvocationError<ExceptionReference>>

    fun unsafeInvokeUnboxed(
        function: PreparedFunction,
        words: LongArray,
        references: Array<Any?>,
    ): ChasmResult<Unit, InvocationError<ExceptionReference>>

    fun sizeMemory(memory: Memory): Int

    fun memoryByteSize(memory: Memory): Int

    fun growMemory(
        memory: Memory,
        pagesToAdd: Int,
    ): Int

    fun readByte(
        memory: Memory,
        pointer: Int,
    ): Byte

    fun readShort(
        memory: Memory,
        pointer: Int,
    ): Short

    fun readInt(
        memory: Memory,
        pointer: Int,
    ): Int

    fun readLong(
        memory: Memory,
        pointer: Int,
    ): Long

    fun readFloat(
        memory: Memory,
        pointer: Int,
    ): Float

    fun readDouble(
        memory: Memory,
        pointer: Int,
    ): Double

    fun readBytes(
        memory: Memory,
        buffer: ByteArray,
        memoryPointer: Int,
        bytesToRead: Int,
        bufferPointer: Int,
    ): ByteArray

    fun readUtf8String(
        memory: Memory,
        pointer: Int,
        stringLengthInBytes: Int,
    ): String

    fun readNullTerminatedUtf8String(
        memory: Memory,
        pointer: Int,
    ): String

    fun writeByte(
        memory: Memory,
        pointer: Int,
        value: Byte,
    )

    fun writeShort(
        memory: Memory,
        pointer: Int,
        value: Short,
    )

    fun writeInt(
        memory: Memory,
        pointer: Int,
        value: Int,
    )

    fun writeLong(
        memory: Memory,
        pointer: Int,
        value: Long,
    )

    fun writeFloat(
        memory: Memory,
        pointer: Int,
        value: Float,
    )

    fun writeDouble(
        memory: Memory,
        pointer: Int,
        value: Double,
    )

    fun writeBytes(
        memory: Memory,
        pointer: Int,
        buffer: ByteArray,
        bufferPointer: Int,
        bytesToWrite: Int,
    )

    fun writeUtf8String(
        memory: Memory,
        pointer: Int,
        string: String,
    )

    fun fillMemory(
        memory: Memory,
        pointer: Int,
        value: Byte,
        bytesToFill: Int,
    )

    fun copyMemory(
        source: Memory,
        sourcePointer: Int,
        destination: Memory,
        destinationPointer: Int,
        bytesToCopy: Int,
    )

    fun moveMemory(
        source: Memory,
        sourcePointer: Int,
        destination: Memory,
        destinationPointer: Int,
        bytesToMove: Int,
    )

    fun readGlobal(global: Global): Long

    fun readGlobalReference(global: Global): Any?

    fun writeGlobal(
        global: Global,
        value: Long,
    )

    fun writeGlobalReference(
        global: Global,
        value: Any?,
    )

    fun unsafeWriteGlobalReference(
        global: Global,
        value: Any?,
    )

    fun sizeTable(table: Table): Int

    fun growTable(
        table: Table,
        elementsToAdd: Int,
        initial: Any?,
    ): Int

    fun readTable(
        table: Table,
        index: Int,
    ): Any?

    fun writeTable(
        table: Table,
        index: Int,
        value: Any?,
    )

    fun readTable(
        table: Table,
        buffer: Array<Any?>,
        elementIndex: Int,
        elementsToRead: Int,
        bufferIndex: Int,
    ): Array<Any?>

    fun writeTable(
        table: Table,
        elementIndex: Int,
        buffer: Array<Any?>,
        bufferIndex: Int,
        elementsToWrite: Int,
    )

    fun fillTable(
        table: Table,
        elementIndex: Int,
        value: Any?,
        elementsToFill: Int,
    )

    fun copyTable(
        source: Table,
        sourceElementIndex: Int,
        destination: Table,
        destinationElementIndex: Int,
        elementsToCopy: Int,
    )

    fun unsafeWriteTable(
        table: Table,
        index: Int,
        value: Any?,
    )

    fun unsafeBorrowElements(table: Table): Array<Any?>

    fun structType(struct: StructReference): HostGcType

    fun readStructField(
        struct: StructReference,
        index: Int,
    ): Long

    fun readStructReference(
        struct: StructReference,
        index: Int,
    ): Any?

    fun writeStructField(
        struct: StructReference,
        index: Int,
        value: Long,
    )

    fun writeStructReference(
        struct: StructReference,
        index: Int,
        value: Any?,
    )

    fun unsafeWriteStructReference(
        struct: StructReference,
        index: Int,
        value: Any?,
    )

    fun arrayType(array: ArrayReference): HostGcType

    fun arrayLength(array: ArrayReference): Int

    fun readArrayElement(
        array: ArrayReference,
        index: Int,
    ): Long

    fun readArrayReference(
        array: ArrayReference,
        index: Int,
    ): Any?

    fun writeArrayElement(
        array: ArrayReference,
        index: Int,
        value: Long,
    )

    fun writeArrayReference(
        array: ArrayReference,
        index: Int,
        value: Any?,
    )

    fun unsafeWriteArrayReference(
        array: ArrayReference,
        index: Int,
        value: Any?,
    )

    fun readArrayElements(
        array: ArrayReference,
        offset: Int,
        destination: LongArray,
        destinationOffset: Int,
        count: Int,
    )

    fun writeArrayElements(
        array: ArrayReference,
        offset: Int,
        source: LongArray,
        sourceOffset: Int,
        count: Int,
    )

    fun fillArray(
        array: ArrayReference,
        offset: Int,
        count: Int,
        value: Long,
    )

    fun copyArray(
        source: ArrayReference,
        sourceOffset: Int,
        destination: ArrayReference,
        destinationOffset: Int,
        count: Int,
    )

    fun <T : Any> unsafeBorrow(
        array: ArrayReference,
        storage: Class<T>,
    ): T

    fun runtimeType(
        instance: Instance,
        typeIndex: Int,
    ): HostGcType

    fun structFieldCount(type: HostGcType): Int

    fun structFieldInfo(
        type: HostGcType,
        fieldIndex: Int,
    ): HostGcFieldInfo

    fun arrayElementInfo(type: HostGcType): HostGcFieldInfo

    fun isSubtype(
        actual: HostGcType,
        expected: HostGcType,
    ): Boolean

    fun allocateStruct(
        type: HostGcType,
        fields: LongArray,
        references: Array<Any?>,
    ): StructReference

    fun unsafeAllocateStruct(
        type: HostGcType,
        fields: LongArray,
        references: Array<Any?>,
    ): StructReference

    fun allocateArray(
        type: HostGcType,
        length: Int,
        initialValue: Long,
    ): ArrayReference

    fun allocateArray(
        type: HostGcType,
        length: Int,
        initialReference: Any?,
    ): ArrayReference

    fun allocateArray(
        type: HostGcType,
        elements: LongArray,
        elementOffset: Int,
        elementCount: Int,
    ): ArrayReference

    fun allocateArray(
        type: HostGcType,
        elements: Array<Any?>,
        elementOffset: Int,
        elementCount: Int,
    ): ArrayReference

    fun unsafeAllocateArray(
        type: HostGcType,
        storage: Any,
    ): ArrayReference

    fun createException(
        tag: Tag,
        words: LongArray,
        references: Array<Any?>,
    ): ExceptionReference

    fun exceptionTag(exception: ExceptionReference): Tag

    fun payloadSize(exception: ExceptionReference): Int

    fun readPayload(
        exception: ExceptionReference,
        index: Int,
    ): Long

    fun readPayloadReference(
        exception: ExceptionReference,
        index: Int,
    ): Any?

    fun raise(exception: ExceptionReference): Nothing

    fun raisePending(): Nothing

    fun takePending(): ExceptionReference

    fun hasPending(): Boolean
}
