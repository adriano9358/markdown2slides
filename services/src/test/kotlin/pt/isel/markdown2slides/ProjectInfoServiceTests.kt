package pt.isel.markdown2slides

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pt.isel.markdown2slides.file.RepositoryProjectContent
import pt.isel.markdown2slides.data.mem.RepositoryProjectInfoInMem
import pt.isel.markdown2slides.data.mem.TransactionManagerInMem
import java.io.File
import java.nio.file.Files
import java.util.*

class ProjectInfoServiceTests {
/*
    private lateinit var trxManager: TransactionManagerInMem
    private lateinit var repoProjectInfo: RepositoryProjectInfoInMem
    private lateinit var tempDir: String
    private lateinit var repoProjectContent: RepositoryProjectContent
    private lateinit var projectInfoService: ProjectInfoService

    private val testDataDir = "testData"
    private val ownerId = UUID.fromString("00000000-0000-0000-0000-000000000000")

    @BeforeEach
    fun setup() {
        trxManager = TransactionManagerInMem()
        repoProjectInfo = RepositoryProjectInfoInMem()
        tempDir = Files.createTempDirectory(testDataDir).toString()
        repoProjectContent = RepositoryProjectContentFileSystem(baseDir = tempDir)
        projectInfoService = ProjectInfoService(trxManager, repoProjectContent)
    }

    @AfterEach
    fun cleanup() {
        File(tempDir).deleteRecursively()
    }

    @Test
    fun `create project`() {
        // Arrange
        val name = "name"
        val description = "description"
        val ownerId = UUID.fromString("00000000-0000-0000-0000-000000000000")
        val visibility = Visibility.PUBLIC

        // Act
        val result = projectInfoService.createProject(name, description, ownerId, visibility)

        // Assert
        assertTrue(result is Either.Right)
        val project = (result as Either.Right).value
        assertEquals(name, project.name)
        assertEquals(description, project.description)
        assertEquals(ownerId, project.ownerId)
        assertEquals(visibility, project.visibility)
    }

    @Test
    fun `delete project`() {
        // Arrange
        val project = projectInfoService.createProject("name", "description", ownerId, Visibility.PUBLIC)

        // Act
        val result = projectInfoService.deleteProject(ownerId, (project as Either.Right).value.id)

        // Assert
        assertTrue(result is Either.Right)
    }

    @Test
    fun `delete project not found`() {
        // Arrange
        val projectId = UUID.randomUUID()

        // Act
        val result = projectInfoService.deleteProject(ownerId, projectId)

        // Assert
        assertTrue(result is Either.Left)
        assertEquals(ProjectError.ProjectNotFound, (result as Either.Left).value)
    }

    @Test
    fun `delete project not authorized`() {
        // Arrange
        val project = projectInfoService.createProject("name", "description", ownerId, Visibility.PUBLIC)

        // Act
        val result = projectInfoService.deleteProject(UUID.randomUUID(), (project as Either.Right).value.id)

        // Assert
        assertTrue(result is Either.Left)
        assertEquals(ProjectError.ProjectNotAuthorized, (result as Either.Left).value)
    }

    @Test
    fun `get empty personal projects`() {
        // Arrange
        val ownerId = UUID.randomUUID()

        // Act
        val result = projectInfoService.getPersonalProjects(ownerId)

        // Assert
        assertTrue(result is Either.Right)
        val projects = (result as Either.Right).value
        assertTrue(projects.isEmpty())
    }



    @Test
    fun `get some personal projects`() {
        // Arrange
        val ownerId = UUID.randomUUID()
        val project1 = projectInfoService.createProject("name1", "description1", ownerId, Visibility.PUBLIC)
        val project2 = projectInfoService.createProject("name2", "description2", ownerId, Visibility.PRIVATE)

        // Act
        val result = projectInfoService.getPersonalProjects(ownerId)

        // Assert
        assertTrue(result is Either.Right)
        val projects = (result as Either.Right).value
        assertEquals(2, projects.size)
        assertEquals((project1 as Either.Right).value, projects[0])
        assertEquals((project2 as Either.Right).value, projects[1])
    }

    @Test
    fun `get project details`() {
        // Arrange
        val project = createValidProject()

        // Act
        val result = projectInfoService.getProjectDetails(ownerId, project.id)

        // Assert
        assertTrue(result is Either.Right)
        assertEquals(project, (result as Either.Right).value)
    }

    @Test
    fun `get project details not found`() {
        // Arrange
        val projectId = UUID.randomUUID()

        // Act
        val result = projectInfoService.getProjectDetails(ownerId, projectId)

        // Assert
        assertTrue(result is Either.Left)
        assertEquals(ProjectError.ProjectNotFound, (result as Either.Left).value)
    }

    @Test
    fun `edit project details`() {
        // Arrange
        val project = createValidProject()

        val name = "new name"
        val description = "new description"
        val visibility = Visibility.PRIVATE

        // Act
        val result = projectInfoService.editProjectDetails(ownerId, project.id, name, description, visibility)

        // Assert
        assertTrue(result is Either.Right)
        val updated = (result as Either.Right).value
        assertEquals(name, updated.name)
        assertEquals(description, updated.description)
        assertEquals(visibility, updated.visibility)
    }

    @Test
    fun `edit project details with null values`() {
        // Arrange
        val project = createValidProject()

        // Act
        val result = projectInfoService.editProjectDetails(ownerId, project.id, null, null, null)

        // Assert
        assertTrue(result is Either.Right)
        val updated = (result as Either.Right).value
        assertEquals(project.name, updated.name)
        assertEquals(project.description, updated.description)
        assertEquals(project.visibility, updated.visibility)
    }

    @Test
    fun `edit project details not found`() {
        // Arrange
        val projectId = UUID.randomUUID()

        // Act
        val result = projectInfoService.editProjectDetails(ownerId, projectId, "name", "description", Visibility.PUBLIC)

        // Assert
        assertTrue(result is Either.Left)
        assertEquals(ProjectError.ProjectNotFound, (result as Either.Left).value)
    }


    private fun createValidProject(): ProjectInfo {
        val result = projectInfoService.createProject("name", "description", ownerId, Visibility.PUBLIC)
        return if(result is Either.Right )result.value else fail("Project creation failed")
    }

*/
}
