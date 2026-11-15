package io.github.charlietap.chasm.compiler.backend.dex

import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.ReferenceType
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.backend.dex.abi.dexType
import io.github.charlietap.chasm.compiler.backend.dex.abi.method
import io.github.charlietap.chasm.compiler.backend.dex.abi.referenceType
import io.github.charlietap.chasm.compiler.backend.dex.abi.valueType
import io.github.charlietap.chasm.compiler.backend.dex.analysis.DataflowSlotKindAnalyzer
import io.github.charlietap.chasm.compiler.backend.dex.analysis.SlotKindAnalyzer
import io.github.charlietap.chasm.compiler.backend.dex.analysis.SlotKindFlow
import io.github.charlietap.chasm.compiler.backend.dex.analysis.functionFacts
import io.github.charlietap.chasm.compiler.backend.dex.lower.MissingHome
import io.github.charlietap.chasm.compiler.backend.dex.lower.RegisterHomes
import io.github.charlietap.chasm.compiler.backend.dex.lower.SlotKindConflict
import io.github.charlietap.chasm.compiler.backend.dex.lower.Unsupported
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.dexter.ClassBuilder
import io.github.charlietap.dexter.ClassType
import io.github.charlietap.dexter.DexMethod
import io.github.charlietap.dexter.DexType
import io.github.charlietap.dexter.Dexter
import io.github.charlietap.dexter.MemberAccess
import io.github.charlietap.dexter.Reg
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FunctionRoutesTest {

    private val module = decodeModule(fixture("routes"))
    private val code = ModuleCompiler(module).getOrThrow { error -> IllegalStateException(error.toString()) }

    @Test
    fun `a function predicted to fit only with helper accesses lowers whole with helper accesses`() {

        val function = code.functions[HELPER]
        assertEquals(Route.WholeHelper, route(lowering(), function))
        val lowering = lowering()

        val generated = lower(lowering, function) { builder, method -> assertNull(FunctionLowerer(lowering, function, builder, method)) }

        assertNull(lowering.chunkStarts[HELPER])
        assertFalse(INLINE_LOAD in methodSmali(generated, function))
    }

    @Test
    fun `a pinned function that is too large inline reuses its slot kinds for the helper attempt`() {

        val function = code.functions[PINNED_HELPER]
        assertEquals(Route.TryInOrder, route(lowering(), function))
        val lowering = lowering()
        val flows = ArrayList<SlotKindFlow>()

        val generated = lower(lowering, function) { builder, method ->
            assertNull(functionLowering(lowering, function, builder, method, slotKindAnalyzer = recording(flows)).lower())
        }

        assertEquals(1, flows.size)
        assertTrue(pinned(flows.single(), function))
        assertNull(lowering.chunkStarts[PINNED_HELPER])
        assertContentEquals(fresh(module, code, function, Route.WholeHelper), generated)
        assertFalse(INLINE_LOAD in methodSmali(generated, function))
    }

    @Test
    fun `a pinned function that is too large in both modes reuses one set of slot kinds through to the split`() {

        val module = decodeModule(fixture("routes_eh"))
        val code = ModuleCompiler(module).getOrThrow { error -> IllegalStateException(error.toString()) }
        val function = code.functions[PINNED_SPLIT]
        assertEquals(Route.TryInOrder, route(ModuleLowering(module, code, OPTIONS), function))
        val lowering = ModuleLowering(module, code, OPTIONS)
        val flows = ArrayList<SlotKindFlow>()

        val generated = lower(lowering, function) { builder, method ->
            assertNull(functionLowering(lowering, function, builder, method, slotKindAnalyzer = recording(flows)).lower())
        }

        assertEquals(1, flows.size)
        assertTrue(pinned(flows.single(), function))
        val starts = assertNotNull(lowering.chunkStarts[PINNED_SPLIT])
        assertTrue(starts.size > 1)
        val names = methodNames(generated)
        starts.indices.forEach { chunk -> assertTrue("f$PINNED_SPLIT\$c$chunk" in names) }
        assertContentEquals(fresh(module, code, function, Route.SplitOnly), generated)
    }

    @Test
    fun `a predicted split that the file cannot hold falls back to whole lowering`() {

        val function = code.functions[FRAME]
        assertEquals(Route.Split, route(lowering(), function))
        val routed = lowering()
        lower(routed, function) { builder, method -> assertNull(FunctionLowerer(routed, function, builder, method)) }
        assertNotNull(routed.chunkStarts[FRAME])
        val whole = wholeDex(function)

        val lowering = lowering()
        val generated = lower(lowering, function) { builder, method ->
            fill(builder, ClassType(lowering.codeClass(function.index)), methodReferences(whole))
            assertNull(FunctionLowerer(lowering, function, builder, method))
        }

        assertNull(lowering.chunkStarts[FRAME])
        assertEquals(methodSmali(whole, function), methodSmali(generated, function))
        assertTrue(methodNames(generated).none { name -> name.startsWith("f$FRAME\$c") })
    }

    @Test
    fun `a slot kind conflict found while emitting a split pins the slot and starts the chunks again`() {

        val function = code.functions[FRAME]
        val expected = forcedStarts(function)
        val lowering = ModuleLowering(module, code, FORCED)
        val received = ArrayList<RegisterHomes?>()
        val flows = ArrayList<SlotKindFlow>()

        val generated = lower(lowering, function) { builder, method ->

            val emitter = ProbingChunkEmitter(lowering, function, builder)
            val conflicting = ChunkEmitter { analysis, facts, homes, planned, chunkMethod, next ->
                received += homes
                if (received.size == 2) {
                    throw SlotKindConflict(0, SlotKindFlow.CONFLICT_NARROW, planned.start)
                }
                emitter.emit(analysis, facts, homes, planned, chunkMethod, next)
            }

            val lowered = functionLowering(lowering, function, builder, method, slotKindAnalyzer = recording(flows), chunkEmitter = conflicting)
            assertNull(lowered.lower())
        }

        assertEquals(1, flows.size)
        val homes = assertNotNull(received.last())
        assertEquals(SlotKindFlow.INT, homes.pin(0))
        assertEquals(SlotKindFlow.UNDEFINED, flows.single().pin(0))
        assertFalse(homes.reusableFor(BooleanArray(frameSlots(lowering, function)) { true }))
        assertContentEquals(intArrayOf(0) + expected, lowering.chunkStarts[FRAME])
        val names = methodNames(generated)
        (0..expected.size).forEach { chunk -> assertTrue("f$FRAME\$c$chunk" in names) }

        val facts = functionFacts(ModuleLowering(module, code, FORCED), function)
        val initialised = BooleanArray(facts.frameSlots) { true }
        val pinned = RegisterHomes(DataflowSlotKindAnalyzer().analyze(function, facts, initialised), initialised).also { analysed -> analysed.pinConflict(0) }
        assertRecovered(generated, upFront(function, pinned), function, orphans = 1, chunks = expected.size)
    }

    @Test
    fun `a missing home found while emitting a split starts the chunks again without slot kinds`() {

        val function = code.functions[FRAME]
        val expected = forcedStarts(function)
        val lowering = ModuleLowering(module, code, FORCED)
        val received = ArrayList<RegisterHomes?>()

        val generated = lower(lowering, function) { builder, method ->

            val emitter = ProbingChunkEmitter(lowering, function, builder)
            val missing = ChunkEmitter { analysis, facts, homes, planned, chunkMethod, next ->
                received += homes
                if (received.size == 2) {
                    throw MissingHome(0, SlotKindFlow.INT)
                }
                emitter.emit(analysis, facts, homes, planned, chunkMethod, next)
            }

            assertNull(functionLowering(lowering, function, builder, method, chunkEmitter = missing).lower())
        }

        assertNotNull(received.first())
        assertTrue(received.drop(2).all { homes -> homes == null })
        assertContentEquals(intArrayOf(0) + expected, lowering.chunkStarts[FRAME])
        assertTrue("f$FRAME\$c${expected.size}" in methodNames(generated))
        assertRecovered(generated, upFront(function, null), function, orphans = 1, chunks = expected.size)
    }

    private fun forcedStarts(function: CirFunction): IntArray {

        val lowering = ModuleLowering(module, code, FORCED)
        lower(lowering, function) { builder, method -> assertNull(FunctionLowerer(lowering, function, builder, method)) }

        return assertNotNull(lowering.chunkStarts[function.index.index]).also { starts -> assertTrue(starts.size > 1) }
    }

    private fun lowering(): ModuleLowering {
        return ModuleLowering(module, code, OPTIONS)
    }

    private fun functionLowering(
        lowering: ModuleLowering,
        function: CirFunction,
        builder: ClassBuilder,
        method: DexMethod,
        router: FunctionRouter = EstimateRouter(),
        slotKindAnalyzer: SlotKindAnalyzer = DataflowSlotKindAnalyzer(),
        chunkEmitter: ChunkEmitter = ProbingChunkEmitter(lowering, function, builder),
    ): FunctionLowering {
        return FunctionLowering(lowering, function, builder, method, router, slotKindAnalyzer, ChunkPlanner(), chunkEmitter)
    }

    private fun route(
        lowering: ModuleLowering,
        function: CirFunction,
    ): Route {
        return EstimateRouter().route(wholeFunctionAnalysis(lowering, function, functionFacts(lowering, function)))
    }

    private fun frameSlots(
        lowering: ModuleLowering,
        function: CirFunction,
    ): Int {
        return functionFacts(lowering, function).frameSlots
    }

    private fun recording(flows: MutableList<SlotKindFlow>): SlotKindAnalyzer {
        return SlotKindAnalyzer { function, facts, initialised ->
            DataflowSlotKindAnalyzer().analyze(function, facts, initialised).also { flow -> flows += flow }
        }
    }

    private fun pinned(
        flow: SlotKindFlow,
        function: CirFunction,
    ): Boolean {
        return (0 until frameSlots(lowering(), function)).any { slot -> flow.pin(slot) != SlotKindFlow.UNDEFINED }
    }

    private fun fresh(
        module: Module,
        code: CirModule,
        function: CirFunction,
        route: Route,
    ): ByteArray {

        val lowering = ModuleLowering(module, code, OPTIONS)
        val flows = ArrayList<SlotKindFlow>()

        val generated = lower(lowering, function) { builder, method ->
            val routed = functionLowering(lowering, function, builder, method, router = FunctionRouter { _ -> route }, slotKindAnalyzer = recording(flows))
            assertNull(routed.lower())
        }
        assertEquals(1, flows.size)

        return generated
    }

    private fun upFront(
        function: CirFunction,
        homes: RegisterHomes?,
    ): ByteArray {

        val lowering = ModuleLowering(module, code, FORCED)

        return lower(lowering, function) { builder, method ->

            val emitter = ProbingChunkEmitter(lowering, function, builder)
            val fixed = ChunkEmitter { analysis, facts, _, planned, chunkMethod, next -> emitter.emit(analysis, facts, homes, planned, chunkMethod, next) }

            assertNull(functionLowering(lowering, function, builder, method, chunkEmitter = fixed).lower())
        }
    }

    private fun assertRecovered(
        recovered: ByteArray,
        fresh: ByteArray,
        function: CirFunction,
        orphans: Int,
        chunks: Int,
    ) {

        val name = "f${function.index.index}"
        val recoveredMethods = methods(renderSmali(listOf(recovered)))
        val freshMethods = methods(renderSmali(listOf(fresh)))
        repeat(chunks) { chunk ->
            assertEquals(body(freshMethods, "$name\$c$chunk"), body(recoveredMethods, "$name\$c${orphans + chunk}"))
        }

        val calls = Regex("->$name\\\$c(\\d+)\\(")
        val dispatcher = checkNotNull(recoveredMethods[name]) { "$name is missing" }
        assertEquals((orphans until orphans + chunks).toList(), calls.findAll(dispatcher).map { call -> call.groupValues[1].toInt() }.toList())
        assertEquals(freshMethods[name], calls.replace(dispatcher) { call -> "->$name\$c${call.groupValues[1].toInt() - orphans}(" })
    }

    private fun wholeDex(function: CirFunction): ByteArray {

        val lowering = lowering()
        val refusing = ChunkEmitter { _, _, _, _, _, _ -> throw Unsupported("no chunks") }

        return lower(lowering, function) { builder, method ->
            assertNull(functionLowering(lowering, function, builder, method, chunkEmitter = refusing).lower())
            assertNull(lowering.chunkStarts[function.index.index])
        }
    }

    private fun lower(
        lowering: ModuleLowering,
        function: CirFunction,
        block: (ClassBuilder, DexMethod) -> Unit,
    ): ByteArray {

        val file = Dexter().beginFile()
        val builder = lowering.codeClassBuilder(file, lowering.codeClass(function.index))
        block(builder, lowering.functionMethod(function.index).method)

        return file.finish()
    }

    private fun fill(
        builder: ClassBuilder,
        owner: ClassType,
        references: List<DexMethod>,
    ) {

        val filler = DexMethod(owner, "fill", DexType.Void, emptyList())
        val body = builder.beginStaticMethod(filler, FILL_REGISTERS, MemberAccess.Public)

        references.forEach { reference -> body.invokeStaticRange(reference, Reg(0)) }
        repeat(MAX_METHOD_IDS - references.size - 1) { index ->
            body.invokeStatic(DexMethod(ClassType("LFill${index / FILL_NAMES};"), "m${index % FILL_NAMES}", DexType.Void, emptyList()))
        }

        body.returnVoid()
        body.finish()
    }

    private fun methodReferences(dex: ByteArray): List<DexMethod> {
        return DexBackedDexFile(Opcodes.forApi(MIN_API), dex).getReferences(ReferenceType.METHOD).map { reference ->
            reference as MethodReference
            DexMethod(
                referenceType(reference.definingClass),
                reference.name,
                dexType(reference.returnType),
                reference.parameterTypes.map { parameter -> valueType(parameter.toString()) },
            )
        }
    }

    private fun methodNames(dex: ByteArray): Set<String> {
        return DexBackedDexFile(Opcodes.forApi(MIN_API), dex).classes.flatMap { type -> type.methods.map { method -> method.name } }.toSet()
    }

    private fun methodSmali(
        dex: ByteArray,
        function: CirFunction,
    ): String {
        return methodSmali(renderSmali(listOf(dex)), "f${function.index.index}")
    }

    private fun methodSmali(
        smali: String,
        name: String,
    ): String {

        val start = smali.indexOf(".method public static $name(")
        check(start >= 0) { "$name is missing" }

        return smali.substring(start, smali.indexOf(".end method", start))
    }

    private fun methods(smali: String): Map<String, String> {

        val methods = HashMap<String, String>()
        var start = smali.indexOf(METHOD_HEADER)
        while (start >= 0) {

            val name = smali.substring(start + METHOD_HEADER.length, smali.indexOf('(', start))
            val end = smali.indexOf(".end method", start)
            methods[name] = smali.substring(start, end)
            start = smali.indexOf(METHOD_HEADER, end)
        }

        return methods
    }

    private fun body(
        methods: Map<String, String>,
        name: String,
    ): String {

        val method = checkNotNull(methods[name]) { "$name is missing" }

        return method.substring(method.indexOf('\n'))
    }
}

private const val FRAME = 0

private const val HELPER = 1

private const val PINNED_HELPER = 2

private const val PINNED_SPLIT = 3

private const val MAX_METHOD_IDS = 65_536

private const val FILL_NAMES = 256

private const val FILL_REGISTERS = 255

private const val INLINE_LOAD = "Lsun/misc/Unsafe;->getInt"

private const val METHOD_HEADER = ".method public static "

private val OPTIONS = DexOptions(byteArrayBaseOffset = 16L)

private val FORCED = DexOptions(byteArrayBaseOffset = 16L, splitInstructions = 1)
