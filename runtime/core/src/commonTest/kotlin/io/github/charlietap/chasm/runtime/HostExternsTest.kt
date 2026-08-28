package io.github.charlietap.chasm.runtime

import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.host.HostExternKind
import io.github.charlietap.chasm.host.HostExternReference
import io.github.charlietap.chasm.host.withExterns
import io.github.charlietap.chasm.runtime.encoder.ReferenceValueEncoder
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class HostExternsTest {

    @Test
    fun `host resources expose the extern API`() {
        val store = Store()
        val context = executionContext(store)
        val marker = store.heap.beginScope()

        val reference = context(context) {
            withExterns { create("value") }
        }

        assertEquals("value", store.heap.value(reference))
        store.heap.endScope(marker)
    }

    @Test
    fun `host values have scoped identities and can be retained`() {
        val store = Store()
        val context = executionContext(store)
        val marker = store.heap.beginScope(2)
        val value = Any()

        val first = context(context) { store.heap.create(value) }
        val second = context(context) { store.heap.create(value) }

        assertEquals(HostExternKind.HOST_VALUE, store.heap.kind(first))
        assertSame(value, store.heap.value(first))
        assertNotEquals(first, second)
        context(context) { store.heap.collect() }
        assertEquals(2, store.heap.countLiveExterns())

        val root = store.heap.retain(first.raw)
        store.heap.endScope(marker)
        context(context) { store.heap.collect() }
        assertEquals(1, store.heap.countLiveExterns())
        assertSame(value, store.heap.value(first))

        store.heap.release(root)
        context(context) { store.heap.collect() }
        assertEquals(0, store.heap.countLiveExterns())
    }

    @Test
    fun `externalized references preserve their representation`() {
        val store = Store()
        val i31 = ReferenceValueEncoder(ReferenceValue.I31(42u))
        val nullReference = ReferenceValueEncoder(ReferenceValue.Null(AbstractHeapType.Any))
        val marker = store.heap.beginScope()

        val externalized = store.heap.externalize(i31)
        val externalizedNull = store.heap.externalize(nullReference)

        assertEquals(HostExternKind.EXTERNALIZED_REFERENCE, store.heap.kind(externalized))
        assertEquals(i31, store.heap.externalizedReference(externalized))
        assertEquals(HostExternKind.NULL, store.heap.kind(externalizedNull))
        assertEquals(store.heap.nullReference(), externalizedNull)
        store.heap.endScope(marker)
    }

    @Test
    fun `extern allocation pressure reclaims dead slots`() {
        val store = Store()
        val context = executionContext(
            store = store,
            config = RuntimeConfig(gcStrategy = GCStrategy.MANUAL),
        )

        fun createUnrooted(value: Any): HostExternReference {
            val marker = store.heap.beginScope()
            val reference = context(context) { store.heap.create(value) }
            store.heap.endScope(marker)
            return reference
        }

        val stackRoot = createUnrooted("stack root")
        context.vstack.push(stackRoot.raw)
        var lastDead = stackRoot
        repeat(63) {
            lastDead = createUnrooted(Any())
        }

        assertEquals(64, store.heap.countLiveExterns())

        val marker = store.heap.beginScope()
        val created = context(context) { store.heap.create("created") }

        assertEquals(2, store.heap.countLiveExterns())
        assertEquals("stack root", store.heap.value(stackRoot))
        assertEquals(lastDead.raw, created.raw)
        store.heap.endScope(marker)
        context.vstack.pop()
    }

    private fun executionContext(
        store: Store,
        config: RuntimeConfig = RuntimeConfig(),
    ) = ExecutionContext(
        cstack = ControlStack(),
        vstack = ValueStack(),
        store = store,
        instance = ModuleInstance(RuntimeTypeMap.Empty),
        config = config,
    )
}
