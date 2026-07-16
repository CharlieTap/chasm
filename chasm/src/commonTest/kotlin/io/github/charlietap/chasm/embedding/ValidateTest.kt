package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.fixture.componentTypes
import io.github.charlietap.chasm.embedding.fixture.publicComponent
import io.github.charlietap.chasm.embedding.fixture.publicModule
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Component
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.fixture.ast.component.component
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.config.componentConfig
import io.github.charlietap.chasm.fixture.config.moduleConfig
import io.github.charlietap.chasm.validator.WasmComponentAnalyzer
import io.github.charlietap.chasm.validator.WasmModuleValidator
import io.github.charlietap.chasm.validator.error.ComponentValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ValidateTest {

    @Test
    fun `calling validate on a component calls WasmComponentAnalyzer and propagates result`() {

        val config = componentConfig()
        val internalComponent = component()
        val component = publicComponent(config, internalComponent)
        val types = componentTypes()
        val analyzer: WasmComponentAnalyzer = { actualConfig, actualComponent ->
            assertEquals(config, actualConfig)
            assertEquals(internalComponent, actualComponent)
            Ok(types)
        }

        val actual = validate(component, analyzer)

        assertIs<ChasmResult.Success<Component>>(actual)
        assertEquals(internalComponent, actual.result.component)
        assertEquals(types, actual.result.analysisCache.types)
        assertEquals(component.preparationCache, actual.result.preparationCache)
    }

    @Test
    fun `calling validate on a component calls WasmComponentAnalyzer and propagates error result`() {

        val component = publicComponent()
        val error = ComponentValidatorError.InvalidComponent("invalid")
        val analyzer: WasmComponentAnalyzer = { actualConfig, actualComponent ->
            assertEquals(component.config, actualConfig)
            assertEquals(component.component, actualComponent)
            Err(error)
        }

        val actual = validate(component, analyzer)

        val expected = ChasmResult.Error(ChasmError.ValidationError(error.toString()))
        assertEquals(expected, actual)
    }

    @Test
    fun `calling validate on an analyzed component reuses its types`() {

        val component = publicComponent(types = componentTypes())
        val analyzer: WasmComponentAnalyzer = { _, _ -> error("analyzer must not be called") }

        val actual = validate(component, analyzer)

        val expected = ChasmResult.Success(component)
        assertEquals(expected, actual)
    }

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
        assertEquals(module.compilationCache, actual.result.compilationCache)
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
