package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.config.moduleConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.fixture.publicModule
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.validator.WasmModuleValidator
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ValidateTest {

    @Test
    fun `calling validate on a module calls WasmModuleValidator and propagates result`() {

        val config = moduleConfig()
        val internalModule = module()
        val module = publicModule(config, internalModule)

        val validator: WasmModuleValidator = { _config, _module ->
            assertEquals(config, _config)
            assertEquals(module.module, _module)
            Ok(module.module)
        }

        val actual = validate(module, validator)

        assertIs<ChasmResult.Success<Module>>(actual)
        assertEquals(internalModule, actual.result.module)
    }

    @Test
    fun `calling validate on a module calls WasmModuleValidator and propagates error result`() {

        val config = moduleConfig()
        val module = publicModule(
            config = config,
        )

        val validator: WasmModuleValidator = { _config, _module ->
            assertEquals(config, _config)
            assertEquals(module.module, _module)
            Err(TypeValidatorError.TypeMismatch)
        }

        val actual = validate(module, validator)

        assertEquals(ChasmResult.Error(ChasmError.ValidationError(TypeValidatorError.TypeMismatch.toString())), actual)
    }
}
