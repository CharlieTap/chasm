package io.github.charlietap.chasm.compiler.backend.abi

const val CONTEXT_TYPE = "Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;"
const val MODULE_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;"
const val FUNCTION_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;"
const val TABLE_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/TableInstance;"
const val MEMORY_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;"
const val GLOBAL_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/GlobalInstance;"
const val DATA_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/DataInstance;"
const val ELEMENT_INSTANCE = "Lio/github/charlietap/chasm/runtime/instance/ElementInstance;"
const val EXCEPTION_TABLE = "Lio/github/charlietap/chasm/runtime/exception/FunctionExceptionTable;"

val EXCEPTION_CARRIERS = listOf(
    "Lio/github/charlietap/chasm/runtime/exception/InvocationException;",
    "Lio/github/charlietap/chasm/runtime/exception/HostRaisedWasmException;",
)

const val STACK_LIMIT_FIELD = "compiledStackLimit"

const val CALL_DEPTH_FIELD = "compiledCallDepth"

const val UNWINDING_FIELD = "compiledUnwinding"

const val PENDING_TAIL_FIELD = "pendingTailFunction"

const val REFERENCE_TYPE_TEST = OBJECT_TYPE

private const val THROWABLE = "Ljava/lang/Throwable;"
private const val NOTHING = "Ljava/lang/Void;"
private const val STRINGS = "[Ljava/lang/String;"

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
private val compiledGc = Owner("Lio/github/charlietap/chasm/runtime/compiled/CompiledGc;")
private val compiledOps = Owner("Lio/github/charlietap/chasm/runtime/compiled/CompiledOps;")
private val compiledNumerics = Owner("Lio/github/charlietap/chasm/runtime/compiled/CompiledNumerics;")
private val globalInstance = Owner(GLOBAL_INSTANCE)
private val javaInteger = Owner("Ljava/lang/Integer;")
private val javaLong = Owner("Ljava/lang/Long;")
private val javaFloat = Owner("Ljava/lang/Float;")
private val javaDouble = Owner("Ljava/lang/Double;")
private val javaSystem = Owner("Ljava/lang/System;")

val FUEL_CHECK = compiledRuntime.method("fuelCheck", CONTEXT_TYPE, returns = "V")
val INTERRUPT_CHECK = compiledRuntime.method("interruptCheck", CONTEXT_TYPE, returns = "V")
val FUEL_AND_INTERRUPT_CHECK = compiledRuntime.method("fuelAndInterruptCheck", CONTEXT_TYPE, returns = "V")
val CALL_STACK_EXHAUSTED = compiledRuntime.method("callStackExhausted", returns = NOTHING)
val UNREACHABLE = compiledRuntime.method("unreachable", returns = NOTHING)
val UNPACK_LONGS = compiledRuntime.method("unpackLongs", STRINGS, returns = "[J")

val OPEN_FRAME = compiledCalls.method("openFrame", CONTEXT_TYPE, "I", returns = "J")
val FRAME = compiledCalls.method("frame", CONTEXT_TYPE, returns = "[J")
val OPEN_ROOTS = compiledCalls.method("openRoots", CONTEXT_TYPE, "I", returns = "J")
val CLEAR_ROOTS = compiledCalls.method("clearRoots", CONTEXT_TYPE, "I", "I", returns = "V")
val CLOSE_FRAME = compiledCalls.method("closeFrame", CONTEXT_TYPE, "J", returns = "V")
val SAVE_STACK = compiledCalls.method("saveStack", CONTEXT_TYPE, returns = "J")
val BRIDGE_DEPTH = compiledCalls.method("depth", CONTEXT_TYPE, returns = "I")
val BRIDGE_CALL = compiledCalls.method("call", CONTEXT_TYPE, MODULE_INSTANCE, FUNCTION_INSTANCE, "[J", "I", returns = "V")
val BRIDGE_CALL_INDIRECT =
    compiledCalls.method("callIndirect", CONTEXT_TYPE, MODULE_INSTANCE, TABLE_INSTANCE, "I", "I", "[J", "I", returns = "V")
val BRIDGE_CALL_REF = compiledCalls.method("callRef", CONTEXT_TYPE, MODULE_INSTANCE, "J", "[J", "I", returns = "V")
val INDIRECT_TARGET = compiledCalls.method("indirectTarget", CONTEXT_TYPE, TABLE_INSTANCE, "I", "I", returns = OBJECT_TYPE)
val REF_TARGET = compiledCalls.method("refTarget", CONTEXT_TYPE, "J", returns = OBJECT_TYPE)
val TAIL_CALL = compiledCalls.method("tailCall", CONTEXT_TYPE, MODULE_INSTANCE, FUNCTION_INSTANCE, "[J", returns = "V")
val TAIL_CALL_INDIRECT =
    compiledCalls.method("tailCallIndirect", CONTEXT_TYPE, MODULE_INSTANCE, TABLE_INSTANCE, "I", "I", "[J", returns = "V")
