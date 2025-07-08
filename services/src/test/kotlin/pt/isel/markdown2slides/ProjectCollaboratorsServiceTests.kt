package pt.isel.markdown2slides

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ProjectCollaboratorsServiceTests : ServiceTestsBase() {

    private lateinit var collaboratorsService: ProjectCollaboratorsService
    private lateinit var projectInfoService: ProjectInfoService

    @BeforeEach
    fun setupService() {
        collaboratorsService = ProjectCollaboratorsService(trxManager)
        projectInfoService = ProjectInfoService(trxManager, repoProjectContent)

        addUser(firstUserId, firstUserName, firstUserEmail)
        addUser(secondUserId, secondUserName, secondUserEmail)
    }

    @Test
    fun `getCollaborators returns list if secondUser is collaborator`() {
        val project = projectInfoService.createProject(
            projectName,
            projectDescription,
            firstUserId,
            projectVisibility
        ).let { either ->
            assertTrue(either is Either.Right)
            (either as Either.Right).value
        }
        trxManager.run {
            repoCollaborators.addCollaborator(project.id, secondUserId, ProjectRole.EDITOR)
        }

        val result = collaboratorsService.getCollaborators(secondUserId, project.id)

        assertTrue(result is Either.Right)
        val collaborators = (result as Either.Right).value
        assertTrue(collaborators.any { it.user.id == secondUserId })
    }

    @Test
    fun `getCollaborators returns list if requester is owner`() {
        val project = projectInfoService.createProject(
            projectName,
            projectDescription,
            firstUserId,
            projectVisibility
        ).let {
            assertTrue(it is Either.Right)
            (it as Either.Right).value
        }

        val result = collaboratorsService.getCollaborators(firstUserId, project.id)

        assertTrue(result is Either.Right)
        val collaborators = (result as Either.Right).value
        assertTrue(collaborators.any { it.user.id == firstUserId })
    }

    @Test
    fun `getCollaborators returns list if project is public`() {
        val project = projectInfoService.createProject(
            projectName,
            projectDescription,
            firstUserId,
            Visibility.PUBLIC
        ).let {
            assertTrue(it is Either.Right)
            (it as Either.Right).value
        }

        val result = collaboratorsService.getCollaborators(secondUserId, project.id)

        assertTrue(result is Either.Right)
    }

    @Test
    fun `getCollaborators fails if requester is unauthorized`() {
        val project = projectInfoService.createProject(
            projectName,
            projectDescription,
            firstUserId,
            projectVisibility
        ).let {
            assertTrue(it is Either.Right)
            (it as Either.Right).value
        }

        val result = collaboratorsService.getCollaborators(secondUserId, project.id)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.ProjectNotAuthorized, (result as Either.Left).value)
    }

    @Test
    fun `getCollaborators fails when project does not exist`() {
        val nonExistentProjectId = UUID.randomUUID()

        val result = collaboratorsService.getCollaborators(secondUserId, nonExistentProjectId)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.ProjectNotFound, (result as Either.Left).value)
    }

    @Test
    fun `removeCollaborator succeeds when owner removes user`() {
        val project = projectInfoService.createProject(
            projectName,
            projectDescription,
            firstUserId,
            projectVisibility
        ).let {
            assertTrue(it is Either.Right)
            (it as Either.Right).value
        }

        trxManager.run {
            repoCollaborators.addCollaborator(project.id, secondUserId, ProjectRole.EDITOR)
        }

        val result = collaboratorsService.removeCollaborator(firstUserId, project.id, secondUserId)

        assertTrue(result is Either.Right)

        val collaborators = trxManager.run {
            repoCollaborators.getCollaborators(project.id)
        }
        assertTrue(collaborators.none { it.user.id == secondUserId })
    }

    @Test
    fun `removeCollaborator fails when non-owner tries to remove someone else`() {
        val thirdUserId = UUID.randomUUID()
        val thirdUserEmail = "third@email.com"
        addUser(thirdUserId, "Third User", thirdUserEmail)

        val project = projectInfoService.createProject(
            projectName,
            projectDescription,
            firstUserId,
            projectVisibility
        ).let {
            assertTrue(it is Either.Right)
            (it as Either.Right).value
        }

        trxManager.run {
            repoCollaborators.addCollaborator(project.id, secondUserId, ProjectRole.EDITOR)
            repoCollaborators.addCollaborator(project.id, thirdUserId, ProjectRole.VIEWER)
        }

        val result = collaboratorsService.removeCollaborator(secondUserId, project.id, thirdUserId)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.ProjectNotAuthorized, (result as Either.Left).value)
    }

    @Test
    fun `removeCollaborator fails when project does not exist`() {
        val nonExistentProjectId = UUID.randomUUID()

        val result = collaboratorsService.removeCollaborator(firstUserId, nonExistentProjectId, secondUserId)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.ProjectNotFound, (result as Either.Left).value)
    }

    @Test
    fun `removeCollaborator fails when user to remove is not a collaborator`() {
        val project = projectInfoService.createProject(
            projectName,
            projectDescription,
            firstUserId,
            projectVisibility
        ).let {
            assertTrue(it is Either.Right)
            (it as Either.Right).value
        }

        val result = collaboratorsService.removeCollaborator(firstUserId, project.id, secondUserId)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.UserNotInProject, (result as Either.Left).value)
    }

    @Test
    fun `removeCollaborator fails when trying to remove owner from project`() {
        val project = projectInfoService.createProject(
            projectName,
            projectDescription,
            firstUserId,
            projectVisibility
        ).let {
            assertTrue(it is Either.Right)
            (it as Either.Right).value
        }

        val result = collaboratorsService.removeCollaborator(firstUserId, project.id, firstUserId)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.ProjectNotAuthorized, (result as Either.Left).value)
    }

    @Test
    fun `getUserRoleInProject returns correct role`() {
        val project = projectInfoService.createProject(
            projectName,
            projectDescription,
            firstUserId,
            projectVisibility
        ).let {
            assertTrue(it is Either.Right)
            (it as Either.Right).value
        }

        trxManager.run {
            repoCollaborators.addCollaborator(project.id, secondUserId, ProjectRole.VIEWER)
        }

        val result = collaboratorsService.getUserRoleInProject(secondUserId, project.id)

        assertTrue(result is Either.Right)
        assertEquals(ProjectRole.VIEWER, (result as Either.Right).value)
    }

    @Test
    fun `getUserRoleInProject fails if user not in project`() {
        val project = projectInfoService.createProject(
            projectName,
            projectDescription,
            firstUserId,
            projectVisibility
        ).let {
            assertTrue(it is Either.Right)
            (it as Either.Right).value
        }

        val result = collaboratorsService.getUserRoleInProject(secondUserId, project.id)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.UserNotInProject, (result as Either.Left).value)
    }

    @Test
    fun `getUserRoleInProject fails if project does not exist`() {
        val nonExistentProjectId = UUID.randomUUID()

        val result = collaboratorsService.getUserRoleInProject(secondUserId, nonExistentProjectId)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.ProjectNotFound, (result as Either.Left).value)
    }
}
