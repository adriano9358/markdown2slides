package pt.isel.markdown2slides

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import java.util.UUID



class RepositoryProjectContentFileSystemTests {

    private val testDataDir = "testData"

    private lateinit var repository: RepositoryProjectContentFileSystem
    private lateinit var tempDir: String

    private val userId = UUID.randomUUID()
    private val projectId = UUID.randomUUID()
    private val image = "image1"
    private val imageFormat = "png"
    private val markdownSample = "# Hello Test"

    @BeforeEach
    fun setup() {
        tempDir = Files.createTempDirectory(testDataDir).toString()
        repository = RepositoryProjectContentFileSystem(baseDir = tempDir)
    }

    @AfterEach
    fun cleanup() {
        File(tempDir).deleteRecursively()
    }

    @Test
    fun `save and retrieve markdown`() {
        val content = markdownSample
        repository.saveMarkdown(userId, projectId, content)

        val retrieved = repository.getMarkdown(userId, projectId)
        assertEquals(content, retrieved)
    }

    @Test
    fun `delete markdown`() {
        val content = markdownSample
        repository.saveMarkdown(userId, projectId, content)
        repository.deleteMarkdown(userId, projectId)
        assertNull(repository.getMarkdown(userId, projectId))
    }

    @Test
    fun `save and retrieve image`() {
        val imageBytes = byteArrayOf(1, 2, 3, 4)
        repository.saveImage(userId, projectId, image, imageFormat, imageBytes)

        val retrieved = repository.getImage(userId, projectId, image, imageFormat)
        assertArrayEquals(imageBytes, retrieved)
    }

    @Test
    fun `delete image`() {
        val imageBytes = byteArrayOf(5, 6, 7)
        repository.saveImage(userId, projectId, image, imageFormat, imageBytes)
        repository.deleteImage(userId, projectId, image, imageFormat)
        assertNull(repository.getImage(userId, projectId, image, imageFormat))
    }

}