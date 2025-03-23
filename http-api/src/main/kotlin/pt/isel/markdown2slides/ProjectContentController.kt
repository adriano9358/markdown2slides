package pt.isel.markdown2slides

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.markdown2slides.model.Problem
import java.util.*

@RestController
@RequestMapping("/projects/content")
class ProjectContentController(private val projectContentService: ProjectContentService) {

    @GetMapping("/{id}")
    fun getProjectContent(
        @PathVariable id: UUID,
    ): ResponseEntity<Any> {
        val project = projectContentService.getProjectContent(defaultUUID,id)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> Problem.ConversionProcessFailure.response()
        }
    }

    @PutMapping("/{id}")
    fun updateProjectContent(
        @PathVariable id: UUID,
        @RequestBody markdown: String,
    ): ResponseEntity<Any> {
        val project = projectContentService.updateProjectContent(defaultUUID, id, markdown)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> Problem.ConversionProcessFailure.response()
        }
    }

    @PostMapping("/{id}/images/{imageName}.{extension}")
    fun uploadImage(
        @PathVariable id: UUID,
        @PathVariable imageName: String,
        @PathVariable extension: String,
        @RequestBody imageBytes: ByteArray
    ): ResponseEntity<Any> {
        val project = projectContentService.uploadImage(defaultUUID, id, imageName, extension, imageBytes)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> Problem.ConversionProcessFailure.response()
        }
    }


    @GetMapping("/{id}/images/{imageName}.{extension}")
    fun getImage(
        @PathVariable id: UUID,
        @PathVariable imageName: String,
        @PathVariable extension: String
    ): ResponseEntity<Any> {
        val project = projectContentService.getImage(defaultUUID, id, imageName, extension)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> Problem.ConversionProcessFailure.response()
        }
    }


    @DeleteMapping("/{id}/images/{imageName}.{extension}")
    fun deleteImage(
        @PathVariable id: UUID,
        @PathVariable imageName: String,
        @PathVariable extension: String
    ): ResponseEntity<Any> {
        val project = projectContentService.deleteImage(defaultUUID, id, imageName, extension)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> Problem.ConversionProcessFailure.response()
        }
    }




}