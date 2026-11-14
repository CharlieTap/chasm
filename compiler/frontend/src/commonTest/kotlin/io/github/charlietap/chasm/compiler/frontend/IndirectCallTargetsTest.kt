package io.github.charlietap.chasm.compiler.frontend

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.FunctionIndex
import io.github.charlietap.chasm.fixture.ast.module.activeElementSegmentMode
import io.github.charlietap.chasm.fixture.ast.module.elementSegment
import io.github.charlietap.chasm.fixture.ast.module.export
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.functionImportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.import
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.ast.module.table
import io.github.charlietap.chasm.fixture.ast.module.tableExportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.tableImportDescriptor
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64AddressType
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import io.github.charlietap.chasm.parallel.ParallelismPolicy
import io.github.charlietap.chasm.type.AbstractHeapType
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail
import io.github.charlietap.chasm.cir.ControlInstruction as CirControlInstruction

class IndirectCallTargetsTest {

    private val binary = definedType(
        recursiveType = functionRecursiveType(
            functionType(
                params = resultType(listOf(i32ValueType(), i32ValueType())),
                results = resultType(listOf(i32ValueType())),
            ),
        ),
    )
    private val nullary = definedType(recursiveType = functionRecursiveType(functionType(results = resultType(listOf(i32ValueType())))), typeIndex = 1)
    private val pair = definedType(
        recursiveType = functionRecursiveType(functionType(results = resultType(listOf(i32ValueType(), i32ValueType())))),
        typeIndex = 2,
    )
    private val binarySite = ControlInstruction.CallIndirect(Index.TypeIndex(0u), Index.TableIndex(0u))
    private val nullarySite = ControlInstruction.CallIndirect(Index.TypeIndex(1u), Index.TableIndex(0u))

    @Test
    fun `a call through a closed table targets its matching entries in index order`() {
        assertArms(fixture(), table = 0, type = 0, indices = listOf(1, 2), functions = listOf(0, 1))
    }

    @Test
    fun `a site without parameters uses only entries of its own type`() {
        val module = fixture(caller = listOf(NumericInstruction.I32Const(7), nullarySite))

        assertArms(module, table = 0, type = 1, indices = listOf(3), functions = listOf(2))
    }

    @Test
    fun `a site with several results is left alone`() {
        val site = ControlInstruction.CallIndirect(Index.TypeIndex(2u), Index.TableIndex(0u))

        assertNoTargets(fixture(segments = listOf(segment(1, 4)), caller = listOf(NumericInstruction.I32Const(7), site)))
    }

    @Test
    fun `a later segment replaces the entries of an earlier one`() {
        val module = fixture(segments = listOf(segment(1, 0, 1, 2), segment(2, 0)))

        assertArms(module, table = 0, type = 0, indices = listOf(1, 2), functions = listOf(0, 0))
    }

    @Test
    fun `null and computed entries are left to the indirect call`() {
        val unknown = Expression(VariableInstruction.GlobalGet(Index.GlobalIndex(0u)))
        val absent = Expression(ReferenceInstruction.RefNull(AbstractHeapType.Func))
        val module = fixture(
            segments = listOf(
                elementSegment(
                    initExpressions = listOf(unknown, Expression(ReferenceInstruction.RefFunc(Index.FunctionIndex(1u))), absent),
                    mode = activeElementSegmentMode(offsetExpr = Expression(NumericInstruction.I32Const(1))),
                ),
            ),
        )

        assertArms(module, table = 0, type = 0, indices = listOf(2), functions = listOf(1))
    }

    @Test
    fun `an exported table is left alone`() {
        assertNoTargets(fixture(exports = listOf(export(descriptor = tableExportDescriptor()))))
    }

    @Test
    fun `an imported table is left alone`() {
        assertNoTargets(fixture(imports = listOf(import(descriptor = tableImportDescriptor())), tables = emptyList()))
    }

    @Test
    fun `a table written by any instruction is left alone`() {
        val table = Index.TableIndex(0u)
        val writes = listOf(
            TableInstruction.TableSet(table),
            TableInstruction.TableGrow(table),
            TableInstruction.TableFill(table),
            TableInstruction.TableInit(Index.ElementIndex(0u), table),
            TableInstruction.TableCopy(Index.TableIndex(1u), table),
        )
        writes.forEach { write ->
            assertNoTargets(fixture(firstBody = listOf(write)))
        }
    }

    @Test
    fun `a table that is only read is still closed`() {
        val table = Index.TableIndex(0u)
        val reads = listOf(
            TableInstruction.TableGet(table),
            TableInstruction.TableSize(table),
            TableInstruction.TableCopy(table, Index.TableIndex(1u)),
            TableInstruction.ElemDrop(Index.ElementIndex(0u)),
            ReferenceInstruction.RefFunc(Index.FunctionIndex(0u)),
        )
        val module = fixture(firstBody = reads, tables = listOf(closedTable(), closedTable(1)))

        assertArms(module, table = 0, type = 0, indices = listOf(1, 2), functions = listOf(0, 1))
    }

