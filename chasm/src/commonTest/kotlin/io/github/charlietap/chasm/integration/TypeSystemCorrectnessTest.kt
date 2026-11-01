package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.validate
import kotlin.test.Test
import kotlin.test.assertIs

class TypeSystemCorrectnessTest {

    @Test
    fun `recursive type cannot declare a later type as its supertype`() {
        assertValidationFails("type_system_forward_supertype.wasm")
    }

    private fun assertValidationFails(fileName: String) {
        val decoded = module(Resource(FILE_DIR + fileName).readBytes())
            .expect("expected $fileName to decode")

        assertIs<ChasmResult.Error<*>>(validate(decoded))
    }

    companion object {
        private const val FILE_DIR = "integration/"
    }
}
