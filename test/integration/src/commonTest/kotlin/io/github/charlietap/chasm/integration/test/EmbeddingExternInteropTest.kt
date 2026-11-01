package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class EmbeddingExternInteropTest {

    @Test
    fun `owned embedding extern passes through the existing invocation API`() = integrationTest(FIXTURE) {
        val runner = runner()
        val hostValue = HostValue("Midna")
        val extern = runner.createExternRef(hostValue).expect("expected extern creation to succeed")
        val argument = extern.asValue().expect("expected extern conversion to succeed")

        val result = runner.invoke("identity", listOf(argument))
            .expect("expected extern identity invocation to succeed")

        assertEquals(listOf(argument), result, engine.name())
        assertSame(hostValue, extern.hostValue().expect("expected extern observation to succeed"), engine.name())
        extern.close()
    }

    private data class HostValue(val name: String)

    private companion object {
        const val FIXTURE = "embedding_extern_identity"
    }
}
