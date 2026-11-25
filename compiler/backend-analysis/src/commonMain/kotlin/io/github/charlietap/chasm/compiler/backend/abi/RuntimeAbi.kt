package io.github.charlietap.chasm.compiler.backend.abi

const val CONTEXT_TYPE = "Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;"
const val MODULE_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;"
const val FUNCTION_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;"
const val TABLE_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/TableInstance;"
const val MEMORY_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;"
const val GLOBAL_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/GlobalInstance;"
const val DATA_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/DataInstance;"
const val ELEMENT_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/ElementInstance;"

const val STACK_LIMIT_FIELD = "compiledStackLimit"

const val CALL_DEPTH_FIELD = "compiledCallDepth"

const val UNWINDING_FIELD = "compiledUnwinding"

const val PENDING_TAIL_FIELD = "pendingTailFunction"

const val MAX_UNCHARGED_LEAF_FRAME_COST = 64

private const val THROWABLE = "Ljava/lang/Throwable;"
private const val NOTHING = "Ljava/lang/Void;"
private const val STRINGS = "[Ljava/lang/String;"

const val UNSAFE_TYPE = "Lsun/misc/Unsafe;"

const val MEMORY_BYTE_ARRAY_FIELD = "byteArray"
const val MEMORY_BYTE_BUFFER_FIELD = "byteBuffer"
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

val FUEL_CHECK = compiledRuntime.method("fuelCheck", CONTEXT_TYPE, returns = "V")
val INTERRUPT_CHECK = compiledRuntime.method("interruptCheck", CONTEXT_TYPE, returns = "V")
val FUEL_AND_INTERRUPT_CHECK = compiledRuntime.method("fuelAndInterruptCheck", CONTEXT_TYPE, returns = "V")
val CALL_STACK_EXHAUSTED = compiledRuntime.method("callStackExhausted", returns = NOTHING)
val UNREACHABLE = compiledRuntime.method("unreachable", returns = NOTHING)
val UNPACK_LONGS = compiledRuntime.method("unpackLongs", STRINGS, returns = "[J")
val DIVIDE_BY_ZERO = compiledRuntime.method("divideByZero", returns = NOTHING)
val NULL_REFERENCE = compiledRuntime.method("nullReference", returns = NOTHING)
val INTEGER_OVERFLOW = compiledRuntime.method("integerOverflow", returns = NOTHING)
val MEMORY_OUT_OF_BOUNDS = compiledRuntime.method("memoryOutOfBounds", returns = NOTHING)

val MEMORY_ACCESS_OUT_OF_BOUNDS = compiledMemory.method("memoryAccessOutOfBounds", MEMORY_INSTANCE, "I", "J", "I", returns = NOTHING)
val MEMORY_COPY = compiledMemory.method("memoryCopy", MEMORY_INSTANCE, MEMORY_INSTANCE, "I", "I", "I", returns = "V")
val MEMORY_FILL = compiledMemory.method("memoryFill", MEMORY_INSTANCE, "I", "I", "I", returns = "V")
val UNSAFE_INSTANCE = compiledMemory.method("unsafe", returns = OBJECT_TYPE)
val BYTE_ARRAY_BASE = compiledMemory.method("byteArrayBase", returns = "J")
val REQUIRE_BYTE_ARRAY = compiledMemory.method("requireByteArray", MEMORY_INSTANCE, returns = "V")
val MEMORY_SIZE = memoryInstance.method("getSize", returns = "I")
val UNSAFE_GET_BYTE = unsafe.method("getByte", OBJECT_TYPE, "J", returns = "B")
val UNSAFE_PUT_BYTE = unsafe.method("putByte", OBJECT_TYPE, "J", "B", returns = "V")
val UNSAFE_GET_INT = unsafe.method("getInt", OBJECT_TYPE, "J", returns = "I")
val UNSAFE_GET_LONG = unsafe.method("getLong", OBJECT_TYPE, "J", returns = "J")
val UNSAFE_PUT_INT = unsafe.method("putInt", OBJECT_TYPE, "J", "I", returns = "V")
val UNSAFE_PUT_LONG = unsafe.method("putLong", OBJECT_TYPE, "J", "J", returns = "V")

