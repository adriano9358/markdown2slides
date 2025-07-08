package pt.isel.markdown2slides

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.markdown2slides.model.Problem
import pt.isel.markdown2slides.utils.getSlideTheme
import pt.isel.markdown2slides.utils.toProblem
import java.util.*

@RestController
@RequestMapping("/api/convert")
class ConversionController(private val converterService: MarkdownConverterService) {

    private val logger = LoggerFactory.getLogger(ConversionController::class.java)

    @PostMapping
    fun convertProject(
        @RequestBody markdown: String,
        @RequestParam(required = false, defaultValue = "false") standalone: Boolean,
        @RequestParam(required = false, defaultValue = "WHITE") theme: String
    ):ResponseEntity<Any>{
        val result = converterService.convertToHtmlSlides(markdown.substring(8), standalone, getSlideTheme(theme))
        return when (result) {
            is Success -> ResponseEntity.ok(result.value)
            is Failure -> result.value.toProblem().response()
        }
    }

}