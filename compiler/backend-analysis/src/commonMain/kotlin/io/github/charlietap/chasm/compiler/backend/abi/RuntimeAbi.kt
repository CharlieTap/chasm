package io.github.charlietap.chasm.compiler.backend.abi

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
const val CONTEXT_TYPE = "Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;"

@InternalChasmApi
const val MODULE_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;"

@InternalChasmApi
const val FUNCTION_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;"

@InternalChasmApi
const val TABLE_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/TableInstance;"

@InternalChasmApi
const val MEMORY_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;"

@InternalChasmApi
const val GLOBAL_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/GlobalInstance;"

@InternalChasmApi
const val DATA_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/DataInstance;"

@InternalChasmApi
const val ELEMENT_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/ElementInstance;"

@InternalChasmApi
const val STACK_LIMIT_FIELD = "compiledStackLimit"

internal const val CALL_DEPTH_FIELD = "compiledCallDepth"

@InternalChasmApi
const val UNWINDING_FIELD = "compiledUnwinding"

@InternalChasmApi
const val PENDING_TAIL_FIELD = "pendingTailFunction"

@InternalChasmApi
const val MAX_UNCHARGED_LEAF_FRAME_COST = 64

private const val THROWABLE = "Ljava/lang/Throwable;"
private const val NOTHING = "Ljava/lang/Void;"
private const val STRINGS = "[Ljava/lang/String;"

@InternalChasmApi
const val UNSAFE_TYPE = "Lsun/misc/Unsafe;"

@InternalChasmApi
const val MEMORY_BYTE_ARRAY_FIELD = "byteArray"

@InternalChasmApi
const val MEMORY_BYTE_BUFFER_FIELD = "byteBuffer"

@InternalChasmApi
const val BYTE_BUFFER_TYPE = "Ljava/nio/ByteBuffer;"

private class Owner(
    val descriptor: String,
) {
    fun method(
        name: String,
        vararg parameters: String,
        returns: String,
    ) = MethodRef(descriptor, name, parameters.asList(), returns)
}

private val compiledRuntime = Owner("Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;")
private val compiledCalls = Owner("Lio/github/charlietap/chasm/runtime/compiled/CompiledCalls;")
private val compiledExceptions = Owner("Lio/github/charlietap/chasm/runtime/compiled/CompiledExceptions;")
private val compiledOps = Owner("Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;")
private val compiledNumerics = Owner("Lio/github/charlietap/chasm/runtime/compiled/CompiledNumerics;")
private val compiledMemory = Owner("Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;")
private val memoryInstance = Owner(MEMORY_INSTANCE)
private val unsafe = Owner(UNSAFE_TYPE)
private val byteBuffer = Owner(BYTE_BUFFER_TYPE)
private val compiledBufferMemory = Owner("Lio/github/charlietap/chasm/runtime/compiled/CompiledBufferMemory;")
private val globalInstance = Owner(GLOBAL_INSTANCE)
private val javaInteger = Owner("Ljava/lang/Integer;")
private val javaLong = Owner("Ljava/lang/Long;")
private val javaFloat = Owner("Ljava/lang/Float;")
private val javaDouble = Owner("Ljava/lang/Double;")
private val javaSystem = Owner("Ljava/lang/System;")
private val javaMath = Owner("Ljava/lang/Math;")

@InternalChasmApi
val FUEL_CHECK = compiledRuntime.method("fuelCheck", CONTEXT_TYPE, returns = "V")

@InternalChasmApi
val INTERRUPT_CHECK = compiledRuntime.method("interruptCheck", CONTEXT_TYPE, returns = "V")

@InternalChasmApi
val FUEL_AND_INTERRUPT_CHECK = compiledRuntime.method("fuelAndInterruptCheck", CONTEXT_TYPE, returns = "V")

@InternalChasmApi
val CALL_STACK_EXHAUSTED = compiledRuntime.method("callStackExhausted", returns = NOTHING)

@InternalChasmApi
val UNREACHABLE = compiledRuntime.method("unreachable", returns = NOTHING)

