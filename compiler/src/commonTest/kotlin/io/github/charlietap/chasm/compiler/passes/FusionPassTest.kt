package io.github.charlietap.chasm.compiler.passes

import io.github.charlietap.chasm.fixture.ir.instruction.blockInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.brIfInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.callInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.endInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.fixture.ir.instruction.f32AbsInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.fusedCall
import io.github.charlietap.chasm.fixture.ir.instruction.fusedF32Abs
import io.github.charlietap.chasm.fixture.ir.instruction.fusedI32Add
import io.github.charlietap.chasm.fixture.ir.instruction.fusedIf
import io.github.charlietap.chasm.fixture.ir.instruction.fusedLocalSet
import io.github.charlietap.chasm.fixture.ir.instruction.fusedLocalTee
import io.github.charlietap.chasm.fixture.ir.instruction.fusedSelect
import io.github.charlietap.chasm.fixture.ir.instruction.globalGetInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.globalSetInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.i32AddInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.i32ConstInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.i32ConstOperand
import io.github.charlietap.chasm.fixture.ir.instruction.i32LoadInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.i32SubInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.ifInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.localGetInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.localGetOperand
import io.github.charlietap.chasm.fixture.ir.instruction.localSetDestination
import io.github.charlietap.chasm.fixture.ir.instruction.localSetInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.localTeeInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.memArg
import io.github.charlietap.chasm.fixture.ir.instruction.nopInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.refAsNonNullInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.refFuncInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.selectInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.valueStackDestination
import io.github.charlietap.chasm.fixture.ir.instruction.valueStackOperand
import io.github.charlietap.chasm.fixture.ir.module.function
import io.github.charlietap.chasm.fixture.ir.module.functionIndex
import io.github.charlietap.chasm.fixture.ir.module.globalIndex
import io.github.charlietap.chasm.fixture.ir.module.local
import io.github.charlietap.chasm.fixture.ir.module.localIndex
import io.github.charlietap.chasm.fixture.ir.module.memory
import io.github.charlietap.chasm.fixture.ir.module.memoryIndex
import io.github.charlietap.chasm.fixture.ir.module.module
import io.github.charlietap.chasm.fixture.ir.module.type
import io.github.charlietap.chasm.fixture.ir.module.typeIndex
import io.github.charlietap.chasm.fixture.type.concreteDefinedTypeHeapType
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionHeapType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.refNonNullReferenceType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.referenceValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.ir.instruction.MemorySuperInstruction
import io.github.charlietap.chasm.ir.instruction.NumericSuperInstruction
import io.github.charlietap.chasm.ir.instruction.ReferenceSuperInstruction
import kotlin.test.Test
import kotlin.test.assertEquals

class FusionPassTest {