val TAIL_CALL_REF = compiledCalls.method("tailCallRef", CONTEXT_TYPE, MODULE_INSTANCE, "J", "[J", returns = "V")
val COMPLETE_TAIL_CALL = compiledCalls.method("completeTailCall", CONTEXT_TYPE, "[J", "I", returns = "V")
val RAISE_UNWINDING = compiledCalls.method("raiseUnwinding", CONTEXT_TYPE, returns = "V")
val IS_NULL = compiledCalls.method("isNull", "J", returns = "Z")

val THROW_TAG = compiledExceptions.method("throwTag", CONTEXT_TYPE, "I", "I", returns = "V")
val THROW_REF = compiledExceptions.method("throwRef", CONTEXT_TYPE, "J", returns = "V")
val CATCH_IN = compiledExceptions.method("catchIn", CONTEXT_TYPE, THROWABLE, EXCEPTION_TABLE, "I", "[J", returns = "I")
val CATCH_PENDING = compiledExceptions.method("catchPending", CONTEXT_TYPE, EXCEPTION_TABLE, "I", "[J", returns = "I")
val PENDING_TAG = compiledExceptions.method("pendingTag", CONTEXT_TYPE, returns = "I")
val TAKE_EXCEPTION = compiledExceptions.method("takeException", CONTEXT_TYPE, "[J", "I", returns = "J")
val WASM_EXCEPTION = compiledExceptions.method("wasmException", THROWABLE, returns = THROWABLE)

val CAST_MATCHES = compiledGc.method("castMatches", CONTEXT_TYPE, "J", OBJECT_TYPE, returns = "Z")
val STRUCT_GET = compiledGc.method("structGet", CONTEXT_TYPE, "J", "I", returns = "J")
val STRUCT_SET = compiledGc.method("structSet", CONTEXT_TYPE, "J", "I", "J", returns = "V")
val STRUCT_GET_STRUCT_GET = compiledGc.method("structGetStructGet", CONTEXT_TYPE, "J", "I", "I", returns = "J")
val STRUCT_NEW = compiledGc.method("structNew", CONTEXT_TYPE, "I", "[J", returns = "J")
val STRUCT_NEW_DEFAULT = compiledGc.method("structNewDefault", CONTEXT_TYPE, "I", "[J", returns = "J")
val ARRAY_GET = compiledGc.method("arrayGet", CONTEXT_TYPE, "J", "I", returns = "J")
val ARRAY_SET = compiledGc.method("arraySet", CONTEXT_TYPE, "J", "I", "J", returns = "V")
val ARRAY_LEN = compiledGc.method("arrayLen", CONTEXT_TYPE, "J", returns = "J")
val ARRAY_COPY = compiledGc.method("arrayCopy", CONTEXT_TYPE, "J", "I", "J", "I", "I", returns = "V")
val ARRAY_NEW_DEFAULT = compiledGc.method("arrayNewDefault", CONTEXT_TYPE, "I", "I", "J", returns = "J")
val REF_CAST_STRUCT_GET = compiledGc.method("refCastStructGet", CONTEXT_TYPE, "J", REFERENCE_TYPE_TEST, "I", returns = "J")
val REF_CAST = compiledGc.method("refCast", CONTEXT_TYPE, "J", REFERENCE_TYPE_TEST, returns = "J")
val REF_TEST = compiledGc.method("refTest", CONTEXT_TYPE, "J", REFERENCE_TYPE_TEST, returns = "J")
val REF_EQ = compiledGc.method("refEq", "J", "J", returns = "J")
val REF_IS_NULL = compiledGc.method("refIsNull", "J", returns = "J")
val REF_AS_NON_NULL = compiledGc.method("refAsNonNull", "J", returns = "J")
val REF_I31 = compiledGc.method("refI31", "I", returns = "J")
val I31_GET_SIGNED = compiledGc.method("i31GetSigned", "J", returns = "J")
val I31_GET_UNSIGNED = compiledGc.method("i31GetUnsigned", "J", returns = "J")