@InternalChasmApi
val UNPACK_LONGS = compiledRuntime.method("unpackLongs", STRINGS, returns = "[J")

@InternalChasmApi
val DIVIDE_BY_ZERO = compiledRuntime.method("divideByZero", returns = NOTHING)

@InternalChasmApi
val NULL_REFERENCE = compiledRuntime.method("nullReference", returns = NOTHING)

@InternalChasmApi
val INTEGER_OVERFLOW = compiledRuntime.method("integerOverflow", returns = NOTHING)

@InternalChasmApi
val MEMORY_OUT_OF_BOUNDS = compiledRuntime.method("memoryOutOfBounds", returns = NOTHING)

@InternalChasmApi
val MEMORY_ACCESS_OUT_OF_BOUNDS = compiledMemory.method("memoryAccessOutOfBounds", MEMORY_INSTANCE, "I", "J", "I", returns = NOTHING)

@InternalChasmApi
val MEMORY_COPY = compiledMemory.method("memoryCopy", MEMORY_INSTANCE, MEMORY_INSTANCE, "I", "I", "I", returns = "V")

@InternalChasmApi
val MEMORY_FILL = compiledMemory.method("memoryFill", MEMORY_INSTANCE, "I", "I", "I", returns = "V")

@InternalChasmApi
val UNSAFE_INSTANCE = compiledMemory.method("unsafe", returns = OBJECT_TYPE)

@InternalChasmApi
val BYTE_ARRAY_BASE = compiledMemory.method("byteArrayBase", returns = "J")

@InternalChasmApi
val REQUIRE_BYTE_ARRAY = compiledMemory.method("requireByteArray", MEMORY_INSTANCE, returns = "V")

@InternalChasmApi
val MEMORY_SIZE = memoryInstance.method("getSize", returns = "I")

@InternalChasmApi
val UNSAFE_GET_BYTE = unsafe.method("getByte", OBJECT_TYPE, "J", returns = "B")

@InternalChasmApi
val UNSAFE_PUT_BYTE = unsafe.method("putByte", OBJECT_TYPE, "J", "B", returns = "V")

@InternalChasmApi
val UNSAFE_GET_INT = unsafe.method("getInt", OBJECT_TYPE, "J", returns = "I")

@InternalChasmApi
val UNSAFE_GET_LONG = unsafe.method("getLong", OBJECT_TYPE, "J", returns = "J")

@InternalChasmApi
val UNSAFE_PUT_INT = unsafe.method("putInt", OBJECT_TYPE, "J", "I", returns = "V")

@InternalChasmApi
val UNSAFE_PUT_LONG = unsafe.method("putLong", OBJECT_TYPE, "J", "J", returns = "V")

@InternalChasmApi
val BYTE_BUFFER_GET_BYTE = byteBuffer.method("get", "I", returns = "B")

@InternalChasmApi
val BYTE_BUFFER_GET_SHORT = byteBuffer.method("getShort", "I", returns = "S")

@InternalChasmApi
val BYTE_BUFFER_GET_INT = byteBuffer.method("getInt", "I", returns = "I")

@InternalChasmApi
val BYTE_BUFFER_GET_LONG = byteBuffer.method("getLong", "I", returns = "J")

@InternalChasmApi
val BYTE_BUFFER_PUT_BYTE = byteBuffer.method("put", "I", "B", returns = BYTE_BUFFER_TYPE)

@InternalChasmApi
val BYTE_BUFFER_PUT_SHORT = byteBuffer.method("putShort", "I", "S", returns = BYTE_BUFFER_TYPE)

@InternalChasmApi
val BYTE_BUFFER_PUT_INT = byteBuffer.method("putInt", "I", "I", returns = BYTE_BUFFER_TYPE)

@InternalChasmApi
val BYTE_BUFFER_PUT_LONG = byteBuffer.method("putLong", "I", "J", returns = BYTE_BUFFER_TYPE)

@InternalChasmApi
val BUFFER_I32_LOAD_8_S = compiledBufferMemory.method("i32Load8S", BYTE_BUFFER_TYPE, "I", "I", "I", returns = "I")

