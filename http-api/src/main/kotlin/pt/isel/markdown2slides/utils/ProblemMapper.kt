package pt.isel.markdown2slides.utils

import pt.isel.markdown2slides.ConversionError
import pt.isel.markdown2slides.ProjectError
import pt.isel.markdown2slides.model.Problem

fun ProjectError.toProblem(): Problem = when (this) {
    ProjectError.ContentDeletionError -> TODO()
    ProjectError.ImageDeletionError -> TODO()
    ProjectError.ImageNotFound -> TODO()
    ProjectError.ProjectNotFound -> TODO()
    ProjectError.ProjectNotAuthorized -> TODO()
    is ProjectError.DatabaseError -> TODO()
    is ProjectError.FileSystemError -> TODO()
    is ProjectError.UnknownError -> TODO()
    else -> TODO()
}

fun ConversionError.toProblem(): Problem = when (this) {
    ConversionError.SomeConversionError -> Problem.ConversionProcessFailure
    ConversionError.PandocError -> TODO()
}