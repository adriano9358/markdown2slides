package pt.isel.markdown2slides

interface RepositoryProjectInfo: Repository<ProjectInfo> {
    fun createProject(name: String, description: String, ownerId: Long, visibility: Visibility): ProjectInfo
    fun getPersonalProjects(ownerId: Long): List<ProjectInfo>
}