package io.github.charlietap.chasm.runtime

import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.GCThreshold
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.host.HostException
import io.github.charlietap.chasm.host.HostTag
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.runtime.exception.HostRaisedWasmException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class HostExceptionsTest {

    @Test
    fun `host exceptions preserve payload bits and buffer offsets`() {
        val store = Store()
        val tagAddress = store.heap.registerTag(
            RTT(0),
            tagType(
                ValueType.Number(NumberType.I32),
                ValueType.Number(NumberType.I64),
                ValueType.Number(NumberType.F32),
                ValueType.Number(NumberType.F64),
            ),
        )
        val context = executionContext(store)
        val payload = longArrayOf(
            -1L,
            Int.MIN_VALUE.toLong(),
            Long.MIN_VALUE + 17,
            Float.fromBits(0x7FC01234).toRawBits().toLong(),
            Double.fromBits(0x7FF8000012345678).toRawBits(),
        )
        val marker = store.heap.beginScope()

        val exception = context(context) {
            store.heap.create(HostTag(tagAddress.address), payload, 1)
        }

        assertEquals(HostTag(tagAddress.address), store.heap.tag(exception))
        assertEquals(4, store.heap.payloadSize(exception))
        assertEquals(payload[1], store.heap.readPayload(exception, 0))
        assertEquals(payload[4], store.heap.readPayload(exception, 3))

        val destination = LongArray(6) { -1L }
        val returned = store.heap.readPayload(
            exception = exception,
            sourceOffset = 1,
            destination = destination,
            destinationOffset = 2,
            length = 2,
        )

        assertSame(destination, returned)
        assertContentEquals(longArrayOf(-1L, -1L, payload[2], payload[3], -1L, -1L), destination)
        store.heap.endScope(marker)
    }

    @Test
    fun `pending and scoped exceptions preserve reference payloads`() {
        val store = Store()
        val runtimeType = store.heap.registerRuntimeType(emptyStructType())
        val tagAddress = store.heap.registerTag(
            RTT(1),
            tagType(ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Any))),
        )
        val context = executionContext(store)
        val callbackMarker = store.heap.beginScope()
        val struct = store.heap.allocateStruct(runtimeType, LongArray(0))
        store.heap.rootScoped(struct)
        val exception = context(context) {
            store.heap.create(HostTag(tagAddress.address), longArrayOf(struct), 0)
        }

        assertFailsWith<HostRaisedWasmException> {
            store.heap.raise(exception)
        }
        store.heap.endScope(callbackMarker)
        store.heap.collectGarbage(store)

        assertTrue(store.heap.hasPending)
        assertNotEquals(-1, store.heap.exceptionTagAddressOrNegative(exception.rawReference))
        assertNotEquals(-1, store.heap.structRuntimeTypeIdOrNegative(struct))

        val observingMarker = store.heap.beginScope()
        assertEquals(exception, store.heap.takePending())
        assertFalse(store.heap.hasPending)
        store.heap.collectGarbage(store)
        assertNotEquals(-1, store.heap.exceptionTagAddressOrNegative(exception.rawReference))
        assertNotEquals(-1, store.heap.structRuntimeTypeIdOrNegative(struct))

        store.heap.endScope(observingMarker)
        store.heap.collectGarbage(store)
        assertEquals(-1, store.heap.exceptionTagAddressOrNegative(exception.rawReference))
        assertEquals(-1, store.heap.structRuntimeTypeIdOrNegative(struct))
    }

    @Test
    fun `raise and raise pending use the same exception`() {
        val store = Store()
        val tagAddress = store.heap.registerTag(RTT(0), tagType())
        val exception = HostException(store.heap.allocateException(tagAddress, LongArray(0)))

        assertFailsWith<HostRaisedWasmException> {
            store.heap.raise(exception)
        }
        assertTrue(store.heap.hasPending)

        assertFailsWith<HostRaisedWasmException> {
            store.heap.raisePending()
        }
        assertEquals(exception.rawReference, store.heap.takePendingExceptionReference())
        assertFalse(store.heap.hasPending)
    }

    @Test
    @OptIn(UnsafeHostApi::class)
    fun `traditional collection sees references in the host stack payload`() {
        val store = Store()
        val runtimeType = store.heap.registerRuntimeType(emptyStructType())
        val struct = store.heap.allocateStruct(runtimeType, LongArray(0))
        val tagAddress = store.heap.registerTag(
            RTT(1),
            tagType(ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Any))),
        )
        val stack = ValueStack().apply { push(struct) }
        val context = ExecutionContext(
            cstack = ControlStack(),
            vstack = stack,
            store = store,
            instance = ModuleInstance(RuntimeTypeMap.Empty),
            config = RuntimeConfig(
                gcStrategy = GCStrategy.TRADITIONAL,
                gcThreshold = GCThreshold.KB(0),
            ),
        )
        val marker = store.heap.beginScope()

        val exception = context(context) {
            store.heap.create(HostTag(tagAddress.address), stack.unsafeElements(), 0)
        }

        assertNotEquals(-1, store.heap.structRuntimeTypeIdOrNegative(struct))
        assertEquals(struct, store.heap.readPayload(exception, 0))
        store.heap.endScope(marker)
    }

    private fun executionContext(store: Store) = ExecutionContext(
        cstack = ControlStack(),
        vstack = ValueStack(),
        store = store,
        instance = ModuleInstance(RuntimeTypeMap.Empty),
        config = RuntimeConfig(),
    )

    private fun emptyStructType() = DefinedTypeFactory(
        listOf(
            RecursiveType(
                subTypes = listOf(
                    SubType.Final(
                        superTypes = emptyList(),
                        compositeType = CompositeType.Struct(StructType(emptyList())),
                    ),
                ),
                state = RecursiveType.State.SYNTAX,
            ),
        ),
    )[0]

    private fun tagType(vararg parameters: ValueType) = TagType(
        attribute = TagType.Attribute.Exception,
        typeIndex = 0,
        functionType = FunctionType(
            params = ResultType(parameters.toList()),
            results = ResultType(emptyList()),
        ),
    )
}