@InternalChasmApi
val BUFFER_I32_LOAD_16_S = compiledBufferMemory.method("i32Load16S", BYTE_BUFFER_TYPE, "I", "I", "I", returns = "I")

@InternalChasmApi
val BUFFER_I32_LOAD = compiledBufferMemory.method("i32Load", BYTE_BUFFER_TYPE, "I", "I", "I", returns = "I")

@InternalChasmApi
val BUFFER_I64_LOAD = compiledBufferMemory.method("i64Load", BYTE_BUFFER_TYPE, "I", "I", "I", returns = "J")

@InternalChasmApi
val BUFFER_I32_STORE_8 = compiledBufferMemory.method("i32Store8", BYTE_BUFFER_TYPE, "I", "I", "I", "I", returns = "V")

@InternalChasmApi
val BUFFER_I32_STORE_16 = compiledBufferMemory.method("i32Store16", BYTE_BUFFER_TYPE, "I", "I", "I", "I", returns = "V")

@InternalChasmApi
val BUFFER_I32_STORE = compiledBufferMemory.method("i32Store", BYTE_BUFFER_TYPE, "I", "I", "I", "I", returns = "V")

@InternalChasmApi
val BUFFER_I64_STORE = compiledBufferMemory.method("i64Store", BYTE_BUFFER_TYPE, "I", "I", "I", "J", returns = "V")

@InternalChasmApi
fun memoryAccess(
    name: String,
    value: String?,
    result: String?,
) = MethodRef(
    compiledMemory.descriptor,
    name,
    if (value == null) listOf(MEMORY_INSTANCE, "I", "I") else listOf(MEMORY_INSTANCE, "I", "I", value),
    result ?: "V",
)

@InternalChasmApi
val OPEN_FRAME = compiledCalls.method("openFrame", CONTEXT_TYPE, "I", returns = "J")

@InternalChasmApi
val FRAME = compiledCalls.method("frame", CONTEXT_TYPE, returns = "[J")

@InternalChasmApi
val CLOSE_FRAME = compiledCalls.method("closeFrame", CONTEXT_TYPE, "J", returns = "V")

@InternalChasmApi
val SAVE_STACK = compiledCalls.method("saveStack", CONTEXT_TYPE, returns = "J")

@InternalChasmApi
val RAISE_UNWINDING = compiledCalls.method("raiseUnwinding", CONTEXT_TYPE, returns = "V")

@InternalChasmApi
val WASM_EXCEPTION = compiledExceptions.method("wasmException", THROWABLE, returns = THROWABLE)

internal val MEMORY_SIZE_PAGES = compiledOps.method("memorySize", MEMORY_INSTANCE, returns = "J")
internal val MEMORY_GROW = compiledOps.method("memoryGrow", CONTEXT_TYPE, MEMORY_INSTANCE, "I", returns = "J")
internal val MEMORY_INIT = compiledOps.method("memoryInit", MEMORY_INSTANCE, DATA_INSTANCE, "I", "I", "I", returns = "V")
internal val DATA_DROP = compiledOps.method("dataDrop", DATA_INSTANCE, returns = "V")

@InternalChasmApi
val I64_MUL_WIDE_HIGH_S = compiledNumerics.method("i64MulWideHighS", "J", "J", returns = "J")

@InternalChasmApi
val I64_MUL_WIDE_HIGH_U = compiledNumerics.method("i64MulWideHighU", "J", "J", returns = "J")

@InternalChasmApi
val F32_TRUNC = compiledNumerics.method("f32Trunc", "F", returns = "F")

@InternalChasmApi
val F64_TRUNC = compiledNumerics.method("f64Trunc", "D", returns = "D")

@InternalChasmApi
val I32_TRUNC_F32_S = compiledNumerics.method("i32TruncF32S", "F", returns = "I")

@InternalChasmApi
val I32_TRUNC_F32_U = compiledNumerics.method("i32TruncF32U", "F", returns = "I")

@InternalChasmApi
val I32_TRUNC_F64_S = compiledNumerics.method("i32TruncF64S", "D", returns = "I")

