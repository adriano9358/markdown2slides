package pt.isel.markdown2slides

import jakarta.inject.Named
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

const val MARKDOWN_DIR = "./data/markdown"
const val IMAGES_DIR = "./data/images"


@Named
class RepositoryProjectContent {

    init {
        File(MARKDOWN_DIR).mkdirs()
        File(IMAGES_DIR).mkdirs()
    }

    fun saveMarkdown(id: String, content: String) {
        val file = File("$MARKDOWN_DIR/$id.md")
        file.writeText(content)
    }

    fun getMarkdown(id: String): String? {
        val file = File("$MARKDOWN_DIR/$id.md")
        return if (file.exists()) file.readText() else null
    }

    fun deleteMarkdown(id: String): Boolean{
        val file = File("$MARKDOWN_DIR/$id.md")
        return file.delete()
    }

    fun saveImage(id: String, imageBytes: ByteArray) {
        val file = File("$IMAGES_DIR/$id.png")
        file.writeBytes(imageBytes)
    }

    fun getImage(id: String): ByteArray? {
        val file = File("$IMAGES_DIR/$id.png")
        return if (file.exists()) file.readBytes() else null
    }
}