package pt.isel.markdown2slides

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.markdown2slides.model.Problem
import pt.isel.markdown2slides.utils.toProblem
import java.util.*

@CrossOrigin(origins = ["http://localhost:8000"])
@RestController
@RequestMapping("/convert")
class ConversionController(private val converterService: MarkdownConverterService) {

    @PostMapping
    fun convertProject(
        @RequestBody markdown: String,
    ):ResponseEntity<Any>{
        val result = converterService.convertToHtmlSlides(markdown.substring(8), UUID.randomUUID(), UUID.randomUUID())
        return when (result) {
            is Success -> ResponseEntity.ok(result.value)
            is Failure -> result.value.toProblem().response()
        }
    }
    /*
    @GetMapping("/export")
    fun exportProject(
        @RequestParam format: String,
    ): ResponseEntity<Any>{
        val result: Either<ConversionError,String> = converterService.exportTo(format)
        return when(result) {
            is Success -> ResponseEntity.ok(result.value)
            is Failure -> ResponseEntity.ok(result.value)
        }
    }*/

}