val BYTE_BUFFER_GET_BYTE = byteBuffer.method("get", "I", returns = "B")
val BYTE_BUFFER_GET_SHORT = byteBuffer.method("getShort", "I", returns = "S")
val BYTE_BUFFER_GET_INT = byteBuffer.method("getInt", "I", returns = "I")
val BYTE_BUFFER_GET_LONG = byteBuffer.method("getLong", "I", returns = "J")
val BYTE_BUFFER_PUT_BYTE = byteBuffer.method("put", "I", "B", returns = BYTE_BUFFER_TYPE)
val BYTE_BUFFER_PUT_SHORT = byteBuffer.method("putShort", "I", "S", returns = BYTE_BUFFER_TYPE)
val BYTE_BUFFER_PUT_INT = byteBuffer.method("putInt", "I", "I", returns = BYTE_BUFFER_TYPE)
val BYTE_BUFFER_PUT_LONG = byteBuffer.method("putLong", "I", "J", returns = BYTE_BUFFER_TYPE)

val BUFFER_I32_LOAD_8_S = compiledBufferMemory.method("i32Load8S", BYTE_BUFFER_TYPE, "I", "I", "I", returns = "I")
val BUFFER_I32_LOAD_16_S = compiledBufferMemory.method("i32Load16S", BYTE_BUFFER_TYPE, "I", "I", "I", returns = "I")
val BUFFER_I32_LOAD = compiledBufferMemory.method("i32Load", BYTE_BUFFER_TYPE, "I", "I", "I", returns = "I")
val BUFFER_I64_LOAD = compiledBufferMemory.method("i64Load", BYTE_BUFFER_TYPE, "I", "I", "I", returns = "J")
val BUFFER_I32_STORE_8 = compiledBufferMemory.method("i32Store8", BYTE_BUFFER_TYPE, "I", "I", "I", "I", returns = "V")
val BUFFER_I32_STORE_16 = compiledBufferMemory.method("i32Store16", BYTE_BUFFER_TYPE, "I", "I", "I", "I", returns = "V")
val BUFFER_I32_STORE = compiledBufferMemory.method("i32Store", BYTE_BUFFER_TYPE, "I", "I", "I", "I", returns = "V")
val BUFFER_I64_STORE = compiledBufferMemory.method("i64Store", BYTE_BUFFER_TYPE, "I", "I", "I", "J", returns = "V")

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

val OPEN_FRAME = compiledCalls.method("openFrame", CONTEXT_TYPE, "I", returns = "J")
val FRAME = compiledCalls.method("frame", CONTEXT_TYPE, returns = "[J")
val CLOSE_FRAME = compiledCalls.method("closeFrame", CONTEXT_TYPE, "J", returns = "V")
val SAVE_STACK = compiledCalls.method("saveStack", CONTEXT_TYPE, returns = "J")
val RAISE_UNWINDING = compiledCalls.method("raiseUnwinding", CONTEXT_TYPE, returns = "V")

val WASM_EXCEPTION = compiledExceptions.method("wasmException", THROWABLE, returns = THROWABLE)

val MEMORY_SIZE_PAGES = compiledOps.method("memorySize", MEMORY_INSTANCE, returns = "J")
val MEMORY_GROW = compiledOps.method("memoryGrow", CONTEXT_TYPE, MEMORY_INSTANCE, "I", returns = "J")
val MEMORY_INIT = compiledOps.method("memoryInit", MEMORY_INSTANCE, DATA_INSTANCE, "I", "I", "I", returns = "V")
val DATA_DROP = compiledOps.method("dataDrop", DATA_INSTANCE, returns = "V")

val I64_MUL_WIDE_HIGH_S = compiledNumerics.method("i64MulWideHighS", "J", "J", returns = "J")
val I64_MUL_WIDE_HIGH_U = compiledNumerics.method("i64MulWideHighU", "J", "J", returns = "J")
val F32_TRUNC = compiledNumerics.method("f32Trunc", "F", returns = "F")
val F64_TRUNC = compiledNumerics.method("f64Trunc", "D", returns = "D")
val I32_TRUNC_F32_S = compiledNumerics.method("i32TruncF32S", "F", returns = "I")
val I32_TRUNC_F32_U = compiledNumerics.method("i32TruncF32U", "F", returns = "I")
val I32_TRUNC_F64_S = compiledNumerics.method("i32TruncF64S", "D", returns = "I")
val I32_TRUNC_F64_U = compiledNumerics.method("i32TruncF64U", "D", returns = "I")
val I64_TRUNC_F32_S = compiledNumerics.method("i64TruncF32S", "F", returns = "J")
val I64_TRUNC_F32_U = compiledNumerics.method("i64TruncF32U", "F", returns = "J")
val I64_TRUNC_F64_S = compiledNumerics.method("i64TruncF64S", "D", returns = "J")
val I64_TRUNC_F64_U = compiledNumerics.method("i64TruncF64U", "D", returns = "J")
val I32_TRUNC_SAT_F32_U = compiledNumerics.method("i32TruncSatF32U", "F", returns = "I")
val I32_TRUNC_SAT_F64_U = compiledNumerics.method("i32TruncSatF64U", "D", returns = "I")
val I64_TRUNC_SAT_F32_U = compiledNumerics.method("i64TruncSatF32U", "F", returns = "J")
val I64_TRUNC_SAT_F64_U = compiledNumerics.method("i64TruncSatF64U", "D", returns = "J")
val F32_CONVERT_I64_U = compiledNumerics.method("f32ConvertI64U", "J", returns = "F")
val F64_CONVERT_I64_U = compiledNumerics.method("f64ConvertI64U", "J", returns = "D")