@InternalChasmApi
val I32_TRUNC_F64_U = compiledNumerics.method("i32TruncF64U", "D", returns = "I")

@InternalChasmApi
val I64_TRUNC_F32_S = compiledNumerics.method("i64TruncF32S", "F", returns = "J")

@InternalChasmApi
val I64_TRUNC_F32_U = compiledNumerics.method("i64TruncF32U", "F", returns = "J")

@InternalChasmApi
val I64_TRUNC_F64_S = compiledNumerics.method("i64TruncF64S", "D", returns = "J")

@InternalChasmApi
val I64_TRUNC_F64_U = compiledNumerics.method("i64TruncF64U", "D", returns = "J")

@InternalChasmApi
val I32_TRUNC_SAT_F32_U = compiledNumerics.method("i32TruncSatF32U", "F", returns = "I")

@InternalChasmApi
val I32_TRUNC_SAT_F64_U = compiledNumerics.method("i32TruncSatF64U", "D", returns = "I")

@InternalChasmApi
val I64_TRUNC_SAT_F32_U = compiledNumerics.method("i64TruncSatF32U", "F", returns = "J")

@InternalChasmApi
val I64_TRUNC_SAT_F64_U = compiledNumerics.method("i64TruncSatF64U", "D", returns = "J")

@InternalChasmApi
val F32_CONVERT_I64_U = compiledNumerics.method("f32ConvertI64U", "J", returns = "F")

@InternalChasmApi
val F64_CONVERT_I64_U = compiledNumerics.method("f64ConvertI64U", "J", returns = "D")

@InternalChasmApi
val GLOBAL_GET = globalInstance.method("getValue", returns = "J")

@InternalChasmApi
val GLOBAL_SET = globalInstance.method("setValue", "J", returns = "V")

@InternalChasmApi
val INT_ROTATE_LEFT = javaInteger.method("rotateLeft", "I", "I", returns = "I")

@InternalChasmApi
val INT_ROTATE_RIGHT = javaInteger.method("rotateRight", "I", "I", returns = "I")

@InternalChasmApi
val INT_LEADING_ZEROS = javaInteger.method("numberOfLeadingZeros", "I", returns = "I")

@InternalChasmApi
val INT_TRAILING_ZEROS = javaInteger.method("numberOfTrailingZeros", "I", returns = "I")

@InternalChasmApi
val INT_BIT_COUNT = javaInteger.method("bitCount", "I", returns = "I")

@InternalChasmApi
val INT_DIVIDE_UNSIGNED = javaInteger.method("divideUnsigned", "I", "I", returns = "I")

@InternalChasmApi
val LONG_ROTATE_LEFT = javaLong.method("rotateLeft", "J", "I", returns = "J")

@InternalChasmApi
val LONG_ROTATE_RIGHT = javaLong.method("rotateRight", "J", "I", returns = "J")

@InternalChasmApi
val LONG_LEADING_ZEROS = javaLong.method("numberOfLeadingZeros", "J", returns = "I")

@InternalChasmApi
val LONG_TRAILING_ZEROS = javaLong.method("numberOfTrailingZeros", "J", returns = "I")

@InternalChasmApi
val LONG_BIT_COUNT = javaLong.method("bitCount", "J", returns = "I")

@InternalChasmApi
val LONG_DIVIDE_UNSIGNED = javaLong.method("divideUnsigned", "J", "J", returns = "J")

@InternalChasmApi
val LONG_REMAINDER_UNSIGNED = javaLong.method("remainderUnsigned", "J", "J", returns = "J")

@InternalChasmApi
val INT_BITS_TO_FLOAT = javaFloat.method("intBitsToFloat", "I", returns = "F")

@InternalChasmApi
val FLOAT_TO_RAW_INT_BITS = javaFloat.method("floatToRawIntBits", "F", returns = "I")

@InternalChasmApi
val LONG_BITS_TO_DOUBLE = javaDouble.method("longBitsToDouble", "J", returns = "D")

