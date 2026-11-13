@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.compiler.backend.jvm

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.backend.abi.BRIDGE_CALL
import io.github.charlietap.chasm.compiler.backend.abi.CLOSE_FRAME
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OPEN_ROOTS
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.runtime.compiled.CHUNK_METHOD_SEPARATOR
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FrameNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode
import org.objectweb.asm.tree.analysis.Analyzer
import org.objectweb.asm.tree.analysis.SourceInterpreter
import kotlin.test.Test
import kotlin.test.assertEquals
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
    }
}