val GLOBAL_GET = globalInstance.method("getValue", returns = "J")
val GLOBAL_SET = globalInstance.method("setValue", "J", returns = "V")

val INT_ROTATE_LEFT = javaInteger.method("rotateLeft", "I", "I", returns = "I")
val INT_ROTATE_RIGHT = javaInteger.method("rotateRight", "I", "I", returns = "I")
val INT_LEADING_ZEROS = javaInteger.method("numberOfLeadingZeros", "I", returns = "I")
val INT_TRAILING_ZEROS = javaInteger.method("numberOfTrailingZeros", "I", returns = "I")
val INT_BIT_COUNT = javaInteger.method("bitCount", "I", returns = "I")
val INT_DIVIDE_UNSIGNED = javaInteger.method("divideUnsigned", "I", "I", returns = "I")
val LONG_ROTATE_LEFT = javaLong.method("rotateLeft", "J", "I", returns = "J")
val LONG_ROTATE_RIGHT = javaLong.method("rotateRight", "J", "I", returns = "J")
val LONG_LEADING_ZEROS = javaLong.method("numberOfLeadingZeros", "J", returns = "I")
val LONG_TRAILING_ZEROS = javaLong.method("numberOfTrailingZeros", "J", returns = "I")
val LONG_BIT_COUNT = javaLong.method("bitCount", "J", returns = "I")
val LONG_DIVIDE_UNSIGNED = javaLong.method("divideUnsigned", "J", "J", returns = "J")
val LONG_REMAINDER_UNSIGNED = javaLong.method("remainderUnsigned", "J", "J", returns = "J")
val INT_BITS_TO_FLOAT = javaFloat.method("intBitsToFloat", "I", returns = "F")
val FLOAT_TO_RAW_INT_BITS = javaFloat.method("floatToRawIntBits", "F", returns = "I")
val LONG_BITS_TO_DOUBLE = javaDouble.method("longBitsToDouble", "J", returns = "D")
val DOUBLE_TO_RAW_LONG_BITS = javaDouble.method("doubleToRawLongBits", "D", returns = "J")
val MATH_MIN_FLOAT = javaMath.method("min", "F", "F", returns = "F")
val MATH_MAX_FLOAT = javaMath.method("max", "F", "F", returns = "F")
val MATH_MIN_DOUBLE = javaMath.method("min", "D", "D", returns = "D")
val MATH_MAX_DOUBLE = javaMath.method("max", "D", "D", returns = "D")
val MATH_CEIL = javaMath.method("ceil", "D", returns = "D")
val MATH_FLOOR = javaMath.method("floor", "D", returns = "D")
val MATH_RINT = javaMath.method("rint", "D", returns = "D")
val MATH_SQRT = javaMath.method("sqrt", "D", returns = "D")
val ARRAYCOPY = javaSystem.method("arraycopy", OBJECT_TYPE, "I", OBJECT_TYPE, "I", "I", returns = "V")

const val EQ_REFERENCE = "Lio/github/charlietap/chasm/reference/EqReference;"
const val I31_REFERENCE = "Lio/github/charlietap/chasm/reference/I31Reference;"
const val MANAGED_STRUCT = "Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedStruct;"
const val MANAGED_ARRAY = "Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedArray;"
const val MANAGED_FUNCTION = "Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedFunction;"

private val managedOps = Owner("Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedOps;")

