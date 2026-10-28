package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleReuseTest {

    @Test
    fun `instances of one decoded module start from its declared sizes however much another grew`() {
        val module = module(Resource(FILE_DIR + "module_reuse.wasm").readBytes()).expect("decodes")
        val firstStore = store()
        val first = instance(firstStore, module, emptyList()).expect("instantiates")
        invoke(firstStore, first, "grow").expect("grows")

        val secondStore = store()
        val second = instance(secondStore, module, emptyList()).expect("instantiates")

        assertEquals(listOf(NumberValue.I32(2)), invoke(firstStore, first, "memory_size").expect("memory size"))
        assertEquals(listOf(NumberValue.I32(1)), invoke(secondStore, second, "memory_size").expect("memory size"))
        assertEquals(listOf(NumberValue.I32(1)), invoke(secondStore, second, "table_size").expect("table size"))
    }

    private companion object {
        const val FILE_DIR = "integration/"
    }
}
