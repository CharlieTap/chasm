package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.unwrap
import io.github.charlietap.chasm.compiler.Compiler
import io.github.charlietap.chasm.executor.instantiator.compat.CompatibilityChecker
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.ir.factory.ModuleFactory
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import io.github.charlietap.chasm.fixture.ast.module.module as astModule
import io.github.charlietap.chasm.fixture.ir.module.module as irModule

class ModuleCompilerTest {

    @Test
    fun `compiles a module with the compatibility factory and compiler dependencies`() {
        val config = runtimeConfig()
        val module = astModule()
        val intermediate = irModule()
        val compiled = irModule()
        val compatibilityChecker: CompatibilityChecker = { actualModule ->
            assertSame(module, actualModule)
            Ok(Unit)
        }
        val moduleFactory: ModuleFactory = { actualModule ->
            assertSame(module, actualModule)
            intermediate
        }
        val compiler: Compiler = { actualConfig, actualModule ->
            assertSame(config, actualConfig)
            assertSame(intermediate, actualModule)
            compiled
        }

        val actual = ModuleCompiler(
            config = config,
            module = module,
            compatibilityChecker = compatibilityChecker,
            moduleFactory = moduleFactory,
            compiler = compiler,
        )

        val compiledModule = actual.unwrap()
        assertSame(compiled, compiledModule.module)
    }

    @Test
    fun `the ast instantiator composes compilation and compiled instantiation`() {
        val config = runtimeConfig()
        val store = store()
        val module = astModule()
        val compiled = CompiledModule(irModule())
        val instance = moduleInstance()
        val compiler: ModuleCompiler = { actualConfig, actualModule ->
            assertSame(config, actualConfig)
            assertSame(module, actualModule)
            Ok(compiled)
        }
        val instantiator: CompiledModuleInstantiator = { actualConfig, actualStore, actualModule, actualImports ->
            assertSame(config, actualConfig)
            assertSame(store, actualStore)
            assertSame(compiled, actualModule)
            assertEquals(emptyList(), actualImports)
            Ok(instance)
        }

        val actual = ModuleInstantiator(
            config = config,
            store = store,
            module = module,
            imports = emptyList(),
            compiler = compiler,
            instantiator = instantiator,
        )

        val expected = Ok(instance)
        assertEquals(expected, actual)
    }

    @Test
    fun `a compiled module can be instantiated repeatedly without sharing instances`() {
        val config = runtimeConfig()
        val compiled = CompiledModule(irModule())

        val firstResult = ModuleInstantiator(config, store(), compiled, emptyList())
        val secondResult = ModuleInstantiator(config, store(), compiled, emptyList())
        val first: ModuleInstance = firstResult.unwrap()
        val second: ModuleInstance = secondResult.unwrap()

        assertNotSame(first, second)
    }
}
