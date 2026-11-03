@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.internal.LineRange
import io.github.charlietap.chasm.aot.internal.trapLinesKey
import io.github.charlietap.chasm.aot.internal.trapLinesResource
import io.github.charlietap.chasm.aot.internal.writeTrapLines
import io.github.charlietap.chasm.runtime.compiled.CHUNK_CLASS_PREFIX
import io.github.charlietap.chasm.runtime.compiled.CODE_CLASS_PREFIX
import io.github.charlietap.chasm.runtime.compiled.FUNCTION_METHOD_PREFIX
import io.github.charlietap.chasm.runtime.compiled.TRAP_DIAGNOSTICS_SOURCE_FILE
import java.io.ByteArrayOutputStream
import java.io.Reader

/**
 * What an Android app's modules compiled with trap diagnostics need to say
 * where a trap happened once R8 has shrunk the app. R8 renumbers the lines of
 * the methods it keeps, and [mapping], the mapping file it wrote, maps them
 * back. Returns a table for each package holding such modules, keyed by the
 * path the app's assets must carry it at.
 */
fun retraceTrapLines(
    mapping: Reader,
): Map<String, ByteArray> {
    val packages = HashMap<String, MutableMap<String, List<LineRange>>>()
    var mapped: MappedClass? = null

    fun finish() {
        val finished = mapped?.takeIf(MappedClass::compiled) ?: return
        // A package's table is written even when empty: without one, renumbered lines would be read as Chasm's own.
        packages.getOrPut(finished.packagePath, ::HashMap).putAll(finished.methods())
    }
    // Only classes named as Chasm names its compiled code are read; every other class's lines are skipped.
    mapping.buffered().lineSequence().forEach { line ->
        val start = line.indexOfFirst { character -> !character.isWhitespace() }
        when {
            start < 0 -> Unit
            line[start] == '#' -> mapped?.comment(line)
            start == 0 -> {
                finish()
                mapped = CLASS.matchEntire(line)
                    ?.takeIf { match -> compiledClassName(match.groupValues[1].substringAfterLast('.')) }
                    ?.let { match -> MappedClass(match.groupValues[1], match.groupValues[2]) }
            }
            else -> mapped?.member(line)
        }
    }
    finish()
    return packages.entries.associate { (packagePath, methods) ->
        trapLinesResource(packagePath) to ByteArrayOutputStream().also { stream -> writeTrapLines(stream, methods) }.toByteArray()
    }
}

// One class of the mapping: the lines of its methods, grouped by the residual lines they share. A group lists the
// frames inlined at those lines innermost first, so its last frame is the method R8 kept.
private class MappedClass(
    private val original: String,
    residual: String,
) {
    private val residualName = residual.substringAfterLast('.')
    private var members = 0
    private val groups = ArrayList<Group>()

    val packagePath: String = original.substringBeforeLast('.', "").replace('.', '/')

    // Only classes Chasm compiled with trap diagnostics carry its source file name, which R8 records before members.
    var compiled = false
        private set

    fun comment(
        comment: String,
    ) {
        when {
            members == 0 && SOURCE_FILE_ID in comment && "\"$TRAP_DIAGNOSTICS_SOURCE_FILE\"" in comment -> compiled = true
            // An outlined range's frames depend on the outline's own frame, which a line alone cannot give.
            OUTLINE_CALL_SITE_ID in comment -> groups.lastOrNull()?.outlined = true
        }
    }

    fun member(
        line: String,
    ) {
        members++
        if (!compiled) return
        val match = METHOD.matchEntire(line) ?: return
        val (first, last, name, originalFirst, originalLast, residual) = match.destructured
        if (first.isEmpty()) return
        val frame = Frame(name, originalFirst.toIntOrNull(), originalLast.toIntOrNull())
        val previous = groups.lastOrNull()
        if (previous != null && previous.first == first.toInt() && previous.last == last.toInt() && previous.residual == residual) {
            previous.outer = frame
        } else {
            groups += Group(first.toInt(), last.toInt(), residual, frame)
        }
    }

    fun methods(): Map<String, List<LineRange>> = groups
        .filter { group -> !group.outlined && group.residual.startsWith(FUNCTION_METHOD_PREFIX) && group.outer.names(original, group.residual) }
        .groupBy(Group::residual) { group -> group.range() }
        .mapKeys { (method, _) -> trapLinesKey(residualName, method) }

    private class Group(
        val first: Int,
        val last: Int,
        val residual: String,
        var outer: Frame,
    ) {
        var outlined = false

        fun range(): LineRange {
            val start = outer.first ?: return LineRange(first, last, first, linear = true)
            val end = outer.last ?: start
            return LineRange(first, last, start, linear = end != start)
        }
    }

    private class Frame(
        private val name: String,
        val first: Int?,
        val last: Int?,
    ) {
        // A frame of the kept method itself, named as it was compiled, with or without its class.
        fun names(
            className: String,
            method: String,
        ): Boolean = name == method || name == "$className.$method"
    }
}

private fun compiledClassName(
    simpleName: String,
): Boolean = simpleName.startsWith(CODE_CLASS_PREFIX) || simpleName.startsWith(CHUNK_CLASS_PREFIX)

private val CLASS = Regex("""^(\S+) -> (\S+):$""")
private val METHOD = Regex("""^\s+(?:(\d+):(\d+):)?\S+ (\S+?)\([^)]*\)(?::(\d+)(?::(\d+))?)? -> (\S+)$""")
private const val SOURCE_FILE_ID = "\"id\":\"sourceFile\""
private const val OUTLINE_CALL_SITE_ID = "\"id\":\"com.android.tools.r8.outlineCallsite\""