val MEMORY_SIZE_PAGES = compiledOps.method("memorySize", MEMORY_INSTANCE, returns = "J")
val MEMORY_GROW = compiledOps.method("memoryGrow", CONTEXT_TYPE, MEMORY_INSTANCE, "I", returns = "J")
val MEMORY_INIT = compiledOps.method("memoryInit", MEMORY_INSTANCE, DATA_INSTANCE, "I", "I", "I", returns = "V")
val DATA_DROP = compiledOps.method("dataDrop", DATA_INSTANCE, returns = "V")
val TABLE_GET = compiledOps.method("tableGet", TABLE_INSTANCE, "I", returns = "J")
val TABLE_SET = compiledOps.method("tableSet", TABLE_INSTANCE, "I", "J", returns = "V")
val TABLE_SIZE = compiledOps.method("tableSize", TABLE_INSTANCE, returns = "J")
val TABLE_GROW = compiledOps.method("tableGrow", CONTEXT_TYPE, TABLE_INSTANCE, "I", "J", returns = "J")
val TABLE_FILL = compiledOps.method("tableFill", TABLE_INSTANCE, "I", "J", "I", returns = "V")
val TABLE_COPY = compiledOps.method("tableCopy", TABLE_INSTANCE, TABLE_INSTANCE, "I", "I", "I", returns = "V")
val TABLE_INIT = compiledOps.method("tableInit", TABLE_INSTANCE, ELEMENT_INSTANCE, "I", "I", "I", returns = "V")
val ELEM_DROP = compiledOps.method("elemDrop", ELEMENT_INSTANCE, returns = "V")
val ARRAY_NEW = compiledOps.method("arrayNew", CONTEXT_TYPE, "I", "I", "J", returns = "J")
val ARRAY_NEW_FIXED = compiledOps.method("arrayNewFixed", CONTEXT_TYPE, "I", "[J", "I", returns = "J")
val ARRAY_NEW_DATA = compiledOps.method("arrayNewData", CONTEXT_TYPE, "I", DATA_INSTANCE, "I", "I", "I", returns = "J")
val ARRAY_NEW_ELEMENT = compiledOps.method("arrayNewElement", CONTEXT_TYPE, "I", ELEMENT_INSTANCE, "I", "I", returns = "J")
val ARRAY_FILL = compiledOps.method("arrayFill", CONTEXT_TYPE, "J", "I", "J", "I", returns = "V")
val ARRAY_INIT_DATA = compiledOps.method("arrayInitData", CONTEXT_TYPE, "J", "I", DATA_INSTANCE, "I", "I", "I", returns = "V")
val ARRAY_INIT_ELEMENT = compiledOps.method("arrayInitElement", CONTEXT_TYPE, "J", "I", ELEMENT_INSTANCE, "I", "I", returns = "V")
val ANY_CONVERT_EXTERN = compiledOps.method("anyConvertExtern", "J", returns = "J")
val EXTERN_CONVERT_ANY = compiledOps.method("externConvertAny", "J", returns = "J")

