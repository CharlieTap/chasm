package io.github.charlietap.chasm.decoder.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.config.moduleConfig
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import kotlin.test.Test
import kotlin.test.assertEquals

class DataModuleTest {

    @Test
    fun `can decode a data module section`() {

        val byteStream = Resource("src/commonTest/resources/data.wasm").readBytes()

        val config = moduleConfig()
        val reader = FakeSourceReader(byteStream)

        val expectedMemory = Memory(
            idx = Index.MemoryIndex(0u),
            type = memoryType(
                limits = limits(1u),
            ),
        )

        val expectedDataSegment = DataSegment(
            idx = Index.DataIndex(0u),
            initData = "Hello, World!".encodeToByteArray().toUByteArray(),
            mode = DataSegment.Mode.Active(
                memoryIndex = Index.MemoryIndex(0u),
                offset = Expression(listOf(NumericInstruction.I32Const(0))),
            ),
        )

        val expected = Ok(
            module(
                version = Version.One,
                memories = listOf(expectedMemory),
                dataSegments = listOf(expectedDataSegment),
            ),
        )

        val actual = WasmModuleDecoder(
            config = config,
            source = reader,
        )

        assertEquals(expected, actual)
    }
}
