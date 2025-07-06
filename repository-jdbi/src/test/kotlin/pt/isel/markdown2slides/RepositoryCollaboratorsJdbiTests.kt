package pt.isel.markdown2slides

import org.jdbi.v3.core.Handle
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*


class RepositoryCollaboratorsJdbiTests: JdbiTests() {

    private val firstUserId = UUID.fromString("00000000-0000-0000-0000-000000000001")
    private val firstUserName = "User1"
    private val firstUserEmail = "user1@email.com"
    private val secondUserId = UUID.fromString("00000000-0000-0000-0000-000000000002")
    private val secondUserName = "User2"
    private val secondUserEmail = "user2@email.com"

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
    fun `test adding a collaborator`(){
        runWithHandle { handle ->
            val collaboratorsRepo = RepositoryCollaboratorsJdbi(handle)

            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            addUser(handle, secondUserId, secondUserName, secondUserEmail)
            val project = addProject(handle, "Project", "Project Description", firstUserId, Visibility.PRIVATE)

            collaboratorsRepo.addCollaborator(project.id, secondUserId, ProjectRole.EDITOR)

            val collaborators = collaboratorsRepo.getCollaborators(project.id)
            assert(collaborators.size == 1) { "There should be one collaborator" }
            assert(collaborators.any{
                it.user.id == secondUserId &&
                it.user.name == secondUserName &&
                it.user.email == secondUserEmail &&
                it.role == ProjectRole.EDITOR &&
                it.project_id == project.id
            }) { "Collaborator should be added successfully" }
        }
    }

    @Test
    fun `test adding multiple collaborators`(){
        runWithHandle { handle ->
            val collaboratorsRepo = RepositoryCollaboratorsJdbi(handle)

            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            addUser(handle, secondUserId, secondUserName, secondUserEmail)
            val project = addProject(handle, "Project", "Project Description", firstUserId, Visibility.PRIVATE)

            collaboratorsRepo.addCollaborator(project.id, firstUserId, ProjectRole.ADMIN)
            collaboratorsRepo.addCollaborator(project.id, secondUserId, ProjectRole.EDITOR)

            val collaborators = collaboratorsRepo.getCollaborators(project.id)
            assert(collaborators.size == 2) { "There should be two collaborators" }
            assert(collaborators.any{ it.user.id == firstUserId && it.role == ProjectRole.ADMIN }) { "First user should be an OWNER" }
            assert(collaborators.any{ it.user.id == secondUserId && it.role == ProjectRole.EDITOR }) { "Second user should be an EDITOR" }
        }
    }

    @Test
    fun `test adding a collaborator more than once with different role updates the role`(){
        runWithHandle { handle ->
            val collaboratorsRepo = RepositoryCollaboratorsJdbi(handle)

            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            addUser(handle, secondUserId, secondUserName, secondUserEmail)
            val project = addProject(handle, "Project", "Project Description", firstUserId, Visibility.PRIVATE)

            collaboratorsRepo.addCollaborator(project.id, secondUserId, ProjectRole.EDITOR)

            collaboratorsRepo.addCollaborator(project.id, secondUserId, ProjectRole.VIEWER)

            val collaborators = collaboratorsRepo.getCollaborators(project.id)
            assert(collaborators.any{
                it.user.id == secondUserId &&
                it.user.name == secondUserName &&
                it.user.email == secondUserEmail &&
                it.role == ProjectRole.VIEWER &&
                it.project_id == project.id
            }) { "Collaborator's role should be updated successfully" }
        }
    }

    @Test
    fun `test getting the role of a user in a project`(){
        runWithHandle { handle ->
            val collaboratorsRepo = RepositoryCollaboratorsJdbi(handle)

            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            addUser(handle, secondUserId, secondUserName, secondUserEmail)
            val project = addProject(handle, "Project", "Project Description", firstUserId, Visibility.PRIVATE)

            collaboratorsRepo.addCollaborator(project.id, secondUserId, ProjectRole.EDITOR)

            val role = collaboratorsRepo.getUserRoleInProject(secondUserId, project.id)
            assert(role == ProjectRole.EDITOR) { "Role should be EDITOR" }

            collaboratorsRepo.addCollaborator(project.id, secondUserId, ProjectRole.VIEWER)

            val updatedRole = collaboratorsRepo.getUserRoleInProject(secondUserId, project.id)
            assert(updatedRole == ProjectRole.VIEWER) { "Role should be updated to VIEWER" }
        }
    }

