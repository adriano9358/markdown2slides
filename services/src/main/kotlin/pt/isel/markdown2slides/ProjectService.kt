package pt.isel.markdown2slides

import jakarta.inject.Named
import pt.isel.markdown2slides.mem.TransactionManagerInMem
import java.time.Instant




@Named
class ProjectInfoService(private val trxManager: TransactionManagerInMem, private val fileSys: RepositoryProjectContent) {

    fun getPersonalProjects(ownerId: Long): Either<ProjectInfoError, List<ProjectInfo>> = trxManager.run {
        val projects = repoProjectInfo.getPersonalProjects(ownerId)
        success(projects)
    }


    fun deleteProject(
        projectId: Long
    ): Either<ProjectInfoError, Boolean> = trxManager.run {
        val a = repoProjectInfo.deleteById(projectId.toInt())
        val b = fileSys.deleteMarkdown(projectId.toString())
        if(b) success(b) else failure(ProjectInfoError.SomeInfoError)
    }

    fun editProjectDetails(
        projectId: Long,
        name: String?,
        description: String?,
        visibility: Visibility?
    ): Either<ProjectInfoError, ProjectInfo> {
        TODO("Not yet implemented")
    }

    fun getProjectDetails(
        projectId: Long
    ): Either<ProjectInfoError, ProjectInfo> {
        TODO("Not yet implemented")
    }

    fun createProject(
        name: String,
        description: String,
        ownerId: Long,
        visibility: Visibility
    ): Either<ProjectInfoError, ProjectInfo> = trxManager.run {
        val a = repoProjectInfo.createProject(name, description, ownerId, visibility)
        success(a)
    }
}

sealed class ProjectInfoError {
    data object SomeInfoError : ProjectInfoError()
}