    @Test
    fun `can fuse an instructions operands despite no explicit destination`() {

        val instructions = listOf(
            localGetInstruction(localIndex(0)),
            localGetInstruction(localIndex(1)),
            i32AddInstruction(),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions,
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val expected = fusedI32Add(
            left = localGetOperand(localIndex(0)),
            right = localGetOperand(localIndex(1)),
            destination = valueStackDestination(),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(1, actual.size)
        assertEquals(expected, actual.first())
    }

    @Test
    fun `can fuse a unary operand instruction with an explicit destination`() {

        val instructions = listOf(
            localGetInstruction(localIndex(0)),
            f32AbsInstruction(),
            localSetInstruction(localIndex(2)),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions,
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val expected = fusedF32Abs(
            operand = localGetOperand(localIndex(0)),
            destination = localSetDestination(localIndex(2)),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(1, actual.size)
        assertEquals(expected, actual.first())
    }

    @Test
    fun `can fuse a binary operand instruction with an explicit destination`() {

        val instructions = listOf(
            localGetInstruction(localIndex(0)),
            localGetInstruction(localIndex(1)),
            i32AddInstruction(),
            localSetInstruction(localIndex(2)),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions,
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val expected = fusedI32Add(
            left = localGetOperand(localIndex(0)),
            right = localGetOperand(localIndex(1)),
            destination = localSetDestination(localIndex(2)),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(1, actual.size)
        assertEquals(expected, actual.first())
    }

    @Test
    fun `can fuse a binary operand instruction where only the right is fusable`() {

        val instructions = listOf(
            nopInstruction(),
            i32ConstInstruction(5),
            i32AddInstruction(),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions,
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val expected = listOf(
            nopInstruction(),
            fusedI32Add(
                left = valueStackOperand(),
                right = i32ConstOperand(5),
                destination = valueStackDestination(),
            ),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse an if instruction`() {

        val instructions = listOf(
            i32ConstInstruction(5),
            ifInstruction(),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions,
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val expected = listOf(
            fusedIf(
                operand = i32ConstOperand(5),
            ),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse instructions between flat control markers`() {

        val instructions = listOf(
            blockInstruction(),
            blockInstruction(),
            i32ConstInstruction(5),
            i32ConstInstruction(2),
            i32AddInstruction(),
            endInstruction(2),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions,
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val expected = listOf(
            blockInstruction(),
            blockInstruction(),
            fusedI32Add(
                left = i32ConstOperand(5),
                right = i32ConstOperand(2),
                destination = valueStackDestination(),
            ),
            endInstruction(2),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse local set`() {

        val instructions = listOf(
            i32ConstInstruction(5),
            localSetInstruction(localIndex(0)),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions,
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val expected = listOf(
            fusedLocalSet(
                operand = i32ConstOperand(5),
                localIdx = localIndex(0),
            ),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse a producer into local tee while retaining its stack result`() {
        val instructions = listOf(
            localGetInstruction(localIndex(0)),
            localGetInstruction(localIndex(1)),
            i32AddInstruction(),
            localTeeInstruction(localIndex(2)),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(instructions),
                ),
            ),
        )
        val context = passContext(module = module)

        val expected = listOf(
            fusedI32Add(
                left = localGetOperand(localIndex(0)),
                right = localGetOperand(localIndex(1)),
                destination = localSetDestination(localIndex(2)),
            ),
            localGetInstruction(localIndex(2)),
        )

        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse a memory load into local tee while retaining its stack result`() {
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(types = listOf(i32ValueType())),
            ),
        )
        val instructions = listOf(
            localGetInstruction(localIndex(0)),
            i32LoadInstruction(memoryIndex(0), memArg()),
            localTeeInstruction(localIndex(1)),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    locals = listOf(local(localIndex(1), i32ValueType())),
                    body = expression(instructions),
                ),
            ),
            memories = listOf(memory(memoryIndex(0))),
        )
        val context = passContext(module = module)

        val expected = listOf(
            MemorySuperInstruction.I32Load(
                addressOperand = localGetOperand(localIndex(0)),
                destination = localSetDestination(localIndex(1)),
                memoryIndex = memoryIndex(0),
                memArg = memArg(),
            ),
            localGetInstruction(localIndex(1)),
        )

        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse a reference producer into local tee while retaining its stack result`() {
        val recursiveType = functionRecursiveType(functionType())
        val functionDefinedType = definedType(recursiveType = recursiveType)
        val functionReferenceType = refNonNullReferenceType(
            concreteDefinedTypeHeapType(functionDefinedType),
        )
        val instructions = listOf(
            refFuncInstruction(functionIndex(0)),
            localTeeInstruction(localIndex(0)),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(functionDefinedType),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    locals = listOf(
                        local(
                            localIndex(0),
                            referenceValueType(functionReferenceType),
                        ),
                    ),
                    body = expression(instructions),
                ),
            ),
        )
        val context = passContext(module = module)

        val expected = listOf(
            ReferenceSuperInstruction.RefFunc(
                destination = localSetDestination(localIndex(0)),
                funcIdx = functionIndex(0),
            ),
            localGetInstruction(localIndex(0)),
        )

        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse ref as non null into local tee while retaining its stack result`() {
        val heapType = functionHeapType()
        val nullableFunctionReference = refNullReferenceType(heapType)
        val functionReference = refNonNullReferenceType(heapType)
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(referenceValueType(nullableFunctionReference)),
                ),
            ),
        )
        val instructions = listOf(
            localGetInstruction(localIndex(0)),
            refAsNonNullInstruction(),
            localTeeInstruction(localIndex(1)),
        )
        val module = module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    locals = listOf(
                        local(
                            localIndex(1),
                            referenceValueType(functionReference),
                        ),
                    ),
                    body = expression(instructions),
                ),
            ),
        )
        val context = passContext(module = module)

        val expected = listOf(
            ReferenceSuperInstruction.RefAsNonNull(
                value = localGetOperand(localIndex(0)),
                destination = localSetDestination(localIndex(1)),
            ),
            localGetInstruction(localIndex(1)),
        )

        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `does not consume local tee when a noncommutative producer cannot fuse`() {
        val instructions = listOf(
            globalGetInstruction(globalIndex(0)),
            globalGetInstruction(globalIndex(1)),
            i32SubInstruction(),
            localTeeInstruction(localIndex(0)),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(instructions),
                ),
            ),
        )
        val context = passContext(module = module)

        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(instructions, actual)
    }

    @Test
    fun `consumes local tee when a noncommutative producer can fuse`() {
        val instructions = listOf(
            localGetInstruction(localIndex(0)),
            localGetInstruction(localIndex(1)),
            i32SubInstruction(),
            localTeeInstruction(localIndex(2)),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(instructions),
                ),
            ),
        )
        val context = passContext(module = module)
        val expected = listOf(
            NumericSuperInstruction.I32Sub(
                left = localGetOperand(localIndex(0)),
                right = localGetOperand(localIndex(1)),
                destination = localSetDestination(localIndex(2)),
            ),
            localGetInstruction(localIndex(2)),
        )

        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `preserves constant and local get tee operand fusion`() {
        val instructions = listOf(
            i32ConstInstruction(5),
            localTeeInstruction(localIndex(0)),
            localGetInstruction(localIndex(1)),
            localTeeInstruction(localIndex(2)),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(instructions),
                ),
            ),
        )
        val context = passContext(module = module)
        val expected = listOf(
            fusedLocalTee(
                operand = i32ConstOperand(5),
                localIdx = localIndex(0),
            ),
            fusedLocalTee(
                operand = localGetOperand(localIndex(1)),
                localIdx = localIndex(2),
            ),
        )

        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `retains the synthetic tee result for a following branch`() {
        val instructions = listOf(
            localGetInstruction(localIndex(0)),
            localGetInstruction(localIndex(1)),
            i32AddInstruction(),
            localTeeInstruction(localIndex(2)),
            brIfInstruction(),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(instructions),
                ),
            ),
        )
        val context = passContext(module = module)
        val expected = listOf(
            fusedI32Add(
                left = localGetOperand(localIndex(0)),
                right = localGetOperand(localIndex(1)),
                destination = localSetDestination(localIndex(2)),
            ),
            localGetInstruction(localIndex(2)),
            brIfInstruction(),
        )

        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `retains the first tee result for a chained tee`() {
        val instructions = listOf(
            localGetInstruction(localIndex(0)),
            localGetInstruction(localIndex(1)),
            i32AddInstruction(),
            localTeeInstruction(localIndex(2)),
            localTeeInstruction(localIndex(3)),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(instructions),
                ),
            ),
        )
        val context = passContext(module = module)
        val expected = listOf(
            fusedI32Add(
                left = localGetOperand(localIndex(0)),
                right = localGetOperand(localIndex(1)),
                destination = localSetDestination(localIndex(2)),
            ),
            localGetInstruction(localIndex(2)),
            localTeeInstruction(localIndex(3)),
        )

        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse a call instruction`() {

        val instructions = listOf(
            i32ConstInstruction(5),
            localGetInstruction(localIndex(2)),
            localGetInstruction(localIndex(1)),
            callInstruction(functionIndex(0)),
        )
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
            types = listOf(
                type(recursiveType = recursiveType),
            ),
            definedTypes = listOf(
                definedType(recursiveType = recursiveType),
            ),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions,
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val expected = listOf(
            fusedCall(
                operands = listOf(
                    i32ConstOperand(5),
                    localGetOperand(localIndex(2)),
                    localGetOperand(localIndex(1)),
                ),
                functionIndex = functionIndex(0),
            ),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse parametric with just a destination`() {

        val instructions = listOf(
            i32AddInstruction(),
            selectInstruction(),
            localSetInstruction(localIndex(1)),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions,
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val expected = listOf(
            i32AddInstruction(),
            fusedSelect(
                const = valueStackOperand(),
                val1 = valueStackOperand(),
                val2 = valueStackOperand(),
                destination = localSetDestination(localIndex(1)),
            ),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse parametric with const and a destination`() {

        val instructions = listOf(
            i32AddInstruction(),
            i32ConstInstruction(5),
            selectInstruction(),
            localSetInstruction(localIndex(1)),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions,
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val expected = listOf(
            i32AddInstruction(),
            fusedSelect(
                const = i32ConstOperand(5),
                val1 = valueStackOperand(),
                val2 = valueStackOperand(),
                destination = localSetDestination(localIndex(1)),
            ),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse parametric with const val2 and a destination`() {

        val instructions = listOf(
            localGetInstruction(localIndex(2)),
            i32ConstInstruction(5),
            selectInstruction(),
            localSetInstruction(localIndex(1)),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions,
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val expected = listOf(
            fusedSelect(
                const = i32ConstOperand(5),
                val1 = valueStackOperand(),
                val2 = localGetOperand(localIndex(2)),
                destination = localSetDestination(localIndex(1)),
            ),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse parametric with const val2 val1 and a destination`() {

        val instructions = listOf(
            i32ConstInstruction(6),
            localGetInstruction(localIndex(2)),
            i32ConstInstruction(5),
            selectInstruction(),
            localSetInstruction(localIndex(1)),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions,
                    ),
                ),
            ),
        )
        val context = passContext(module = module)

        val expected = listOf(
            fusedSelect(
                const = i32ConstOperand(5),
                val1 = i32ConstOperand(6),
                val2 = localGetOperand(localIndex(2)),
                destination = localSetDestination(localIndex(1)),
            ),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `does not fuse global get as a producer operand`() {
        val instructions = listOf(
            globalGetInstruction(globalIndex(0)),
            localGetInstruction(localIndex(0)),
            i32AddInstruction(),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(instructions),
                ),
            ),
        )
        val context = passContext(module = module)

        val expected = listOf(
            globalGetInstruction(globalIndex(0)),
            fusedI32Add(
                left = valueStackOperand(),
                right = localGetOperand(localIndex(0)),
                destination = valueStackDestination(),
            ),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `does not fuse global set as a numeric destination`() {
        val instructions = listOf(
            localGetInstruction(localIndex(0)),
            localGetInstruction(localIndex(1)),
            i32AddInstruction(),
            globalSetInstruction(globalIndex(0)),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(instructions),
                ),
            ),
        )
        val context = passContext(module = module)

        val expected = listOf(
            fusedI32Add(
                left = localGetOperand(localIndex(0)),
                right = localGetOperand(localIndex(1)),
                destination = valueStackDestination(),
            ),
            globalSetInstruction(globalIndex(0)),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }
}
