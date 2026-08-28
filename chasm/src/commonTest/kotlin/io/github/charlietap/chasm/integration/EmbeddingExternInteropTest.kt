package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.embedding.extern.createExternRef
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.embedding.validate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class EmbeddingExternInteropTest {

    @Test
    fun `owned embedding extern passes through the existing invocation API`() {
        val store = store()
        val module = module(Resource(FIXTURE).readBytes())
            .expect("expected extern identity module to decode")
            .let(::validate)
            .expect("expected extern identity module to validate")
        val instance = instance(store, module, emptyList())
            .expect("expected extern identity module to instantiate")
        val hostValue = HostValue("Midna")
        val extern = createExternRef(store, hostValue).expect("expected extern creation to succeed")
        val argument = extern.asValue().expect("expected extern conversion to succeed")

        val result = invoke(store, instance, "identity", listOf(argument))
            .expect("expected extern identity invocation to succeed")

        assertEquals(listOf(argument), result)
        assertSame(hostValue, extern.hostValue().expect("expected extern observation to succeed"))
        extern.close()
    }

    private data class HostValue(val name: String)

    private companion object {
        const val FIXTURE = "integration/embedding_extern_identity.wasm"
    }
}