val CAST_FAILED = managedOps.method("castFailed", returns = NOTHING)
val ARRAY_OUT_OF_BOUNDS = managedOps.method("arrayOutOfBounds", returns = NOTHING)
val GUEST_HEAP_EXHAUSTED = managedOps.method("guestHeapExhausted", returns = NOTHING)
val REFILL_ALLOCATION = managedOps.method("refillAllocation", CONTEXT_TYPE, "I", returns = "V")
val CHECK_ARRAY_LENGTH = managedOps.method("checkArrayLength", "I", "I", returns = "I")
const val ALLOCATION_METER = "Lio/github/charlietap/chasm/runtime/execution/AllocationMeter;"
const val ALLOCATION_FIELD = "allocation"
const val ALLOCATION_REMAINING_FIELD = "remaining"
val SAME_I31 = managedOps.method("sameI31", OBJECT_TYPE, OBJECT_TYPE, returns = "Z")
val FUNCTION_MATCHES = managedOps.method("functionMatches", CONTEXT_TYPE, OBJECT_TYPE, "I", returns = "Z")
val REFERENCE_TARGET = managedOps.method("referenceTarget", OBJECT_TYPE, returns = OBJECT_TYPE)

private val managedArrays = Owner("Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedArrays;")
val ARRAY_LENGTH = managedArrays.method("arrayLength", MANAGED_ARRAY, returns = "I")

val COPY_ARRAY = managedArrays.method("copyArray", OBJECT_TYPE, "I", OBJECT_TYPE, "I", "I", returns = "V")
val FILL_INTS = managedArrays.method("fillInts", OBJECT_TYPE, "I", "I", "I", returns = "V")
val FILL_LONGS = managedArrays.method("fillLongs", OBJECT_TYPE, "I", "J", "I", returns = "V")
val FILL_FLOATS = managedArrays.method("fillFloats", OBJECT_TYPE, "I", "F", "I", returns = "V")
val FILL_DOUBLES = managedArrays.method("fillDoubles", OBJECT_TYPE, "I", "D", "I", returns = "V")
val FILL_BYTES = managedArrays.method("fillBytes", OBJECT_TYPE, "I", "I", "I", returns = "V")
val FILL_CHARS = managedArrays.method("fillChars", OBJECT_TYPE, "I", "I", "I", returns = "V")
val FILL_OBJECTS = managedArrays.method("fillObjects", OBJECT_TYPE, "I", OBJECT_TYPE, "I", returns = "V")
const val REFERENCE_GLOBAL = "Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceGlobal;"
const val REFERENCE_TABLE = "Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceTable;"
const val REFERENCE_SEGMENT = "Lio/github/charlietap/chasm/runtime/compiled/gc/ReferenceSegment;"
const val REFERENCE_GLOBAL_VALUE = "value"
const val REFERENCE_STORAGE_ELEMENTS = "elements"
const val COMPILED_STRUCT = "Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledStruct;"
const val COMPILED_ARRAY = "Lio/github/charlietap/chasm/runtime/compiled/gc/CompiledArray;"
const val RTT_TYPE = "Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;"
const val RTT_ARRAY_TYPE = "[Lio/github/charlietap/chasm/runtime/compiled/gc/Rtt;"
const val COMPILED_RTT_FIELD = "rtt"
const val RTT_SUPERTYPES_FIELD = "supertypes"
const val RTT_DEPTH_FIELD = "depth"
const val STORAGE_ARRAY_DATA_FIELD = "data"
const val SPILLED_INTS_FIELD = "spilledInts"
const val SPILLED_REFERENCES_FIELD = "spilledReferences"
const val I31_VALUE_FIELD = "value"
const val WASM_EXCEPTION_TYPE = "Lio/github/charlietap/chasm/runtime/compiled/gc/WasmException;"
const val WASM_EXCEPTION_WORDS = "words"
const val WASM_EXCEPTION_REFERENCES = "references"
val THROW_EXCEPTION = managedOps.method("throwException", CONTEXT_TYPE, "I", "[J", "[Ljava/lang/Object;", returns = "V")
val CREATE_EXCEPTION = managedOps.method("createException", CONTEXT_TYPE, "I", "[J", "[Ljava/lang/Object;", returns = WASM_EXCEPTION_TYPE)
val RETHROW_EXCEPTION = managedOps.method("rethrowException", CONTEXT_TYPE, OBJECT_TYPE, returns = "V")
val PENDING_EXCEPTION_TAG = managedOps.method("pendingExceptionTag", CONTEXT_TYPE, returns = "I")
val TAKE_MANAGED_EXCEPTION = managedOps.method("takeException", CONTEXT_TYPE, returns = WASM_EXCEPTION_TYPE)
val CALL_OBJECTS = managedOps.method("callObjects", CONTEXT_TYPE, MODULE_INSTANCE, FUNCTION_INSTANCE, "[J", "[Ljava/lang/Object;", "I", returns = "V")
val CALL_INDIRECT_OBJECTS = managedOps.method("callIndirectObjects", CONTEXT_TYPE, MODULE_INSTANCE, REFERENCE_TABLE, "I", "I", "[J", "[Ljava/lang/Object;", "I", returns = "V")
val RESULT_REFERENCES = managedOps.method("resultReferences", CONTEXT_TYPE, "I", returns = "[Ljava/lang/Object;")
val CALL_REFERENCE_OBJECTS = managedOps.method("callReferenceObjects", CONTEXT_TYPE, MODULE_INSTANCE, OBJECT_TYPE, "[J", "[Ljava/lang/Object;", "I", returns = "V")
val CHECK_DATA_RANGE = managedArrays.method("checkDataRange", DATA_INSTANCE, "I", "I", "I", returns = "V")
val CHECK_ELEMENT_RANGE = managedArrays.method("checkElementRange", REFERENCE_SEGMENT, "I", "I", returns = "V")
val COPY_DATA = managedArrays.method("copyData", DATA_INSTANCE, "I", OBJECT_TYPE, "I", "I", returns = "V")
val COPY_ELEMENTS = managedArrays.method("copyElements", REFERENCE_SEGMENT, "I", "[Ljava/lang/Object;", "I", "I", returns = "V")
val INIT_ARRAY_DATA = managedArrays.method("initArrayData", OBJECT_TYPE, "I", DATA_INSTANCE, "I", "I", "I", returns = "V")
val INIT_ARRAY_ELEMENTS = managedArrays.method("initArrayElements", OBJECT_TYPE, "I", REFERENCE_SEGMENT, "I", "I", returns = "V")
val REQUEST_TAIL_OBJECTS = managedOps.method("requestTailObjects", CONTEXT_TYPE, MODULE_INSTANCE, FUNCTION_INSTANCE, "[J", "[Ljava/lang/Object;", returns = "V")
val REQUEST_TAIL_INDIRECT_OBJECTS = managedOps.method("requestTailIndirectObjects", CONTEXT_TYPE, MODULE_INSTANCE, REFERENCE_TABLE, "I", "I", "[J", "[Ljava/lang/Object;", returns = "V")
val REQUEST_TAIL_REFERENCE_OBJECTS = managedOps.method("requestTailReferenceObjects", CONTEXT_TYPE, MODULE_INSTANCE, OBJECT_TYPE, "[J", "[Ljava/lang/Object;", returns = "V")
val COMPLETE_TAIL_OBJECTS = managedOps.method("completeTailObjects", CONTEXT_TYPE, "[J", "[Ljava/lang/Object;", "I", returns = "V")

