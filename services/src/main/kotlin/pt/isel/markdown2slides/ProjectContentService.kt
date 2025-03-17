package pt.isel.markdown2slides

import jakarta.inject.Named

@Named
class ProjectContentService(private val fileRepo: RepositoryProjectContent) {

    fun getProjectContent(id: Long): Either<ConversionError, String> {
        val content = fileRepo.getMarkdown(id.toString()) ?: return failure(ConversionError.SomeConversionError)
        return success(content)
    }

    fun updateProjectContent(id: Long, markdown: String): Either<ConversionError, String> {
        fileRepo.saveMarkdown(id.toString(), markdown)
        return success(markdown)
    }

}