package io.github.charlietap.chasm.aot.internal

import io.github.charlietap.chasm.InternalChasmApi
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

/**
 * Lines [first] to [last] a shrinker gave a compiled method, which were
 * line [original] before it, or [original] onwards when [linear].
 */
@InternalChasmApi
class LineRange(
    val first: Int,
    val last: Int,
    val original: Int,
    val linear: Boolean,
)

/**
 * The lines of a package's compiled methods after a shrinker renumbered
 * them, keyed by [trapLinesKey], mapped back to the lines Chasm gave them.
 */
@InternalChasmApi
class TrapLines(
    methods: Map<String, List<LineRange>>,
) {
    private val methods = methods.mapValues { (_, ranges) -> MethodLines(ranges) }

    /** The line Chasm gave [line] of [methodName] in the class named [className] without its package, or -1. */
    fun original(
        className: String,
        methodName: String,
        line: Int,
    ): Int {
        val range = methods[trapLinesKey(className, methodName)]?.range(line) ?: return -1
        return if (range.linear) range.original + line - range.first else range.original
    }
}

// R8 gives a method's ranges disjoint lines, which sorted are searched; ranges that overlap are kept in their order,
// where the first to cover a line wins.
@InternalChasmApi
private class MethodLines(
    ranges: List<LineRange>,
) {
    private val disjoint: Boolean
    private val ranges: List<LineRange>

    init {
        val sorted = ranges.sortedBy(LineRange::first)
        disjoint = (1 until sorted.size).all { index -> sorted[index - 1].last < sorted[index].first }
        this.ranges = if (disjoint) sorted else ranges
    }

    fun range(
        line: Int,
    ): LineRange? {
        if (!disjoint) return ranges.firstOrNull { range -> line in range.first..range.last }
        val index = ranges.binarySearch { range ->
            when {
                range.last < line -> -1
                range.first > line -> 1
                else -> 0
            }
        }
        return ranges.getOrNull(index)
    }
}

@InternalChasmApi
fun trapLinesKey(
    className: String,
    methodName: String,
): String = "$className.$methodName"

@InternalChasmApi
fun trapLinesResource(
    packagePath: String,
): String = "$packagePath/lines.$TRAP_LINES_EXTENSION"

@InternalChasmApi
const val TRAP_LINES_EXTENSION = "chasmlines"

@InternalChasmApi
fun writeTrapLines(
    output: OutputStream,
    methods: Map<String, List<LineRange>>,
) {
    val buffer = ByteArrayOutputStream()
    buffer.writeInt(MAGIC)
    buffer.writeUnsigned(methods.size)
    methods.toSortedMap().forEach { (key, ranges) ->
        val name = key.encodeToByteArray()
        buffer.writeUnsigned(name.size)
        buffer.write(name)
        buffer.writeUnsigned(ranges.size)
        ranges.forEach { range ->
            buffer.writeUnsigned(range.first)
            buffer.writeUnsigned(range.last - range.first)
            buffer.writeUnsigned(range.original)
            buffer.writeUnsigned(if (range.linear) 1 else 0)
        }
    }
    buffer.writeTo(output)
}

/** Null when [input] is not a line table this runtime can read. */
@InternalChasmApi
fun readTrapLines(
    input: InputStream,
): TrapLines? = try {
    val reader = Reader(input.readBytes())
    if (reader.int() != MAGIC) {
        null
    } else {
        val methods = HashMap<String, List<LineRange>>()
        repeat(reader.count()) {
            val key = reader.string()
            methods[key] = List(reader.count()) {
                val first = reader.unsigned()
                val last = first + reader.unsigned()
                val original = reader.unsigned()
                LineRange(first, last, original, reader.unsigned() == 1)
            }
        }
        if (reader.finished()) TrapLines(methods) else null
    }
} catch (_: IndexOutOfBoundsException) {
    null
} catch (_: IllegalArgumentException) {
    null
}

private const val MAGIC = 0x43544C31
