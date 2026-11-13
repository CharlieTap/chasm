package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.functionImportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.import
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class LeafCallInlinerTest {

    private val inliner = LeafCallInliner(LeafAnalyzer(), FunctionInliner())
    private val funcref = ValueType.Reference(ReferenceType.Ref(AbstractHeapType.Func))
    private val binary = definedType(
        recursiveType = functionRecursiveType(
            functionType(
                params = resultType(listOf(i32ValueType(), i32ValueType())),
                results = resultType(listOf(i32ValueType())),
            ),
        ),
    )
    private val nullary = definedType(recursiveType = functionRecursiveType(functionType(results = resultType(listOf(i32ValueType())))), typeIndex = 1)
    private val reference = definedType(
        recursiveType = functionRecursiveType(functionType(params = resultType(listOf(funcref)), results = resultType(listOf(funcref)))),
        typeIndex = 2,
    )
    private val pair = definedType(
        recursiveType = functionRecursiveType(functionType(results = resultType(listOf(i32ValueType(), i32ValueType())))),
        typeIndex = 3,
    )
    private val constants = listOf(NumericInstruction.I32Const(1), NumericInstruction.I32Const(2))
    private val computed = listOf(NumericInstruction.I32Const(1), NumericInstruction.I32Const(2), NumericInstruction.I32Const(3), NumericInstruction.I32Add)
    private val call = ControlInstruction.Call(Index.FunctionIndex(0u))

    @Test
    fun `constant arguments replace the reads of parameters the leaf never writes`() {

        val inlined = inline(leaf(local(0), local(1), NumericInstruction.I32Add), constants + call)

        assertEquals(Expression(NumericInstruction.I32Const(1), NumericInstruction.I32Const(2), NumericInstruction.I32Add), inlined.body)
        assertEquals(emptyList(), inlined.locals)
    }

    @Test
    fun `a computed argument is stored to a fresh local`() {

        val sum = listOf(NumericInstruction.I32Const(2), NumericInstruction.I32Const(3), NumericInstruction.I32Add)
        val leaf = leaf(local(0), local(1), NumericInstruction.I32Add, local(1), NumericInstruction.I32Add)

        val inlined = inline(leaf, sum + NumericInstruction.I32Const(5) + call)

        val expected = sum + listOf(
            set(0),
            local(0),
            NumericInstruction.I32Const(5),
            NumericInstruction.I32Add,
            NumericInstruction.I32Const(5),
            NumericInstruction.I32Add,
        )
        assertEquals(Expression(expected), inlined.body)
        assertEquals(listOf(Local(Index.LocalIndex(0u), i32ValueType())), inlined.locals)
    }

    @Test
    fun `an argument is stored when the leaf writes its parameter`() {

        val inlined = inline(leaf(local(0), VariableInstruction.LocalTee(Index.LocalIndex(1u)), local(1), NumericInstruction.I32Add), constants + call)

        val expected = constants + listOf(
            set(0),
            set(1),
            local(1),
            VariableInstruction.LocalTee(Index.LocalIndex(0u)),
            local(0),
            NumericInstruction.I32Add,
        )
        assertEquals(Expression(expected), inlined.body)
        assertEquals(listOf(Local(Index.LocalIndex(0u), i32ValueType()), Local(Index.LocalIndex(1u), i32ValueType())), inlined.locals)
    }

    @Test
    fun `a computed argument the leaf never reads is dropped`() {

        val inlined = inline(leaf(local(0)), computed + call)

        assertEquals(Expression(computed + listOf(ParametricInstruction.Drop, set(0), local(0))), inlined.body)
    }

    @Test
    fun `a local added by an earlier inlined call is not read in place of a parameter`() {

        val inlined = inline(leaf(local(0)), computed + call + NumericInstruction.I32Const(7) + call)

        assertEquals(Expression(computed + listOf(ParametricInstruction.Drop, set(0), local(0), set(0), local(0))), inlined.body)
    }

    @Test
    fun `a return inside the leaf branches to a block that replaces the call`() {

        val leaf = leaf(
            local(0),
            ControlInstruction.If(BlockType.Empty),
            local(1),
            ControlInstruction.Return,
            ControlInstruction.End(1),
            local(0),
        )

        val inlined = inline(leaf, constants + call)

        val expected = Expression(
            ControlInstruction.Block(BlockType.ValType(i32ValueType())),
            NumericInstruction.I32Const(1),
            ControlInstruction.If(BlockType.Empty),
            NumericInstruction.I32Const(2),
            ControlInstruction.Br(Index.LabelIndex(1u)),
            ControlInstruction.End(1),
            NumericInstruction.I32Const(1),
            ControlInstruction.End(1),
        )
        assertEquals(expected, inlined.body)
    }

    @Test
    fun `a branch to the leaf's own label keeps the block`() {

        val leaf = leaf(local(0), local(1), ControlInstruction.BrIf(Index.LabelIndex(0u)), ParametricInstruction.Drop, local(1))

        val body = inline(leaf, constants + call).body.instructions

        assertEquals(ControlInstruction.Block(BlockType.ValType(i32ValueType())), body.first())
        assertEquals(ControlInstruction.End(1), body.last())
    }

    @Test
    fun `a default is stored only for a nullable local read before it is written`() {

        val leaf = function(
            typeIndex = Index.TypeIndex(0u),
            locals = listOf(Local(Index.LocalIndex(0u), i32ValueType()), Local(Index.LocalIndex(1u), i32ValueType())),
            body = Expression(local(2), set(3), local(3), local(0), NumericInstruction.I32Add),
        )

        val inlined = inline(leaf, constants + call)

        val expected = Expression(
            NumericInstruction.I32Const(0),
            set(0),
            local(0),
            set(1),
            local(1),
            NumericInstruction.I32Const(1),
            NumericInstruction.I32Add,
        )
        assertEquals(expected, inlined.body)
    }

    @Test
    fun `non-nullable reference parameters and locals are inlined without a default`() {

        val leaf = function(
            typeIndex = Index.TypeIndex(2u),
            locals = listOf(Local(Index.LocalIndex(0u), funcref)),
            body = Expression(local(0), set(1), local(1)),
        )
        val caller = function(
            idx = Index.FunctionIndex(1u),
            typeIndex = Index.TypeIndex(1u),
            body = Expression(ReferenceInstruction.RefFunc(Index.FunctionIndex(0u)), call, ParametricInstruction.Drop, NumericInstruction.I32Const(0)),
        )
        val module = module(definedTypes = listOf(binary, nullary, reference), functions = listOf(leaf, caller))

        val inlined = inliner.rewrite(module, module.typeResolver()).functions[1]

        val expected = Expression(
            ReferenceInstruction.RefFunc(Index.FunctionIndex(0u)),
            set(0),
            local(0),
            set(1),
            local(1),
            ParametricInstruction.Drop,
            NumericInstruction.I32Const(0),
        )
        assertEquals(expected, inlined.body)
        assertEquals(listOf(Local(Index.LocalIndex(0u), funcref), Local(Index.LocalIndex(1u), funcref)), inlined.locals)
    }

    @Test
    fun `a function with a loop is not inlined`() {

        val looping = leaf(ControlInstruction.Loop(BlockType.Empty), ControlInstruction.End(1), local(0))
        val caller = function(idx = Index.FunctionIndex(1u), typeIndex = Index.TypeIndex(1u), body = Expression(constants + call))
        val module = module(definedTypes = listOf(binary, nullary), functions = listOf(looping, caller))

        assertSame(module, inliner.rewrite(module, module.typeResolver()))
    }

    @Test
    fun `a leaf of thirty instructions is inlined and a longer one is not`() {

        val longest = inline(leaf(*Array(30) { _ -> NumericInstruction.I32Const(0) }), constants + call)
        val longer = inline(leaf(*Array(31) { _ -> NumericInstruction.I32Const(0) }), constants + call)

        assertEquals(emptyList(), calls(longest))
        assertEquals(listOf(call), calls(longer))
    }

    @Test
    fun `a leaf of sixteen locals is inlined and one with more is not`() {

        val most = List(14) { index -> Local(Index.LocalIndex(index.toUInt()), i32ValueType()) }
        val more = List(15) { index -> Local(Index.LocalIndex(index.toUInt()), i32ValueType()) }

        assertEquals(emptyList(), calls(inline(function(locals = most, body = Expression(local(0))), constants + call)))
        assertEquals(listOf(call), calls(inline(function(locals = more, body = Expression(local(0))), constants + call)))
    }

    @Test
    fun `a leaf with several results is not inlined`() {

        val leaf = function(typeIndex = Index.TypeIndex(3u), body = Expression(constants))
        val caller = function(idx = Index.FunctionIndex(1u), typeIndex = Index.TypeIndex(1u), body = Expression(call, ParametricInstruction.Drop))
        val module = module(definedTypes = listOf(binary, nullary, reference, pair), functions = listOf(leaf, caller))

        assertEquals(listOf(call), calls(inliner.rewrite(module, module.typeResolver()).functions[1]))
    }

    @Test
    fun `a call to an imported function is not inlined`() {

        val imported = ControlInstruction.Call(Index.FunctionIndex(0u))
        val leaf = function(idx = Index.FunctionIndex(1u), typeIndex = Index.TypeIndex(0u), body = Expression(local(0)))
        val caller = function(
            idx = Index.FunctionIndex(2u),
            typeIndex = Index.TypeIndex(1u),
            body = Expression(constants + imported + ParametricInstruction.Drop + constants + ControlInstruction.Call(Index.FunctionIndex(1u))),
        )
        val module = module(
            definedTypes = listOf(binary, nullary),
            imports = listOf(import(descriptor = functionImportDescriptor(Index.TypeIndex(0u)))),
            functions = listOf(leaf, caller),
        )

        assertEquals(listOf(imported), calls(inliner.rewrite(module, module.typeResolver()).functions[1]))
    }

    @Test
    fun `calls past the inlining budget are left in place`() {

        val leaf = leaf(*Array(30) { _ -> local(0) })

        val inlined = inline(leaf, List(8) { _ -> constants + call }.flatten())

        assertEquals(listOf(call), calls(inlined))
        assertEquals(constants + call, inlined.body.instructions.takeLast(3))
    }

    private fun calls(function: Function): List<ControlInstruction.Call> {
        return function.body.instructions.filterIsInstance<ControlInstruction.Call>()
    }

    private fun leaf(vararg body: Instruction): Function {
        return function(typeIndex = Index.TypeIndex(0u), body = Expression(*body))
    }

    private fun inline(
        leaf: Function,
        caller: List<Instruction>,
    ): Function {

        val calling = function(idx = Index.FunctionIndex(1u), typeIndex = Index.TypeIndex(1u), body = Expression(caller))
        val module = module(definedTypes = listOf(binary, nullary), functions = listOf(leaf, calling))

        return inliner.rewrite(module, module.typeResolver()).functions[1]
    }

    private fun local(index: Int): VariableInstruction.LocalGet {
        return VariableInstruction.LocalGet(Index.LocalIndex(index.toUInt()))
    }

    private fun set(index: Int): VariableInstruction.LocalSet {
        return VariableInstruction.LocalSet(Index.LocalIndex(index.toUInt()))
    }
}