@InternalChasmApi
val DOUBLE_TO_RAW_LONG_BITS = javaDouble.method("doubleToRawLongBits", "D", returns = "J")

@InternalChasmApi
val MATH_MIN_FLOAT = javaMath.method("min", "F", "F", returns = "F")

@InternalChasmApi
val MATH_MAX_FLOAT = javaMath.method("max", "F", "F", returns = "F")

@InternalChasmApi
val MATH_MIN_DOUBLE = javaMath.method("min", "D", "D", returns = "D")

@InternalChasmApi
val MATH_MAX_DOUBLE = javaMath.method("max", "D", "D", returns = "D")

@InternalChasmApi
val MATH_CEIL = javaMath.method("ceil", "D", returns = "D")

@InternalChasmApi
val MATH_FLOOR = javaMath.method("floor", "D", returns = "D")

@InternalChasmApi
val MATH_RINT = javaMath.method("rint", "D", returns = "D")

@InternalChasmApi
val MATH_SQRT = javaMath.method("sqrt", "D", returns = "D")

@InternalChasmApi
val ARRAYCOPY = javaSystem.method("arraycopy", OBJECT_TYPE, "I", OBJECT_TYPE, "I", "I", returns = "V")

internal const val EQ_REFERENCE = "Lio/github/charlietap/chasm/reference/EqReference;"

@InternalChasmApi
const val I31_REFERENCE = "Lio/github/charlietap/chasm/reference/I31Reference;"
internal const val MANAGED_STRUCT = "Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedStruct;"

@InternalChasmApi
const val MANAGED_ARRAY = "Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedArray;"

@InternalChasmApi
const val MANAGED_FUNCTION = "Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFunction;"

private val managedOps = Owner("Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;")

@InternalChasmApi
val CAST_FAILED = managedOps.method("castFailed", returns = NOTHING)

@InternalChasmApi
val ARRAY_OUT_OF_BOUNDS = managedOps.method("arrayOutOfBounds", returns = NOTHING)

@InternalChasmApi
val GUEST_HEAP_EXHAUSTED = managedOps.method("guestHeapExhausted", returns = NOTHING)

@InternalChasmApi
val REFILL_ALLOCATION = managedOps.method("refillAllocation", CONTEXT_TYPE, "I", returns = "V")

@InternalChasmApi
val CHECK_ARRAY_LENGTH = managedOps.method("checkArrayLength", "I", "I", returns = "I")

@InternalChasmApi
const val ALLOCATION_METER = "Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;"

@InternalChasmApi
const val ALLOCATION_FIELD = "allocation"

@InternalChasmApi
const val ALLOCATION_REMAINING_FIELD = "remaining"

@InternalChasmApi
val SAME_I31 = managedOps.method("sameI31", OBJECT_TYPE, OBJECT_TYPE, returns = "Z")

@InternalChasmApi
val FUNCTION_MATCHES = managedOps.method("functionMatches", CONTEXT_TYPE, OBJECT_TYPE, "I", returns = "Z")

@InternalChasmApi
val REFERENCE_TARGET = managedOps.method("referenceTarget", OBJECT_TYPE, returns = OBJECT_TYPE)

private val managedArrays = Owner("Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedArrays;")

@InternalChasmApi
val ARRAY_LENGTH = managedArrays.method("arrayLength", MANAGED_ARRAY, returns = "I")

@InternalChasmApi
val COPY_ARRAY = managedArrays.method("copyArray", OBJECT_TYPE, "I", OBJECT_TYPE, "I", "I", returns = "V")

@InternalChasmApi
val FILL_INTS = managedArrays.method("fillInts", OBJECT_TYPE, "I", "I", "I", returns = "V")

@InternalChasmApi
val FILL_LONGS = managedArrays.method("fillLongs", OBJECT_TYPE, "I", "J", "I", returns = "V")

@InternalChasmApi
val FILL_FLOATS = managedArrays.method("fillFloats", OBJECT_TYPE, "I", "F", "I", returns = "V")

@InternalChasmApi
val FILL_DOUBLES = managedArrays.method("fillDoubles", OBJECT_TYPE, "I", "D", "I", returns = "V")

