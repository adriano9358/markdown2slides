package pt.isel.markdown2slides.utils

import pt.isel.markdown2slides.SlideTheme

fun getSlideTheme(theme: String): SlideTheme {
    return try {
        SlideTheme.valueOf(theme.uppercase())
    } catch (e: IllegalArgumentException) {
        SlideTheme.WHITE
    }
}
