import org.gradle.api.provider.Property

interface KotlinConventionsExtension {
    val jvmBytecodeVersion: Property<Int>
}
