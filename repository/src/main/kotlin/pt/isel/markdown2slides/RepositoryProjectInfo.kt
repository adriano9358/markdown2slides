package pt.isel.markdown2slides

import java.util.*

interface RepositoryProjectInfo: Repository<ProjectInfo> {
    fun createProject(name: String, description: String, ownerId: UUID, visibility: Visibility): ProjectInfo
    fun getPersonalProjects(ownerId: UUID): List<ProjectInfo>
}