val I32_DIV_S = compiledNumerics.method("i32DivS", "I", "I", returns = "I")
val I32_DIV_U = compiledNumerics.method("i32DivU", "I", "I", returns = "I")
val I32_REM_S = compiledNumerics.method("i32RemS", "I", "I", returns = "I")
val I32_REM_U = compiledNumerics.method("i32RemU", "I", "I", returns = "I")
val I64_DIV_S = compiledNumerics.method("i64DivS", "J", "J", returns = "J")
val I64_DIV_U = compiledNumerics.method("i64DivU", "J", "J", returns = "J")
val I64_REM_S = compiledNumerics.method("i64RemS", "J", "J", returns = "J")
val I64_REM_U = compiledNumerics.method("i64RemU", "J", "J", returns = "J")
val I64_MUL_WIDE_HIGH_S = compiledNumerics.method("i64MulWideHighS", "J", "J", returns = "J")
val I64_MUL_WIDE_HIGH_U = compiledNumerics.method("i64MulWideHighU", "J", "J", returns = "J")
val F32_MIN = compiledNumerics.method("f32Min", "F", "F", returns = "F")
val F32_MAX = compiledNumerics.method("f32Max", "F", "F", returns = "F")
val F32_COPYSIGN = compiledNumerics.method("f32Copysign", "F", "F", returns = "F")
val F32_ABS = compiledNumerics.method("f32Abs", "F", returns = "F")
val F32_CEIL = compiledNumerics.method("f32Ceil", "F", returns = "F")
val F32_FLOOR = compiledNumerics.method("f32Floor", "F", returns = "F")
val F32_TRUNC = compiledNumerics.method("f32Trunc", "F", returns = "F")
val F32_NEAREST = compiledNumerics.method("f32Nearest", "F", returns = "F")
val F32_SQRT = compiledNumerics.method("f32Sqrt", "F", returns = "F")
val F64_MIN = compiledNumerics.method("f64Min", "D", "D", returns = "D")
val F64_MAX = compiledNumerics.method("f64Max", "D", "D", returns = "D")
val F64_COPYSIGN = compiledNumerics.method("f64Copysign", "D", "D", returns = "D")
val F64_ABS = compiledNumerics.method("f64Abs", "D", returns = "D")
val F64_CEIL = compiledNumerics.method("f64Ceil", "D", returns = "D")
val F64_FLOOR = compiledNumerics.method("f64Floor", "D", returns = "D")
val F64_TRUNC = compiledNumerics.method("f64Trunc", "D", returns = "D")
val F64_NEAREST = compiledNumerics.method("f64Nearest", "D", returns = "D")
val F64_SQRT = compiledNumerics.method("f64Sqrt", "D", returns = "D")
val I32_TRUNC_F32_S = compiledNumerics.method("i32TruncF32S", "F", returns = "I")
val I32_TRUNC_F32_U = compiledNumerics.method("i32TruncF32U", "F", returns = "I")
val I32_TRUNC_F64_S = compiledNumerics.method("i32TruncF64S", "D", returns = "I")
val I32_TRUNC_F64_U = compiledNumerics.method("i32TruncF64U", "D", returns = "I")
val I64_TRUNC_F32_S = compiledNumerics.method("i64TruncF32S", "F", returns = "J")
val I64_TRUNC_F32_U = compiledNumerics.method("i64TruncF32U", "F", returns = "J")
val I64_TRUNC_F64_S = compiledNumerics.method("i64TruncF64S", "D", returns = "J")
val I64_TRUNC_F64_U = compiledNumerics.method("i64TruncF64U", "D", returns = "J")
val I32_TRUNC_SAT_F32_S = compiledNumerics.method("i32TruncSatF32S", "F", returns = "I")
val I32_TRUNC_SAT_F32_U = compiledNumerics.method("i32TruncSatF32U", "F", returns = "I")
val I32_TRUNC_SAT_F64_S = compiledNumerics.method("i32TruncSatF64S", "D", returns = "I")
val I32_TRUNC_SAT_F64_U = compiledNumerics.method("i32TruncSatF64U", "D", returns = "I")
val I64_TRUNC_SAT_F32_S = compiledNumerics.method("i64TruncSatF32S", "F", returns = "J")
val I64_TRUNC_SAT_F32_U = compiledNumerics.method("i64TruncSatF32U", "F", returns = "J")
val I64_TRUNC_SAT_F64_S = compiledNumerics.method("i64TruncSatF64S", "D", returns = "J")
val I64_TRUNC_SAT_F64_U = compiledNumerics.method("i64TruncSatF64U", "D", returns = "J")
val F32_CONVERT_I32_U = compiledNumerics.method("f32ConvertI32U", "I", returns = "F")
val F32_CONVERT_I64_U = compiledNumerics.method("f32ConvertI64U", "J", returns = "F")
val F64_CONVERT_I32_U = compiledNumerics.method("f64ConvertI32U", "I", returns = "D")
val F64_CONVERT_I64_U = compiledNumerics.method("f64ConvertI64U", "J", returns = "D")

val GLOBAL_GET = globalInstance.method("getValue", returns = "J")
val GLOBAL_SET = globalInstance.method("setValue", "J", returns = "V")

val INT_ROTATE_LEFT = javaInteger.method("rotateLeft", "I", "I", returns = "I")
val INT_ROTATE_RIGHT = javaInteger.method("rotateRight", "I", "I", returns = "I")
val INT_LEADING_ZEROS = javaInteger.method("numberOfLeadingZeros", "I", returns = "I")
val INT_TRAILING_ZEROS = javaInteger.method("numberOfTrailingZeros", "I", returns = "I")
val INT_BIT_COUNT = javaInteger.method("bitCount", "I", returns = "I")
val LONG_ROTATE_LEFT = javaLong.method("rotateLeft", "J", "I", returns = "J")
val LONG_ROTATE_RIGHT = javaLong.method("rotateRight", "J", "I", returns = "J")
val LONG_LEADING_ZEROS = javaLong.method("numberOfLeadingZeros", "J", returns = "I")
val LONG_TRAILING_ZEROS = javaLong.method("numberOfTrailingZeros", "J", returns = "I")
val LONG_BIT_COUNT = javaLong.method("bitCount", "J", returns = "I")
val INT_BITS_TO_FLOAT = javaFloat.method("intBitsToFloat", "I", returns = "F")
val FLOAT_TO_RAW_INT_BITS = javaFloat.method("floatToRawIntBits", "F", returns = "I")
val LONG_BITS_TO_DOUBLE = javaDouble.method("longBitsToDouble", "J", returns = "D")
val DOUBLE_TO_RAW_LONG_BITS = javaDouble.method("doubleToRawLongBits", "D", returns = "J")
val ARRAYCOPY = javaSystem.method("arraycopy", OBJECT_TYPE, "I", OBJECT_TYPE, "I", "I", returns = "V")