@InternalChasmApi
val FILL_BYTES = managedArrays.method("fillBytes", OBJECT_TYPE, "I", "I", "I", returns = "V")

@InternalChasmApi
val FILL_CHARS = managedArrays.method("fillChars", OBJECT_TYPE, "I", "I", "I", returns = "V")

@InternalChasmApi
val FILL_OBJECTS = managedArrays.method("fillObjects", OBJECT_TYPE, "I", OBJECT_TYPE, "I", returns = "V")

@InternalChasmApi
const val REFERENCE_GLOBAL = "Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceGlobal;"

@InternalChasmApi
const val REFERENCE_TABLE = "Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;"

@InternalChasmApi
const val REFERENCE_SEGMENT = "Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceSegment;"

@InternalChasmApi
const val REFERENCE_GLOBAL_VALUE = "value"

@InternalChasmApi
const val REFERENCE_STORAGE_ELEMENTS = "elements"

@InternalChasmApi
const val COMPILED_STRUCT = "Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;"
internal const val COMPILED_ARRAY = "Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledArray;"

@InternalChasmApi
const val RTT_TYPE = "Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;"

@InternalChasmApi
const val RTT_ARRAY_TYPE = "[Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;"

@InternalChasmApi
const val COMPILED_RTT_FIELD = "rtt"

@InternalChasmApi
const val RTT_SUPERTYPES_FIELD = "supertypes"

@InternalChasmApi
const val RTT_DEPTH_FIELD = "depth"

@InternalChasmApi
const val STORAGE_ARRAY_DATA_FIELD = "data"

@InternalChasmApi
const val SPILLED_INTS_FIELD = "spilledInts"

@InternalChasmApi
const val SPILLED_REFERENCES_FIELD = "spilledReferences"

@InternalChasmApi
const val I31_VALUE_FIELD = "value"

@InternalChasmApi
const val WASM_EXCEPTION_TYPE = "Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;"

@InternalChasmApi
const val WASM_EXCEPTION_WORDS = "words"

@InternalChasmApi
const val WASM_EXCEPTION_REFERENCES = "references"

@InternalChasmApi
val THROW_EXCEPTION = managedOps.method("throwException", CONTEXT_TYPE, "I", "[J", "[Ljava/lang/Object;", returns = "V")

@InternalChasmApi
val CREATE_EXCEPTION = managedOps.method("createException", CONTEXT_TYPE, "I", "[J", "[Ljava/lang/Object;", returns = WASM_EXCEPTION_TYPE)

@InternalChasmApi
val RETHROW_EXCEPTION = managedOps.method("rethrowException", CONTEXT_TYPE, OBJECT_TYPE, returns = "V")

@InternalChasmApi
val PENDING_EXCEPTION_TAG = managedOps.method("pendingExceptionTag", CONTEXT_TYPE, returns = "I")

@InternalChasmApi
val TAKE_MANAGED_EXCEPTION = managedOps.method("takeException", CONTEXT_TYPE, returns = WASM_EXCEPTION_TYPE)

@InternalChasmApi
val CALL_OBJECTS = managedOps.method("callObjects", CONTEXT_TYPE, MODULE_INSTANCE, FUNCTION_INSTANCE, "[J", "[Ljava/lang/Object;", "I", returns = "V")

@InternalChasmApi
val CALL_INDIRECT_OBJECTS = managedOps.method("callIndirectObjects", CONTEXT_TYPE, MODULE_INSTANCE, REFERENCE_TABLE, "I", "I", "[J", "[Ljava/lang/Object;", "I", returns = "V")

@InternalChasmApi
val RESULT_REFERENCES = managedOps.method("resultReferences", CONTEXT_TYPE, "I", returns = "[Ljava/lang/Object;")

@InternalChasmApi
val CALL_REFERENCE_OBJECTS = managedOps.method("callReferenceObjects", CONTEXT_TYPE, MODULE_INSTANCE, OBJECT_TYPE, "[J", "[Ljava/lang/Object;", "I", returns = "V")

