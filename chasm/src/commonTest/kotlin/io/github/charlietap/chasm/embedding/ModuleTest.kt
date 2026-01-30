package io.github.charlietap.chasm.embedding

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.decoder.reader.ByteArraySourceReader
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import kotlin.test.Test
import kotlin.test.assertIs

class ModuleTest {

    @Test
    fun `can create a module using bytes`() {

        val bytes = Resource(FILE_DIR + "empty.wasm").readBytes()

        val actual = module(bytes)

        assertIs<ChasmResult.Success<Module>>(actual)
    }

    @Test
    fun `can create a module using a stream`() {

        val bytes = Resource(FILE_DIR + "empty.wasm").readBytes()
        val reader = ByteArraySourceReader(bytes)

        val actual = module(reader)

        assertIs<ChasmResult.Success<Module>>(actual)
    }

    private companion object {
        const val FILE_DIR = "embedding/"
    }
}
