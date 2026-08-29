import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

interface PublishingConventionsExtension {
    val name: Property<String>
    val description: Property<String>
    val functionalTestRepository: DirectoryProperty
}
