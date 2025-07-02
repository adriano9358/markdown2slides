package pt.isel.markdown2slides

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*
import pt.isel.markdown2slides.model.InviteUserDTO
import pt.isel.markdown2slides.model.RespondInvitationDTO
import pt.isel.markdown2slides.model.RoleUpdateDTO
import pt.isel.markdown2slides.utils.toProblem
import java.util.*

@RestController
@RequestMapping("/api")
class ProjectInvitationsController(
    val invitationService: ProjectInvitationsService
) {

    @PostMapping("/projects/{projectId}/invitations")
    fun inviteUser(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable projectId: UUID,
        @RequestBody invite: InviteUserDTO
    ): ResponseEntity<Any> {
        val inviterId = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")

        val result = invitationService.createInvitation(projectId, invite.email, invite.role, inviterId)
        return when (result) {
            is Success -> ResponseEntity.ok(mapOf("invitationId" to result.value))
            is Failure -> result.value.toProblem().response()
        }
    }

    @GetMapping("/projects/{projectId}/invitations")
    fun getProjectInvitations(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable projectId: UUID
    ): ResponseEntity<Any> {
        val inviterId = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")

        return when (val result = invitationService.getInvitationsForProject(projectId, inviterId)) {
            is Success -> ResponseEntity.ok(result.value)
            is Failure -> result.value.toProblem().response()
        }
    }

    @DeleteMapping("/projects/{projectId}/invitations")
    fun deleteAllProjectInvitations(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable projectId: UUID
    ): ResponseEntity<Any> {
        val inviterId = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")

        val result = invitationService.deleteAllForProject(projectId, inviterId)
        return when (result) {
            is Success -> ResponseEntity.ok().build()
            is Failure -> result.value.toProblem().response()
        }
    }

    @PutMapping("/invitations/{invitationId}/role")
    fun modifyInvitationRole(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable invitationId: UUID,
        @RequestBody body: RoleUpdateDTO
    ): ResponseEntity<Any> {
        val inviterId = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")

        val result = invitationService.modifyInvitationRole(invitationId, inviterId, body.role)
        return when (result) {
            is Success -> ResponseEntity.ok().build()
            is Failure -> result.value.toProblem().response()
        }
    }

    @DeleteMapping("/invitations/{invitationId}")
    fun deleteInvitation(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable invitationId: UUID
    ): ResponseEntity<Any> {
        val inviterId = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")

        val result = invitationService.deleteInvitation(invitationId, inviterId)
        return when (result) {
            is Success -> ResponseEntity.ok().build()
            is Failure -> result.value.toProblem().response()
        }
    }

    @PostMapping("/invitations/{invitationId}/respond")
    fun respondToInvitation(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable invitationId: UUID,
        @RequestBody response: RespondInvitationDTO
    ): ResponseEntity<Any> {
        val email = principal.retrieveEmail() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")

        val result = invitationService.respondToInvitation(invitationId, email, response.status)
        return when (result) {
            is Success -> ResponseEntity.ok().build()
            is Failure -> result.value.toProblem().response()
        }
    }

    @GetMapping("/invitations")
    fun getUserInvitations(
        @AuthenticationPrincipal principal: OAuth2User,
    ): ResponseEntity<Any> {
        val email = principal.retrieveEmail() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")

        return when (val result = invitationService.getInvitationsForUser(email)) {
            is Success -> ResponseEntity.ok(result.value)
            is Failure -> result.value.toProblem().response()
        }
    }
}

fun OAuth2User.retrieveEmail(): String? {
    return this.getAttribute<String>("email")
}