import dev.zacsweers.metro.createGraph
import kotlinx.browser.document

fun main() {

    val appGraph = createGraph<AppGraph>()
    val factorialService = appGraph.factorial
    val stringService = appGraph.strings

    val root = document.getElementById("root")

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

    root?.textContent = output.joinToString()
}
