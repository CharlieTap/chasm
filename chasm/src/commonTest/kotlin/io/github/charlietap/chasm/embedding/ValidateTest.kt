package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.fixture.module.module
import io.github.charlietap.chasm.validator.WasmModuleValidator
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import kotlin.test.Test
import kotlin.test.assertEquals

class ValidateTest {

    @Test
    fun `calling validate on a module calls WasmModuleValidator and propagates result`() {

        val module = module()

        val validator: WasmModuleValidator = { actualModule ->
            assertEquals(module, actualModule)
            Ok(module)
        }

        val actual = validate(module, validator)

        assertEquals(ChasmResult.Success(module), actual)
    }

    @Test
    fun `calling validate on a module calls WasmModuleValidator and propagates error result`() {

        val module = module()

        val validator: WasmModuleValidator = { actualModule ->
            assertEquals(module, actualModule)
            Err(TypeValidatorError.TypeMismatch)
        }

        val actual = validate(module, validator)

        assertEquals(ChasmResult.Error(ChasmError.ValidationError(TypeValidatorError.TypeMismatch)), actual)
    }
}
