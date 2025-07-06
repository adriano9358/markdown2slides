package pt.isel.markdown2slides

import pt.isel.markdown2slides.file.RepositoryProjectContent
import pt.isel.markdown2slides.data.mem.RepositoryProjectInfoInMem
import pt.isel.markdown2slides.data.mem.TransactionManagerInMem
import java.util.*

class ProjectContentServiceTests {

    private lateinit var tempDir: String
    private lateinit var repoProjectContent: RepositoryProjectContent
    private lateinit var projectContentService: ProjectContentService
    private lateinit var trxManager: TransactionManagerInMem
    private lateinit var repoProjectInfo: RepositoryProjectInfoInMem
    private lateinit var projectInfoService: ProjectInfoService

    private val testDataDir = "testData"
    private val ownerId = UUID.fromString("00000000-0000-0000-0000-000000000000")
/*
    @BeforeEach
    fun setup() {
        tempDir = Files.createTempDirectory(testDataDir).toString()
        repoProjectContent = RepositoryProjectContentFileSystem(baseDir = tempDir)
        projectContentService = ProjectContentService(repoProjectContent)
        trxManager = TransactionManagerInMem()
        repoProjectInfo = RepositoryProjectInfoInMem()
        projectInfoService = ProjectInfoService(trxManager, repoProjectContent)
    }

    @AfterEach
    fun cleanup() {
        File(tempDir).deleteRecursively()
    }
*/
/*
    private fun createValidProject(): ProjectInfo {
        val result = projectInfoService.createProject("name", "description", ownerId, Visibility.PUBLIC)
        return if(result is Either.Right )result.value else Assertions.fail("Project creation failed")
    }

    @Test
    fun `get project content`() {
        // Arrange
        val project = createValidProject()

        // Act
        val result = projectContentService.getProjectContent(ownerId, project.id)

        // Assert
        assertTrue(result is Either.Right)
        assertTrue((result as Either.Right).value.isEmpty())
    }

    @Test
    fun `get project content with invalid project id`() {
        // Arrange
        val invalidProjectId = UUID.randomUUID()

        // Act
        val result = projectContentService.getProjectContent(ownerId, invalidProjectId)

        // Assert
        assertTrue(result is Either.Left)
        assertTrue((result as Either.Left).value is ProjectError.ProjectNotFound)
    }


    @Test
    fun `update project content`() {
        // Arrange
        val project = createValidProject()
        val markdown = "markdown"

        // Act
        val contentBefore = projectContentService.getProjectContent(ownerId, project.id)
        val result = projectContentService.updateProjectContent(ownerId, project.id, markdown)
        val contentAfter = projectContentService.getProjectContent(ownerId, project.id)

        // Assert
        assertTrue(result is Either.Right)
        assertTrue((contentBefore as Either.Right).value.isEmpty())
        assertTrue((contentAfter as Either.Right).value == markdown)
    }

    @Test
    fun `update project content with invalid project id`() {
        // Arrange
        val invalidProjectId = UUID.randomUUID()
        val markdown = "markdown"

        // Act
        val result = projectContentService.updateProjectContent(ownerId, invalidProjectId, markdown)

        // Assert
        assertTrue(result is Either.Left)
        assertTrue((result as Either.Left).value is ProjectError.ProjectNotFound)
    }

    @Test
    fun `upload image`() {
        // Arrange
        val project = createValidProject()
        val imageName = "image"
        val extension = "png"
        val imageBytes = byteArrayOf(1, 2, 3)

        // Act
        val result = projectContentService.uploadImage(ownerId, project.id, imageName, extension, imageBytes)

        // Assert
        assertTrue(result is Either.Right)
    }

    @Test
    fun `upload image with invalid project id`() {
        // Arrange
        val invalidProjectId = UUID.randomUUID()
        val imageName = "image"
        val extension = "png"
        val imageBytes = byteArrayOf(1, 2, 3)

        // Act
        val result = projectContentService.uploadImage(ownerId, invalidProjectId, imageName, extension, imageBytes)

        // Assert
        assertTrue(result is Either.Left)
        assertTrue((result as Either.Left).value is ProjectError.ProjectNotFound)
    }

    @Test
    fun `get image`() {
        // Arrange
        val project = createValidProject()
        val imageName = "image"
        val extension = "png"
        val imageBytes = byteArrayOf(1, 2, 3)
        projectContentService.uploadImage(ownerId, project.id, imageName, extension, imageBytes)

        // Act
        val result = projectContentService.getImage(ownerId, project.id, imageName, extension)

        // Assert
        assertTrue(result is Either.Right)
        assertTrue((result as Either.Right).value.contentEquals(imageBytes))
    }


    @Test
    fun `get image with invalid project id`() {
        // Arrange
        val invalidProjectId = UUID.randomUUID()
        val imageName = "image"
        val extension = "png"

        // Act
        val result = projectContentService.getImage(ownerId, invalidProjectId, imageName, extension)

        // Assert
        assertTrue(result is Either.Left)
        assertTrue((result as Either.Left).value is ProjectError.ImageNotFound)
    }

    @Test
    fun `delete image`() {
        // Arrange
        val project = createValidProject()
        val imageName = "image"
        val extension = "png"
        val imageBytes = byteArrayOf(1, 2, 3)
        projectContentService.uploadImage(ownerId, project.id, imageName, extension, imageBytes)

        // Act
        val result = projectContentService.deleteImage(ownerId, project.id, imageName, extension)
        val image = projectContentService.getImage(ownerId, project.id, imageName, extension)

        // Assert
        assertTrue(result is Either.Right)
        assertTrue((image as Either.Left).value is ProjectError.ImageNotFound)
    }

    @Test
    fun `delete image with invalid project id`() {
        // Arrange
        val invalidProjectId = UUID.randomUUID()
        val imageName = "image"
        val extension = "png"

        // Act
        val result = projectContentService.deleteImage(ownerId, invalidProjectId, imageName, extension)

        // Assert
        assertTrue(result is Either.Left)
        assertTrue((result as Either.Left).value is ProjectError.ImageNotFound)
    }*/

}
