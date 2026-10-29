package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.publicExport
import io.github.charlietap.chasm.embedding.fixture.publicFunction
import io.github.charlietap.chasm.embedding.fixture.publicGlobal
import io.github.charlietap.chasm.embedding.fixture.publicInstance
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicTable
import io.github.charlietap.chasm.embedding.fixture.publicTag
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import io.github.charlietap.chasm.fixture.runtime.instance.exportInstance
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.globalAddress
import io.github.charlietap.chasm.fixture.runtime.instance.globalExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.runtime.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableAddress
import io.github.charlietap.chasm.fixture.runtime.instance.tableExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.tagAddress
import io.github.charlietap.chasm.fixture.runtime.instance.tagExternalValue
import io.github.charlietap.chasm.fixture.runtime.store
import kotlin.test.Test
import kotlin.test.assertEquals

class ExportsTest {

    @Test
    fun `exports name each export with a handle to the resource it exports`() {

        val store = store()
        val instance = publicInstance(
            store = store,
            moduleInstance = moduleInstance(
                exports = mutableListOf(
                    exportInstance(nameValue("first_function"), functionExternalValue(functionAddress(3))),
                    exportInstance(nameValue("second_function"), functionExternalValue(functionAddress(7))),
                    exportInstance(nameValue("global"), globalExternalValue(globalAddress(2))),
                    exportInstance(nameValue("memory"), memoryExternalValue(memoryAddress(1))),
                    exportInstance(nameValue("table"), tableExternalValue(tableAddress(4))),
                    exportInstance(nameValue("tag"), tagExternalValue(tagAddress(5))),
                ),
            ),
        )

        val expected = listOf(
            publicExport("first_function", publicFunction(functionExternalValue(functionAddress(3)), store)),
            publicExport("second_function", publicFunction(functionExternalValue(functionAddress(7)), store)),
            publicExport("global", publicGlobal(globalExternalValue(globalAddress(2)), store)),
            publicExport("memory", publicMemory(memoryExternalValue(memoryAddress(1)), store)),
            publicExport("table", publicTable(tableExternalValue(tableAddress(4)), store)),
            publicExport("tag", publicTag(tagExternalValue(tagAddress(5)), store)),
        )

        assertEquals(expected, exports(instance))
    }
}
