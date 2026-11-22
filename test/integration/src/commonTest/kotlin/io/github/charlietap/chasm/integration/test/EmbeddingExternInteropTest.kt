package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class EmbeddingExternInteropTest {

    @Test
    fun `a host object passes through the invocation API as an externref`() = integrationTest(FIXTURE) {
        val runner = runner()
        val hostValue = HostValue("Midna")
        val argument = ReferenceValue.Extern(hostValue)

        val result = runner.invoke("identity", listOf(argument))
            .expect("expected extern identity invocation to succeed")

        assertEquals(listOf<ReferenceValue>(argument), result, engine.name())
        assertSame(hostValue, assertIs<ReferenceValue.Extern>(result.single(), engine.name()).value, engine.name())
    }

    private data class HostValue(val name: String)

    private companion object {
        const val FIXTURE = "embedding_extern_identity"
    }
}
