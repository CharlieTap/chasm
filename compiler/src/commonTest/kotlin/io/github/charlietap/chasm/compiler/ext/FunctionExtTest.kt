package io.github.charlietap.chasm.compiler.ext

import io.github.charlietap.chasm.fixture.ir.instruction.blockInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.elseInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.endInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.fixture.ir.instruction.ifInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.nopInstruction
import io.github.charlietap.chasm.fixture.ir.module.function
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionExtTest {

    @Test
    fun `traverseInstructions yields the flat function body in order`() {
        val instructions = listOf(
            blockInstruction(),
            ifInstruction(),
            nopInstruction(),
            elseInstruction(),
            nopInstruction(),
            endInstruction(2),
        )
        val function = function(
            body = expression(instructions = instructions),
        )

        assertEquals(instructions, function.traverseInstructions().toList())
    }
}
