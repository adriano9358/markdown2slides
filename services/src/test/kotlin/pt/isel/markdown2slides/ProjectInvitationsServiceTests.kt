package pt.isel.markdown2slides

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ProjectInvitationsServiceTests : ServiceTestsBase() {

    private lateinit var invitationsService: ProjectInvitationsService
    private lateinit var projectInfoService: ProjectInfoService

    @BeforeEach
    fun setupService() {
        invitationsService = ProjectInvitationsService(trxManager)
        projectInfoService = ProjectInfoService(trxManager, repoProjectContent)

        addUser(firstUserId, firstUserName, firstUserEmail)
        addUser(secondUserId, secondUserName, secondUserEmail)
    }

    @Test
    fun `createInvitation succeeds for valid case`() {
        val project = projectInfoService.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            .let {
                assertTrue(it is Either.Right)
                (it as Either.Right).value
            }

        val result = invitationsService.createInvitation(project.id, secondUserEmail, ProjectRole.EDITOR, firstUserId)

        assertTrue(result is Either.Right)
    }

    @Test
    fun `createInvitation fails if user is already a collaborator`() {
        val project = projectInfoService.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            .let { (it as Either.Right).value }

        trxManager.run {
            repoCollaborators.addCollaborator(project.id, secondUserId, ProjectRole.EDITOR)
        }

        val result = invitationsService.createInvitation(project.id, secondUserEmail, ProjectRole.EDITOR, firstUserId)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.UserAlreadyInProject, (result as Either.Left).value)
    }

    @Test
    fun `createInvitation fails if user is not the project owner`() {
        val project = projectInfoService.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            .let { (it as Either.Right).value }

        val result = invitationsService.createInvitation(project.id, secondUserEmail, ProjectRole.EDITOR, secondUserId)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.InviterNotAuthorized, (result as Either.Left).value)
    }

    @Test
    fun `createInvitation fails if project does not exist`() {
        val nonExistentProjectId = UUID.randomUUID()

        val result = invitationsService.createInvitation(nonExistentProjectId, secondUserEmail, ProjectRole.EDITOR, firstUserId)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.ProjectNotFound, (result as Either.Left).value)
    }

    @Test
    fun `createInvitation fails if invitation already exists`() {
        val project = projectInfoService.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            .let { (it as Either.Right).value }

        invitationsService.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)

        val result = invitationsService.createInvitation(project.id, secondUserEmail, ProjectRole.EDITOR, firstUserId)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.InvitationAlreadyExists, (result as Either.Left).value)
    }

    @Test
    fun `getInvitationsForUser returns list`() {
        val project = projectInfoService.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            .let { (it as Either.Right).value }

        invitationsService.createInvitation(project.id, secondUserEmail, ProjectRole.EDITOR, firstUserId)

        val result = invitationsService.getInvitationsForUser(secondUserEmail)

        assertTrue(result is Either.Right)
        assertTrue((result as Either.Right).value.isNotEmpty())
    }

    @Test
    fun `modifyInvitationRole succeeds`() {
        val project = projectInfoService.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            .let { (it as Either.Right).value }

        val invitationId = invitationsService.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)
            .let { (it as Either.Right).value }

        val result = invitationsService.modifyInvitationRole(invitationId, firstUserId, ProjectRole.EDITOR)

        assertTrue(result is Either.Right)
    }

    @Test
    fun `modifyInvitationRole fails if invitation does not exist`() {
        val nonExistentInvitationId = UUID.randomUUID()

        val result = invitationsService.modifyInvitationRole(nonExistentInvitationId, firstUserId, ProjectRole.EDITOR)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.InvitationNotFound, (result as Either.Left).value)
    }

    @Test
    fun `modifyInvitationRole fails if user is not the inviter`() {
        val project = projectInfoService.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            .let { (it as Either.Right).value }

        val invitationId = invitationsService.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)
            .let { (it as Either.Right).value }

        val result = invitationsService.modifyInvitationRole(invitationId, secondUserId, ProjectRole.EDITOR)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.InviterNotAuthorized, (result as Either.Left).value)
    }

    @Test
    fun `modifyInvitationRole fails if invitation is not pending`() {
        val project = projectInfoService.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            .let { (it as Either.Right).value }

        val invitationId = invitationsService.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)
            .let { (it as Either.Right).value }

        invitationsService.respondToInvitation(invitationId, secondUserEmail, InvitationStatus.ACCEPTED)

        val result = invitationsService.modifyInvitationRole(invitationId, firstUserId, ProjectRole.EDITOR)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.InvitationAlreadyResponded, (result as Either.Left).value)
    }

    @Test
    fun `getInvitationsForProject returns list of invitations`() {
        val project = projectInfoService.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            .let { (it as Either.Right).value }

        invitationsService.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)

        val result = invitationsService.getInvitationsForProject(project.id, firstUserId)

        assertTrue(result is Either.Right)
        assertTrue((result as Either.Right).value.isNotEmpty())
    }

    @Test
    fun `getInvitationsForProject fails if project does not exist`() {
        val nonExistentProjectId = UUID.randomUUID()

        val result = invitationsService.getInvitationsForProject(nonExistentProjectId, firstUserId)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.ProjectNotFound, (result as Either.Left).value)
    }

    @Test
    fun `getInvitationsForProject fails if user is not the project owner`() {
        val project = projectInfoService.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            .let { (it as Either.Right).value }

        val result = invitationsService.getInvitationsForProject(project.id, secondUserId)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.InviterNotAuthorized, (result as Either.Left).value)
    }



    @Test
    fun `respondToInvitation with ACCEPTED adds user as collaborator`() {
        val project = projectInfoService.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            .let { (it as Either.Right).value }

        val invitationId = invitationsService.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)
            .let { (it as Either.Right).value }

        val result = invitationsService.respondToInvitation(invitationId, secondUserEmail, InvitationStatus.ACCEPTED)

        assertTrue(result is Either.Right)

        val collaborators = trxManager.run {
            repoCollaborators.getCollaborators(project.id)
        }
        assertTrue(collaborators.any { it.user.id == secondUserId })
    }

    @Test
    fun `respondToInvitation with DECLINED does not add user as collaborator`() {
        val project = projectInfoService.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            .let { (it as Either.Right).value }

        val invitationId = invitationsService.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)
            .let { (it as Either.Right).value }

        val result = invitationsService.respondToInvitation(invitationId, secondUserEmail, InvitationStatus.DECLINED)

        assertTrue(result is Either.Right)

        val collaborators = trxManager.run {
            repoCollaborators.getCollaborators(project.id)
        }
        assertTrue(collaborators.none { it.user.id == secondUserId })
    }

    @Test
    fun `respondToInvitation fails if invitation does not exist`() {
        val nonExistentInvitationId = UUID.randomUUID()

        val result = invitationsService.respondToInvitation(nonExistentInvitationId, secondUserEmail, InvitationStatus.ACCEPTED)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.InvitationNotFound, (result as Either.Left).value)
    }

    @Test
    fun `respondToInvitation fails if new status is still PENDING`() {
        val project = projectInfoService.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            .let { (it as Either.Right).value }

        val invitationId = invitationsService.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)
            .let { (it as Either.Right).value }

        val result = invitationsService.respondToInvitation(invitationId, secondUserEmail, InvitationStatus.PENDING)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.InvalidInvitationResponse, (result as Either.Left).value)
    }

    @Test
    fun `respondToInvitation fails if user is not the invited email`() {
        val project = projectInfoService.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            .let { (it as Either.Right).value }

        val invitationId = invitationsService.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)
            .let { (it as Either.Right).value }

        val result = invitationsService.respondToInvitation(invitationId, firstUserEmail, InvitationStatus.ACCEPTED)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.InviterNotAuthorized, (result as Either.Left).value)
    }

    @Test
    fun `respondToInvitation fails if invitation is not pending`() {
        val project = projectInfoService.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            .let { (it as Either.Right).value }

        val invitationId = invitationsService.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)
            .let { (it as Either.Right).value }

        invitationsService.respondToInvitation(invitationId, secondUserEmail, InvitationStatus.ACCEPTED)

        val result = invitationsService.respondToInvitation(invitationId, secondUserEmail, InvitationStatus.DECLINED)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.InvitationAlreadyResponded, (result as Either.Left).value)
    }

    @Test
    fun `deleteInvitation removes pending invitation`() {
        val project = projectInfoService.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            .let { (it as Either.Right).value }

        val invitationId = invitationsService.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)
            .let { (it as Either.Right).value }

        val result = invitationsService.deleteInvitation(invitationId, firstUserId)

        assertTrue(result is Either.Right)

        val userInvitations = invitationsService.getInvitationsForUser(secondUserEmail)
        assertTrue((userInvitations as Either.Right).value.none { it.id == invitationId })
    }

    @Test
    fun `deleteInvitation fails if invitation does not exist`() {
        val nonExistentInvitationId = UUID.randomUUID()

        val result = invitationsService.deleteInvitation(nonExistentInvitationId, firstUserId)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.InvitationNotFound, (result as Either.Left).value)
    }

    @Test
    fun `deleteInvitation fails if user is not the inviter`() {
        val project = projectInfoService.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            .let { (it as Either.Right).value }

        val invitationId = invitationsService.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)
            .let { (it as Either.Right).value }

        val result = invitationsService.deleteInvitation(invitationId, secondUserId)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.InviterNotAuthorized, (result as Either.Left).value)
    }

    @Test
    fun `deleteInvitation fails if invitation is not pending`() {
        val project = projectInfoService.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            .let { (it as Either.Right).value }

        val invitationId = invitationsService.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)
            .let { (it as Either.Right).value }

        invitationsService.respondToInvitation(invitationId, secondUserEmail, InvitationStatus.ACCEPTED)

        val result = invitationsService.deleteInvitation(invitationId, firstUserId)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.InvitationAlreadyResponded, (result as Either.Left).value)
    }

    @Test
    fun `deleteAllForProject deletes all pending invitations`() {
        val project = projectInfoService.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            .let { (it as Either.Right).value }

        invitationsService.createInvitation(project.id, secondUserEmail, ProjectRole.EDITOR, firstUserId)

        val result = invitationsService.deleteAllForProject(project.id, firstUserId)

        assertTrue(result is Either.Right)

        val invitations = invitationsService.getInvitationsForProject(project.id, firstUserId)
        assertTrue((invitations as Either.Right).value.isEmpty())
    }

    @Test
    fun `deleteAllForProject fails if project does not exist`() {
        val nonExistentProjectId = UUID.randomUUID()

        val result = invitationsService.deleteAllForProject(nonExistentProjectId, firstUserId)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.ProjectNotFound, (result as Either.Left).value)
    }

    @Test
    fun `deleteAllForProject fails if user is not the project owner`() {
        val project = projectInfoService.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            .let { (it as Either.Right).value }

        val result = invitationsService.deleteAllForProject(project.id, secondUserId)

        assertTrue(result is Either.Left)
        assertEquals(ProjectError.InviterNotAuthorized, (result as Either.Left).value)
    }
}
