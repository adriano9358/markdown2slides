package pt.isel.markdown2slides

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*
import pt.isel.markdown2slides.model.AddCollaboratorDTO
import pt.isel.markdown2slides.model.RoleUpdateDTO
import pt.isel.markdown2slides.utils.toProblem
import java.util.*

@RestController
@CrossOrigin(origins = ["http://localhost:8000"])
class ProjectCollaboratorsController(
    val collaboratorService: ProjectCollaboratorsService
) {
/*
    @PostMapping
    fun addCollaborator(
        @PathVariable projectId: UUID,
        @RequestBody dto: AddCollaboratorDTO
    ): ResponseEntity<Any> {
        val role = try {
            ProjectRole.fromString(dto.role)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body("Invalid role: ${dto.role}")
        }

        val result = collaboratorService.addCollaborator(projectId, dto.userId, role)
        return when (result) {
            is Success -> ResponseEntity.ok().build()
            is Failure -> result.value.toProblem().response()
        }
    }*/

    @GetMapping("/projects/{projectId}/collaborators")
    fun listCollaborators(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable projectId: UUID
    ): ResponseEntity<Any> {
        val userId = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")

        val result = collaboratorService.getCollaborators(userId, projectId)
        return when (result) {
            is Success -> ResponseEntity.ok(result.value)
            is Failure -> result.value.toProblem().response()
        }
    }

    @GetMapping("/projects/{projectId}/role")
    fun getUserRoleInProject(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable projectId: UUID
    ): ResponseEntity<Any> {
        val userId = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")

        val result = collaboratorService.getUserRoleInProject(userId, projectId)
        return when (result) {
            is Success -> ResponseEntity.ok(RoleUpdateDTO(result.value))
            is Failure -> result.value.toProblem().response()
        }
    }

    @DeleteMapping("/projects/{projectId}/collaborators/{userId}")
    fun removeCollaborator(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable projectId: UUID,
        @PathVariable userId: UUID
    ): ResponseEntity<Any> {
        val ownerId = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")

        val result = collaboratorService.removeCollaborator(ownerId, projectId, userId)
        return when (result) {
            is Success -> ResponseEntity.ok().build()
            is Failure -> result.value.toProblem().response()
        }
    }
}
