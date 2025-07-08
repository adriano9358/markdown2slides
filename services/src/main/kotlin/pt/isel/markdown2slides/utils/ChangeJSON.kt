package pt.isel.markdown2slides.utils

sealed class ChangeJSON

data class Retain(val length: Int) : ChangeJSON()
data class Delete(val length: Int) : ChangeJSON()
data class Replace(val length: Int, val insert: List<String>) : ChangeJSON()