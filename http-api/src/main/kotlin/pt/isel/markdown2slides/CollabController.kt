package pt.isel.markdown2slides

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlinx.coroutines.*
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import pt.isel.markdown2slides.model.PushRequestBody
import pt.isel.markdown2slides.utils.CursorInfo
import pt.isel.markdown2slides.utils.toProblem
import java.util.*


@RestController
@RequestMapping("/api/collab")
class CollabController(val authorityService: AuthorityService) {

    @GetMapping("/{projectId}")
    fun getInitial(@PathVariable projectId: UUID, @AuthenticationPrincipal principal: OAuth2User): ResponseEntity<Any> {
        val userId = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")
        val result = authorityService.getDocument(projectId, userId)
        return when (result) {
            is Success -> ResponseEntity.ok(result.value)
            is Failure -> result.value.toProblem().response()
        }
    }

    @GetMapping("/{projectId}/updates/{version}")
    suspend fun getUpdates(@PathVariable projectId: UUID, @PathVariable version: Int, @AuthenticationPrincipal principal: OAuth2User): ResponseEntity<Any> {
        val userId = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")
        val updates = authorityService.pullUpdates(projectId, userId, version)
        return when (updates) {
            is Success -> ResponseEntity.ok(updates.value)
            is Failure -> updates.value.toProblem().response()
        }
    }

    @PostMapping("/{projectId}/updates/{version}")
    fun postUpdates(@PathVariable projectId: UUID, @PathVariable version: Int, @RequestBody body: PushRequestBody, @AuthenticationPrincipal principal: OAuth2User): ResponseEntity<Any> {
        val userId = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")
        val result = authorityService.pushUpdates(projectId, userId, version, body.updates)
        return when (result) {
            is Success -> ResponseEntity.ok(result.value)
            is Failure -> result.value.toProblem().response()
        }
    }

    @PutMapping("/{projectId}/cursor/{userId}")
    fun putCursor(
        @PathVariable projectId: UUID,
        @PathVariable userId: String,
        @RequestBody cursorInfo: CursorInfo,
        @AuthenticationPrincipal principal: OAuth2User
    ): ResponseEntity<Any> {
        val userId = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")
        val others = authorityService.updateCursor(projectId, userId, cursorInfo)
        return when (others) {
            is Success -> ResponseEntity.ok(others.value)
            is Failure -> others.value.toProblem().response()
        }
    }
}

