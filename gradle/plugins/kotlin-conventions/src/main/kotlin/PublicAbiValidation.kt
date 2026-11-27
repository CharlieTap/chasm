import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

@OptIn(ExperimentalAbiValidation::class)
fun KotlinMultiplatformExtension.validatePublicAbi() {
    abiValidation {
        filters {
            exclude {
                annotatedWith.add("io.github.charlietap.chasm.InternalChasmApi")
            }
        }
    }
}
