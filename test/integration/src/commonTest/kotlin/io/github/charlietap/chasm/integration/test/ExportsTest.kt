package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.embedding.shapes.map
import io.github.charlietap.chasm.integration.exports
import io.github.charlietap.chasm.integration.integrationTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ExportsTest {

    @Test
    fun `can instantiate a module and return its exports`() = integrationTest(FIXTURE) {

        val actual = exports().map { exports ->
            exports.map { export -> export.name to export.value::class }
        }

        val expected = listOf(
            "exported_function" to Function::class,
            "exported_global" to Global::class,
            "exported_memory" to Memory::class,
            "exported_table" to Table::class,
            "exported_tag" to Tag::class,
        )

        assertEquals(ChasmResult.Success(expected), actual, engine.name())
    }

    private companion object {
        const val FIXTURE = "export"
    }
}
