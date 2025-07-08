package pt.isel.markdown2slides

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ProjectInfoServiceTests : ServiceTestsBase() {

    private lateinit var projectInfoService: ProjectInfoService


    @BeforeEach
    fun setupService() {
        projectInfoService = ProjectInfoService(trxManager, repoProjectContent)
        addUser(firstUserId, firstUserName, firstUserEmail)
    }

    @Test
    fun `create project succeeds`() {
        val name = projectName
        val description = projectDescription
        val visibility = projectVisibility

        val result = projectInfoService.createProject(name, description, firstUserId, visibility)

        assertTrue(result is Either.Right)
        val project = (result as Either.Right).value
        assertEquals(name, project.name)
        assertEquals(description, project.description)
        assertEquals(firstUserId, project.ownerId)
        assertEquals(visibility, project.visibility)
    }

    @Test
    fun `createProject should add owner as ADMIN collaborator`() {
        val name = projectName
        val description = projectDescription
        val visibility = projectVisibility

        val result = projectInfoService.createProject(name, description, firstUserId, visibility)

        assertTrue(result is Either.Right)
        val project = (result as Either.Right).value

        val collaborators = trxManager.run {
            repoCollaborators.getCollaborators(project.id)
        }

        assertEquals(1, collaborators.size)
        val collaborator = collaborators.first()
        assertEquals(firstUserId, collaborator.user.id)
        assertEquals(ProjectRole.ADMIN, collaborator.role)
        assertEquals(project.id, collaborator.project_id)
    }

    @Test
    fun `deleteProject should succeed`() {
        val project = projectInfoService.createProject(
            "ToDelete",
            "desc",
            firstUserId,
            Visibility.PRIVATE
        ).let { result ->
            assertTrue(result is Either.Right)
            (result as Either.Right).value
        }


        val result = projectInfoService.deleteProject(firstUserId, project.id)

        assertTrue(result is Either.Right)
        val deleted = trxManager.run { repoProjectInfo.findById(project.id) }
        assertNull(deleted)
    }

    @Test
    fun `deleteProject should fail if project not found`() {
        val result = projectInfoService.deleteProject(firstUserId, UUID.randomUUID())

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.ProjectNotFound, (result as Either.Left).value)
    }

    @Test
    fun `deleteProject should fail if owner mismatch`() {
        addUser(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Test User 2", "testuser2@email.com")
        val project = projectInfoService.createProject(
            "Another",
            "desc",
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            Visibility.PRIVATE
        ).let { result ->
            assertTrue(result is Either.Right)
            (result as Either.Right).value
        }

        val result = projectInfoService.deleteProject(firstUserId, project.id)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.ProjectNotAuthorized, (result as Either.Left).value)
    }

    @Test
    fun `getPersonalProjects returns only the owner's projects`() {
        repeat(3) {
            projectInfoService.createProject("p$it", "desc", firstUserId, Visibility.PUBLIC)
        }
        val otherId = UUID.fromString("00000000-0000-0000-0000-000000000001")
        addUser(otherId, "Other", "other@email.com")
        projectInfoService.createProject("Other", "desc", otherId, Visibility.PRIVATE)


        val result = projectInfoService.getPersonalProjects(firstUserId)

        assertTrue(result is Either.Right)
        val projects = (result as Either.Right).value
        assertEquals(3, projects.size)
        assertTrue(projects.all { it.ownerId == firstUserId })
    }

    @Test
    fun `getProjectDetails returns the project for the correct owner`() {
        val project = projectInfoService.createProject("Detail", "desc", firstUserId, Visibility.PRIVATE).let { result ->
            assertTrue(result is Either.Right)
            (result as Either.Right).value
        }

        val result = projectInfoService.getProjectDetails(firstUserId, project.id)

        assertTrue(result is Either.Right)
        assertEquals(project.id, (result as Either.Right).value.id)
    }

    @Test
    fun `getProjectDetails fails if project not found`() {
        val result = projectInfoService.getProjectDetails(firstUserId, UUID.randomUUID())

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.ProjectNotFound, (result as Either.Left).value)
    }

    @Test
    fun `getProjectDetails fails if user is not owner`() {
        val userId = UUID.randomUUID()
        addUser(userId, "Other", "other@email.com")
        val project = projectInfoService.createProject("NotMine", "desc", userId, Visibility.PRIVATE).let { result ->
            assertTrue(result is Either.Right)
            (result as Either.Right).value
        }


        val result = projectInfoService.getProjectDetails(firstUserId, project.id)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.ProjectNotAuthorized, (result as Either.Left).value)
    }

    @Test
    fun `editProjectDetails updates name, description and visibility`() {
        val original = projectInfoService.createProject("OldName", "OldDesc", firstUserId, Visibility.PRIVATE).let { result ->
            assertTrue(result is Either.Right)
            (result as Either.Right).value
        }

        val result = projectInfoService.editProjectDetails(
            firstUserId,
            original.id,
            name = "NewName",
            description = "NewDesc",
            visibility = Visibility.PUBLIC
        )

        assertTrue(result is Either.Right)
        val updated = (result as Either.Right).value
        assertEquals("NewName", updated.name)
        assertEquals("NewDesc", updated.description)
        assertEquals(Visibility.PUBLIC, updated.visibility)
        assertTrue(updated.updatedAt.isAfter(original.updatedAt))
    }

    @Test
    fun `editProjectDetails fails if project does not exist`() {
        val result = projectInfoService.editProjectDetails(
            firstUserId,
            UUID.randomUUID(),
            name = "NewName",
            description = "NewDesc",
            visibility = Visibility.PUBLIC
        )

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.ProjectNotFound, (result as Either.Left).value)
    }

    @Test
    fun `editProjectDetails fails if not owner`() {
        val otherUserId = UUID.randomUUID()
        trxManager.run {
            repoUser.save(User(otherUserId, "Other", "other@email.com"))
        }

        val project = trxManager.run {
            repoProjectInfo.createProject("Title", "Desc", otherUserId, Visibility.PRIVATE)
        }

        val result = projectInfoService.editProjectDetails(
            firstUserId,
            project.id,
            name = "Hacked",
            description = null,
            visibility = null
        )

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.ProjectNotAuthorized, (result as Either.Left).value)
    }

}