@InternalChasmApi
val CHECK_DATA_RANGE = managedArrays.method("checkDataRange", DATA_INSTANCE, "I", "I", "I", returns = "V")

@InternalChasmApi
val CHECK_ELEMENT_RANGE = managedArrays.method("checkElementRange", REFERENCE_SEGMENT, "I", "I", returns = "V")

@InternalChasmApi
val COPY_DATA = managedArrays.method("copyData", DATA_INSTANCE, "I", OBJECT_TYPE, "I", "I", returns = "V")

@InternalChasmApi
val COPY_ELEMENTS = managedArrays.method("copyElements", REFERENCE_SEGMENT, "I", "[Ljava/lang/Object;", "I", "I", returns = "V")

@InternalChasmApi
val INIT_ARRAY_DATA = managedArrays.method("initArrayData", OBJECT_TYPE, "I", DATA_INSTANCE, "I", "I", "I", returns = "V")

@InternalChasmApi
val INIT_ARRAY_ELEMENTS = managedArrays.method("initArrayElements", OBJECT_TYPE, "I", REFERENCE_SEGMENT, "I", "I", returns = "V")

@InternalChasmApi
val REQUEST_TAIL_OBJECTS = managedOps.method("requestTailObjects", CONTEXT_TYPE, MODULE_INSTANCE, FUNCTION_INSTANCE, "[J", "[Ljava/lang/Object;", returns = "V")

@InternalChasmApi
val REQUEST_TAIL_INDIRECT_OBJECTS = managedOps.method("requestTailIndirectObjects", CONTEXT_TYPE, MODULE_INSTANCE, REFERENCE_TABLE, "I", "I", "[J", "[Ljava/lang/Object;", returns = "V")

@InternalChasmApi
val REQUEST_TAIL_REFERENCE_OBJECTS = managedOps.method("requestTailReferenceObjects", CONTEXT_TYPE, MODULE_INSTANCE, OBJECT_TYPE, "[J", "[Ljava/lang/Object;", returns = "V")

@InternalChasmApi
val COMPLETE_TAIL_OBJECTS = managedOps.method("completeTailObjects", CONTEXT_TYPE, "[J", "[Ljava/lang/Object;", "I", returns = "V")

private val managedTables = Owner("Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedTables;")

@InternalChasmApi
val MANAGED_TABLE_GET = managedTables.method("tableGet", REFERENCE_TABLE, "I", returns = OBJECT_TYPE)

@InternalChasmApi
val MANAGED_TABLE_SET = managedTables.method("tableSet", REFERENCE_TABLE, "I", OBJECT_TYPE, returns = "V")

@InternalChasmApi
val MANAGED_TABLE_SIZE = managedTables.method("tableSize", REFERENCE_TABLE, returns = "I")

@InternalChasmApi
val MANAGED_TABLE_GROW = managedTables.method("tableGrow", REFERENCE_TABLE, "I", OBJECT_TYPE, returns = "I")

@InternalChasmApi
val MANAGED_TABLE_FILL = managedTables.method("tableFill", REFERENCE_TABLE, "I", OBJECT_TYPE, "I", returns = "V")

@InternalChasmApi
val MANAGED_TABLE_COPY = managedTables.method("tableCopy", REFERENCE_TABLE, REFERENCE_TABLE, "I", "I", "I", returns = "V")

@InternalChasmApi
val MANAGED_TABLE_INIT = managedTables.method("tableInit", REFERENCE_TABLE, REFERENCE_SEGMENT, "I", "I", "I", returns = "V")

@InternalChasmApi
val MANAGED_ELEMENT_DROP = managedTables.method("elementDrop", REFERENCE_SEGMENT, returns = "V")

@InternalChasmApi
val MANAGED_INDIRECT_ENTRY = managedTables.method("indirectEntry", CONTEXT_TYPE, REFERENCE_TABLE, "I", "I", returns = OBJECT_TYPE)

@InternalChasmApi
const val COMPILED_MODULE = "Lio/github/charlietap/chasm/compiled/CompiledModule;"
