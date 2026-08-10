package io.github.charlietap.chasm.compiler.passes

import io.github.charlietap.chasm.fixture.ir.instruction.blockInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.brIfInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.brInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.catchAllHandler
import io.github.charlietap.chasm.fixture.ir.instruction.elseInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.emptyBlockType
import io.github.charlietap.chasm.fixture.ir.instruction.endInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.fixture.ir.instruction.frameSlotOperand
import io.github.charlietap.chasm.fixture.ir.instruction.fusedBrIf
import io.github.charlietap.chasm.fixture.ir.instruction.fusedBrOnCast
import io.github.charlietap.chasm.fixture.ir.instruction.fusedBrOnNull
import io.github.charlietap.chasm.fixture.ir.instruction.fusedBrTable
import io.github.charlietap.chasm.fixture.ir.instruction.fusedIf
import io.github.charlietap.chasm.fixture.ir.instruction.loopInstruction
import io.github.charlietap.chasm.fixture.ir.module.function
import io.github.charlietap.chasm.fixture.ir.module.labelIndex
import io.github.charlietap.chasm.fixture.ir.module.module
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class JumpPassTest {

    @Test
    fun `flattens block exits into direct jumps`() {
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions = listOf(
                            blockInstruction(),
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
                            endInstruction(),
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
                AdminInstruction.JumpIfCopy(
                    operand = frameSlotOperand(0),
                    sourceSlot = 1,
                    destinationSlot = 2,
                    offset = 2,
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
    fun `flattens unfused stack branches into direct jumps`() {
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions = listOf(
                            blockInstruction(),
                            brIfInstruction(labelIndex(0)),
                            endInstruction(),
                            AdminInstruction.EndFunction,
                        ),
                    ),
                ),
            ),
        )

        val result = JumpPass(passContext(module = module), module)

        assertEquals(
            listOf(
                AdminInstruction.JumpIf(FusedOperand.ValueStack, offset = 1),
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
                    body = expression(
                        instructions = listOf(
                            loopInstruction(),
                            brInstruction(labelIndex(0)),
                            endInstruction(),
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
    fun `flattens jump table taken paths into separate stubs`() {
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions = listOf(
                            blockInstruction(),
                            fusedBrTable(
                                operand = frameSlotOperand(0),
                                labelIndices = listOf(labelIndex(0)),
                                defaultLabelIndex = labelIndex(1),
                                takenInstructions = listOf(
                                    listOf(AdminInstruction.CopySlots(listOf(1), listOf(2))),
                                ),
                                defaultTakenInstructions = listOf(
                                    AdminInstruction.CopySlots(listOf(3), listOf(4)),
                                ),
                            ),
                            endInstruction(),
                            AdminInstruction.EndFunction,
                        ),
                    ),
                ),
            ),
        )

        val result = JumpPass(passContext(module = module), module)

        assertEquals(
            listOf(
                AdminInstruction.JumpTable(
                    operand = frameSlotOperand(0),
                    offsets = listOf(2),
                    defaultOffset = 4,
                ),
                AdminInstruction.EndFunction,
                AdminInstruction.CopySlots(listOf(1), listOf(2)),
                AdminInstruction.Jump(offset = 1),
                AdminInstruction.CopySlots(listOf(3), listOf(4)),
                AdminInstruction.Jump(offset = 1),
            ),
            result.functions[0].body.instructions,
        )
    }

    @Test
    fun `flattens reference branch taken paths into separate stubs`() {
        val brOnNull = fusedBrOnNull(
            operand = frameSlotOperand(0),
            labelIndex = labelIndex(0),
            takenInstructions = listOf(AdminInstruction.CopySlots(listOf(1), listOf(2))),
        )
        val brOnCast = fusedBrOnCast(
            operand = frameSlotOperand(3),
            labelIndex = labelIndex(0),
            takenInstructions = listOf(AdminInstruction.CopySlots(listOf(4), listOf(5))),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions = listOf(
                            blockInstruction(),
                            brOnNull,
                            endInstruction(),
                            blockInstruction(),
                            brOnCast,
                            endInstruction(),
                            AdminInstruction.EndFunction,
                        ),
                    ),
                ),
            ),
        )

        val result = JumpPass(passContext(module = module), module)

        assertEquals(
            listOf(
                AdminInstruction.JumpOnNull(frameSlotOperand(0), offset = 3),
                AdminInstruction.JumpOnCast(
                    operand = frameSlotOperand(3),
                    offset = 5,
                    srcReferenceType = brOnCast.srcReferenceType,
                    dstReferenceType = brOnCast.dstReferenceType,
                ),
                AdminInstruction.EndFunction,
                AdminInstruction.CopySlots(listOf(1), listOf(2)),
                AdminInstruction.Jump(offset = 1),
                AdminInstruction.CopySlots(listOf(4), listOf(5)),
                AdminInstruction.Jump(offset = 2),
            ),
            result.functions[0].body.instructions,
        )
    }

    @Test
    fun `moves handler exits into conditional taken stubs`() {
        val tryTable = ControlInstruction.TryTable(
            blockType = emptyBlockType(),
            handlers = emptyList(),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions = listOf(
                            tryTable,
                            fusedBrIf(
                                operand = frameSlotOperand(0),
                                labelIndex = labelIndex(1),
                            ),
                            endInstruction(),
                            AdminInstruction.EndFunction,
                        ),
                    ),
                ),
            ),
        )

        val result = JumpPass(passContext(module = module), module)

        assertEquals(
            listOf(
                AdminInstruction.PushHandler(
                    handlers = emptyList(),
                    offsets = emptyList(),
                    payloadDestinationSlots = emptyList(),
                    endOffset = 2,
                ),
                AdminInstruction.JumpIf(frameSlotOperand(0), offset = 4),
                AdminInstruction.PopHandler,
                AdminInstruction.EndFunction,
                AdminInstruction.PopHandler,
                AdminInstruction.Jump(offset = 3),
            ),
            result.functions[0].body.instructions,
        )
    }

    @Test
    fun `patches branches to the implicit function label`() {
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions = listOf(
                            loopInstruction(),
                            brInstruction(labelIndex(1)),
                            endInstruction(),
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
                    body = expression(
                        instructions = listOf(
                            fusedIf(
                                operand = frameSlotOperand(0),
                            ),
                            brInstruction(labelIndex(0)),
                            elseInstruction(),
                            AdminInstruction.CopySlots(
                                sourceSlots = listOf(1),
                                destinationSlots = listOf(2),
                            ),
                            endInstruction(),
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
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions = listOf(
                            tryTable,
                            brInstruction(labelIndex(0)),
                            endInstruction(),
                            blockInstruction(),
                            brInstruction(labelIndex(0)),
                            endInstruction(),
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
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions = listOf(
                            tryTable,
                            endInstruction(),
                            loopInstruction(),
                            brInstruction(labelIndex(0)),
                            endInstruction(),
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
                    body = expression(
                        instructions = listOf(
                            tryTable,
                            endInstruction(),
                            blockInstruction(),
                            brInstruction(labelIndex(0)),
                            endInstruction(),
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
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions = listOf(
                            tryTable,
                            blockInstruction(),
                            brInstruction(labelIndex(0)),
                            endInstruction(),
                            endInstruction(),
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

    @Test
    fun `rejects control streams that have not been normalized by frame slot lowering`() {
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions = listOf(
                            blockInstruction(),
                            loopInstruction(),
                            endInstruction(2),
                            AdminInstruction.EndFunction,
                        ),
                    ),
                ),
            ),
        )

        assertFailsWith<IllegalArgumentException> {
            JumpPass(passContext(module = module), module)
        }
    }
}
