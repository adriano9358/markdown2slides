package pt.isel.markdown2slides

import org.jdbi.v3.core.Handle
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class RepositoryProjectInfoJdbiTests: JdbiTests() {

    private val firstUserId = UUID.fromString("00000000-0000-0000-0000-000000000001")
    private val firstUserName = "User1"
    private val firstUserEmail = "user1@email.com"
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
    fun `test creating a project`() {
        runWithHandle { handle ->
            val repo = RepositoryProjectInfoJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)

            val project = repo.createProject(projectName, projectDescription, firstUserId, projectVisibility)

            assert(project.name == projectName)
            assert(project.description == projectDescription)
            assert(project.ownerId == firstUserId)
            assert(project.visibility == projectVisibility)
        }
    }

    @Test
    fun `test getting a project by id`() {
        runWithHandle { handle ->
            val repo = RepositoryProjectInfoJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)

            val createdProject = repo.createProject(projectName, projectDescription, firstUserId, projectVisibility)
            val fetchedProject = repo.findById(createdProject.id)

            assert(fetchedProject != null)
            assert(fetchedProject!!.id == createdProject.id)
            assert(fetchedProject.name == createdProject.name)
            assert(fetchedProject.description == createdProject.description)
            assert(fetchedProject.ownerId == createdProject.ownerId)
            assert(fetchedProject.visibility == createdProject.visibility)
        }
    }

    @Test
    fun `test getting a project by id that does not exist`() {
        runWithHandle { handle ->
            val repo = RepositoryProjectInfoJdbi(handle)
            val nonExistentId = UUID.randomUUID()
            val fetchedProject = repo.findById(nonExistentId)

            assert(fetchedProject == null)
        }
    }

    @Test
    fun `test getting all the projects`() {
        runWithHandle { handle ->
            val repo = RepositoryProjectInfoJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)

            val allProjectsBefore = repo.findAll()
            assert(allProjectsBefore.isEmpty())

            val project1 = repo.createProject("Project 1", "Description 1", firstUserId, projectVisibility)
            val project2 = repo.createProject("Project 2", "Description 2", firstUserId, projectVisibility)

            val allProjects = repo.findAll()

            assert(allProjects.size == 2)
            assert(allProjects.any { it.id == project1.id && it.name == project1.name && it.description == project1.description && it.ownerId == project1.ownerId && it.visibility == project1.visibility })
            assert(allProjects.any { it.id == project2.id && it.name == project2.name && it.description == project2.description && it.ownerId == project2.ownerId && it.visibility == project2.visibility })
        }
    }

    @Test
    fun `test updating project visibility`() {
        runWithHandle { handle ->
            val repo = RepositoryProjectInfoJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)

            val project = repo.createProject(projectName, projectDescription, firstUserId, projectVisibility)

            val newVisibility = Visibility.PRIVATE
            repo.save(project.copy(
                visibility = newVisibility
            ))

            val updatedProject = repo.findById(project.id)
            assert(updatedProject != null)
            assert(updatedProject!!.visibility == newVisibility)
        }
    }

    @Test
    fun `test updating project name and description`() {
        runWithHandle { handle ->
            val repo = RepositoryProjectInfoJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)

            val project = repo.createProject(projectName, projectDescription, firstUserId, projectVisibility)

            val newName = "Updated Project Name"
            val newDescription = "Updated Project Description"
            repo.save(project.copy(
                name = newName,
                description = newDescription
            ))

            val updatedProject = repo.findById(project.id)
            assert(updatedProject != null)
            assert(updatedProject!!.name == newName)
            assert(updatedProject.description == newDescription)
        }
    }

    @Test
    fun `test deleting project by id`() {
        runWithHandle { handle ->
            val repo = RepositoryProjectInfoJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)

            val project = repo.createProject(projectName, projectDescription, firstUserId, projectVisibility)

            repo.deleteById(project.id)

            val deletedProject = repo.findById(project.id)
            assert(deletedProject == null)

            val allProjects = repo.findAll()
            assert(allProjects.isEmpty())
        }
    }

    @Test
    fun `test deleting project that does not exist`() {
        runWithHandle { handle ->
            val repo = RepositoryProjectInfoJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)

            val nonExistentId = UUID.randomUUID()
            repo.deleteById(nonExistentId)

            assert(true)
        }
    }

    @Test
    fun `test clearing all projects`() {
        runWithHandle { handle ->
            val repo = RepositoryProjectInfoJdbi(handle)
            addUser(handle, firstUserId, firstUserName, firstUserEmail)

            repo.createProject("Project 1", "Description 1", firstUserId, projectVisibility)
            repo.createProject("Project 2", "Description 2", firstUserId, projectVisibility)

            val allProjectsBeforeClear = repo.findAll()
            assert(allProjectsBeforeClear.size == 2)

            repo.clear()

            val allProjectsAfterClear = repo.findAll()
            assert(allProjectsAfterClear.isEmpty())
        }
    }

    @Test
    fun `test getting personal projects`() {
        runWithHandle { handle ->
            val repo = RepositoryProjectInfoJdbi(handle)

            addUser(handle, firstUserId, firstUserName, firstUserEmail)
            val secondUser = User(UUID.randomUUID(), "User2", "user2@email.com")
            addUser(handle, secondUser.id, secondUser.name, secondUser.email)


            val project1 = repo.createProject("Project 1", "Description 1", firstUserId, projectVisibility)
            addCollaborator(handle, project1.id, firstUserId, ProjectRole.ADMIN)

            val project2 = repo.createProject("Project 2", "Description 2", secondUser.id, projectVisibility)
            addCollaborator(handle, project2.id, firstUserId, ProjectRole.EDITOR)

            val project3 = repo.createProject("Project 3", "Description 3", secondUser.id, projectVisibility)
            addCollaborator(handle, project3.id, firstUserId, ProjectRole.VIEWER)

            val personalProjects = repo.getPersonalProjects(firstUserId)

            assert(personalProjects.size == 3)
            assert(personalProjects.any{ it.id == project1.id && it.name == project1.name && it.description == project1.description && it.ownerId == project1.ownerId && it.visibility == project1.visibility})
            assert(personalProjects.any{ it.id == project2.id && it.name == project2.name && it.description == project2.description && it.ownerId == secondUser.id && it.visibility == project2.visibility})
            assert(personalProjects.any{ it.id == project3.id && it.name == project3.name && it.description == project3.description && it.ownerId == secondUser.id && it.visibility == project3.visibility})
        }
    }

    @Test
    fun `test getting personal projects for a user that does not exist`() {
        runWithHandle { handle ->
            val repo = RepositoryProjectInfoJdbi(handle)

            val nonExistentUserId = UUID.randomUUID()
            val personalProjects = repo.getPersonalProjects(nonExistentUserId)

            assert(personalProjects.isEmpty()) { "There should be no projects for a non-existent user" }
        }
    }
}