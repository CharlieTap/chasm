package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.embedding.value.NumberValue
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleReuseTest {

    @Test
    fun `instances of one decoded module start from its declared sizes however much another grew`() = runTest {
        val module = module(Resource(FILE_DIR + "module_reuse.wasm").readBytes()).expect("decodes")
        val first = instance(store(), module, emptyList()).expect("instantiates")
        invoke(first, "grow").expect("grows")

        val second = instance(store(), module, emptyList()).expect("instantiates")

        assertEquals(listOf(NumberValue.I32(2)), invoke(first, "memory_size").expect("memory size"))
        assertEquals(listOf(NumberValue.I32(1)), invoke(second, "memory_size").expect("memory size"))
        assertEquals(listOf(NumberValue.I32(1)), invoke(second, "table_size").expect("table size"))
    }

    private companion object {
        const val FILE_DIR = "integration/"
    }
}
