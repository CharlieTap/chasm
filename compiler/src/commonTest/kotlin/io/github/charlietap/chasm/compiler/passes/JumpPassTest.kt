package io.github.charlietap.chasm.compiler.passes

import io.github.charlietap.chasm.fixture.ir.instruction.blockInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.brInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.catchAllHandler
import io.github.charlietap.chasm.fixture.ir.instruction.emptyBlockType
import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.fixture.ir.instruction.frameSlotOperand
import io.github.charlietap.chasm.fixture.ir.instruction.fusedBrIf
import io.github.charlietap.chasm.fixture.ir.instruction.fusedIf
import io.github.charlietap.chasm.fixture.ir.instruction.loopInstruction
import io.github.charlietap.chasm.fixture.ir.module.function
import io.github.charlietap.chasm.fixture.ir.module.labelIndex
import io.github.charlietap.chasm.fixture.ir.module.module
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import kotlin.test.Test
import kotlin.test.assertEquals

class JumpPassTest {

    @Test
    fun `flattens block exits into direct jumps`() {
        val module = module(
            functions = listOf(
                function(
                    frameSlotMode = true,
                    body = expression(
                        instructions = listOf(
                            blockInstruction(
                                instructions = listOf(
                                    fusedBrIf(
                                        operand = frameSlotOperand(0),
                                        labelIndex = labelIndex(0),
                                        takenInstructions = listOf(
                                            AdminInstruction.CopySlots(
                                                sourceSlots = listOf(1),
                                                destinationSlots = listOf(2),
                                            ),
                                        ),
                                    ),
                                    AdminInstruction.CopySlots(
                                        sourceSlots = listOf(3),
                                        destinationSlots = listOf(4),
                                    ),
                                    AdminInstruction.EndBlock,
                                ),
                            ),
                            AdminInstruction.EndBlock,
                            AdminInstruction.EndFunction,
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val result = JumpPass(context, module)

        assertEquals(
            listOf(
                AdminInstruction.JumpIf(
                    operand = frameSlotOperand(0),
                    offset = 2,
                    takenInstructions = listOf(
                        AdminInstruction.CopySlots(
                            sourceSlots = listOf(1),
                            destinationSlots = listOf(2),
                        ),
                    ),
                ),
                AdminInstruction.CopySlots(
                    sourceSlots = listOf(3),
                    destinationSlots = listOf(4),
                ),
                AdminInstruction.EndFunction,
            ),
            result.functions[0].body.instructions,
        )
    }

    @Test
    fun `flattens loop back edges into direct jumps`() {
        val module = module(
            functions = listOf(
                function(
                    frameSlotMode = true,
                    body = expression(
                        instructions = listOf(
                            loopInstruction(
                                instructions = listOf(
                                    brInstruction(labelIndex(0)),
                                    AdminInstruction.EndBlock,
                                ),
                            ),
                            AdminInstruction.EndBlock,
                            AdminInstruction.EndFunction,
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val result = JumpPass(context, module)

        assertEquals(
            listOf(
                AdminInstruction.Jump(offset = 0),
                AdminInstruction.EndFunction,
            ),
            result.functions[0].body.instructions,
        )
    }

    @Test
    fun `patches branches to the implicit function label`() {
        val module = module(
            functions = listOf(
                function(
                    frameSlotMode = true,
                    body = expression(
                        instructions = listOf(
                            loopInstruction(
                                instructions = listOf(
                                    brInstruction(labelIndex(1)),
                                    AdminInstruction.EndBlock,
                                ),
                            ),
                            AdminInstruction.EndBlock,
                            AdminInstruction.EndFunction,
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val result = JumpPass(context, module)

        assertEquals(
            listOf(
                AdminInstruction.Jump(offset = 1),
                AdminInstruction.EndFunction,
            ),
            result.functions[0].body.instructions,
        )
    }

    @Test
    fun `flattens if blocks into patched direct jumps`() {
        val module = module(
            functions = listOf(
                function(
                    frameSlotMode = true,
                    body = expression(
                        instructions = listOf(
                            fusedIf(
                                operand = frameSlotOperand(0),
                                thenInstructions = listOf(
                                    brInstruction(labelIndex(0)),
                                    AdminInstruction.EndBlock,
                                ),
                                elseInstructions = listOf(
                                    AdminInstruction.CopySlots(
                                        sourceSlots = listOf(1),
                                        destinationSlots = listOf(2),
                                    ),
                                    AdminInstruction.EndBlock,
                                ),
                            ),
                            AdminInstruction.EndBlock,
                            AdminInstruction.EndFunction,
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val result = JumpPass(context, module)

        assertEquals(
            listOf(
                AdminInstruction.JumpIf(
                    operand = frameSlotOperand(0),
                    offset = 3,
                ),
                AdminInstruction.CopySlots(
                    sourceSlots = listOf(1),
                    destinationSlots = listOf(2),
                ),
                AdminInstruction.Jump(offset = 4),
                AdminInstruction.Jump(offset = 4),
                AdminInstruction.EndFunction,
            ),
            result.functions[0].body.instructions,
        )
    }

    @Test
    fun `lowers isolated try_table body while lowering sibling control`() {
        val tryTable = ControlInstruction.TryTable(
            blockType = emptyBlockType(),
            handlers = emptyList(),
            instructions = listOf(
                brInstruction(labelIndex(0)),
                AdminInstruction.EndBlock,
            ),
        )
        val module = module(
            functions = listOf(
                function(
                    frameSlotMode = true,
                    body = expression(
                        instructions = listOf(
                            tryTable,
                            blockInstruction(
                                instructions = listOf(
                                    brInstruction(labelIndex(0)),
                                    AdminInstruction.EndBlock,
                                ),
                            ),
                            AdminInstruction.EndBlock,
                            AdminInstruction.EndFunction,
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val result = JumpPass(context, module)

        assertEquals(
            listOf(
                AdminInstruction.PushHandler(
                    handlers = emptyList(),
                    offsets = emptyList(),
                    payloadDestinationSlots = emptyList(),
                    endOffset = 2,
                ),
                AdminInstruction.Jump(offset = 2),
                AdminInstruction.PopHandler,
                AdminInstruction.Jump(offset = 4),
                AdminInstruction.EndFunction,
            ),
            result.functions[0].body.instructions,
        )
    }

    @Test
    fun `lowers try_table handlers that target the enclosing block label`() {
        val tryTable = ControlInstruction.TryTable(
            blockType = emptyBlockType(),
            handlers = listOf(catchAllHandler(labelIndex = labelIndex(0))),
            instructions = listOf(AdminInstruction.EndBlock),
        )
        val module = module(
            functions = listOf(
                function(
                    frameSlotMode = true,
                    body = expression(
                        instructions = listOf(
                            tryTable,
                            loopInstruction(
                                instructions = listOf(
                                    brInstruction(labelIndex(0)),
                                    AdminInstruction.EndBlock,
                                ),
                            ),
                            AdminInstruction.EndBlock,
                            AdminInstruction.EndFunction,
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val result = JumpPass(context, module)

        assertEquals(
            listOf(
                AdminInstruction.PushHandler(
                    handlers = tryTable.handlers,
                    offsets = listOf(3),
                    payloadDestinationSlots = emptyList(),
                    endOffset = 1,
                ),
                AdminInstruction.PopHandler,
                AdminInstruction.Jump(offset = 2),
                AdminInstruction.EndFunction,
            ),
            result.functions[0].body.instructions,
        )
    }

    @Test
    fun `lowers function around try_table that targets the function label`() {
        val tryTable = ControlInstruction.TryTable(
            blockType = emptyBlockType(),
            handlers = listOf(catchAllHandler(labelIndex = labelIndex(0))),
            instructions = listOf(AdminInstruction.EndBlock),
        )
        val expectedInstructions = listOf(
            AdminInstruction.PushHandler(
                handlers = tryTable.handlers,
                offsets = listOf(3),
                payloadDestinationSlots = emptyList(),
                endOffset = 1,
            ),
            AdminInstruction.PopHandler,
            AdminInstruction.Jump(offset = 3),
            AdminInstruction.EndFunction,
        )
        val module = module(
            functions = listOf(
                function(
                    frameSlotMode = true,
                    body = expression(
                        instructions = listOf(
                            tryTable,
                            blockInstruction(
                                instructions = listOf(
                                    brInstruction(labelIndex(0)),
                                    AdminInstruction.EndBlock,
                                ),
                            ),
                            AdminInstruction.EndBlock,
                            AdminInstruction.EndFunction,
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val result = JumpPass(context, module)

        assertEquals(expectedInstructions, result.functions[0].body.instructions)
    }

    @Test
    fun `lowers nested control inside try_table bodies into direct jumps`() {
        val tryTable = ControlInstruction.TryTable(
            blockType = emptyBlockType(),
            handlers = emptyList(),
            instructions = listOf(
                blockInstruction(
                    instructions = listOf(
                        brInstruction(labelIndex(0)),
                        AdminInstruction.EndBlock,
                    ),
                ),
                AdminInstruction.EndBlock,
            ),
        )
        val module = module(
            functions = listOf(
                function(
                    frameSlotMode = true,
                    body = expression(
                        instructions = listOf(
                            tryTable,
                            AdminInstruction.EndBlock,
                            AdminInstruction.EndFunction,
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val result = JumpPass(context, module)

        assertEquals(
            listOf(
                AdminInstruction.PushHandler(
                    handlers = emptyList(),
                    offsets = emptyList(),
                    payloadDestinationSlots = emptyList(),
                    endOffset = 2,
                ),
                AdminInstruction.Jump(offset = 2),
                AdminInstruction.PopHandler,
                AdminInstruction.EndFunction,
            ),
            result.functions[0].body.instructions,
        )
    }
}
