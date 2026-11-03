package io.github.charlietap.chasm.compiler.backend.dex

import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CorpusCensusTest {

    @Test
    fun `every corpus module generates`() {
        if (System.getProperty("codegen.census") != "true") return
        val root = File(System.getProperty("user.dir"), "../../chasm/build/wasm-corpus-resources/corpus")
        val modules = root.walkTopDown().filter { it.extension == "wasm" }.sortedBy { it.path }.toList()
        assertTrue(modules.isNotEmpty(), "no corpus modules under $root: run :chasm:prepareCorpusResources first")
        val failures = ArrayList<String>()
        var bytes = 0L
        for (file in modules) {
            val split = System.getProperty("codegen.split")?.toInt()
            for (options in listOf(DexOptions(splitInstructions = split), DexOptions(byteArrayBaseOffset = 16L, splitInstructions = split))) {
                try {
                    bytes += generate(decodeModule(file.readBytes()), options).dex.sumOf { it.size }
                } catch (unsupported: UnsupportedFunctions) {
                    failures += "${file.relativeTo(root)}: ${unsupported.message}"
                } catch (failure: Throwable) {
                    failures += "${file.relativeTo(root)}: ${failure::class.simpleName}: ${failure.message?.take(300)}"
                }
            }
        }
        println("CENSUS modules=${modules.size} failed=${failures.size} dex=${bytes / 1024}K")
        assertEquals(emptyList(), failures)
    }

    @Test
    fun `corpus generation is deterministic`() {
        val root = System.getProperty("codegen.determinism")?.let(::File) ?: return
        val modules = root.walkTopDown().filter { it.extension == "wasm" }.sortedBy { it.path }.toList()
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val differing = ArrayList<String>()
        for (file in modules.shuffled()) {
            val options = DexOptions(packageName = "chasm.jit.h0", byteArrayBaseOffset = 16L)
            val first = runCatching { generate(decodeModule(file.readBytes()), options).dex }.getOrNull() ?: continue
            val second = generate(decodeModule(file.readBytes()), options).dex
            if (first.size != second.size || first.indices.any { index -> !first[index].contentEquals(second[index]) }) {
                differing += file.name
            }
        }
        for (file in modules) {
            val options = DexOptions(packageName = "chasm.jit.h0", byteArrayBaseOffset = 16L)
            val dex = runCatching { generate(decodeModule(file.readBytes()), options).dex }.getOrNull() ?: continue
            dex.forEach(digest::update)
        }
        println("DETERMINISM modules=${modules.size} differing=$differing digest=${digest.digest().joinToString("") { "%02x".format(it) }}")
        assertEquals(emptyList(), differing)
    }

    @Test
    fun `corpus runtime sites`() {
        val root = System.getProperty("codegen.sites")?.let(::File) ?: return
        val modules = root.walkTopDown().filter { it.extension == "wasm" }.sortedBy { it.path }.toList()
        val census = SiteCensus()
        for (file in modules) {
            val module = runCatching { decodeModule(file.readBytes()) }.getOrNull() ?: continue
            val dex = runCatching { generate(module, DexOptions(byteArrayBaseOffset = 16L)) }.getOrNull() ?: continue
            census.module(dex.dex)
        }
        census.report().forEach { line -> println("SITES $line") }
    }
}

private class SiteCensus {

    private val files = ArrayList<Int>()
    private val classes = HashMap<String, Long>()
    private val methods = HashMap<String, Long>()
    private val units = HashMap<String, Long>()
    private val instructions = HashMap<String, Long>()
    private val sites = HashMap<String, Long>()
    private val members = HashMap<String, Long>()
    private var dexBytes = 0L

    fun module(dex: List<ByteArray>) {
        files += dex.size
        for (bytes in dex) {
            dexBytes += bytes.size
            for (type in DexBackedDexFile(Opcodes.forApi(26), bytes).classes) {
                val role = role(type.type)
                classes.merge(role, 1L, Long::plus)
                for (method in type.methods) {
                    methods.merge(role, 1L, Long::plus)
                    val implementation = method.implementation ?: continue
                    for (instruction in implementation.instructions) {
                        units.merge(role, instruction.codeUnits.toLong(), Long::plus)
                        instructions.merge(role, 1L, Long::plus)
                        val kind = kind(instruction.opcode.name) ?: continue
                        sites.merge("$role $kind", 1L, Long::plus)
                        if (role != "code" || instruction !is ReferenceInstruction) continue
                        val member = when (val reference = instruction.reference) {
                            is MethodReference -> "${reference.definingClass.substringAfterLast('/')}.${reference.name}"
                            is FieldReference -> "${reference.definingClass.substringAfterLast('/')}.${reference.name}"
                            else -> reference.toString().substringAfterLast('/')
                        }
                        members.merge("$kind $member", 1L, Long::plus)
                    }
                }
            }
        }
    }

    fun report(): List<String> {
        files.sort()
        val lines = ArrayList<String>()
        lines += "modules=${files.size} dexFiles=${files.sum()} median=${files[files.size / 2]} " +
            "p90=${files[files.size * 9 / 10]} max=${files.last()} dexBytes=${dexBytes / 1024}K"
        for (role in classes.keys.sorted()) {
            lines += "role=$role classes=${classes[role]} methods=${methods[role]} " +
                "instructions=${instructions[role] ?: 0} units=${units[role] ?: 0}"
        }
        for ((key, count) in sites.entries.sortedWith(compareBy({ it.key.substringBefore(' ') }, { -it.value }))) {
            val role = key.substringBefore(' ')
            val per = count * 1000.0 / (instructions[role] ?: 1L)
            lines += "%-30s %10d per1k=%.1f".format(key, count, per)
        }
        val code = instructions["code"] ?: 1L
        for ((member, count) in members.entries.sortedByDescending { it.value }.take(40)) {
            lines += "member %-60s %9d per1k=%.2f".format(member, count, count * 1000.0 / code)
        }
        return lines
    }

    private fun role(type: String): String = when {
        type.contains("/Code") -> "code"
        type.contains("/Entry") -> "entry"
        type.contains("/Install") -> "install"
        type.contains("/Sig") -> "sig"
        else -> "other"
    }

    private fun kind(name: String): String? = when {
        name.startsWith("invoke-static") -> "invoke-static"
        name.startsWith("invoke-virtual") -> "invoke-virtual"
        name.startsWith("invoke-interface") -> "invoke-interface"
        name.startsWith("invoke-direct") -> "invoke-direct"
        name.startsWith("iget") -> "iget"
        name.startsWith("iput") -> "iput"
        name.startsWith("sget") || name.startsWith("sput") -> "static-field"
        name == "check-cast" -> "check-cast"
        name == "instance-of" -> "instance-of"
        name.startsWith("new-instance") -> "new-instance"
        name.startsWith("new-array") || name.startsWith("filled-new-array") -> "new-array"
        name.startsWith("const-class") || name.startsWith("const-string") -> "const-ref"
        else -> null
    }
}