    @Test
    fun `a segment with a computed offset opens its table`() {
        val module = fixture(
            segments = listOf(
                segment(1, 0, 1),
                elementSegment(
                    initExpressions = listOf(Expression(ReferenceInstruction.RefFunc(Index.FunctionIndex(2u)))),
                    mode = activeElementSegmentMode(offsetExpr = Expression(VariableInstruction.GlobalGet(Index.GlobalIndex(0u)))),
                ),
            ),
        )

        assertNoTargets(module)
    }

    @Test
    fun `entries written after a segment that may trap are not trusted`() {
        val overflowing = fixture(segments = listOf(segment(1, 0), segment(3, 2, 2), segment(2, 1)))

        assertArms(overflowing, table = 0, type = 0, indices = listOf(1), functions = listOf(0))
    }

    @Test
    fun `a table indexed by i64 is left alone`() {
        assertNoTargets(fixture(tables = listOf(table(type = tableType(addressType = i64AddressType(), limits = limits(min = 4u))))))
    }

    @Test
    fun `a site with more than four matching entries is left alone`() {
        assertNoTargets(fixture(tables = listOf(table(type = tableType(limits = limits(min = 8u)))), segments = listOf(segment(0, 0, 1, 0, 1, 0))))
    }

    @Test
    fun `a site whose type matches no entry is left alone`() {
        assertNoTargets(fixture(segments = listOf(segment(1, 0, 1)), caller = listOf(NumericInstruction.I32Const(7), nullarySite)))
    }

    @Test
    fun `a call with a computed selector tests each target before falling back to the indirect call`() {
        val selector = VariableInstruction.LocalGet(Index.LocalIndex(0u))
        val module = fixture(caller = listOf(NumericInstruction.I32Const(1), NumericInstruction.I32Const(2), selector, binarySite), callerType = 0)

        val instructions = compiledCaller(module)

        assertEquals(listOf(FunctionIndex(0), FunctionIndex(1)), instructions.filterIsInstance<CirControlInstruction.Call>().map { call -> call.function })
        assertEquals(2, instructions.count { instruction -> instruction is AdminInstruction.JumpIfConditionMismatch })
        assertEquals(1, instructions.count { instruction -> instruction is CirControlInstruction.CallIndirectS })
    }

    @Test
    fun `a call with a constant selector calls its target directly`() {
        val module = fixture(caller = listOf(NumericInstruction.I32Const(1), NumericInstruction.I32Const(2), NumericInstruction.I32Const(2), binarySite))

        val instructions = compiledCaller(module)

        assertEquals(listOf(FunctionIndex(1)), instructions.filterIsInstance<CirControlInstruction.Call>().map { call -> call.function })
        assertEquals(0, instructions.count { instruction -> instruction is CirControlInstruction.CallIndirectI })
    }

    @Test
    fun `a call is left indirect when devirtualisation is off`() {
        val selector = VariableInstruction.LocalGet(Index.LocalIndex(0u))
        val module = fixture(caller = listOf(NumericInstruction.I32Const(1), NumericInstruction.I32Const(2), selector, binarySite), callerType = 0)

        val instructions = compiledCaller(module, devirtualise = false)

        assertEquals(0, instructions.count { instruction -> instruction is CirControlInstruction.Call })
        assertEquals(1, instructions.count { instruction -> instruction is CirControlInstruction.CallIndirectS })
    }

    @Test
    fun `parallel compilation devirtualises the same calls as serial compilation`() = runTest {
        val selector = VariableInstruction.LocalGet(Index.LocalIndex(0u))
        val module = fixture(caller = listOf(NumericInstruction.I32Const(1), NumericInstruction.I32Const(2), selector, binarySite), callerType = 0)
        val types = module.typeResolver()

        val serial = ModuleCompiler(module, types, recordProvenance = false, devirtualiseIndirectCalls = true)
            .getOrThrow { error -> IllegalStateException(error.toString()) }
        val parallel = ParallelModuleCompiler(
            module = module,
            types = types,
            taskExecutor = SequentialTaskExecutor(),
            mode = CompilationMode.PARALLEL,
            availableProcessors = 2,
            policy = ParallelismPolicy(minimumWork = 0, workPerWorker = 1),
            devirtualiseIndirectCalls = true,
            functionCompiler = ::FunctionCompiler,
            moduleCompiler = { _, _ -> fail("the parallel path compiles the module") },
        ).getOrThrow { error -> IllegalStateException(error.toString()) }

        assertEquals(serial.functions.map { function -> function.instructions.toList() }, parallel.functions.map { function -> function.instructions.toList() })
    }

