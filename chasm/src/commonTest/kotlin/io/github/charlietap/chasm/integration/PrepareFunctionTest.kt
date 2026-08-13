package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.prepareFunction
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.shapes.getOrNull
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PrepareFunctionTest {

    @Test
    fun `can prepare a function from a decoded module`() {
        val bytes = Resource("integration/export.wasm").readBytes()
        val store = store()
        val instance = module(bytes)
            .flatMap { module -> instance(store, module, emptyList()) }
            .getOrNull()
        val prepared = instance?.let { prepareFunction(store, it, "exported_function").getOrNull() }

        assertNotNull(prepared)
        assertEquals(ChasmResult.Success(emptyList<ExecutionValue>()), prepared())
        assertEquals(ChasmResult.Success(emptyList<ExecutionValue>()), prepared())
    }
}
