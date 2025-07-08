package pt.isel.markdown2slides.model

import pt.isel.markdown2slides.utils.CollabUpdate


data class PushRequestBody(val updates: List<CollabUpdate>)