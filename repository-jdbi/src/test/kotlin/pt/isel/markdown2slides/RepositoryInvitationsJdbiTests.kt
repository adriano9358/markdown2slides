package pt.isel.markdown2slides

import org.jdbi.v3.core.Handle
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class RepositoryInvitationsJdbiTests: JdbiTests() {

    private val firstUserId = UUID.fromString("00000000-0000-0000-0000-000000000001")
    private val firstUserName = "User1"
    private val firstUserEmail = "user1@email.com"
    private val secondUserId = UUID.fromString("00000000-0000-0000-0000-000000000002")
    private val secondUserName = "User2"
    private val secondUserEmail = "user2@email.com"
    private val projectName = "Test Project"
    private val projectDescription = "A test project description"
    private val projectVisibility = Visibility.PUBLIC

    @BeforeEach
    fun clean() {
        runWithHandle { handle: Handle ->
            RepositoryCollaboratorsJdbi(handle).clear()
            RepositoryInvitationsJdbi(handle).clear()
            RepositoryProjectInfoJdbi(handle).clear()
            RepositoryUserJdbi(handle).clear()
        }
    }

    @Test
    fun `test create invitation`() {
        runWithHandle { handle ->
            val repo = RepositoryInvitationsJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            val project = addProject(handle, "Project 1", "Project 1 Description", firstUserId, projectVisibility)

            val id = repo.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)

            val invitation = repo.getInvitationById(id)

            assert(invitation != null)
            assert(invitation!!.id == id)
            assert(invitation.project_id == project.id)
            assert(invitation.email == secondUserEmail)
            assert(invitation.role == ProjectRole.VIEWER)
            assert(invitation.invited_by == firstUserId)
            assert(invitation.status == InvitationStatus.PENDING)
        }
    }


    @Test
    fun `test get invitation that does not exist by id`() {
        runWithHandle { handle ->
            val repo = RepositoryInvitationsJdbi(handle)
            val invitation = repo.getInvitationById(UUID.randomUUID())
            assert(invitation == null)
        }
    }

    @Test
    fun `test get invitation by email and project`() {
        runWithHandle { handle ->
            val repo = RepositoryInvitationsJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            addUser(handle, secondUserId, secondUserName, secondUserEmail)
            val project = addProject(handle, projectName, projectDescription, firstUserId, projectVisibility)

            val invitationId = repo.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)

            val invitation = repo.getInvitationByEmailAndProject(secondUserEmail, project.id)

            assert(invitation != null)
            assert(invitation!!.id == invitationId)
            assert(invitation.project_id == project.id)
            assert(invitation.email == secondUserEmail)
            assert(invitation.role == ProjectRole.VIEWER)
            assert(invitation.invited_by == firstUserId)
            assert(invitation.status == InvitationStatus.PENDING)
        }
    }

    @Test
    fun `test get invitation by email and project that does not exist`() {
        runWithHandle { handle ->
            val repo = RepositoryInvitationsJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            val project = addProject(handle, projectName, projectDescription, firstUserId, projectVisibility)

            val invitation = repo.getInvitationByEmailAndProject(secondUserEmail, project.id)

            assert(invitation == null)
        }
    }

    @Test
    fun `test update role of invitation`() {
        runWithHandle { handle ->
            val repo = RepositoryInvitationsJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            val project = addProject(handle, projectName, projectDescription, firstUserId, projectVisibility)

            val invitationId = repo.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)

            repo.updateRole(invitationId, ProjectRole.EDITOR)

            val updatedInvitation = repo.getInvitationById(invitationId)

            assert(updatedInvitation != null)
            assert(updatedInvitation!!.role == ProjectRole.EDITOR)
        }
    }

    @Test
    fun `test update the status of invitation to ACCEPTED`() {
        runWithHandle { handle ->
            val repo = RepositoryInvitationsJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            val project = addProject(handle, projectName, projectDescription, firstUserId, projectVisibility)

            val invitationId = repo.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)

            repo.updateStatus(invitationId, InvitationStatus.ACCEPTED)

            val updatedInvitation = repo.getInvitationById(invitationId)

            assert(updatedInvitation != null)
            assert(updatedInvitation!!.status == InvitationStatus.ACCEPTED)
        }
    }

    @Test
    fun `test update the status of invitation to DECLINED`() {
        runWithHandle { handle ->
            val repo = RepositoryInvitationsJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            val project = addProject(handle, projectName, projectDescription, firstUserId, projectVisibility)

            val invitationId = repo.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)

            repo.updateStatus(invitationId, InvitationStatus.DECLINED)

            val updatedInvitation = repo.getInvitationById(invitationId)

            assert(updatedInvitation != null)
            assert(updatedInvitation!!.status == InvitationStatus.DECLINED)
        }
    }

    @Test
    fun `test delete invitation by id`() {
        runWithHandle { handle ->
            val repo = RepositoryInvitationsJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            val project = addProject(handle, projectName, projectDescription, firstUserId, projectVisibility)

            val invitationId = repo.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)

            repo.deleteInvitation(invitationId)

            val deletedInvitation = repo.getInvitationById(invitationId)

            assert(deletedInvitation == null)
        }
    }

    @Test
    fun `test delete answered invitation by id`() {
        runWithHandle { handle ->
            val repo = RepositoryInvitationsJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            val project = addProject(handle, projectName, projectDescription, firstUserId, projectVisibility)

            val invitationId = repo.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)

            repo.updateStatus(invitationId, InvitationStatus.ACCEPTED)

            repo.deleteAnsweredInvitations(project.id, secondUserEmail)

            val deletedInvitation = repo.getInvitationById(invitationId)

            assert(deletedInvitation == null)
        }
    }

    @Test
    fun `test delete all answered invitations of a project`() {
        runWithHandle { handle ->
            val repo = RepositoryInvitationsJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            addUser(handle, secondUserId, secondUserName, secondUserEmail)
            val project = addProject(handle, projectName, projectDescription, firstUserId, projectVisibility)

            val invitation1 = repo.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)
            val invitation2 = repo.createInvitation(project.id, secondUserEmail, ProjectRole.EDITOR, firstUserId)

            repo.updateStatus(invitation1, InvitationStatus.ACCEPTED)

            repo.deleteAnsweredInvitations(project.id, secondUserEmail)

            assert(repo.getInvitationById(invitation1) == null)
            assert(repo.getInvitationById(invitation2) != null)
        }
    }

    @Test
    fun `test delete unanswered invitations of a project`() {
        runWithHandle { handle ->
            val repo = RepositoryInvitationsJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            addUser(handle, secondUserId, secondUserName, secondUserEmail)
            val project = addProject(handle, projectName, projectDescription, firstUserId, projectVisibility)

            val invitation1 = repo.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)
            val invitation2 = repo.createInvitation(project.id, secondUserEmail, ProjectRole.EDITOR, firstUserId)
            repo.updateStatus(invitation1, InvitationStatus.ACCEPTED)

            repo.deleteAllForProject(project.id)

            assert(repo.getInvitationById(invitation1) != null)
            assert(repo.getInvitationById(invitation2) == null)
        }
    }

    @Test
    fun `test clearing all invitations`() {
        runWithHandle { handle ->
            val repo = RepositoryInvitationsJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            addUser(handle, secondUserId, secondUserName, secondUserEmail)
            val project = addProject(handle, projectName, projectDescription, firstUserId, projectVisibility)

            val invitation1 = repo.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)
            val invitation2 = repo.createInvitation(project.id, secondUserEmail, ProjectRole.EDITOR, firstUserId)
            repo.updateStatus(invitation1, InvitationStatus.ACCEPTED)

            repo.clear()

            assert(repo.getInvitationById(invitation1) == null)
            assert(repo.getInvitationById(invitation2) == null)
        }
    }

    @Test
    fun `test getting all invitations for project`() {
        runWithHandle { handle ->
            val repo = RepositoryInvitationsJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            addUser(handle, secondUserId, secondUserName, secondUserEmail)
            val project = addProject(handle, projectName, projectDescription, firstUserId, projectVisibility)

            val invitation1 = repo.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)
            val invitation2 = repo.createInvitation(project.id, secondUserEmail, ProjectRole.EDITOR, firstUserId)

            val invitations = repo.getInvitationsForProject(project.id)

            assert(invitations.size == 2)
            assert(invitations.any { it.id == invitation1 })
            assert(invitations.any { it.id == invitation2 })
        }
    }

    @Test
    fun `test getting all invitations for user`() {
        runWithHandle { handle ->
            val repo = RepositoryInvitationsJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            addUser(handle, secondUserId, secondUserName, secondUserEmail)
            val project = addProject(handle, projectName, projectDescription, firstUserId, projectVisibility)
            val project2 = addProject(handle, "Project 2", "Another project description", firstUserId, projectVisibility)

            val invitation1 = repo.createInvitation(project.id, secondUserEmail, ProjectRole.VIEWER, firstUserId)
            val invitation2 = repo.createInvitation(project2.id, secondUserEmail, ProjectRole.EDITOR, firstUserId)

            val invitations = repo.getInvitationsForUser(secondUserEmail)

            assert(invitations.size == 2)
            assert(invitations.any { it.id == invitation1 })
            assert(invitations.any { it.id == invitation2 })
        }
    }
}