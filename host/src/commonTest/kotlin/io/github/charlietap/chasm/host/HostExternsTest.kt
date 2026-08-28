package io.github.charlietap.chasm.host

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class HostExternsTest {

    @Test
    fun `createNullable returns the null extern reference for null`() {
        val externs = RecordingHostExterns()

        val reference = context(TestHostResources()) {
            externs.createNullable(null)
        }

        assertEquals(NULL_EXTERN, reference)
        assertEquals(null, externs.createdValue)
    }

    @Test
    fun `createNullable creates an extern reference for a value`() {
        val externs = RecordingHostExterns()
        val value = Any()

        val reference = context(TestHostResources()) {
            externs.createNullable(value)
        }

        assertEquals(CREATED_EXTERN, reference)
        assertSame(value, externs.createdValue)
    }

    @Test
    fun `createRetained retains the created extern reference`() {
        val externs = RecordingHostExterns()
        val references = RecordingExternReferences()
        val value = Any()

        val root = context(TestHostResources(references)) {
            externs.createRetained(value)
        }

        assertSame(value, externs.createdValue)
        assertEquals(CREATED_EXTERN.raw, references.retainedReference)
        assertEquals(RETAINED_ROOT, root)
    }

    @Test
    fun `withExterns makes the caller extern API the receiver`() {
        val externs = RecordingHostExterns()
        val resources = TestHostResources(hostExterns = externs)

        val actual = context(resources) {
            withExterns { this }
        }

        assertSame(externs, actual)
    }
}

private val NULL_EXTERN = HostExternReference(1L)
private val CREATED_EXTERN = HostExternReference(2L)
private val RETAINED_ROOT = HostReferenceRoot(3)

private class RecordingHostExterns : HostExterns {

    var createdValue: Any? = null

    context(resources: HostResources)
    override fun create(value: Any): HostExternReference {
        createdValue = value
        return CREATED_EXTERN
    }

    override fun nullReference(): HostExternReference = NULL_EXTERN

    override fun kind(reference: HostExternReference): HostExternKind = error("unused")

    override fun value(reference: HostExternReference): Any = error("unused")

    override fun externalizedReference(reference: HostExternReference): HostReference = error("unused")

    override fun externalize(reference: HostReference): HostExternReference = error("unused")
}

private class RecordingExternReferences : HostReferences {

    var retainedReference: HostReference? = null

    override fun beginScope(capacity: Int): Int = error("unused")

    override fun rootScoped(reference: HostReference): HostReference = error("unused")

    override fun endScope(marker: Int) = error("unused")

    override fun retain(reference: HostReference): HostReferenceRoot {
        retainedReference = reference
        return RETAINED_ROOT
    }

    override fun reference(root: HostReferenceRoot): HostReference = error("unused")

    override fun release(root: HostReferenceRoot) = error("unused")
}

private class TestHostResources(
    override val references: HostReferences = RecordingExternReferences(),
    private val hostExterns: HostExterns? = null,
) : HostResources {

    override val gc: HostGc
        get() = error("unused")

    override val externs: HostExterns
        get() = requireNotNull(hostExterns)

    override fun memory(module: HostModuleInstance, index: Int): HostMemory = error("unused")

    override fun table(module: HostModuleInstance, index: Int): HostTable = error("unused")

    override fun global(module: HostModuleInstance, index: Int): HostGlobal = error("unused")

    override fun tag(module: HostModuleInstance, index: Int): HostTag = error("unused")
}
