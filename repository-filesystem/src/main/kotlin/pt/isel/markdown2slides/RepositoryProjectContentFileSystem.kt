package pt.isel.markdown2slides

import jakarta.inject.Named
import pt.isel.markdown2slides.file.RepositoryProjectContent
import java.io.File
import java.nio.file.Path
import java.util.UUID

const val MARKDOWN_FILE_NAME = "content.md"
const val IMAGE_FOLDER_NAME = "image"
const val BASE_FILE_DIR = "./data"
const val MARKDOWN_FILE_INIT_CONTENT = "# Slide 1\n\nThis is an example slide. To create another slide, add a new line with `---` followed by the slide content, like this:\n\n---\n\n# Slide 2\n\nThis is the second slide. You can add as many slides as you want by repeating the `---` pattern."

@Named
class RepositoryProjectContentFileSystem(private val baseDir: String = BASE_FILE_DIR): RepositoryProjectContent {

    init {
        File(baseDir).mkdirs()
    }

    private fun getProjectDir(userId: UUID, projectId: UUID): Path =
        Path.of(baseDir, userId.toString(), projectId.toString())

    private fun getMarkdownFile(userId: UUID, projectId: UUID): Path =
        getProjectDir(userId, projectId).resolve(MARKDOWN_FILE_NAME)

    private fun getImagesDir(userId: UUID, projectId: UUID): Path =
        getProjectDir(userId, projectId).resolve(IMAGE_FOLDER_NAME)

    private fun getImageFile(userId: UUID, projectId: UUID, image: String, extension: String): Path =
        getImagesDir(userId, projectId).resolve("$image.$extension")

    override fun createProject(userId: UUID, projectId: UUID) {
        val projectDir = getProjectDir(userId, projectId).toFile()
        val imagesDir = getImagesDir(userId, projectId).toFile()
        projectDir.mkdirs()
        imagesDir.mkdirs()

        val markdownFile = getMarkdownFile(userId, projectId).toFile()
        if (!markdownFile.exists()) {
            markdownFile.writeText(MARKDOWN_FILE_INIT_CONTENT)
        }
    }

    override fun checkProjectExists(userId: UUID, projectId: UUID): Boolean {
        val projectDir = getProjectDir(userId, projectId).toFile()
        val markdownFile = getMarkdownFile(userId, projectId).toFile()
        val imagesDir = getImagesDir(userId, projectId).toFile()

        return projectDir.exists() && projectDir.isDirectory &&
                markdownFile.exists() && markdownFile.isFile &&
                imagesDir.exists() && imagesDir.isDirectory
    }

    override fun saveMarkdown(userId: UUID, projectId: UUID, content: String) {
        val projectDir = getProjectDir(userId, projectId).toFile()
        projectDir.mkdirs()

        val markdownFile = getMarkdownFile(userId, projectId).toFile()
        markdownFile.writeText(content)
    }

    override fun getMarkdown(userId: UUID, projectId: UUID): String? {
        val markdownFile = getMarkdownFile(userId, projectId).toFile()
        return if (markdownFile.exists()) markdownFile.readText() else null
    }

    override fun deleteMarkdown(userId: UUID, projectId: UUID): Boolean {
        val markdownFile = getMarkdownFile(userId, projectId).toFile()
        return markdownFile.delete()
    }

    override fun saveImage(userId: UUID, projectId: UUID, image: String, extension: String, imageBytes: ByteArray) {
        val imagesDir = getImagesDir(userId, projectId).toFile()
        imagesDir.mkdirs()

        val imageFile = getImageFile(userId, projectId, image, extension).toFile()
        imageFile.writeBytes(imageBytes)
    }

    override fun getImage(userId: UUID, projectId: UUID, image: String, extension: String): ByteArray? {
        val imageFile = getImageFile(userId, projectId, image, extension).toFile()
        return if (imageFile.exists()) imageFile.readBytes() else null
    }

    override fun deleteImage(userId: UUID, projectId: UUID, image: String, extension: String): Boolean {
        val imageFile = getImageFile(userId, projectId, image, extension).toFile()
        return imageFile.delete()
    }

    override fun deleteProjectContent(userId: UUID, projectId: UUID): Boolean {
        val projectDir = getProjectDir(userId, projectId).toFile()
        return projectDir.deleteRecursively()
    }
}
