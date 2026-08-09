package io.github.charlietap.chasm.compiler.passes

import io.github.charlietap.chasm.fixture.ir.instruction.blockInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.endInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.fixture.ir.module.function
import io.github.charlietap.chasm.fixture.ir.module.module
import io.github.charlietap.chasm.fixture.ir.module.type
import io.github.charlietap.chasm.fixture.ir.module.typeIndex
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FlatControlFlowPassTest {

    @Test
    fun `lowers deeply nested control without using the host stack`() {
        val depth = 20_000
        val recursiveType = functionRecursiveType(functionType())
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = buildList {
                            repeat(depth) {
                                add(blockInstruction())
                            }
                            add(endInstruction(depth))
                        },
                    ),
                ),
            ),
        )
        val controlled = ControlFlowPass(passContext(module = module), module)
        val framed = FrameSlotPass(passContext(module = controlled), controlled)

        val actual = JumpPass(passContext(module = framed), framed)

        assertEquals(
            listOf(AdminInstruction.EndFunction),
            actual.functions.single().body.instructions,
        )
    }

    @Test
    fun `rejects deeply nested malformed control without using the host stack`() {
        val depth = 20_000
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions = buildList {
                            repeat(depth) {
                                add(blockInstruction())
                            }
                            add(AdminInstruction.EndFunction)
                        },
                    ),
                ),
            ),
        )

        assertFailsWith<IllegalArgumentException> {
            JumpPass(passContext(module = module), module)
        }
    }
}
