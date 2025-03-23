package pt.isel.markdown2slides.model

import pt.isel.markdown2slides.utils.DomainValidationException

const val MAX_NAME_LENGTH = 50
const val MIN_NAME_LENGTH = 3

data class CreateProjectDetailsDTO (
    val name: String,
    val description: String,
    val visibility: Boolean,
){
    init {
        val errors = mutableListOf<String>()

        if (name.length !in MIN_NAME_LENGTH .. MAX_NAME_LENGTH) {
            errors.add("Name must be longer than 10 characters and shorter than 50 characters")
        }

        if (description.isBlank() || description.length > 500) {
            errors.add("Description cannot be empty and must be shorter than 500 characters")
        }

        if (errors.isNotEmpty()) {
            throw DomainValidationException(errors)
        }
    }
}