    @Test
    fun `test getting the role of a user in a project when user is not a collaborator`(){
        runWithHandle { handle ->
            val collaboratorsRepo = RepositoryCollaboratorsJdbi(handle)

            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            addUser(handle, secondUserId, secondUserName, secondUserEmail)
            val project = addProject(handle, "Project", "Project Description", firstUserId, Visibility.PRIVATE)

            val role = collaboratorsRepo.getUserRoleInProject(secondUserId, project.id)
            assert(role == null) { "Role should be null when user is not a collaborator" }
        }
    }

    @Test
    fun `test removing a collaborator`(){
        runWithHandle { handle ->
            val collaboratorsRepo = RepositoryCollaboratorsJdbi(handle)

            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            addUser(handle, secondUserId, secondUserName, secondUserEmail)
            val project = addProject(handle, "Project", "Project Description", firstUserId, Visibility.PRIVATE)

            collaboratorsRepo.addCollaborator(project.id, secondUserId, ProjectRole.EDITOR)

            collaboratorsRepo.removeCollaborator(project.id, secondUserId)

            val collaborators = collaboratorsRepo.getCollaborators(project.id)
            assert(collaborators.isEmpty()) { "There should be no collaborators after removal" }
        }
    }

    @Test
    fun `test removing a collaborator that does not exist does not throw an error`(){
        runWithHandle { handle ->
            val collaboratorsRepo = RepositoryCollaboratorsJdbi(handle)

            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            addUser(handle, secondUserId, secondUserName, secondUserEmail)
            val project = addProject(handle, "Project", "Project Description", firstUserId, Visibility.PRIVATE)

            collaboratorsRepo.removeCollaborator(project.id, secondUserId)

            assert(true)
        }
    }

    @Test
    fun `test clearing collaborators`(){
        runWithHandle { handle ->
            val collaboratorsRepo = RepositoryCollaboratorsJdbi(handle)

            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            addUser(handle, secondUserId, secondUserName, secondUserEmail)
            val project = addProject(handle, "Project", "Project Description", firstUserId, Visibility.PRIVATE)

            collaboratorsRepo.addCollaborator(project.id, firstUserId, ProjectRole.ADMIN)
            collaboratorsRepo.addCollaborator(project.id, secondUserId, ProjectRole.EDITOR)

            collaboratorsRepo.clear()

            val collaborators = collaboratorsRepo.getCollaborators(project.id)
            assert(collaborators.isEmpty()) { "There should be no collaborators after clearing" }
        }
    }

    @Test
    fun `test getting the ids of the projects the user is a collaborator in`(){
        runWithHandle { handle ->
            val collaboratorsRepo = RepositoryCollaboratorsJdbi(handle)

            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            addUser(handle, secondUserId, secondUserName, secondUserEmail)
            val project1 = addProject(handle, "Project1", "Project Description 1", firstUserId, Visibility.PRIVATE)
            val project2 = addProject(handle, "Project2", "Project Description 2", firstUserId, Visibility.PRIVATE)

            val userProjectsBefore = collaboratorsRepo.getUserProjects(secondUserId)
            assert(userProjectsBefore.isEmpty()) { "There should be no projects for the user initially" }

            collaboratorsRepo.addCollaborator(project1.id, secondUserId, ProjectRole.EDITOR)
            collaboratorsRepo.addCollaborator(project2.id, secondUserId, ProjectRole.VIEWER)

            val userProjects = collaboratorsRepo.getUserProjects(secondUserId)
            assert(userProjects.size == 2) { "There should be two projects for the user" }
            assert(userProjects.contains(project1.id)) { "First project should be in the user's projects" }
            assert(userProjects.contains(project2.id)) { "Second project should be in the user's projects" }
        }
    }




}