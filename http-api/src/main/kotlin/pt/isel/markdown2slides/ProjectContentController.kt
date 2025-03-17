package pt.isel.markdown2slides

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.markdown2slides.model.Problem

@RestController
@RequestMapping("/projects/content")
class ProjectContentController(val projectContentService: ProjectContentService) {

    @GetMapping("/{id}")
    fun getProjectContent(
        @PathVariable id: Long,
    ): ResponseEntity<Any> {
        val project = projectContentService.getProjectContent(id)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> Problem.ConversionProcessFailure.response(HttpStatus.SERVICE_UNAVAILABLE)
        }
    }

    @PutMapping("/{id}")
    fun updateProjectContent(
        @PathVariable id: Long,
        @RequestBody markdown: String,
    ): ResponseEntity<Any> {
        val project = projectContentService.updateProjectContent(id, markdown)
        return when (project) {
            is Success -> ResponseEntity.ok(project.value)
            is Failure -> Problem.ConversionProcessFailure.response(HttpStatus.SERVICE_UNAVAILABLE)
        }
    }


}