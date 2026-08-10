package io.github.charlietap.chasm.compiler.passes

import io.github.charlietap.chasm.fixture.ir.instruction.arrayNewDefaultInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.blockInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.brIfInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.brInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.brOnCastFailInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.brOnNonNullInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.brOnNullInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.brTableInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.callIndirectInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.callInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.callRefInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.catchAllRefHandler
import io.github.charlietap.chasm.fixture.ir.instruction.dropInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.elseInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.endInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.fixture.ir.instruction.frameSlotDestination
import io.github.charlietap.chasm.fixture.ir.instruction.frameSlotOperand
import io.github.charlietap.chasm.fixture.ir.instruction.fusedBrIf
import io.github.charlietap.chasm.fixture.ir.instruction.fusedBrIfCondition
import io.github.charlietap.chasm.fixture.ir.instruction.fusedBrOnCastFail
import io.github.charlietap.chasm.fixture.ir.instruction.fusedBrOnNonNull
import io.github.charlietap.chasm.fixture.ir.instruction.fusedBrOnNull
import io.github.charlietap.chasm.fixture.ir.instruction.fusedBrTable
import io.github.charlietap.chasm.fixture.ir.instruction.fusedCall
import io.github.charlietap.chasm.fixture.ir.instruction.fusedCallIndirect
import io.github.charlietap.chasm.fixture.ir.instruction.fusedCallRef
import io.github.charlietap.chasm.fixture.ir.instruction.fusedGlobalGet
import io.github.charlietap.chasm.fixture.ir.instruction.fusedGlobalSet
import io.github.charlietap.chasm.fixture.ir.instruction.fusedI32Add
import io.github.charlietap.chasm.fixture.ir.instruction.fusedI32Const
import io.github.charlietap.chasm.fixture.ir.instruction.fusedI32Eqz
import io.github.charlietap.chasm.fixture.ir.instruction.fusedIf
import io.github.charlietap.chasm.fixture.ir.instruction.fusedIfCondition
import io.github.charlietap.chasm.fixture.ir.instruction.fusedLocalSet
import io.github.charlietap.chasm.fixture.ir.instruction.fusedLocalTee
import io.github.charlietap.chasm.fixture.ir.instruction.fusedReturnCall
import io.github.charlietap.chasm.fixture.ir.instruction.fusedReturnCallIndirect
import io.github.charlietap.chasm.fixture.ir.instruction.fusedReturnCallRef
import io.github.charlietap.chasm.fixture.ir.instruction.fusedSelect
import io.github.charlietap.chasm.fixture.ir.instruction.globalGetInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.i32AddInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.i32ConstInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.i32ConstOperand
import io.github.charlietap.chasm.fixture.ir.instruction.i32EqzCondition
import io.github.charlietap.chasm.fixture.ir.instruction.i32EqzInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.i32LoadInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.i64ConstOperand
import io.github.charlietap.chasm.fixture.ir.instruction.ifInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.localGetInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.localGetOperand
import io.github.charlietap.chasm.fixture.ir.instruction.localSetDestination
import io.github.charlietap.chasm.fixture.ir.instruction.localTeeInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.loopInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.memArg
import io.github.charlietap.chasm.fixture.ir.instruction.memoryGrowInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.memorySizeInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.refAsNonNullInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.refFuncInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.refNullInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.returnCallIndirectInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.returnCallInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.returnCallRefInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.returnInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.selectInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.signedTypeIndexBlockType
import io.github.charlietap.chasm.fixture.ir.instruction.structGetInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.structNewInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.tableSizeInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.unreachableInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.valueBlockType
import io.github.charlietap.chasm.fixture.ir.instruction.valueStackDestination
import io.github.charlietap.chasm.fixture.ir.instruction.valueStackOperand
import io.github.charlietap.chasm.fixture.ir.module.fieldIndex
import io.github.charlietap.chasm.fixture.ir.module.function
import io.github.charlietap.chasm.fixture.ir.module.functionImport
import io.github.charlietap.chasm.fixture.ir.module.functionImportDescriptor
import io.github.charlietap.chasm.fixture.ir.module.functionIndex
import io.github.charlietap.chasm.fixture.ir.module.globalIndex
import io.github.charlietap.chasm.fixture.ir.module.labelIndex
import io.github.charlietap.chasm.fixture.ir.module.local
import io.github.charlietap.chasm.fixture.ir.module.localIndex
import io.github.charlietap.chasm.fixture.ir.module.memoryIndex
import io.github.charlietap.chasm.fixture.ir.module.module
import io.github.charlietap.chasm.fixture.ir.module.tableIndex
import io.github.charlietap.chasm.fixture.ir.module.type
import io.github.charlietap.chasm.fixture.ir.module.typeIndex
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.finalSubType
import io.github.charlietap.chasm.fixture.type.functionHeapType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.immutableFieldType
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.fixture.type.refNonNullReferenceType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.referenceValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.structCompositeType
import io.github.charlietap.chasm.fixture.type.structType
import io.github.charlietap.chasm.fixture.type.valueStorageType
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.AggregateSuperInstruction
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.MemorySuperInstruction
import io.github.charlietap.chasm.ir.instruction.NumericSuperInstruction
import io.github.charlietap.chasm.ir.instruction.ReferenceSuperInstruction
import io.github.charlietap.chasm.ir.instruction.TableSuperInstruction
import io.github.charlietap.chasm.type.AbstractHeapType
import kotlin.test.Test
import kotlin.test.assertEquals

class FrameSlotPassTest {

