@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.compiler.backend.jvm

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.backend.abi.BRIDGE_CALL
import io.github.charlietap.chasm.compiler.backend.abi.CLEAR_ROOTS
import io.github.charlietap.chasm.compiler.backend.abi.CLOSE_FRAME
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OPEN_ROOTS
import io.github.charlietap.chasm.compiler.backend.abi.TAKE_EXCEPTION
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.runtime.compiled.CHUNK_METHOD_SEPARATOR
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FrameNode
import org.objectweb.asm.tree.IntInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode
import org.objectweb.asm.tree.analysis.Analyzer
import org.objectweb.asm.tree.analysis.SourceInterpreter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RootFramesTest {

    private val module = decodeModule(fixture("call_effects"))
    private val methods: Map<String, MethodNode> = lowerExports(JvmOptions()).mapValues { (_, methods) -> methods.single() }

    @Test
    fun `a function holding references opens a root frame when something it calls may collect`() {
        listOf("calls_chain", "calls_import", "calls_indirect", "calls_indirect_dynamic", "calls_ref", "calls_tail_alloc")
            .forEach { name -> assertEquals(true, opensRoots(name), name) }
    }

    @Test
    fun `a function holding references opens no root frame when nothing it runs collects`() {
        listOf("get", "calls_get", "touch_arrays", "touch_references", "even", "odd").forEach { name ->
            assertEquals(false, opensRoots(name), name)
        }
    }

    @Test
    fun `a function that collects opens no root frame when no reference is live across the collection`() {
        listOf("struct_new", "struct_new_default", "array_new_ii", "via_alloc", "dead_across_call").forEach { name ->
            assertEquals(false, opensRoots(name), name)
            assertEquals(false, invokes(name, FRAME), name)
            assertEquals(false, invokes(name, CLOSE_FRAME), name)
        }
    }

    @Test
    fun `a tail call to a function that collects needs no root frame in the caller`() {
        listOf("tail_alloc", "tail_indirect", "tail_ref").forEach { name -> assertEquals(false, opensRoots(name), name) }
    }

    @Test
    fun `a reference is published only while a collection may still follow before it is read`() {
        assertEquals(1 to 0, publicationsAroundCall("live_across_call"))
        assertEquals(0, publicationsAroundCall("copies_around_call").second)
        assertEquals(0, publicationsAroundCall("copies_after_call").second)
    }

    @Test
    fun `a self tail call clears the root frame then republishes its reference parameters`() {

        val method = checkNotNull(methods["restarts"])
        val clear = method.instructions.indexOfFirst { instruction -> instruction.invokes(CLEAR_ROOTS) }
        val sites = publicationSites(method)

        assertTrue(clear >= 0)
        assertEquals(1, sites.count { site -> site > clear })
    }

    @Test
    fun `a catch clears the roots of operands above the handler`() {

        val method = checkNotNull(methods["catches"])
        val calls = method.instructions.filter { instruction -> instruction is MethodInsnNode }
        val clear = calls.indexOfFirst { call -> call.invokes(CLEAR_ROOTS) }
        val take = calls.indexOfFirst { call -> call.invokes(TAKE_EXCEPTION) }

        assertTrue(clear in 0 until take)
        assertEquals(CLEARED_FIRST to CLEARED_COUNT, clearedRoots(method, method.instructions.indexOf(calls[clear])))
    }

    @Test
    fun `a chunked catch does not clear a payload slot at or above its stack height`() {

        val code = compile()
        val catches = code.functions.single { function -> function.exceptions?.regions.orEmpty().isNotEmpty() }
        val exceptions = checkNotNull(catches.exceptions)
        val region = exceptions.regions.single()
        val handler = region.catches.single().copy(tag = null, payloadSlots = intArrayOf(BOX_SLOT), exceptionReference = true, stackSlots = BOX_SLOT)
        val overlapping = catches.copy(exceptions = exceptions.copy(regions = listOf(region.copy(catches = listOf(handler)))))

        val chunks = lowerExports(JvmOptions(splitInstructions = 2), code.copy(functions = code.functions.map { function -> if (function === catches) overlapping else function }))
        val matchers = checkNotNull(chunks["catches"]).filter { method -> method.instructions.any { instruction -> instruction.invokes(TAKE_EXCEPTION) } }
        val stores = matchers.map { method -> matcherStores(method) }

        assertTrue(stores.isNotEmpty())
        stores.forEach { (cleared, payload) -> assertFalse(payload in cleared) }
    }

    @Test
    fun `a chunk publishes a reference written before a call and not one written after the last call`() {

        val chunks = lowerExports(JvmOptions(splitInstructions = 2)).mapValues { (_, methods) ->
            methods.filter { method -> CHUNK_METHOD_SEPARATOR in method.name }
        }

        val around = checkNotNull(chunks["copies_around_call"])
        val after = checkNotNull(chunks["copies_after_call"])

        assertTrue(around.isNotEmpty())
        assertTrue(after.isNotEmpty())
        assertTrue(publications(around) > 0)
        assertEquals(0, publications(after))
    }

    private fun matcherStores(method: MethodNode): Pair<List<Int>, Int> {

        val instructions = method.instructions.toList()
        val clear = instructions.indexOfFirst { instruction -> instruction.invokes(CLEAR_ROOTS) }
        val take = instructions.indexOfFirst { instruction -> instruction.invokes(TAKE_EXCEPTION) }
        val cleared = (clear until take).filter { index -> instructions[index].opcode == Opcodes.LCONST_0 }.map { index -> (instructions[index + 1] as VarInsnNode).`var` }
        val payload = instructions[take + 1] as VarInsnNode

        assertTrue(clear in 0 until take)
        assertEquals(Opcodes.LSTORE, payload.opcode)

        return cleared to payload.`var`
    }

    private fun publications(methods: List<MethodNode>): Int {
        return methods.sumOf { method -> publicationSites(method).size }
    }

    private fun publicationSites(method: MethodNode): List<Int> {

        val instructions = method.instructions.toList()
        val fetch = instructions.indices.firstOrNull { index ->
            instructions[index].invokes(FRAME) && instructions.getOrNull(index + 1)?.opcode == Opcodes.ASTORE
        }
        val roots = (instructions[checkNotNull(fetch) { "no root frame array in ${method.name}" } + 1] as VarInsnNode).`var`
        val frames = Analyzer(SourceInterpreter()).analyze(OWNER, method)

        fun loadsRoots(
            index: Int,
            depth: Int,
        ): Boolean {

            val frame = frames[index] ?: return false

            return frame.getStack(frame.stackSize - depth).insns.all { source ->
                source is VarInsnNode && source.opcode == Opcodes.ALOAD && source.`var` == roots
            }
        }

        val body = instructions.indexOfFirst { instruction -> instruction is FrameNode }.takeIf { index -> index >= 0 } ?: instructions.size
        check((body until instructions.size).none { index -> instructions[index].opcode == Opcodes.LALOAD && loadsRoots(index, 2) }) {
            "local $roots of ${method.name} is read after the prologue, so it is not the root frame array"
        }

        return instructions.indices.filter { index -> instructions[index].opcode == Opcodes.LASTORE && loadsRoots(index, 3) }
    }

    private fun clearedRoots(
        method: MethodNode,
        clear: Int,
    ): Pair<Int, Int> {

        val frame = checkNotNull(Analyzer(SourceInterpreter()).analyze(OWNER, method)[clear])
        val start = frame.getStack(frame.stackSize - 2).insns.single()
        val count = frame.getStack(frame.stackSize - 1).insns.single()

        assertEquals(Opcodes.IADD, start.opcode)

        return constant(start.previous) to constant(count)
    }

    private fun constant(instruction: AbstractInsnNode): Int {
        return when (instruction) {
            is IntInsnNode -> instruction.operand
            else -> instruction.opcode - Opcodes.ICONST_0
        }
    }

    private fun publicationsAroundCall(name: String): Pair<Int, Int> {

        val method = checkNotNull(methods[name])
        val bridge = method.instructions.indexOfLast { instruction -> instruction.invokes(BRIDGE_CALL) }
        assertTrue(bridge >= 0, name)

        val sites = publicationSites(method)
        return sites.count { site -> site < bridge } to sites.count { site -> site > bridge }
    }

    private fun opensRoots(name: String): Boolean {
        return invokes(name, OPEN_ROOTS)
    }

    private fun invokes(
        name: String,
        runtime: MethodRef,
    ): Boolean {
        val method = checkNotNull(methods[name]) { "no method for $name" }
        return method.instructions.any { instruction -> instruction.invokes(runtime) }
    }

    private fun AbstractInsnNode.invokes(method: MethodRef): Boolean {
        return this is MethodInsnNode && owner == Type.getType(method.owner).internalName && name == method.name
    }

    private fun compile(): CirModule {
        return ModuleCompiler(module).getOrThrow { error -> IllegalStateException(error.toString()) }
    }

    private fun lowerExports(
        options: JvmOptions,
        code: CirModule = compile(),
    ): Map<String, List<MethodNode>> {

        val output = Generated()
        ModuleLowerer(module, code, options, output, ModuleDeclarationsFactory(module))

        val methods = output.classes.values.flatMap { bytes -> ClassNode().also { node -> ClassReader(bytes).accept(node, 0) }.methods }
        return module.exports.mapNotNull { export ->
            val function = export.descriptor as? Export.Descriptor.Function ?: return@mapNotNull null
            val name = "f${function.functionIndex.idx}"
            val lowered = methods.filter { method -> method.name == name || method.name.startsWith("$name$CHUNK_METHOD_SEPARATOR") }
            if (lowered.isEmpty()) null else export.name.name to lowered
        }.toMap()
    }

    private companion object {
        const val OWNER = "RootFrames"
        const val CLEARED_FIRST = 2
        const val CLEARED_COUNT = 1
        const val BOX_SLOT = 0
    }
}
