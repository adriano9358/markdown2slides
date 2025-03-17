package pt.isel.markdown2slides

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.markdown2slides.model.Problem


@RestController
@RequestMapping("/convert")
class ConversionController(val converterService: MarkdownConverterService) {

    @PostMapping
    fun convertProject(
        @RequestBody markdown: String,
    ):ResponseEntity<Any>{
        val result = converterService.convertToHtmlSlides(markdown, 1L)
        return when (result) {
            is Success -> ResponseEntity.ok(result.value)
            is Failure -> Problem.ConversionProcessFailure.response(HttpStatus.SERVICE_UNAVAILABLE)
        }
    }

    @GetMapping("/export")
    fun exportProject(
        @RequestParam format: String,
    ): ResponseEntity<Any>{
        val result: Either<ConversionError,String> = converterService.exportTo(format)
        return when(result) {
            is Success -> ResponseEntity.ok(result.value)
            is Failure -> Problem.ConversionProcessFailure.response(HttpStatus.SERVICE_UNAVAILABLE)
        }
    }

}