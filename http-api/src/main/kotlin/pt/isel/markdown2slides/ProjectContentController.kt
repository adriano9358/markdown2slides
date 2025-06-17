package pt.isel.markdown2slides

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*
import pt.isel.markdown2slides.model.Problem
import java.util.*

@CrossOrigin(origins = ["http://localhost:8000"])
@RestController
@RequestMapping("/projects/content")
class ProjectContentController(private val projectContentService: ProjectContentService) {

    @GetMapping("/{id}")
    fun getProjectContent(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable id: UUID,
    ): ResponseEntity<Any> {
        val uuid = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")
        val project = projectContentService.getProjectContent(uuid,id)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> Problem.ConversionProcessFailure.response()
        }
    }

    @PutMapping("/{id}")
    fun updateProjectContent(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable id: UUID,
        @RequestBody markdown: String,
    ): ResponseEntity<Any> {
        val uuid = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")
        val project = projectContentService.updateProjectContent(uuid, id, markdown)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> Problem.ConversionProcessFailure.response()
        }
    }

    @PostMapping("/{id}/images/{imageName}.{extension}")
    fun uploadImage(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable id: UUID,
        @PathVariable imageName: String,
        @PathVariable extension: String,
        @RequestBody imageBytes: ByteArray
    ): ResponseEntity<Any> {
        val uuid = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")
        val project = projectContentService.uploadImage(uuid, id, imageName, extension, imageBytes)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> Problem.ConversionProcessFailure.response()
        }
    }


    @GetMapping("/{id}/images/{imageName}.{extension}")
    fun getImage(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable id: UUID,
        @PathVariable imageName: String,
        @PathVariable extension: String
    ): ResponseEntity<Any> {
        val uuid = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")
        val project = projectContentService.getImage(uuid, id, imageName, extension)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> Problem.ConversionProcessFailure.response()
        }
    }


    @DeleteMapping("/{id}/images/{imageName}.{extension}")
    fun deleteImage(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable id: UUID,
        @PathVariable imageName: String,
        @PathVariable extension: String
    ): ResponseEntity<Any> {
        val uuid = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")
        val project = projectContentService.deleteImage(uuid, id, imageName, extension)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> Problem.ConversionProcessFailure.response()
        }
    }




}