package io.github.charlietap.chasm.executor.instantiator.runtime.allocation

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.instantiator.matching.ImportDescriptorMatcher
import io.github.charlietap.chasm.executor.instantiator.matching.ImportMatcher
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.fixture.executor.instantiator.instantiationContext
import io.github.charlietap.chasm.fixture.executor.runtime.instance.externalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.import
import io.github.charlietap.chasm.fixture.ir.module.functionImport
import io.github.charlietap.chasm.fixture.ir.module.module
import io.github.charlietap.chasm.fixture.ir.value.nameValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ImportMatcherTest {

    @Test
    fun `can match an import and return the external value`() {

        val functionImport = functionImport(
            moduleName = nameValue("env"),
            entityName = nameValue("function"),
        )

        val module = module(
            imports = listOf(
                functionImport,
            ),
        )

        val context = instantiationContext(
            module = module,
        )

        val importExternal = externalValue()
        val imports = listOf(
            import(
                moduleName = "env",
                entityName = "function",
                externalValue = importExternal,
            ),
        )

        val importDescriptorMatcher: ImportDescriptorMatcher = { _context, _descriptor, _external ->
            assertEquals(context, _context)
            assertEquals(functionImport.descriptor, _descriptor)
            assertEquals(importExternal, _external)

            Ok(true)
        }

        val expected = listOf(importExternal)

        val actual = ImportMatcher(
            context = context,
            imports = imports,
            descriptorMatcher = importDescriptorMatcher,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `returns an error listing the missing imports`() {

        val functionImport = functionImport(
            moduleName = nameValue("env"),
            entityName = nameValue("function"),
        )

        val module = module(
            imports = listOf(
                functionImport,
            ),
        )

        val context = instantiationContext(
            module = module,
        )

        val importDescriptorMatcher: ImportDescriptorMatcher = { _, _, _ ->
            Ok(true)
        }

        val expected = InstantiationError.MissingImports(
            listOf(
                functionImport,
            ),
        )

        val actual = ImportMatcher(
            context = context,
            imports = emptyList(),
            descriptorMatcher = importDescriptorMatcher,
        )

        assertEquals(Err(expected), actual)
    }
}
