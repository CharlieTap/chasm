import dev.zacsweers.metro.createGraph

fun main() {
    setRootText(runDemo())
}

fun runDemo(): String {
    val appGraph = createGraph<AppGraph>()
    val factorialService = appGraph.factorial
    val stringService = appGraph.strings
    val interopService = appGraph.interop

    val output = mutableListOf<String>()

    val factorial = factorialService.factorial(10)
    output += "factorial: $factorial"

    val truncated = stringService.truncate("abcde")
    output += "truncate function: $truncated"

    val truncateNullTerminated = stringService.truncateNullTerminated("foobar")
    output += "truncate null terminated function: $truncateNullTerminated"

    val truncateLenPrefixed = stringService.truncateLenPrefixed("hijkl")
    output += "truncate len prefixed function: $truncateLenPrefixed"

    val truncatePacked = stringService.truncatePacked("packed")
    output += "truncate packed function: $truncatePacked"

    val interopMultipleReturn = interopService.interopMultipleReturn()
    output += "interop int: ${interopService.intFunction()}"
    output += "interop long: ${interopService.longFunction()}"
    output += "interop float: ${interopService.floatFunction()}"
    output += "interop double: ${interopService.doubleFunction()}"
    output += "interop i32 param: ${interopService.identityI32(123)}"
    output += "interop i64 param: ${interopService.identityI64(456)}"
    output += "interop f32 param: ${interopService.identityF32(7.5f)}"
    output += "interop f64 param: ${interopService.identityF64(8.25)}"
    output += "interop mixed params: ${interopService.mixedNumericFunction(1, 2, 3.5f, 4.25)}"
    output += "interop multi return: r0 = ${interopMultipleReturn.r0}, r1 = ${interopMultipleReturn.r1}, r2 = ${interopMultipleReturn.r2}, r3 = ${interopMultipleReturn.r3}"
    output += "interop pointer and length string: ${interopService.pointerAndLengthString()}"
    output += "interop length prefixed string: ${interopService.lengthPrefixedString()}"
    output += "interop null terminated string: ${interopService.nullTerminatedString()}"
    output += "interop packed string: ${interopService.packedI64String()}"
    output += "interop immutable global: ${interopService.immutableGlobal}"
    output += "interop mutable global before: ${interopService.mutableGlobal}"
    interopService.unitFunction()
    output += "interop mutable global after unit: ${interopService.mutableGlobal}"
    interopService.mutableGlobal = 512
    output += "interop mutable global after write: ${interopService.mutableGlobal}"

    return output.joinToString()
}

internal expect fun setRootText(value: String)