    @Test
    fun `can lower fused i32 add into frame slots`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            fusedI32Add(
                                left = localGetOperand(localIndex(0)),
                                right = localGetOperand(localIndex(1)),
                                destination = valueStackDestination(),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(3, actual.frameSlots)
        assertEquals(
            listOf(
                fusedI32Add(
                    left = frameSlotOperand(0),
                    right = frameSlotOperand(1),
                    destination = frameSlotDestination(2),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can reuse consumed slot for stacked i32 eqz`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            fusedI32Add(
                                left = localGetOperand(localIndex(0)),
                                right = localGetOperand(localIndex(1)),
                                destination = valueStackDestination(),
                            ),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(3, actual.frameSlots)
        assertEquals(
            listOf(
                fusedI32Add(
                    left = frameSlotOperand(0),
                    right = frameSlotOperand(1),
                    destination = frameSlotDestination(2),
                ),
                fusedI32Eqz(
                    operand = frameSlotOperand(2),
                    destination = frameSlotDestination(2),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower i32 load address and local destination into frame slots`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    locals = listOf(local(localIndex(2), i32ValueType())),
                    body = expression(
                        instructions = listOf(
                            fusedI32Add(
                                left = localGetOperand(localIndex(0)),
                                right = localGetOperand(localIndex(1)),
                                destination = valueStackDestination(),
                            ),
                            MemorySuperInstruction.I32Load(
                                addressOperand = valueStackOperand(),
                                destination = localSetDestination(localIndex(2)),
                                memoryIndex = memoryIndex(0),
                                memArg = memArg(),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(4, actual.frameSlots)
        assertEquals(
            listOf(
                fusedI32Add(
                    left = frameSlotOperand(0),
                    right = frameSlotOperand(1),
                    destination = frameSlotDestination(3),
                ),
                MemorySuperInstruction.I32Load(
                    addressOperand = frameSlotOperand(3),
                    destination = frameSlotDestination(2),
                    memoryIndex = memoryIndex(0),
                    memArg = memArg(),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower a fallthrough block by moving into canonical result slots`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            blockInstruction(
                                blockType = valueBlockType(i32ValueType()),
                            ),
                            fusedI32Add(
                                left = localGetOperand(localIndex(0)),
                                right = localGetOperand(localIndex(1)),
                                destination = valueStackDestination(),
                            ),
                            endInstruction(),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(3, actual.frameSlots)
        assertEquals(
            listOf(
                blockInstruction(
                    blockType = valueBlockType(i32ValueType()),
                ),
                fusedI32Add(
                    left = frameSlotOperand(0),
                    right = frameSlotOperand(1),
                    destination = frameSlotDestination(2),
                ),
                endInstruction(),
                fusedI32Eqz(
                    operand = frameSlotOperand(2),
                    destination = frameSlotDestination(2),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower a branch to the current block into canonical result slots`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            blockInstruction(
                                blockType = valueBlockType(i32ValueType()),
                            ),
                            fusedI32Add(
                                left = localGetOperand(localIndex(0)),
                                right = localGetOperand(localIndex(1)),
                                destination = valueStackDestination(),
                            ),
                            brInstruction(labelIndex(0)),
                            endInstruction(),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(3, actual.frameSlots)
        assertEquals(
            listOf(
                blockInstruction(
                    blockType = valueBlockType(i32ValueType()),
                ),
                fusedI32Add(
                    left = frameSlotOperand(0),
                    right = frameSlotOperand(1),
                    destination = frameSlotDestination(2),
                ),
                brInstruction(labelIndex(0)),
                endInstruction(),
                fusedI32Eqz(
                    operand = frameSlotOperand(2),
                    destination = frameSlotDestination(2),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower plain br_if by making the condition explicit`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            blockInstruction(
                                blockType = valueBlockType(i32ValueType()),
                            ),
                            fusedI32Add(
                                left = localGetOperand(localIndex(0)),
                                right = localGetOperand(localIndex(1)),
                                destination = valueStackDestination(),
                            ),
                            fusedI32Eqz(
                                operand = localGetOperand(localIndex(0)),
                                destination = valueStackDestination(),
                            ),
                            brIfInstruction(labelIndex(0)),
                            endInstruction(),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(4, actual.frameSlots)
        assertEquals(
            listOf(
                blockInstruction(
                    blockType = valueBlockType(i32ValueType()),
                ),
                fusedI32Add(
                    left = frameSlotOperand(0),
                    right = frameSlotOperand(1),
                    destination = frameSlotDestination(2),
                ),
                fusedBrIfCondition(
                    condition = i32EqzCondition(frameSlotOperand(0)),
                    labelIndex = labelIndex(0),
                ),
                endInstruction(),
                fusedI32Eqz(
                    operand = frameSlotOperand(2),
                    destination = frameSlotDestination(2),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `fuses local tee producer directly into the slot read by br_if`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                        i32ValueType(),
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            blockInstruction(),
                            localGetInstruction(localIndex(0)),
                            localGetInstruction(localIndex(1)),
                            i32AddInstruction(),
                            localTeeInstruction(localIndex(2)),
                            brIfInstruction(labelIndex(0)),
                            endInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)
        val fusedModule = FusionPass(context, module)

        val actual = FrameSlotPass(context, fusedModule).functions[0]

        assertEquals(4, actual.frameSlots)
        assertEquals(
            listOf(
                blockInstruction(),
                fusedI32Add(
                    left = frameSlotOperand(0),
                    right = frameSlotOperand(1),
                    destination = frameSlotDestination(2),
                ),
                fusedBrIf(
                    operand = frameSlotOperand(2),
                    labelIndex = labelIndex(0),
                ),
                endInstruction(),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower an explicit return into canonical return slots`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                        i32ValueType(),
                    ),
                ),
                results = resultType(
                    types = listOf(
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            fusedI32Add(
                                left = localGetOperand(localIndex(0)),
                                right = localGetOperand(localIndex(1)),
                                destination = valueStackDestination(),
                            ),
                            returnInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(3, actual.frameSlots)
        assertEquals(
            listOf(
                fusedI32Add(
                    left = frameSlotOperand(0),
                    right = frameSlotOperand(1),
                    destination = frameSlotDestination(2),
                ),
                *frameSlotCopyInstructions(listOf(2), listOf(0)).toTypedArray(),
                returnInstruction(),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower a br_table by making the index explicit`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            blockInstruction(
                                blockType = valueBlockType(i32ValueType()),
                            ),
                            fusedI32Add(
                                left = localGetOperand(localIndex(0)),
                                right = localGetOperand(localIndex(1)),
                                destination = valueStackDestination(),
                            ),
                            fusedI32Eqz(
                                operand = localGetOperand(localIndex(0)),
                                destination = valueStackDestination(),
                            ),
                            brTableInstruction(
                                labelIndices = listOf(labelIndex(0)),
                                defaultLabelIndex = labelIndex(0),
                            ),
                            endInstruction(),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(4, actual.frameSlots)
        assertEquals(
            listOf(
                blockInstruction(
                    blockType = valueBlockType(i32ValueType()),
                ),
                fusedI32Add(
                    left = frameSlotOperand(0),
                    right = frameSlotOperand(1),
                    destination = frameSlotDestination(2),
                ),
                fusedI32Eqz(
                    operand = frameSlotOperand(0),
                    destination = frameSlotDestination(3),
                ),
                fusedBrTable(
                    operand = frameSlotOperand(3),
                    labelIndices = listOf(labelIndex(0)),
                    defaultLabelIndex = labelIndex(0),
                    takenInstructions = listOf(emptyList()),
                    defaultTakenInstructions = emptyList(),
                ),
                endInstruction(),
                fusedI32Eqz(
                    operand = frameSlotOperand(2),
                    destination = frameSlotDestination(2),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower a plain if by making the condition explicit`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            fusedI32Eqz(
                                operand = localGetOperand(localIndex(0)),
                                destination = valueStackDestination(),
                            ),
                            ifInstruction(
                                blockType = valueBlockType(i32ValueType()),
                            ),
                            fusedI32Eqz(
                                operand = localGetOperand(localIndex(0)),
                                destination = valueStackDestination(),
                            ),
                            elseInstruction(),
                            fusedI32Eqz(
                                operand = localGetOperand(localIndex(0)),
                                destination = valueStackDestination(),
                            ),
                            endInstruction(),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                fusedIfCondition(
                    condition = i32EqzCondition(frameSlotOperand(0)),
                    blockType = valueBlockType(i32ValueType()),
                ),
                fusedI32Eqz(
                    operand = frameSlotOperand(0),
                    destination = frameSlotDestination(1),
                ),
                elseInstruction(),
                fusedI32Eqz(
                    operand = frameSlotOperand(0),
                    destination = frameSlotDestination(1),
                ),
                endInstruction(),
                fusedI32Eqz(
                    operand = frameSlotOperand(1),
                    destination = frameSlotDestination(1),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower a loop back edge into canonical parameter slots`() {
        val functionRecursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val loopRecursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(
                type(recursiveType = functionRecursiveType),
                type(recursiveType = loopRecursiveType),
            ),
            definedTypes = listOf(
                definedType(recursiveType = functionRecursiveType),
                definedType(recursiveType = loopRecursiveType),
            ),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            fusedI32Add(
                                left = localGetOperand(localIndex(0)),
                                right = localGetOperand(localIndex(1)),
                                destination = valueStackDestination(),
                            ),
                            loopInstruction(
                                blockType = signedTypeIndexBlockType(1),
                            ),
                            fusedI32Add(
                                left = localGetOperand(localIndex(0)),
                                right = localGetOperand(localIndex(1)),
                                destination = valueStackDestination(),
                            ),
                            brInstruction(labelIndex(0)),
                            endInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(4, actual.frameSlots)
        assertEquals(
            listOf(
                fusedI32Add(
                    left = frameSlotOperand(0),
                    right = frameSlotOperand(1),
                    destination = frameSlotDestination(2),
                ),
                loopInstruction(
                    blockType = signedTypeIndexBlockType(1),
                ),
                fusedI32Add(
                    left = frameSlotOperand(0),
                    right = frameSlotOperand(1),
                    destination = frameSlotDestination(3),
                ),
                *frameSlotCopyInstructions(listOf(3), listOf(2)).toTypedArray(),
                brInstruction(labelIndex(0)),
                endInstruction(),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower a loop br_if using canonical parameter slots`() {
        val functionRecursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val loopRecursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                    ),
                ),
                results = resultType(
                    types = listOf(
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(
                type(recursiveType = functionRecursiveType),
                type(recursiveType = loopRecursiveType),
            ),
            definedTypes = listOf(
                definedType(recursiveType = functionRecursiveType),
                definedType(recursiveType = loopRecursiveType),
            ),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            fusedI32Add(
                                left = localGetOperand(localIndex(0)),
                                right = localGetOperand(localIndex(1)),
                                destination = valueStackDestination(),
                            ),
                            loopInstruction(
                                blockType = signedTypeIndexBlockType(1),
                            ),
                            fusedI32Add(
                                left = localGetOperand(localIndex(0)),
                                right = localGetOperand(localIndex(1)),
                                destination = valueStackDestination(),
                            ),
                            fusedI32Eqz(
                                operand = localGetOperand(localIndex(0)),
                                destination = valueStackDestination(),
                            ),
                            brIfInstruction(labelIndex(0)),
                            fusedI32Add(
                                left = valueStackOperand(),
                                right = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                            endInstruction(),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(5, actual.frameSlots)
        assertEquals(
            listOf(
                fusedI32Add(
                    left = frameSlotOperand(0),
                    right = frameSlotOperand(1),
                    destination = frameSlotDestination(2),
                ),
                loopInstruction(
                    blockType = signedTypeIndexBlockType(1),
                ),
                fusedI32Add(
                    left = frameSlotOperand(0),
                    right = frameSlotOperand(1),
                    destination = frameSlotDestination(3),
                ),
                fusedBrIfCondition(
                    condition = i32EqzCondition(frameSlotOperand(0)),
                    labelIndex = labelIndex(0),
                    sourceSlots = listOf(3),
                    destinationSlots = listOf(2),
                ),
                fusedI32Add(
                    left = frameSlotOperand(3),
                    right = frameSlotOperand(2),
                    destination = frameSlotDestination(2),
                ),
                endInstruction(),
                fusedI32Eqz(
                    operand = frameSlotOperand(2),
                    destination = frameSlotDestination(2),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `materializes loop entry params before lowering a loop body`() {
        val functionRecursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val loopRecursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(
                type(recursiveType = functionRecursiveType),
                type(recursiveType = loopRecursiveType),
            ),
            definedTypes = listOf(
                definedType(recursiveType = functionRecursiveType),
                definedType(recursiveType = loopRecursiveType),
            ),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            fusedI32Const(
                                value = 1,
                                destination = valueStackDestination(),
                            ),
                            localGetInstruction(localIndex(0)),
                            loopInstruction(
                                blockType = signedTypeIndexBlockType(1),
                            ),
                            fusedI32Add(
                                left = valueStackOperand(),
                                right = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                            localGetInstruction(localIndex(0)),
                            brInstruction(labelIndex(0)),
                            endInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(3, actual.frameSlots)
        assertEquals(
            listOf(
                fusedI32Const(
                    value = 1,
                    destination = frameSlotDestination(1),
                ),
                *frameSlotCopyInstructions(listOf(0), listOf(2)).toTypedArray(),
                loopInstruction(
                    blockType = signedTypeIndexBlockType(1),
                ),
                fusedI32Add(
                    left = frameSlotOperand(2),
                    right = frameSlotOperand(1),
                    destination = frameSlotDestination(1),
                ),
                *frameSlotCopyInstructions(listOf(0), listOf(2)).toTypedArray(),
                brInstruction(labelIndex(0)),
                endInstruction(),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower br_on_null by branching with slots below the tested reference`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                        referenceValueType(refNullReferenceType()),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            blockInstruction(
                                blockType = valueBlockType(i32ValueType()),
                            ),
                            localGetInstruction(localIndex(0)),
                            localGetInstruction(localIndex(1)),
                            brOnNullInstruction(labelIndex(0)),
                            dropInstruction(),
                            endInstruction(),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(4, actual.frameSlots)
        assertEquals(
            listOf(
                blockInstruction(
                    blockType = valueBlockType(i32ValueType()),
                ),
                fusedBrOnNull(
                    operand = frameSlotOperand(1),
                    labelIndex = labelIndex(0),
                    takenInstructions = buildList {
                        addAll(frameSlotCopyInstructions(listOf(0), listOf(2)))
                    },
                ),
                *frameSlotCopyInstructions(listOf(0), listOf(2)).toTypedArray(),
                endInstruction(),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower br_on_nonnull while consuming the fallthrough operand`() {
        val referenceType = refNullReferenceType()
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        referenceValueType(referenceType),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            blockInstruction(
                                blockType = valueBlockType(referenceValueType(referenceType)),
                            ),
                            localGetInstruction(localIndex(0)),
                            brOnNonNullInstruction(labelIndex(0)),
                            localGetInstruction(localIndex(0)),
                            endInstruction(),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                blockInstruction(
                    blockType = valueBlockType(referenceValueType(referenceType)),
                ),
                fusedBrOnNonNull(
                    operand = frameSlotOperand(0),
                    labelIndex = labelIndex(0),
                    takenInstructions = buildList {
                        addAll(frameSlotCopyInstructions(listOf(0), listOf(1)))
                    },
                ),
                *frameSlotCopyInstructions(listOf(0), listOf(1)).toTypedArray(),
                endInstruction(),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower br_on_cast_fail without consuming the fallthrough operand`() {
        val srcReferenceType = refNullReferenceType()
        val dstReferenceType = refNonNullReferenceType()
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        referenceValueType(srcReferenceType),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            blockInstruction(
                                blockType = valueBlockType(referenceValueType(srcReferenceType)),
                            ),
                            localGetInstruction(localIndex(0)),
                            brOnCastFailInstruction(
                                labelIndex = labelIndex(0),
                                srcReferenceType = srcReferenceType,
                                dstReferenceType = dstReferenceType,
                            ),
                            endInstruction(),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                blockInstruction(
                    blockType = valueBlockType(referenceValueType(srcReferenceType)),
                ),
                fusedBrOnCastFail(
                    operand = frameSlotOperand(0),
                    labelIndex = labelIndex(0),
                    srcReferenceType = srcReferenceType,
                    dstReferenceType = dstReferenceType,
                    takenInstructions = buildList {
                        addAll(frameSlotCopyInstructions(listOf(0), listOf(1)))
                    },
                ),
                *frameSlotCopyInstructions(listOf(0), listOf(1)).toTypedArray(),
                endInstruction(),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower try_table while threading handler payload destination slots`() {
        val exceptionReferenceType = refNonNullReferenceType(AbstractHeapType.Exception)
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        referenceValueType(exceptionReferenceType),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            blockInstruction(
                                blockType = valueBlockType(referenceValueType(exceptionReferenceType)),
                            ),
                            ControlInstruction.TryTable(
                                blockType = valueBlockType(referenceValueType(exceptionReferenceType)),
                                handlers = listOf(catchAllRefHandler(labelIndex(0))),
                            ),
                            localGetInstruction(localIndex(0)),
                            endInstruction(),
                            endInstruction(),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                blockInstruction(
                    blockType = valueBlockType(referenceValueType(exceptionReferenceType)),
                ),
                ControlInstruction.TryTable(
                    blockType = valueBlockType(referenceValueType(exceptionReferenceType)),
                    handlers = listOf(catchAllRefHandler(labelIndex(0))),
                    payloadDestinationSlots = listOf(listOf(1)),
                ),
                *frameSlotCopyInstructions(listOf(0), listOf(1)).toTypedArray(),
                endInstruction(),
                endInstruction(),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can materialize throw_ref operands from frame slots`() {
        val exceptionReferenceType = refNonNullReferenceType(AbstractHeapType.Exception)
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        referenceValueType(exceptionReferenceType),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            ControlInstruction.ThrowRef,
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                ControlSuperInstruction.ThrowRef(frameSlotOperand(0)),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower global get into a frame slot before numeric consumers`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            globalGetInstruction(globalIndex(0)),
                            fusedI32Add(
                                left = valueStackOperand(),
                                right = localGetOperand(localIndex(0)),
                                destination = valueStackDestination(),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                fusedGlobalGet(
                    globalIdx = globalIndex(0),
                    destination = frameSlotDestination(1),
                ),
                fusedI32Add(
                    left = frameSlotOperand(1),
                    right = frameSlotOperand(0),
                    destination = frameSlotDestination(1),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower unreachable fused numeric tails into frame slots`() {
        val recursiveType = functionRecursiveType(functionType())
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            unreachableInstruction(),
                            fusedI32Add(
                                left = valueStackOperand(),
                                right = i32ConstOperand(1),
                                destination = valueStackDestination(),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(1, actual.frameSlots)
        assertEquals(
            listOf(
                unreachableInstruction(),
                fusedI32Add(
                    left = frameSlotOperand(0),
                    right = i32ConstOperand(1),
                    destination = frameSlotDestination(0),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower unreachable fused numeric tails inside nested blocks`() {
        val recursiveType = functionRecursiveType(functionType())
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            blockInstruction(),
                            unreachableInstruction(),
                            fusedI32Add(
                                left = valueStackOperand(),
                                right = i32ConstOperand(1),
                                destination = valueStackDestination(),
                            ),
                            endInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(1, actual.frameSlots)
        assertEquals(
            listOf(
                blockInstruction(),
                unreachableInstruction(),
                fusedI32Add(
                    left = frameSlotOperand(0),
                    right = i32ConstOperand(1),
                    destination = frameSlotDestination(0),
                ),
                endInstruction(),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `preserves nested conditionals in unreachable control`() {
        val recursiveType = functionRecursiveType(functionType())
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            unreachableInstruction(),
                            ifInstruction(),
                            elseInstruction(),
                            endInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(0, actual.frameSlots)
        assertEquals(
            listOf(
                unreachableInstruction(),
                fusedIf(operand = i32ConstOperand(0)),
                elseInstruction(),
                endInstruction(),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower unreachable fused global set tails into frame slots`() {
        val recursiveType = functionRecursiveType(functionType())
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            unreachableInstruction(),
                            fusedGlobalSet(
                                operand = valueStackOperand(),
                                globalIdx = globalIndex(0),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(1, actual.frameSlots)
        assertEquals(
            listOf(
                unreachableInstruction(),
                fusedGlobalSet(
                    operand = frameSlotOperand(0),
                    globalIdx = globalIndex(0),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower unreachable fused local tee tails into frame slots`() {
        val recursiveType = functionRecursiveType(functionType())
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    locals = listOf(local(type = i32ValueType())),
                    body = expression(
                        instructions = listOf(
                            unreachableInstruction(),
                            fusedLocalTee(
                                operand = valueStackOperand(),
                                localIdx = localIndex(0),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                unreachableInstruction(),
                fusedLocalSet(
                    operand = frameSlotOperand(1),
                    localIdx = localIndex(0),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `preserves live local aliases when overwriting a local`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(i32ValueType()),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            fusedLocalSet(
                                operand = i32ConstOperand(7),
                                localIdx = localIndex(0),
                            ),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                *frameSlotCopyInstructions(listOf(0), listOf(1)).toTypedArray(),
                fusedLocalSet(
                    operand = i32ConstOperand(7),
                    localIdx = localIndex(0),
                ),
                fusedI32Eqz(
                    operand = frameSlotOperand(1),
                    destination = frameSlotDestination(1),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `elides reachable local tee writes when the source is the target local`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(types = listOf(i32ValueType())),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            fusedLocalTee(
                                operand = localGetOperand(localIndex(0)),
                                localIdx = localIndex(0),
                            ),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                fusedI32Eqz(
                    operand = frameSlotOperand(0),
                    destination = frameSlotDestination(1),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `elides reachable local set writes when the source is the target local`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(types = listOf(i32ValueType())),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            fusedLocalSet(
                                operand = localGetOperand(localIndex(0)),
                                localIdx = localIndex(0),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(1, actual.frameSlots)
        assertEquals(emptyList(), actual.body.instructions)
    }

    @Test
    fun `keeps immediate local tee values virtual after the required local write`() {
        val recursiveType = functionRecursiveType(functionType())
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    locals = listOf(local(type = i32ValueType())),
                    body = expression(
                        instructions = listOf(
                            fusedLocalTee(
                                operand = i32ConstOperand(7),
                                localIdx = localIndex(0),
                            ),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                fusedLocalSet(
                    operand = i32ConstOperand(7),
                    localIdx = localIndex(0),
                ),
                fusedI32Eqz(
                    operand = i32ConstOperand(7),
                    destination = frameSlotDestination(1),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `preserves live aliases before a producer writes directly to a local`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(types = listOf(i32ValueType())),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            fusedI32Eqz(
                                operand = i32ConstOperand(0),
                                destination = localSetDestination(localIndex(0)),
                            ),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                *frameSlotCopyInstructions(listOf(0), listOf(1)).toTypedArray(),
                fusedI32Eqz(
                    operand = i32ConstOperand(0),
                    destination = frameSlotDestination(0),
                ),
                fusedI32Eqz(
                    operand = frameSlotOperand(1),
                    destination = frameSlotDestination(1),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `preserves live aliases before a destination only producer writes to a local`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(types = listOf(i32ValueType())),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            fusedI32Const(
                                value = 7,
                                destination = localSetDestination(localIndex(0)),
                            ),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                *frameSlotCopyInstructions(listOf(0), listOf(1)).toTypedArray(),
                fusedI32Const(
                    value = 7,
                    destination = frameSlotDestination(0),
                ),
                fusedI32Eqz(
                    operand = frameSlotOperand(1),
                    destination = frameSlotDestination(1),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `erases a synthetic local get after a folded tee producer`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(types = listOf(i32ValueType())),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            fusedI32Eqz(
                                operand = i32ConstOperand(0),
                                destination = localSetDestination(localIndex(0)),
                            ),
                            localGetInstruction(localIndex(0)),
                            fusedLocalSet(
                                operand = i32ConstOperand(7),
                                localIdx = localIndex(0),
                            ),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                fusedI32Eqz(
                    operand = i32ConstOperand(0),
                    destination = frameSlotDestination(0),
                ),
                *frameSlotCopyInstructions(listOf(0), listOf(1)).toTypedArray(),
                fusedLocalSet(
                    operand = i32ConstOperand(7),
                    localIdx = localIndex(0),
                ),
                fusedI32Eqz(
                    operand = frameSlotOperand(1),
                    destination = frameSlotDestination(1),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `preserves aliases around materialized operands for aggregate local destinations`() {
        val structRecursiveType = recursiveType(
            subTypes = listOf(
                finalSubType(
                    compositeType = structCompositeType(
                        structType = structType(
                            fields = listOf(immutableFieldType(valueStorageType(i32ValueType()))),
                        ),
                    ),
                ),
            ),
        )
        val functionRecursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        referenceValueType(refNullReferenceType(AbstractHeapType.Struct)),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(
                type(idx = typeIndex(0), recursiveType = structRecursiveType),
                type(idx = typeIndex(1), recursiveType = functionRecursiveType),
            ),
            definedTypes = listOf(
                definedType(recursiveType = structRecursiveType, typeIndex = 0),
                definedType(recursiveType = functionRecursiveType, typeIndex = 1),
            ),
            functions = listOf(
                function(
                    typeIndex = typeIndex(1),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            i32ConstInstruction(7),
                            AggregateSuperInstruction.StructNew(
                                destination = localSetDestination(localIndex(0)),
                                typeIndex = typeIndex(0),
                                fieldSlots = emptyList(),
                            ),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(3, actual.frameSlots)
        assertEquals(
            listOf(
                fusedI32Const(
                    value = 7,
                    destination = frameSlotDestination(2),
                ),
                *frameSlotCopyInstructions(listOf(0), listOf(1)).toTypedArray(),
                AggregateSuperInstruction.StructNew(
                    destination = frameSlotDestination(0),
                    typeIndex = typeIndex(0),
                    fieldSlots = listOf(2),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `preserves only unconsumed aliases when a producer reads and writes the same local`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(types = listOf(i32ValueType())),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            localGetInstruction(localIndex(0)),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = localSetDestination(localIndex(0)),
                            ),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(3, actual.frameSlots)
        assertEquals(
            listOf(
                *frameSlotCopyInstructions(listOf(0), listOf(1)).toTypedArray(),
                fusedI32Eqz(
                    operand = frameSlotOperand(0),
                    destination = frameSlotDestination(0),
                ),
                fusedI32Eqz(
                    operand = frameSlotOperand(1),
                    destination = frameSlotDestination(1),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `preserves a local tee result when its target is overwritten before consumption`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(types = listOf(i32ValueType(), i32ValueType())),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            fusedLocalTee(
                                operand = localGetOperand(localIndex(1)),
                                localIdx = localIndex(0),
                            ),
                            fusedLocalSet(
                                operand = i32ConstOperand(7),
                                localIdx = localIndex(0),
                            ),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(3, actual.frameSlots)
        assertEquals(
            listOf(
                fusedLocalSet(
                    operand = frameSlotOperand(1),
                    localIdx = localIndex(0),
                ),
                *frameSlotCopyInstructions(listOf(0), listOf(2)).toTypedArray(),
                fusedLocalSet(
                    operand = i32ConstOperand(7),
                    localIdx = localIndex(0),
                ),
                fusedI32Eqz(
                    operand = frameSlotOperand(2),
                    destination = frameSlotDestination(2),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `keeps a temporary tee source as the logical result after copying it to a local`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(types = listOf(i32ValueType(), i32ValueType())),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            fusedI32Add(
                                left = localGetOperand(localIndex(0)),
                                right = localGetOperand(localIndex(1)),
                                destination = valueStackDestination(),
                            ),
                            fusedLocalTee(
                                operand = valueStackOperand(),
                                localIdx = localIndex(0),
                            ),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(3, actual.frameSlots)
        assertEquals(
            listOf(
                fusedI32Add(
                    left = frameSlotOperand(0),
                    right = frameSlotOperand(1),
                    destination = frameSlotDestination(2),
                ),
                fusedLocalSet(
                    operand = frameSlotOperand(2),
                    localIdx = localIndex(0),
                ),
                fusedI32Eqz(
                    operand = frameSlotOperand(2),
                    destination = frameSlotDestination(2),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `materializes a self tee alias into a block result slot`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(types = listOf(i32ValueType())),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            blockInstruction(
                                blockType = valueBlockType(i32ValueType()),
                            ),
                            fusedLocalTee(
                                operand = localGetOperand(localIndex(0)),
                                localIdx = localIndex(0),
                            ),
                            endInstruction(),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                blockInstruction(
                    blockType = valueBlockType(i32ValueType()),
                ),
                *frameSlotCopyInstructions(listOf(0), listOf(1)).toTypedArray(),
                endInstruction(),
                fusedI32Eqz(
                    operand = frameSlotOperand(1),
                    destination = frameSlotDestination(1),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `preserves aliases for every local set in a multi destination producer`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(types = listOf(i64ValueType(), i64ValueType())),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            localGetInstruction(localIndex(1)),
                            NumericSuperInstruction.I64Add128(
                                leftLow = i64ConstOperand(1),
                                leftHigh = i64ConstOperand(2),
                                rightLow = i64ConstOperand(3),
                                rightHigh = i64ConstOperand(4),
                                destinationLow = localSetDestination(localIndex(0)),
                                destinationHigh = localSetDestination(localIndex(1)),
                            ),
                            dropInstruction(),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(4, actual.frameSlots)
        assertEquals(
            listOf(
                *frameSlotCopyInstructions(listOf(0), listOf(2)).toTypedArray(),
                *frameSlotCopyInstructions(listOf(1), listOf(3)).toTypedArray(),
                NumericSuperInstruction.I64Add128(
                    leftLow = i64ConstOperand(1),
                    leftHigh = i64ConstOperand(2),
                    rightLow = i64ConstOperand(3),
                    rightHigh = i64ConstOperand(4),
                    destinationLow = frameSlotDestination(0),
                    destinationHigh = frameSlotDestination(1),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `preserves a source local before mixed stack and local destinations overwrite it`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(types = listOf(i64ValueType())),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            NumericSuperInstruction.I64MulWideU(
                                left = localGetOperand(localIndex(0)),
                                right = i64ConstOperand(2),
                                destinationLow = valueStackDestination(),
                                destinationHigh = localSetDestination(localIndex(0)),
                            ),
                            dropInstruction(),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(3, actual.frameSlots)
        assertEquals(
            listOf(
                *frameSlotCopyInstructions(listOf(0), listOf(1)).toTypedArray(),
                NumericSuperInstruction.I64MulWideU(
                    left = frameSlotOperand(0),
                    right = i64ConstOperand(2),
                    destinationLow = frameSlotDestination(2),
                    destinationHigh = frameSlotDestination(0),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower unreachable fused select tails into frame slots`() {
        val recursiveType = functionRecursiveType(functionType())
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            unreachableInstruction(),
                            fusedSelect(
                                const = valueStackOperand(),
                                val1 = i32ConstOperand(11),
                                val2 = i32ConstOperand(22),
                                destination = valueStackDestination(),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                unreachableInstruction(),
                fusedSelect(
                    const = frameSlotOperand(0),
                    val1 = i32ConstOperand(11),
                    val2 = i32ConstOperand(22),
                    destination = frameSlotDestination(1),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower unreachable fused call tails into frame slots`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(i32ValueType()),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            unreachableInstruction(),
                            fusedCall(
                                operands = listOf(localGetOperand(localIndex(0))),
                                functionIndex = functionIndex(0),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                unreachableInstruction(),
                *frameSlotCopyInstructions(listOf(0), listOf(1)).toTypedArray(),
                fusedCall(
                    functionIndex = functionIndex(0),
                    callFrameSlot = 1,
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower fused calls with results directly into frame slots`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(i32ValueType()),
                ),
                results = resultType(
                    types = listOf(i32ValueType()),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            fusedCall(
                                operands = listOf(localGetOperand(localIndex(0))),
                                functionIndex = functionIndex(0),
                            ),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                *frameSlotCopyInstructions(listOf(0), listOf(1)).toTypedArray(),
                fusedCall(
                    functionIndex = functionIndex(0),
                    resultSlots = listOf(1),
                    callFrameSlot = 1,
                ),
                fusedI32Eqz(
                    operand = frameSlotOperand(1),
                    destination = frameSlotDestination(1),
                ),
                *frameSlotCopyInstructions(listOf(1), listOf(0)).toTypedArray(),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower imported fused calls with results into shared interface slots`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(i32ValueType()),
                ),
                results = resultType(
                    types = listOf(i32ValueType()),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            imports = listOf(
                functionImport(
                    descriptor = functionImportDescriptor(
                        typeIndex = typeIndex(0),
                    ),
                ),
            ),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            fusedCall(
                                operands = listOf(localGetOperand(localIndex(0))),
                                functionIndex = functionIndex(0),
                            ),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                *frameSlotCopyInstructions(listOf(0), listOf(1)).toTypedArray(),
                fusedCall(
                    functionIndex = functionIndex(0),
                    resultSlots = listOf(1),
                    callFrameSlot = 1,
                ),
                fusedI32Eqz(
                    operand = frameSlotOperand(1),
                    destination = frameSlotDestination(1),
                ),
                *frameSlotCopyInstructions(listOf(1), listOf(0)).toTypedArray(),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `keeps strict call frames above consumed temporary operands`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            i32ConstInstruction(1),
                            i32ConstInstruction(2),
                            callInstruction(functionIndex(0)),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(4, actual.frameSlots)
        assertEquals(
            listOf(
                fusedI32Const(
                    value = 1,
                    destination = frameSlotDestination(2),
                ),
                fusedI32Const(
                    value = 2,
                    destination = frameSlotDestination(3),
                ),
                fusedCall(
                    functionIndex = functionIndex(0),
                    callFrameSlot = 2,
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `keeps strict call frames above live local aliases`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(i32ValueType()),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            fusedCall(
                                operands = listOf(localGetOperand(localIndex(0))),
                                functionIndex = functionIndex(0),
                            ),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(3, actual.frameSlots)
        assertEquals(
            listOf(
                *frameSlotCopyInstructions(listOf(0), listOf(2)).toTypedArray(),
                fusedCall(
                    functionIndex = functionIndex(0),
                    callFrameSlot = 2,
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `keeps strict call frames above live immediates`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(i32ValueType()),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            i32ConstInstruction(7),
                            fusedCall(
                                operands = listOf(localGetOperand(localIndex(0))),
                                functionIndex = functionIndex(0),
                            ),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(3, actual.frameSlots)
        assertEquals(
            listOf(
                *frameSlotCopyInstructions(listOf(0), listOf(2)).toTypedArray(),
                fusedCall(
                    functionIndex = functionIndex(0),
                    callFrameSlot = 2,
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `keeps strict call frames above live materialized temps when explicit operands bypass the stack`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(i32ValueType()),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            i32EqzInstruction(),
                            fusedCall(
                                operands = listOf(localGetOperand(localIndex(0))),
                                functionIndex = functionIndex(0),
                            ),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(3, actual.frameSlots)
        assertEquals(
            listOf(
                fusedI32Eqz(
                    operand = frameSlotOperand(0),
                    destination = frameSlotDestination(1),
                ),
                *frameSlotCopyInstructions(listOf(0), listOf(2)).toTypedArray(),
                fusedCall(
                    functionIndex = functionIndex(0),
                    callFrameSlot = 2,
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `keeps strict call frames above live immediates when a later struct new consumes them`() {
        val calleeRecursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(i32ValueType()),
                ),
                results = resultType(
                    types = listOf(i32ValueType()),
                ),
            ),
        )
        val structRecursiveType = recursiveType(
            subTypes = listOf(
                finalSubType(
                    compositeType = structCompositeType(
                        structType = structType(
                            fields = List(3) {
                                immutableFieldType(valueStorageType(i32ValueType()))
                            },
                        ),
                    ),
                ),
            ),
        )
        val callerRecursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(i32ValueType()),
                ),
            ),
        )
        val module = module(
            types = listOf(
                type(idx = typeIndex(0), recursiveType = calleeRecursiveType),
                type(idx = typeIndex(1), recursiveType = structRecursiveType),
                type(idx = typeIndex(2), recursiveType = callerRecursiveType),
            ),
            definedTypes = listOf(
                definedType(recursiveType = calleeRecursiveType, typeIndex = 0),
                definedType(recursiveType = structRecursiveType, typeIndex = 1),
                definedType(recursiveType = callerRecursiveType, typeIndex = 2),
            ),
            functions = listOf(
                function(
                    idx = functionIndex(0),
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                        ),
                    ),
                ),
                function(
                    idx = functionIndex(1),
                    typeIndex = typeIndex(2),
                    body = expression(
                        instructions = listOf(
                            i32ConstInstruction(101),
                            i32ConstInstruction(0),
                            localGetInstruction(localIndex(0)),
                            callInstruction(functionIndex(0)),
                            structNewInstruction(typeIndex(1)),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[1]

        assertEquals(4, actual.frameSlots)
        assertEquals(
            listOf(
                *frameSlotCopyInstructions(listOf(0), listOf(3)).toTypedArray(),
                fusedCall(
                    functionIndex = functionIndex(0),
                    resultSlots = listOf(3),
                    callFrameSlot = 3,
                ),
                fusedI32Const(
                    value = 101,
                    destination = frameSlotDestination(1),
                ),
                fusedI32Const(
                    value = 0,
                    destination = frameSlotDestination(2),
                ),
                AggregateSuperInstruction.StructNew(
                    destination = frameSlotDestination(3),
                    typeIndex = typeIndex(1),
                    fieldSlots = listOf(1, 2, 3),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower return calls directly from frame slots`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(i32ValueType()),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            returnCallInstruction(functionIndex(0)),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                fusedReturnCall(
                    operands = listOf(frameSlotOperand(0)),
                    functionIndex = functionIndex(0),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower call_indirect directly from frame slots`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(i32ValueType()),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            i32ConstInstruction(0),
                            callIndirectInstruction(typeIndex(0), tableIndex(0)),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(3, actual.frameSlots)
        assertEquals(
            listOf(
                *frameSlotCopyInstructions(listOf(0), listOf(1)).toTypedArray(),
                fusedCallIndirect(
                    elementIndex = i32ConstOperand(0),
                    typeIndex = typeIndex(0),
                    tableIndex = tableIndex(0),
                    callFrameSlot = 1,
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower call_indirect results into shared interface slots`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(i32ValueType()),
                ),
                results = resultType(
                    types = listOf(i32ValueType()),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            i32ConstInstruction(0),
                            callIndirectInstruction(typeIndex(0), tableIndex(0)),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(3, actual.frameSlots)
        assertEquals(
            listOf(
                *frameSlotCopyInstructions(listOf(0), listOf(1)).toTypedArray(),
                fusedCallIndirect(
                    elementIndex = i32ConstOperand(0),
                    typeIndex = typeIndex(0),
                    tableIndex = tableIndex(0),
                    resultSlots = listOf(1),
                    callFrameSlot = 1,
                ),
                fusedI32Eqz(
                    operand = frameSlotOperand(1),
                    destination = frameSlotDestination(1),
                ),
                *frameSlotCopyInstructions(listOf(1), listOf(0)).toTypedArray(),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can stage call_indirect element indices before interface placement`() {
        val currentRecursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(i32ValueType()),
                ),
            ),
        )
        val calleeRecursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(i32ValueType()),
                ),
            ),
        )
        val module = module(
            types = listOf(
                type(recursiveType = currentRecursiveType),
                type(recursiveType = calleeRecursiveType),
            ),
            definedTypes = listOf(
                definedType(recursiveType = currentRecursiveType),
                definedType(recursiveType = calleeRecursiveType),
            ),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            i32EqzInstruction(),
                            fusedCallIndirect(
                                elementIndex = valueStackOperand(),
                                operands = listOf(localGetOperand(localIndex(0))),
                                typeIndex = typeIndex(1),
                                tableIndex = tableIndex(0),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(3, actual.frameSlots)
        assertEquals(
            listOf(
                fusedI32Eqz(
                    operand = frameSlotOperand(0),
                    destination = frameSlotDestination(1),
                ),
                *frameSlotCopyInstructions(listOf(1), listOf(2)).toTypedArray(),
                *frameSlotCopyInstructions(listOf(0), listOf(1)).toTypedArray(),
                fusedCallIndirect(
                    elementIndex = frameSlotOperand(2),
                    typeIndex = typeIndex(1),
                    tableIndex = tableIndex(0),
                    callFrameSlot = 1,
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower call_ref directly from frame slots`() {
        val currentRecursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                        referenceValueType(refNullReferenceType(functionHeapType())),
                    ),
                ),
            ),
        )
        val calleeRecursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(i32ValueType()),
                ),
            ),
        )
        val module = module(
            types = listOf(
                type(recursiveType = currentRecursiveType),
                type(recursiveType = calleeRecursiveType),
            ),
            definedTypes = listOf(
                definedType(recursiveType = currentRecursiveType),
                definedType(recursiveType = calleeRecursiveType),
            ),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            localGetInstruction(localIndex(1)),
                            callRefInstruction(typeIndex(1)),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(4, actual.frameSlots)
        assertEquals(
            listOf(
                *frameSlotCopyInstructions(listOf(0), listOf(2)).toTypedArray(),
                fusedCallRef(
                    functionReference = frameSlotOperand(1),
                    typeIndex = typeIndex(1),
                    callFrameSlot = 2,
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower call_ref results into shared interface slots`() {
        val currentRecursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                        referenceValueType(refNullReferenceType(functionHeapType())),
                    ),
                ),
                results = resultType(
                    types = listOf(i32ValueType()),
                ),
            ),
        )
        val calleeRecursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(i32ValueType()),
                ),
                results = resultType(
                    types = listOf(i32ValueType()),
                ),
            ),
        )
        val module = module(
            types = listOf(
                type(recursiveType = currentRecursiveType),
                type(recursiveType = calleeRecursiveType),
            ),
            definedTypes = listOf(
                definedType(recursiveType = currentRecursiveType),
                definedType(recursiveType = calleeRecursiveType),
            ),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            localGetInstruction(localIndex(1)),
                            callRefInstruction(typeIndex(1)),
                            fusedI32Eqz(
                                operand = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(4, actual.frameSlots)
        assertEquals(
            listOf(
                *frameSlotCopyInstructions(listOf(0), listOf(2)).toTypedArray(),
                fusedCallRef(
                    functionReference = frameSlotOperand(1),
                    typeIndex = typeIndex(1),
                    resultSlots = listOf(2),
                    callFrameSlot = 2,
                ),
                fusedI32Eqz(
                    operand = frameSlotOperand(2),
                    destination = frameSlotDestination(2),
                ),
                *frameSlotCopyInstructions(listOf(2), listOf(0)).toTypedArray(),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower return_call_indirect directly from frame slots`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(i32ValueType()),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            i32ConstInstruction(0),
                            returnCallIndirectInstruction(typeIndex(0), tableIndex(0)),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(3, actual.frameSlots)
        assertEquals(
            listOf(
                fusedReturnCallIndirect(
                    elementIndex = i32ConstOperand(0),
                    operands = listOf(frameSlotOperand(0)),
                    typeIndex = typeIndex(0),
                    tableIndex = tableIndex(0),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower return_call_ref directly from frame slots`() {
        val currentRecursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                        referenceValueType(refNullReferenceType(functionHeapType())),
                    ),
                ),
            ),
        )
        val calleeRecursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(i32ValueType()),
                ),
            ),
        )
        val module = module(
            types = listOf(
                type(recursiveType = currentRecursiveType),
                type(recursiveType = calleeRecursiveType),
            ),
            definedTypes = listOf(
                definedType(recursiveType = currentRecursiveType),
                definedType(recursiveType = calleeRecursiveType),
            ),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            localGetInstruction(localIndex(1)),
                            returnCallRefInstruction(typeIndex(1)),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(4, actual.frameSlots)
        assertEquals(
            listOf(
                fusedReturnCallRef(
                    functionReference = frameSlotOperand(1),
                    operands = listOf(frameSlotOperand(0)),
                    typeIndex = typeIndex(1),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower unreachable fused table get tails into frame slots`() {
        val recursiveType = functionRecursiveType(functionType())
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            unreachableInstruction(),
                            TableSuperInstruction.TableGet(
                                elementIndex = valueStackOperand(),
                                destination = valueStackDestination(),
                                tableIdx = tableIndex(0),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(1, actual.frameSlots)
        assertEquals(
            listOf(
                unreachableInstruction(),
                TableSuperInstruction.TableGet(
                    elementIndex = frameSlotOperand(0),
                    destination = frameSlotDestination(0),
                    tableIdx = tableIndex(0),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower unreachable fused ref eq tails into frame slots`() {
        val recursiveType = functionRecursiveType(functionType())
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            unreachableInstruction(),
                            ReferenceSuperInstruction.RefEq(
                                reference1 = valueStackOperand(),
                                reference2 = valueStackOperand(),
                                destination = valueStackDestination(),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                unreachableInstruction(),
                ReferenceSuperInstruction.RefEq(
                    reference1 = frameSlotOperand(0),
                    reference2 = frameSlotOperand(1),
                    destination = frameSlotDestination(1),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower unreachable fused struct get tails into frame slots`() {
        val recursiveType = functionRecursiveType(functionType())
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            unreachableInstruction(),
                            AggregateSuperInstruction.StructGet(
                                address = valueStackOperand(),
                                destination = valueStackDestination(),
                                typeIndex = typeIndex(0),
                                fieldIndex = fieldIndex(0),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(1, actual.frameSlots)
        assertEquals(
            listOf(
                unreachableInstruction(),
                AggregateSuperInstruction.StructGet(
                    address = frameSlotOperand(0),
                    destination = frameSlotDestination(0),
                    typeIndex = typeIndex(0),
                    fieldIndex = fieldIndex(0),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower raw stack numeric instructions into frame slots`() {
        val recursiveType = functionRecursiveType(functionType())
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            i32ConstInstruction(1),
                            i32ConstInstruction(2),
                            i32AddInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                fusedI32Add(
                    left = i32ConstOperand(1),
                    right = i32ConstOperand(2),
                    destination = frameSlotDestination(1),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower raw ref null without spill bridge`() {
        val recursiveType = functionRecursiveType(functionType())
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            refNullInstruction(refNullReferenceType().heapType),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(1, actual.frameSlots)
        assertEquals(
            listOf(
                ReferenceSuperInstruction.RefNull(
                    destination = frameSlotDestination(0),
                    type = refNullReferenceType().heapType,
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower raw ref func without spill bridge`() {
        val recursiveType = functionRecursiveType(functionType())
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            refFuncInstruction(functionIndex(0)),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(1, actual.frameSlots)
        assertEquals(
            listOf(
                ReferenceSuperInstruction.RefFunc(
                    destination = frameSlotDestination(0),
                    funcIdx = functionIndex(0),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower raw table size without spill bridge`() {
        val recursiveType = functionRecursiveType(functionType())
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            tableSizeInstruction(tableIndex(0)),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(1, actual.frameSlots)
        assertEquals(
            listOf(
                TableSuperInstruction.TableSize(
                    destination = frameSlotDestination(0),
                    tableIdx = tableIndex(0),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower raw memory size without spill bridge`() {
        val recursiveType = functionRecursiveType(functionType())
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            memorySizeInstruction(memoryIndex(0)),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(1, actual.frameSlots)
        assertEquals(
            listOf(
                MemorySuperInstruction.MemorySize(
                    destination = frameSlotDestination(0),
                    memoryIndex = memoryIndex(0),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower raw select into frame slots`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                        i32ValueType(),
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            localGetInstruction(localIndex(1)),
                            localGetInstruction(localIndex(2)),
                            selectInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(6, actual.frameSlots)
        assertEquals(
            listOf(
                fusedSelect(
                    const = frameSlotOperand(2),
                    val1 = frameSlotOperand(0),
                    val2 = frameSlotOperand(1),
                    destination = frameSlotDestination(4),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower raw memory loads between frame slot producers and consumers`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            i32LoadInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                MemorySuperInstruction.I32Load(
                    addressOperand = frameSlotOperand(0),
                    destination = frameSlotDestination(1),
                    memoryIndex = memoryIndex(0),
                    memArg = memArg(),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower raw aggregate stack instructions into frame slots`() {
        val referenceType = refNullReferenceType()
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        referenceValueType(referenceType),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            structGetInstruction(),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                AggregateSuperInstruction.StructGet(
                    address = frameSlotOperand(0),
                    destination = frameSlotDestination(1),
                    typeIndex = typeIndex(0),
                    fieldIndex = fieldIndex(0),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower memory grow into fused frame slots without bridge instructions`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            memoryGrowInstruction(memoryIndex(0)),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                MemorySuperInstruction.MemoryGrow(
                    pagesToAdd = frameSlotOperand(0),
                    destination = frameSlotDestination(1),
                    memoryIndex = memoryIndex(0),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower ref as non null into fused frame slots without bridge instructions`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        referenceValueType(refNullReferenceType()),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            refAsNonNullInstruction(),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                ReferenceSuperInstruction.RefAsNonNull(
                    value = frameSlotOperand(0),
                    destination = frameSlotDestination(1),
                ),
            ),
            actual.body.instructions,
        )
    }

    @Test
    fun `can lower array new default into fused frame slots without bridge instructions`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions = listOf(
                            localGetInstruction(localIndex(0)),
                            arrayNewDefaultInstruction(typeIndex(0)),
                            dropInstruction(),
                        ),
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FrameSlotPass(context, module).functions[0]

        assertEquals(2, actual.frameSlots)
        assertEquals(
            listOf(
                AggregateSuperInstruction.ArrayNewDefault(
                    size = frameSlotOperand(0),
                    destination = frameSlotDestination(1),
                    typeIndex = typeIndex(0),
                ),
            ),
            actual.body.instructions,
        )
    }
}

private fun frameSlotCopyInstructions(
    sourceSlots: List<Int>,
    destinationSlots: List<Int>,
): List<Instruction> = listOf(
    AdminInstruction.CopySlots(
        sourceSlots = sourceSlots,
        destinationSlots = destinationSlots,
    ),
)
