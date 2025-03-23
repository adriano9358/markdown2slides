package pt.isel.markdown2slides

import jakarta.inject.Named
import java.io.File
import java.io.IOException

const val FILE_PREFIX = "slides"
const val INPUT_FILE_FORMAT = ".md"
const val OUTPUT_FILE_FORMAT = ".html"

@Named
class MarkdownConverterService {

    fun convertToHtmlSlides(markdown: String, projectId: Long = 1L): Either<ConversionError, String> {
        val markdownFile = File.createTempFile(FILE_PREFIX + projectId, INPUT_FILE_FORMAT).apply {
            writeText(markdown)
        }

        val outputFile = File.createTempFile(FILE_PREFIX + projectId, OUTPUT_FILE_FORMAT)

        val command = listOf(
            "pandoc",
            "-t", "revealjs",
            "--standalone",
            "-o", outputFile.absolutePath,
            markdownFile.absolutePath
        )

        executePandoc(command, outputFile)
        return success(outputFile.readText())
    }

    /*fun convertToPdfSlides(markdown: String): ByteArray {
        val markdownFile = File.createTempFile("slides", ".md").apply {
            writeText(markdown)
        }
        val outputFile = File.createTempFile("slides", ".pdf")

        val command = listOf(
            "pandoc",
            "-t", "beamer",  // Usa Beamer para PDF estilo LaTeX
            "-o", outputFile.absolutePath,
            markdownFile.absolutePath
        )

        executePandoc(command, outputFile)
        return outputFile.readBytes()
    }*/

    private fun executePandoc(command: List<String>, outputFile: File): Either<ConversionError,String> {
        return try {
            val process = ProcessBuilder(command)
                .redirectErrorStream(true)
                .start()

            process.waitFor()
            if (process.exitValue() != 0) {
                //throw IOException("Erro ao executar Pandoc")
                failure(ConversionError.SomeConversionError)
            }
            success(outputFile.readText())
        } catch (e: IOException) {
            //throw RuntimeException("Falha na conversão: ${e.message}", e)
            failure(ConversionError.SomeConversionError)
        }
    }

    fun exportTo(format: String): Either<ConversionError, String> {
        TODO("Not yet implemented")
    }
}

sealed class ConversionError {
    data object SomeConversionError : ConversionError()
}
