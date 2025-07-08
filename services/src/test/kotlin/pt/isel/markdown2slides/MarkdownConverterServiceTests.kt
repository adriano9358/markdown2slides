package pt.isel.markdown2slides

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class MarkdownConverterServiceTests {

    private val converterService = MarkdownConverterService()

    @Test
    fun `convertToHtmlSlides returns HTML when markdown is valid`() {
        val markdown = """
            # Slide 1
            
            ---
            
            # Slide 2
        """.trimIndent()

        val result = converterService.convertToHtmlSlides(markdown, s = false, theme = SlideTheme.WHITE)

        assertTrue(result is Either.Right)
        val html = (result as Either.Right).value
        assertTrue(html.contains("<section"))

    }
}