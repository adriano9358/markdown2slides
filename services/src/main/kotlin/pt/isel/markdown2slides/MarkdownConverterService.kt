package pt.isel.markdown2slides

import jakarta.inject.Named
import java.io.File
import java.io.IOException
import java.util.*

const val FILE_PREFIX = "slides"
const val INPUT_FILE_FORMAT = ".md"
const val OUTPUT_FILE_FORMAT = ".html"

@Named
class MarkdownConverterService {

    fun convertToHtmlSlides(markdown: String, s: Boolean = false, theme: SlideTheme): Either<ConversionError, String> {
        val randomUUID = UUID.randomUUID().toString()
        val markdownFile = File.createTempFile(FILE_PREFIX + randomUUID, INPUT_FILE_FORMAT).apply {
            writeText(markdown)
        }
        val outputFile = File.createTempFile(FILE_PREFIX + randomUUID, OUTPUT_FILE_FORMAT)

        val command = mutableListOf(
            "pandoc",
            "-t", "revealjs"
        )
        if (s) {
            command.add("--standalone")
        }
        command.addAll(
            listOf(
                "-V", "revealjs-url=https://unpkg.com/reveal.js@4.6.1",
                "-V", "theme=" + theme.name.lowercase(),
                "-o", outputFile.absolutePath,
                markdownFile.absolutePath
            )
        )

        return executePandoc(command, outputFile)
    }

    private fun executePandoc(command: List<String>, outputFile: File): Either<ConversionError,String> {
        return try {
            val process = ProcessBuilder(command)
                .redirectErrorStream(true)
                .start()

            process.waitFor()
            if (process.exitValue() != 0) {
                failure(ConversionError.PandocError)
            }
            success(outputFile.readText())
        } catch (e: IOException) {
            failure(ConversionError.SomeConversionError)
        }
    }

}

sealed class ConversionError {
    data object PandocError : ConversionError()
    data object SomeConversionError : ConversionError()
}
