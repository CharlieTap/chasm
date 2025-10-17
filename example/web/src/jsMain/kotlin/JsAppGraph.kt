import com.test.chasm.FactorialService
import com.test.chasm.StringService
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
interface AppGraph {
    val factorial: FactorialService
    val strings: StringService
}
