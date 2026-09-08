package io.github.charlietap.chasm.compiler

import com.github.michaelbull.result.unwrap
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.compiler.context.CompilerContext
import io.github.charlietap.chasm.compiler.diagnostic.CompilerDiagnostics
import io.github.charlietap.chasm.compiler.diagnostic.CompilerInstructionObserver
import io.github.charlietap.chasm.fixture.ast.instruction.blockInstruction
import io.github.charlietap.chasm.fixture.ast.instruction.callInstruction
import io.github.charlietap.chasm.fixture.ast.instruction.catchAllHandler
import io.github.charlietap.chasm.fixture.ast.instruction.catchCatchHandler
import io.github.charlietap.chasm.fixture.ast.instruction.endInstruction
import io.github.charlietap.chasm.fixture.ast.instruction.expression
import io.github.charlietap.chasm.fixture.ast.instruction.tryTableInstruction
import io.github.charlietap.chasm.fixture.ast.instruction.unreachableInstruction
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.ast.module.tagIndex
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tagAddress
import io.github.charlietap.chasm.fixture.runtime.instance.tagInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.exception.CompiledCatch
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.type.ModuleTypeResolver
import io.github.charlietap.chasm.type.BlockType
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction as RuntimeControlInstruction

class ExceptionTableCompilerTest {
    private val catchAll = catchAllHandler()

    @Test
    fun `functions without catches publish no exception table`() {
        val plain = compile(Expression(ControlInstruction.Nop))
        val emptyTry = compile(
            Expression(
                ControlInstruction.TryTable(BlockType.Empty, emptyList()),
                ControlInstruction.Nop,
                ControlInstruction.End(1),
            ),
        )
        assertFalse(plain.program.hasExceptionHandlers)
        assertFalse(emptyTry.program.hasExceptionHandlers)
    }

    @Test
    fun `nested empty try does not close its enclosing region`() {
        val fixture = compile(
            expression(
                instructions = listOf(
                    blockInstruction(),
                    tryTableInstruction(handlers = listOf(catchAll)),
                    tryTableInstruction(handlers = emptyList()),
                    callInstruction(),
                    endInstruction(1),
                    tryTableInstruction(handlers = listOf(catchAll)),
                    callInstruction(),
                    endInstruction(1),
                    callInstruction(),
                    endInstruction(2),
                ),
            ),
        )
        val table = assertNotNull(fixture.program.exceptionTable(0))
        assertEquals(2, table.regions.size)
        val outer = table.regions[0]
        val inner = table.regions[1]
        assertEquals(-1, outer.parentRegion)
        assertEquals(0, inner.parentRegion)
        assertTrue(inner.startOffset > outer.startOffset)
        assertTrue(inner.endOffset < outer.endOffset)
        assertEquals(0, table.innermostRegion(inner.endOffset))
        assertEquals(-1, table.innermostRegion(outer.endOffset))
    }

    @Test
    fun `catch order uses resolved tag addresses and a shared continuation`() {
        val fixture = compile(
            expression(
                instructions = listOf(
                    blockInstruction(),
                    tryTableInstruction(
                        handlers = listOf(
                            catchCatchHandler(tagIndex = tagIndex(1u)),
                            catchCatchHandler(tagIndex = tagIndex(0u)),
                            catchAll,
                        ),
                    ),
                    unreachableInstruction(),
                    endInstruction(2),
                ),
            ),
        )
        val region = assertNotNull(fixture.program.exceptionTable(0)).regions.single()
        assertEquals(listOf(42, 17, CompiledCatch.CATCH_ALL_TAG), region.catches.map { it.tagAddress })
        for (handler in region.catches) {
            assertEquals(region.catches.first().targetOffset, handler.targetOffset)
            assertTrue(handler.targetOffset >= region.endOffset)
            assertContentEquals(intArrayOf(), handler.payloadSlots)
            assertFalse(handler.includeExceptionReference)
        }
    }

    @Test
    fun `tail call remains outside exception lookup while normal instructions stay protected`() {
        val fixture = compile(
            Expression(
                ControlInstruction.Block(BlockType.Empty),
                ControlInstruction.TryTable(BlockType.Empty, listOf(catchAll)),
                ControlInstruction.Call(Index.FunctionIndex(0u)),
                ControlInstruction.ReturnCall(Index.FunctionIndex(0u)),
                ControlInstruction.End(2),
            ),
        )
        val table = assertNotNull(fixture.program.exceptionTable(0))
        val tailCallIndex = fixture.instructions.indexOfFirst { it is RuntimeControlInstruction.ReturnWasmCall }
        assertTrue(tailCallIndex >= 0)
        val tailCallOffset = fixture.instructionOffsets[tailCallIndex]
        assertTrue(tailCallOffset >= 0)
        assertContentEquals(intArrayOf(tailCallOffset), table.tailCallOffsets)
        assertTrue(table.excludesTailCall(tailCallOffset))
        assertFalse(table.excludesTailCall(table.regions.single().startOffset))
    }

    private fun compile(body: Expression): CompiledFixture {
        val module = module(definedTypes = listOf(definedType(recursiveType = functionRecursiveType())))
        val store = store()
        val instructions = mutableListOf<LinkedInstruction>()
        val dispatchables = mutableListOf<DispatchableInstruction>()
        val context = CompilerContext(
            module = module,
            types = ModuleTypeResolver(module),
            runtimeTypes = store.heap.registerRuntimeTypes(module.definedTypes),
            instance = moduleInstance(tagAddresses = mutableListOf(tagAddress(17), tagAddress(42))),
            tags = arrayOf(tagInstance(), tagInstance()),
            functions = arrayOf(wasmFunctionInstance()),
            diagnostics = CompilerDiagnostics(
                CompilerInstructionObserver { dispatchable, instruction ->
                    instructions += instruction
                    dispatchables += dispatchable
                },
            ),
        )
        val program = Program()
        FunctionCompiler(context, function(body = body), program).unwrap()
        return CompiledFixture(program, instructions, dispatchables.map { program.instructions.indexOf(it) })
    }

    private class CompiledFixture(val program: Program, val instructions: List<LinkedInstruction>, val instructionOffsets: List<Int>)
}