private val managedTables = Owner("Lio/github/charlietap/chasm/runtime/compiled/gc/ManagedTables;")

val MANAGED_TABLE_GET = managedTables.method("tableGet", REFERENCE_TABLE, "I", returns = OBJECT_TYPE)
val MANAGED_TABLE_SET = managedTables.method("tableSet", REFERENCE_TABLE, "I", OBJECT_TYPE, returns = "V")
val MANAGED_TABLE_SIZE = managedTables.method("tableSize", REFERENCE_TABLE, returns = "I")
val MANAGED_TABLE_GROW = managedTables.method("tableGrow", REFERENCE_TABLE, "I", OBJECT_TYPE, returns = "I")
val MANAGED_TABLE_FILL = managedTables.method("tableFill", REFERENCE_TABLE, "I", OBJECT_TYPE, "I", returns = "V")
val MANAGED_TABLE_COPY = managedTables.method("tableCopy", REFERENCE_TABLE, REFERENCE_TABLE, "I", "I", "I", returns = "V")
val MANAGED_TABLE_INIT = managedTables.method("tableInit", REFERENCE_TABLE, REFERENCE_SEGMENT, "I", "I", "I", returns = "V")
val MANAGED_ELEMENT_DROP = managedTables.method("elementDrop", REFERENCE_SEGMENT, returns = "V")
val MANAGED_INDIRECT_ENTRY = managedTables.method("indirectEntry", CONTEXT_TYPE, REFERENCE_TABLE, "I", "I", returns = OBJECT_TYPE)

const val COMPILED_MODULE = "Lio/github/charlietap/chasm/compiled/CompiledModule;"
