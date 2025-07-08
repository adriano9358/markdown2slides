package pt.isel.markdown2slides.utils

sealed class Inserted

data class Single(val text: String) : Inserted()
data class Multiple(val texts: List<String>) : Inserted()