package io.github.charlietap.chasm.embedding.engine

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.error.InvocationError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Exports
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.PreparedFunction
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostGcFieldInfo
import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.reference.FunctionReference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType

@InternalChasmApi
interface Engine {

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

    fun functionReference(function: Function): FunctionReference

    suspend fun instance(
        module: Module,
        imports: List<Import>,
        config: RuntimeConfig,
    ): ChasmResult<Instance, InstantiationError>

    fun exports(instance: Instance): Exports

    fun prepareFunction(
        instance: Instance,
        name: String,
    ): ChasmResult<PreparedFunction, ExecutionError.Rejected>

    fun invoke(
        function: PreparedFunction,
        args: List<ExecutionValue>,
    ): ChasmResult<List<ExecutionValue>, InvocationError>

    fun invokeUnboxed(
        function: PreparedFunction,
        words: LongArray,
        references: Array<Any?>,
    ): ChasmResult<Unit, InvocationError>

    fun unsafeInvokeUnboxed(
        function: PreparedFunction,
        words: LongArray,
        references: Array<Any?>,
    ): ChasmResult<Unit, InvocationError>

    fun sizeMemory(memory: Memory): Int

    fun memoryByteSize(memory: Memory): Int

    fun growMemory(
        memory: Memory,
        pagesToAdd: Int,
    ): Int

    fun unsafeBorrowMemory(memory: Memory): Any?

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

    fun readTableElements(
        table: Table,
        buffer: Array<Any?>,
        elementIndex: Int,
        elementsToRead: Int,
        bufferIndex: Int,
    ): Array<Any?>

    fun writeTableElements(
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

    fun allocateNumericArray(
        type: HostGcType,
        length: Int,
        initialValue: Long,
    ): ArrayReference

    fun allocateReferenceArray(
        type: HostGcType,
        length: Int,
        initialReference: Any?,
    ): ArrayReference

    fun allocateNumericArray(
        type: HostGcType,
        elements: LongArray,
        elementOffset: Int,
        elementCount: Int,
    ): ArrayReference

    fun allocateReferenceArray(
        type: HostGcType,
        elements: Array<Any?>,
        elementOffset: Int,
        elementCount: Int,
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
