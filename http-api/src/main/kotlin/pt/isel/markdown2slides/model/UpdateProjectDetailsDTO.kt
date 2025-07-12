package pt.isel.markdown2slides.model

import pt.isel.markdown2slides.utils.DomainValidationException

data class UpdateProjectDetailsDTO(
    val name: String? = null,
    val description: String? = null,
    val visibility: Boolean? = null
){
    init {
        val errors = mutableListOf<String>()

        if (name != null && name.length !in MIN_NAME_LENGTH .. MAX_NAME_LENGTH) {
            errors.add("Name must be longer than 10 characters and shorter than 50 characters")
        }

        if (description != null && (description.isBlank() || description.length > 500)) {
            errors.add("Description cannot be empty and must be shorter than 500 characters")
        }

        if (errors.isNotEmpty()) {
            throw DomainValidationException(errors)
        }
    }
}