package pt.isel.markdown2slides

import org.jdbi.v3.core.Handle


class RepositoryProjectInfoJdbi(
    private val handle: Handle,
) : RepositoryProjectInfo {

    override fun createProject(name: String, description: String, ownerId: Long, visibility: Visibility): ProjectInfo {
        TODO("Not yet implemented")
    }

    override fun getPersonalProjects(ownerId: Long): List<ProjectInfo> {
        TODO("Not yet implemented")
    }

    override fun findById(id: Int): ProjectInfo? {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<ProjectInfo> {
        TODO("Not yet implemented")
    }

    override fun save(entity: ProjectInfo) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

}