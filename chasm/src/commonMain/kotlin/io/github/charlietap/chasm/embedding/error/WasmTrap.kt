package io.github.charlietap.chasm.embedding.error

import kotlin.jvm.JvmInline

/**
 * A stack trace for Wasm code that failed. [frames] lists the most recent call
 * first. Very deep traces are shortened, and [omittedFrames] counts the calls
 * left out.
 */
class WasmTrap internal constructor(
    val reason: TrapReason,
    private val message: String,
    val frames: List<WasmFrame>,
    val omittedFrames: Int,
    /** False if some of the earliest calls could not be recovered. */
    val traceComplete: Boolean,
    val memoryAccess: MemoryAccess?,
    private val omissionIndex: Int,
) {

    override fun toString(): String = buildString {
        when {
            reason == TrapReason.OTHER -> append(message)
            // Keep values such as a host failure's reason.
            message.contains('(') -> append("${reason.description}: $message")
            else -> append(reason.description)
        }
        memoryAccess?.let { access ->
            append("\n  ")
            append(access)
        }
        val modules = frames.mapNotNullTo(HashSet()) { frame -> frame.moduleName }
        frames.forEachIndexed { index, frame ->
            if (index == omissionIndex && omittedFrames > 0) {
                append("\n  ... ")
                append(omittedFrames)
                append(if (omittedFrames == 1) " frame omitted ..." else " frames omitted ...")
            }
            append("\n  at ")
            append(frame.describe(includeModule = modules.size > 1))
        }
        if (omissionIndex >= frames.size && omittedFrames > 0) {
            append("\n  ... ")
            append(omittedFrames)
            append(" frames omitted ...")
        }
        if (!traceComplete) append("\n  ... trace incomplete")
    }
}

class WasmFrame internal constructor(
    val moduleName: String?,
    /** The function's index within its module, as shown by Wasm tools. */
    val functionIndex: Int,
    /** The function's name, if the module provides one. */
    val functionName: String?,
    /** Position in the module's bytes of the failing instruction, or of the call for earlier frames. */
    val wasmOffset: Int?,
) {

    internal fun describe(includeModule: Boolean): String = buildString {
        if (functionName != null) {
            append(functionName)
            append(" (func[")
            append(functionIndex)
            append("])")
        } else {
            append("func[")
            append(functionIndex)
            append(']')
        }
        if (includeModule && moduleName != null) {
            append(" in module ")
            append(moduleName)
        }
        if (wasmOffset != null) {
            append(" wasm offset 0x")
            append(wasmOffset.toString(HEX_RADIX).padStart(OFFSET_DIGITS, '0'))
        } else {
            append(" wasm offset unknown")
        }
    }

    override fun equals(other: Any?): Boolean = other is WasmFrame &&
        other.moduleName == moduleName &&
        other.functionIndex == functionIndex &&
        other.functionName == functionName &&
        other.wasmOffset == wasmOffset

    override fun hashCode(): Int {
        var result = moduleName?.hashCode() ?: 0
        result = 31 * result + functionIndex
        result = 31 * result + (functionName?.hashCode() ?: 0)
        result = 31 * result + (wasmOffset ?: -1)
        return result
    }

    override fun toString(): String = describe(includeModule = true)
}

class MemoryAccess internal constructor(
    val kind: Kind,
    val memoryIndex: Int,
    val address: Long,
    /** Number of bytes accessed. */
    val width: Long,
    /** Size of the memory in bytes when the access failed. */
    val memorySize: Long,
) {

    /** More kinds may be added in future, so `when` expressions should include an `else` branch. */
    enum class Kind { READ, WRITE, COPY, FILL, INIT }

    override fun equals(other: Any?): Boolean = other is MemoryAccess &&
        other.kind == kind &&
        other.memoryIndex == memoryIndex &&
        other.address == address &&
        other.width == width &&
        other.memorySize == memorySize

    override fun hashCode(): Int {
        var result = kind.hashCode()
        result = 31 * result + memoryIndex
        result = 31 * result + address.hashCode()
        result = 31 * result + width.hashCode()
        result = 31 * result + memorySize.hashCode()
        return result
    }

    override fun toString(): String = buildString {
        append("memory ")
        append(memoryIndex)
        append(", ")
        append(kind.name.lowercase())
        append(' ')
        append(width)
        append(if (width == 1L) " byte at 0x" else " bytes at 0x")
        append(address.toString(HEX_RADIX).padStart(ADDRESS_DIGITS, '0'))
        append(", memory size 0x")
        append(memorySize.toString(HEX_RADIX).padStart(ADDRESS_DIGITS, '0'))
    }
}

@JvmInline
value class TrapReason private constructor(val description: String) {

    override fun toString(): String = description

    companion object {
        val UNREACHABLE = TrapReason("unreachable executed")
        val MEMORY_OUT_OF_BOUNDS = TrapReason("memory out of bounds")
        val INTEGER_DIVIDE_BY_ZERO = TrapReason("integer divide by zero")
        val INTEGER_OVERFLOW = TrapReason("integer overflow")
        val INVALID_CONVERSION = TrapReason("invalid conversion to integer")
        val TABLE_OUT_OF_BOUNDS = TrapReason("table out of bounds")
        val INDIRECT_CALL_TYPE_MISMATCH = TrapReason("indirect call type mismatch")
        val NULL_REFERENCE = TrapReason("null reference")
        val CAST_FAILURE = TrapReason("cast failure")
        val ARRAY_OUT_OF_BOUNDS = TrapReason("array out of bounds")
        val CALL_STACK_EXHAUSTED = TrapReason("call stack exhausted")
        val GUEST_HEAP_EXHAUSTED = TrapReason("guest heap exhausted")
        val FUEL_EXHAUSTED = TrapReason("fuel exhausted")
        val INTERRUPTED = TrapReason("interrupted")
        val HOST_FUNCTION_FAILURE = TrapReason("host function failed")
        val UNCAUGHT_EXCEPTION = TrapReason("uncaught exception")
        val OTHER = TrapReason("other")
    }
}

/** Thrown by [io.github.charlietap.chasm.embedding.shapes.expect] when the error has a stack trace. */
class WasmTrapException(
    val trap: WasmTrap,
    message: String,
) : IllegalStateException(message)

private const val HEX_RADIX = 16
private const val OFFSET_DIGITS = 5
private const val ADDRESS_DIGITS = 8
