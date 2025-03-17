package pt.isel.markdown2slides

import org.springframework.http.HttpStatus
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
import pt.isel.markdown2slides.model.Problem
import pt.isel.markdown2slides.model.UpdateProjectDetailsDTO

@RestController
@RequestMapping("/projects")
class ProjectInfoController(val projectService: ProjectInfoService) {

    @GetMapping
    fun listPersonalProjects(): ResponseEntity<Any> {
        val projects = projectService.getPersonalProjects(1L)
        return when (projects) {
            is Success -> ResponseEntity.ok(projects.value)
            is Failure -> Problem.ConversionProcessFailure.response(HttpStatus.SERVICE_UNAVAILABLE)
        }
    }

    @PostMapping
    fun createProject(
        @RequestBody projectDetails: CreateProjectDetailsDTO,
    ): ResponseEntity<Any> {
        val visibility = if(projectDetails.visibility) Visibility.PRIVATE else Visibility.PUBLIC
        val project = projectService.createProject(projectDetails.name, projectDetails.description, 1L, visibility)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> Problem.ConversionProcessFailure.response(HttpStatus.SERVICE_UNAVAILABLE)
        }
    }

    @GetMapping("/{id}")
    fun getProjectDetails(
        @PathVariable projectId: Long,
    ): ResponseEntity<Any> {
        val project = projectService.getProjectDetails(projectId)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> Problem.ConversionProcessFailure.response(HttpStatus.SERVICE_UNAVAILABLE)
        }
    }

    @PutMapping("/{id}")
    fun editProjectDetails(
        @PathVariable id: Long,
        @RequestBody projectDetails: UpdateProjectDetailsDTO,
    ): ResponseEntity<Any>{
        val project = projectService.editProjectDetails(id, projectDetails.name, projectDetails.description, projectDetails.visibility)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> Problem.ConversionProcessFailure.response(HttpStatus.SERVICE_UNAVAILABLE)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteProject(
        @PathVariable id: Long,
    ): ResponseEntity<Any> {
        val project = projectService.deleteProject(id)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> Problem.ConversionProcessFailure.response(HttpStatus.SERVICE_UNAVAILABLE)
        }
    }

}
