package pt.isel.markdown2slides

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.markdown2slides.model.CreateProjectDetailsDTO
import pt.isel.markdown2slides.model.UpdateProjectDetailsDTO
import pt.isel.markdown2slides.utils.toProblem
import java.util.*

val defaultUUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

@RestController
@RequestMapping("/projects")
class ProjectInfoController(val projectInfoService: ProjectInfoService) {

    @GetMapping
    fun listPersonalProjects(): ResponseEntity<Any> {
        val projects = projectInfoService.getPersonalProjects(defaultUUID)
        return when (projects) {
            is Success -> ResponseEntity.ok(projects.value)
            is Failure -> projects.value.toProblem().response()
        }
    }

    @PostMapping
    fun createProject(
        @RequestBody projectDetails: CreateProjectDetailsDTO,
    ): ResponseEntity<Any> {
        val visibility = if(projectDetails.visibility) Visibility.PRIVATE else Visibility.PUBLIC
        val project = projectInfoService.createProject(projectDetails.name, projectDetails.description, defaultUUID, visibility)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> project.value.toProblem().response()
        }
    }

    @GetMapping("/{id}")
    fun getProjectDetails(
        @PathVariable projectId: UUID,
    ): ResponseEntity<Any> {
        val project = projectInfoService.getProjectDetails(defaultUUID, projectId)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> project.value.toProblem().response()
        }
    }

    @PutMapping("/{id}")
    fun editProjectDetails(
        @PathVariable id: UUID,
        @RequestBody projectDetails: UpdateProjectDetailsDTO,
    ): ResponseEntity<Any>{
        val visibility = when(projectDetails.visibility) {
            true -> Visibility.PRIVATE
            false -> Visibility.PUBLIC
            null -> null
        }
        val project = projectInfoService.editProjectDetails(defaultUUID, id, projectDetails.name, projectDetails.description, visibility)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> project.value.toProblem().response()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteProject(
        @PathVariable id: UUID,
    ): ResponseEntity<Any> {
        val project = projectInfoService.deleteProject(defaultUUID, id)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> project.value.toProblem().response()
        }
    }

}
