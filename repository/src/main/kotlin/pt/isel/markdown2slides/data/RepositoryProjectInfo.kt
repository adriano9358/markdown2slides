package pt.isel.markdown2slides.data

import pt.isel.markdown2slides.ProjectInfo
import pt.isel.markdown2slides.Visibility
import java.util.*

interface RepositoryProjectInfo: Repository<ProjectInfo> {
    fun createProject(name: String, description: String, ownerId: UUID, visibility: Visibility): ProjectInfo
    fun getPersonalProjects(ownerId: UUID): List<ProjectInfo>
}