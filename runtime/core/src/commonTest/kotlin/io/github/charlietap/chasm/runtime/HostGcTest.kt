package io.github.charlietap.chasm.runtime

import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.GCThreshold
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.host.HostGc
import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class HostGcTest {

    @Test
    fun `host gc exposes direct aggregate access metadata and bulk transfer`() {
        val store = Store()
        val runtimeTypes = store.heap.registerRuntimeTypes(hostTypes())
        val gc: HostGc = store.heap
        val structReference = store.heap.allocateStruct(
            runtimeTypes[STRUCT_TYPE_INDEX],
            longArrayOf(0x1FF, 42, 117),
        )
        val arrayReference = store.heap.allocateArrayFromElements(
            runtimeTypes[NUMBER_ARRAY_TYPE_INDEX],
            LongArray(8) { it.toLong() },
            0,
            8,
        )
        val packedArrayReference = store.heap.allocateArrayFilled(
            runtimeTypes[PACKED_ARRAY_TYPE_INDEX],
            2,
            0x1FFFFL,
        )

        val structType = gc.structType(structReference)
        assertEquals(runtimeTypes[STRUCT_TYPE_INDEX].value, structType.id)
        assertEquals(3, gc.structFieldCount(structType))
        assertEquals(true, gc.structFieldInfo(structType, 0).isPackedI8)
        assertEquals(true, gc.structFieldInfo(structType, 0).mutable)
        assertEquals(true, gc.structFieldInfo(structType, 1).isReference)
        assertEquals(true, gc.structFieldInfo(structType, 2).isI64)
        assertEquals(42, gc.readStructField(structReference, 1))
        gc.writeStructField(structReference, 2, 118)
        assertEquals(118, gc.readStructField(structReference, 2))

        val arrayType = gc.arrayType(arrayReference)
        assertEquals(true, gc.arrayElementInfo(arrayType).isI64)
        assertEquals(8, gc.arrayLength(arrayReference))
        gc.fillArray(arrayReference, 1, 2, 9)
        gc.copyArray(arrayReference, 0, arrayReference, 2, 6)
        val destination = LongArray(12) { -1 }
        assertEquals(destination, gc.readArrayElements(arrayReference, 0, destination, 2, 8))
        assertContentEquals(longArrayOf(-1, -1, 0, 9, 0, 9, 9, 3, 4, 5, -1, -1), destination)

        gc.writeArrayElements(arrayReference, 2, longArrayOf(21, 22, 23, 24), 1, 2)
        assertEquals(22, gc.readArrayElement(arrayReference, 2))
        assertEquals(23, gc.readArrayElement(arrayReference, 3))

        val packedArrayType = gc.arrayType(packedArrayReference)
        assertEquals(true, gc.arrayElementInfo(packedArrayType).isPackedI16)
        gc.writeArrayElement(packedArrayReference, 1, 0x1FFL)
        assertEquals(0x1FFL, gc.readArrayElement(packedArrayReference, 1))

        val dedicated = store.heap.allocateArrayFilled(runtimeTypes[NUMBER_ARRAY_TYPE_INDEX], 1024, 0)
        val dedicatedBuffer = LongArray(4) { it + 31L }
        gc.writeArrayElements(dedicated, 1020, dedicatedBuffer, 0, dedicatedBuffer.size)
        assertContentEquals(dedicatedBuffer, gc.readArrayElements(dedicated, 1020, LongArray(4), 0, 4))
    }

    @Test
    fun `host gc collection includes additional and stack roots`() {
        val store = Store()
        val runtimeType = store.heap.registerRuntimeTypes(hostTypes())[EMPTY_STRUCT_TYPE_INDEX]
        val context = executionContext(store)
        val additional = store.heap.allocateStruct(runtimeType, longArrayOf())

        context(context) {
            context.gc.collect(longArrayOf(0L, additional, 0L), rootOffset = 1, rootCount = 1)
        }
        assertNotEquals(-1, store.heap.structRuntimeTypeIdOrNegative(additional))
        store.heap.collectGarbage(store)
        assertEquals(-1, store.heap.structRuntimeTypeIdOrNegative(additional))

        val stackReference = store.heap.allocateStruct(runtimeType, longArrayOf())
        context.vstack.push(stackReference)
        context(context) { context.gc.collect() }
        assertNotEquals(-1, store.heap.structRuntimeTypeIdOrNegative(stackReference))
        context.vstack.pop()
        context(context) { context.gc.collect() }
        assertEquals(-1, store.heap.structRuntimeTypeIdOrNegative(stackReference))
    }

    @Test
    fun `host allocation roots reference sources across automatic collection`() {
        val store = Store()
        val runtimeTypes = store.heap.registerRuntimeTypes(hostTypes())
        val module = ModuleInstance(runtimeTypes)
        val context = executionContext(
            store,
            module,
            RuntimeConfig(
                gcStrategy = GCStrategy.TRADITIONAL,
                gcThreshold = GCThreshold.KB(0),
            ),
        )
        val child = store.heap.allocateStruct(runtimeTypes[EMPTY_STRUCT_TYPE_INDEX], longArrayOf())
        val callbackMarker = store.heap.beginScope(5)

        context(context) {
            val gc = context.gc
            val resolved = gc.runtimeType(module, REFERENCE_ARRAY_TYPE_INDEX)
            assertEquals(runtimeTypes[REFERENCE_ARRAY_TYPE_INDEX].value, resolved.id)
            assertEquals(true, gc.isSubtype(resolved, resolved))

            val array = gc.allocateArray(resolved, longArrayOf(child))
            assertEquals(child, gc.readArrayElement(array, 0))
            assertNotEquals(-1, store.heap.structRuntimeTypeIdOrNegative(child))

            val struct = gc.allocateStruct(
                HostGcType(runtimeTypes[STRUCT_TYPE_INDEX].value),
                longArrayOf(7, child, 11),
            )
            assertEquals(child, gc.readStructField(struct, 1))

            val emptyStruct = gc.allocateStruct(
                HostGcType(runtimeTypes[EMPTY_STRUCT_TYPE_INDEX].value),
                longArrayOf(),
            )
            assertEquals(0, gc.structFieldCount(gc.structType(emptyStruct)))

            val emptyArray = gc.allocateArray(
                HostGcType(runtimeTypes[NUMBER_ARRAY_TYPE_INDEX].value),
                longArrayOf(),
            )
            assertEquals(0, gc.arrayLength(emptyArray))

            val filledArray = gc.allocateArray(
                HostGcType(runtimeTypes[NUMBER_ARRAY_TYPE_INDEX].value),
                length = 3,
                initialValue = 19L,
            )
            assertEquals(19L, gc.readArrayElement(filledArray, 2))
        }

        val marker = store.heap.beginScope()
        assertEquals(5, marker)
        store.heap.endScope(marker)
        store.heap.endScope(callbackMarker)
        store.heap.collectGarbage(store)
        assertEquals(-1, store.heap.structRuntimeTypeIdOrNegative(child))
    }

    private fun executionContext(
        store: Store,
        module: ModuleInstance = ModuleInstance(RuntimeTypeMap.Empty),
        config: RuntimeConfig = RuntimeConfig(),
    ) = ExecutionContext(ControlStack(), ValueStack(), store, module, config)

    private companion object {
        const val EMPTY_STRUCT_TYPE_INDEX = 0
        const val STRUCT_TYPE_INDEX = 1
        const val NUMBER_ARRAY_TYPE_INDEX = 2
        const val REFERENCE_ARRAY_TYPE_INDEX = 3
        const val PACKED_ARRAY_TYPE_INDEX = 4

        fun hostTypes(): List<DefinedType> = DefinedTypeFactory(
            listOf(
                RecursiveType(
                    subTypes = listOf(
                        SubType.Final(
                            superTypes = emptyList(),
                            compositeType = CompositeType.Struct(StructType(emptyList())),
                        ),
                        SubType.Final(
                            superTypes = emptyList(),
                            compositeType = CompositeType.Struct(
                                StructType(
                                    listOf(
                                        FieldType(StorageType.Packed(PackedType.I8), Mutability.Var),
                                        referenceField(),
                                        FieldType(
                                            StorageType.Value(ValueType.Number(NumberType.I64)),
                                            Mutability.Const,
                                        ),
                                    ),
                                ),
                            ),
                        ),
                        SubType.Final(
                            superTypes = emptyList(),
                            compositeType = CompositeType.Array(
                                ArrayType(
                                    FieldType(
                                        StorageType.Value(ValueType.Number(NumberType.I64)),
                                        Mutability.Var,
                                    ),
                                ),
                            ),
                        ),
                        SubType.Final(
                            superTypes = emptyList(),
                            compositeType = CompositeType.Array(ArrayType(referenceField())),
                        ),
                        SubType.Final(
                            superTypes = emptyList(),
                            compositeType = CompositeType.Array(
                                ArrayType(
                                    FieldType(
                                        StorageType.Packed(PackedType.I16),
                                        Mutability.Var,
                                    ),
                                ),
                            ),
                        ),
                    ),
                    state = RecursiveType.State.SYNTAX,
                ),
            ),
        )

        fun referenceField() = FieldType(
            StorageType.Value(
                ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Any)),
            ),
            Mutability.Var,
        )
    }
}
