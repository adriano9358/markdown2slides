package pt.isel.markdown2slides

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import java.util.*

abstract class JdbiTests {

    private val jdbi = Jdbi.create(
        PGSimpleDataSource().apply { setURL(EnvironmentTest.getDbUrl())
    }).configureWithAppRequirements()

    protected fun runWithHandle(block: (Handle) -> Unit) = jdbi.useTransaction<Exception>(block)

    protected fun addUser(handle: Handle, userId: UUID, name: String, email: String) {
        val userRepo = RepositoryUserJdbi(handle)
        userRepo.save(User(userId, name, email))
    }

    protected fun addProject(handle: Handle, name: String, description: String, ownerId: UUID, visibility: Visibility): ProjectInfo {
        val projectRepo = RepositoryProjectInfoJdbi(handle)
        return projectRepo.createProject(name, description, ownerId, visibility)
    }

    protected fun addCollaborator(handle: Handle, projectId: UUID, userId: UUID, role: ProjectRole) {
        val collaboratorsRepo = RepositoryCollaboratorsJdbi(handle)
        collaboratorsRepo.addCollaborator(projectId, userId, role)
    }
}
