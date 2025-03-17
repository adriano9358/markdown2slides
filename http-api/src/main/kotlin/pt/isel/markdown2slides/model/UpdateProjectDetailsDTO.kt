package pt.isel.markdown2slides.model

import pt.isel.markdown2slides.Visibility

data class UpdateProjectDetailsDTO(
    val name: String? = null,
    val description: String? = null,
    val visibility: Visibility? = null
)