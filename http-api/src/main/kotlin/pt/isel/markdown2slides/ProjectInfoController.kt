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


@RestController
@RequestMapping("/api/projects")
class ProjectInfoController(val projectInfoService: ProjectInfoService) {

    @GetMapping
    fun listPersonalProjects(
        @AuthenticationPrincipal principal: OAuth2User
    ): ResponseEntity<Any> {
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
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable projectId: UUID,
    ): ResponseEntity<Any> {
        val uuid = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")
        val project = projectInfoService.getProjectDetails(uuid, projectId)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> project.value.toProblem().response()
        }
    }

    @PutMapping("/{id}")
    fun editProjectDetails(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable id: UUID,
        @RequestBody projectDetails: UpdateProjectDetailsDTO,
    ): ResponseEntity<Any>{
        val uuid = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")
        val visibility = when(projectDetails.visibility) {
            true -> Visibility.PRIVATE
            false -> Visibility.PUBLIC
            null -> null
        }
        val project = projectInfoService.editProjectDetails(uuid, id, projectDetails.name, projectDetails.description, visibility)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> project.value.toProblem().response()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteProject(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable id: UUID,
    ): ResponseEntity<Any> {
        val uuid = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")
        val project = projectInfoService.deleteProject(uuid, id)
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