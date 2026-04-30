package io.github.charlietap.chasm.vm

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.getOrNull
import io.github.charlietap.chasm.embedding.store
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class Issue119Test {

    // https://github.com/CharlieTap/chasm/issues/119
    @Test
    fun `returns missing imports for issue 119 module`() {
        val bytes = Resource("integration/issue119_app.wasm").readBytes()

        val wasmModule = module(bytes).getOrNull() ?: error("Failed to decode module")
        val result = instance(store(), wasmModule, emptyList())

        val error = assertIs<ChasmResult.Error<*>>(result).error
        val message = error.toString()
        assertTrue(message.contains("MissingImports"), message)
        assertTrue(!message.contains("frame-slot"), message)
    }
}
