package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.validate
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TypeSystemCorrectnessTest {

    @Test
    fun `ref test recognizes a non-null exception reference`() {
        val result = testRunner(
            fileName = EXCEPTION_CAST_FIXTURE,
            fileDirectory = FILE_DIR,
            functionName = "test_exception_reference",
        )

        assertEquals(
            ChasmResult.Success(listOf(NumberValue.I32(1))),
            result,
        )
    }

    @Test
    fun `ref cast accepts a non-null exception reference`() {
        val result = testRunner(
            fileName = EXCEPTION_CAST_FIXTURE,
            fileDirectory = FILE_DIR,
            functionName = "cast_exception_reference",
        )

        assertEquals(
            ChasmResult.Success(listOf(NumberValue.I32(1))),
            result,
        )
    }

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
        private const val EXCEPTION_CAST_FIXTURE = "type_system_exception_cast.wasm"
    }
}
