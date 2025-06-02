package pt.isel.markdown2slides

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*
import pt.isel.markdown2slides.model.CreateProjectDetailsDTO
import pt.isel.markdown2slides.model.UpdateProjectDetailsDTO
import pt.isel.markdown2slides.utils.toProblem
import java.util.*

val defaultUUID = UUID.fromString("3a427d49-0e7c-46d4-95c0-18ca6b34aa48")

@CrossOrigin(origins = ["http://localhost:8000"])
@RestController
@RequestMapping("/projects")
class ProjectInfoController(val projectInfoService: ProjectInfoService) {

    @GetMapping
    fun listPersonalProjects(@AuthenticationPrincipal principal: OidcUser): ResponseEntity<Any> {
        val uuid = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")

        val projects = projectInfoService.getPersonalProjects(uuid)
        return when (projects) {
            is Success -> ResponseEntity.ok(projects.value)
            is Failure -> projects.value.toProblem().response()
        }
    }

    @PostMapping
    fun createProject(
        @AuthenticationPrincipal principal: OAuth2User,
        @RequestBody projectDetails: CreateProjectDetailsDTO,
    ): ResponseEntity<Any> {
        val uuid = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")
        val visibility = if(projectDetails.visibility) Visibility.PRIVATE else Visibility.PUBLIC
        val project = projectInfoService.createProject(projectDetails.name, projectDetails.description, uuid, visibility)
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


fun OAuth2User.retrieveId(): UUID? {
    return this.getAttribute<String>("userId")?.let { userId ->
        try {
            UUID.fromString(userId)
        } catch (ex: IllegalArgumentException) {
            null
        }
    }
}