    @Test
    fun `the public parallel compiler devirtualises when it falls back to serial compilation`() = runTest {
        val selector = VariableInstruction.LocalGet(Index.LocalIndex(0u))
        val module = fixture(caller = listOf(NumericInstruction.I32Const(1), NumericInstruction.I32Const(2), selector, binarySite), callerType = 0)
        val types = module.typeResolver()

        val serial = ModuleCompiler(module, types, recordProvenance = false, devirtualiseIndirectCalls = true)
            .getOrThrow { error -> IllegalStateException(error.toString()) }
        val fallback = ParallelModuleCompiler(module, types, SequentialTaskExecutor(), devirtualiseIndirectCalls = true)
            .getOrThrow { error -> IllegalStateException(error.toString()) }

        assertEquals(serial.functions.map { function -> function.instructions.toList() }, fallback.functions.map { function -> function.instructions.toList() })
        assertEquals(2, fallback.functions[3].instructions.count { instruction -> instruction is CirControlInstruction.Call })
    }

    @Test
    fun `an imported function in a closed table is called directly`() {
        val selector = VariableInstruction.LocalGet(Index.LocalIndex(0u))
        val module = fixture(
            imports = listOf(import(descriptor = functionImportDescriptor(Index.TypeIndex(0u)))),
            segments = listOf(segment(1, 0, 1)),
            caller = listOf(NumericInstruction.I32Const(1), NumericInstruction.I32Const(2), selector, binarySite),
            callerType = 0,
        )

        val instructions = compiledCaller(module)

        assertEquals(listOf(FunctionIndex(0), FunctionIndex(1)), instructions.filterIsInstance<CirControlInstruction.Call>().map { call -> call.function })
    }

    private fun assertArms(
        module: Module,
        table: Int,
        type: Int,
        indices: List<Int>,
        functions: List<Int>,
    ) {
        val arms = checkNotNull(indirectCallTargets(module, module.typeResolver())?.at(table, type)) { "no targets" }

        assertEquals(indices, arms.indices.toList())
        assertEquals(functions, arms.functions.toList())
    }

    private fun assertNoTargets(module: Module) {
        assertNull(indirectCallTargets(module, module.typeResolver()))
    }

    private fun compiledCaller(
        module: Module,
        devirtualise: Boolean = true,
    ): List<Any> {
        val compiled = ModuleCompiler(module, module.typeResolver(), recordProvenance = false, devirtualiseIndirectCalls = devirtualise)
            .getOrThrow { error -> IllegalStateException(error.toString()) }
        return compiled.functions[3].instructions.toList()
    }

    private class SequentialTaskExecutor : ParallelTaskExecutor {

        override suspend fun <T> execute(tasks: List<ParallelTaskScope.() -> T>): List<T> {
            val scope = ParallelTaskScope {}
            return tasks.map { task -> task(scope) }
        }
    }

    private fun closedTable(
        index: Int = 0,
    ): Table {
        return table(idx = Index.TableIndex(index.toUInt()), type = tableType(limits = limits(min = 4u)))
    }

    private fun segment(
        offset: Int,
        vararg functions: Int,
        table: Int = 0,
    ): ElementSegment {
        return elementSegment(
            initExpressions = functions.map { function -> Expression(ReferenceInstruction.RefFunc(Index.FunctionIndex(function.toUInt()))) },
            mode = activeElementSegmentMode(tableIndex = Index.TableIndex(table.toUInt()), offsetExpr = Expression(NumericInstruction.I32Const(offset))),
        )
    }

    private fun fixture(
        imports: List<Import> = emptyList(),
        exports: List<Export> = emptyList(),
        tables: List<Table> = listOf(closedTable()),
        segments: List<ElementSegment> = listOf(segment(1, 0, 1, 2)),
        firstBody: List<Instruction> = listOf(VariableInstruction.LocalGet(Index.LocalIndex(0u))),
        caller: List<Instruction> = listOf(NumericInstruction.I32Const(1), NumericInstruction.I32Const(2), NumericInstruction.I32Const(7), binarySite),
        callerType: Int = 1,
    ): Module {
        return module(
            definedTypes = listOf(binary, nullary, pair),
            imports = imports,
            exports = exports,
            tables = tables,
            elementSegments = segments,
            functions = listOf(
                function(idx = Index.FunctionIndex(0u), typeIndex = Index.TypeIndex(0u), body = Expression(firstBody)),
                function(idx = Index.FunctionIndex(1u), typeIndex = Index.TypeIndex(0u), body = Expression(VariableInstruction.LocalGet(Index.LocalIndex(1u)))),
                function(idx = Index.FunctionIndex(2u), typeIndex = Index.TypeIndex(1u), body = Expression(NumericInstruction.I32Const(0))),
                function(idx = Index.FunctionIndex(3u), typeIndex = Index.TypeIndex(callerType.toUInt()), body = Expression(caller)),
                function(idx = Index.FunctionIndex(4u), typeIndex = Index.TypeIndex(2u), body = Expression(NumericInstruction.I32Const(0), NumericInstruction.I32Const(1))),
            ),
        )
    }
}
