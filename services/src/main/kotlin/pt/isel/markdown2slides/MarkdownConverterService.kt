package pt.isel.markdown2slides

import jakarta.inject.Named
import java.io.File
import java.io.IOException
import java.util.*

const val FILE_PREFIX = "slides"
const val INPUT_FILE_FORMAT = ".md"
const val OUTPUT_FILE_FORMAT = ".html"
const val DEFAULT_USER_ID = "3a427d49-0e7c-46d4-95c0-18ca6b34aa48"

@Named
class MarkdownConverterService {

    fun convertToHtmlSlides(markdown: String, userId: UUID, projectId: UUID, s:Boolean = false): Either<ConversionError, String> {
        val markdownFile = File.createTempFile(FILE_PREFIX + projectId, INPUT_FILE_FORMAT).apply {
            writeText(markdown)
        }

        val outputFile = File.createTempFile(FILE_PREFIX + projectId, OUTPUT_FILE_FORMAT)

        val projectDir = File("storage/$DEFAULT_USER_ID/$projectId")
        if (!projectDir.exists()) projectDir.mkdirs()

        val fullCommand = "pandoc -t revealjs --standalone -V revealjs-url=https://unpkg.com/reveal.js@^4 theme=serif -o ./Output.html ./test.md"

        val command = mutableListOf(
            "pandoc",
            "-t", "revealjs"
        )

        if (s) {
            command.add("--standalone")
        }

        command.addAll(
            listOf(
                "-V", "revealjs-url=https://unpkg.com/reveal.js@^4",
                "-V", "theme=serif",
                "-o", outputFile.absolutePath,
                markdownFile.absolutePath
            )
        )

        return executePandoc(command, outputFile)
        //return success(outputFile.readText())
    }

    /*fun convertToHtmlSlides(markdown: String, projectId: Long = 1L, userId: String = DEFAULT_USER_ID): Either<ConversionError, String> {
        val projectDir = File("data/$userId/e7453f29-a228-4cc1-89c4-d25ad8eb95ed")
        if (!projectDir.exists()) projectDir.mkdirs()

        val markdownFile = File.createTempFile(FILE_PREFIX + projectId, INPUT_FILE_FORMAT, projectDir).apply {
            writeText(markdown) // no replacements!
        }

        val outputFile = File.createTempFile(FILE_PREFIX + projectId, OUTPUT_FILE_FORMAT, projectDir)

        val command = listOf(
            "pandoc",
            "-t", "revealjs",
            "-o", outputFile.absolutePath,
            markdownFile.name // just the filename
        )

        return executePandoc(command, outputFile, projectDir)
    }*/


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


    /*private fun executePandoc(command: List<String>, outputFile: File, workingDir: File): Either<ConversionError, String> {
        return try {
            val process = ProcessBuilder(command)
                .directory(workingDir)
                .redirectErrorStream(true)
                .start()

            process.waitFor()
            if (process.exitValue() != 0) {
                failure(ConversionError.SomeConversionError)
            }
            success(outputFile.readText())
        } catch (e: IOException) {
            failure(ConversionError.SomeConversionError)
        }
    }*/


    fun exportTo(format: String): Either<ConversionError, String> {
        TODO("Not yet implemented")
    }
}

sealed class ConversionError {
    data object SomeConversionError : ConversionError()
}
