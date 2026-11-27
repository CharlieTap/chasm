package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.fixture.componentTypes
import io.github.charlietap.chasm.embedding.fixture.publicComponent
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Component
import io.github.charlietap.chasm.embedding.shapes.DecodedModule
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.fixture.ast.component.component
import io.github.charlietap.chasm.fixture.config.componentConfig
import io.github.charlietap.chasm.fixture.config.moduleConfig
import io.github.charlietap.chasm.validator.WasmComponentAnalyzer
import io.github.charlietap.chasm.validator.error.ComponentValidatorError
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

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
        assertEquals(types, actual.result.types)
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

        val expected = ChasmResult.Error(ModuleError.Invalid(error.toString()))
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
    fun `calling validate on an unvalidated module validates it with its config`() = runTest {

        val config = moduleConfig(decodeNameSection = true)
        val module = module(Resource(FILE_DIR + "empty.wasm").readBytes(), config, validate = false)
            .expect("the module decodes")
        val decoded = assertIs<DecodedModule>(module._payload())

        val actual = validate(module)

        assertIs<ChasmResult.Success<Module>>(actual)
        val validated = assertIs<DecodedModule>(actual.result._payload())
        assertFalse(decoded.validated)
        assertTrue(validated.validated)
        assertEquals(config, validated.config)
        assertEquals(decoded.module, validated.module)
    }

    @Test
    fun `calling validate on a validated module does not validate it again`() = runTest {

        val module = module(Resource(FILE_DIR + "empty.wasm").readBytes())
            .expect("the module decodes and validates")

        val actual = validate(module)

        assertSame(module, assertIs<ChasmResult.Success<Module>>(actual).result)
    }

    @Test
    fun `calling validate on an invalid module propagates the error result`() = runTest {

        val module = module(Resource(FILE_DIR + "invalid.wasm").readBytes(), validate = false)
            .expect("the module decodes")

        val actual = validate(module)

        assertIs<ModuleError.Invalid>(assertIs<ChasmResult.Error<ModuleError.Invalid>>(actual).error)
    }

    private companion object {
        const val FILE_DIR = "embedding/"
    }
}
