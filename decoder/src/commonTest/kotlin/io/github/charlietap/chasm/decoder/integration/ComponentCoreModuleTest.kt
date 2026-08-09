package io.github.charlietap.chasm.decoder.integration

import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.ast.component.CoreModule
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.decoder.WasmComponentDecoder
import io.github.charlietap.chasm.fixture.config.componentConfig
import io.github.charlietap.chasm.type.BlockType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ComponentCoreModuleTest {

    @Test
    fun `decodes flat instructions from an embedded core module`() {
        val module = byteArrayOf(
            0x00,
            0x61,
            0x73,
            0x6D,
            0x01,
            0x00,
            0x00,
            0x00,
            0x01,
            0x04,
            0x01,
            0x60,
            0x00,
            0x00,
            0x03,
            0x02,
            0x01,
            0x00,
            0x0A,
            0x08,
            0x01,
            0x06,
            0x00,
            0x02,
            0x40,
            0x01,
            0x0B,
            0x0B,
        )
        val component = byteArrayOf(
            0x00,
            0x61,
            0x73,
            0x6D,
            0x0D,
            0x00,
            0x01,
            0x00,
            0x01,
            module.size.toByte(),
        ) + module

        WasmComponentDecoder(componentConfig(), component).fold(
            success = { decoded ->
                val decodedModule = (decoded.definitions.single() as CoreModule).module
                val expected = Expression(
                    ControlInstruction.Block(BlockType.Empty),
                    ControlInstruction.Nop,
                    ControlInstruction.End(1),
                )

                assertEquals(expected, decodedModule.functions.single().body)
            },
            failure = { error -> fail("component decode failed: $error") },
        )
    }
}
