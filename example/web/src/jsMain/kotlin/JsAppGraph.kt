import com.tap.chasm.di.FactorialProvider
import com.test.chasm.FactorialService
import com.test.chasm.StringService
import dev.zacsweers.metro.DependencyGraph

interface AppGraph: FactorialProvider

@DependencyGraph
interface JsAppGraph : AppGraph {
    val factorial: FactorialService

    val strings: StringService
}
