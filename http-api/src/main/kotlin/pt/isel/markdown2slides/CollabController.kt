package pt.isel.markdown2slides

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import pt.isel.markdown2slides.model.PushRequestBody
import pt.isel.markdown2slides.utils.CursorInfo
import java.util.*


@RestController
@RequestMapping("/api/collab")
class CollabController(val authorityService: AuthorityService) {

    private val logger = LoggerFactory.getLogger(CollabController::class.java)

    @GetMapping("/{projectId}")
    fun getInitial(@PathVariable projectId: UUID, @AuthenticationPrincipal principal: OAuth2User): ResponseEntity<Any> {
        logger.info("Getting initial document for project $projectId")
        val userId = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")

        return ResponseEntity.ok(authorityService.getDocument(projectId, userId))
    }

    @GetMapping("/{projectId}/updates/{version}")
    suspend fun getUpdates(@PathVariable projectId: UUID, @PathVariable version: Int, @AuthenticationPrincipal principal: OAuth2User): ResponseEntity<Any> {
        logger.info("Pulling updates for project $projectId since version $version")
        val userId = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")
        val updates = authorityService.pullUpdates(projectId, userId, version)
        return ResponseEntity.ok(updates)
    }

    @PostMapping("/{projectId}/updates/{version}")
    fun postUpdates(@PathVariable projectId: UUID, @PathVariable version: Int, @RequestBody body: PushRequestBody, @AuthenticationPrincipal principal: OAuth2User): ResponseEntity<Any> {
        logger.info("Pushing updates for project $projectId by user ${body.updates.firstOrNull()} with version $version")
        val userId = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")
        val result = authorityService.pushUpdates(projectId, userId, version, body.updates)
        return ResponseEntity.ok(result)
    }

    @PutMapping("/{projectId}/cursor/{userId}")
    fun putCursor(
        @PathVariable projectId: UUID,
        @PathVariable userId: String,
        @RequestBody cursorInfo: CursorInfo,
        @AuthenticationPrincipal principal: OAuth2User
    ): ResponseEntity<Any> {
        logger.info("Updating cursor for user $userId in project $projectId to $cursorInfo")
        val userId = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")
        val others = authorityService.updateCursor(projectId, userId, cursorInfo)
        return ResponseEntity.ok(others)
    }
}

