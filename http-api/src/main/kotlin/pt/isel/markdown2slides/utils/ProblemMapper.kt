package pt.isel.markdown2slides.utils

import pt.isel.markdown2slides.ConversionError
import pt.isel.markdown2slides.ProjectError
import pt.isel.markdown2slides.model.Problem

fun ProjectError.toProblem(): Problem = when (this) {
    is ProjectError.ContentDeletionError -> TODO()
    is ProjectError.ImageDeletionError -> TODO()
    is ProjectError.ImageNotFound -> TODO()
    is ProjectError.ProjectNotFound -> TODO()
    is ProjectError.ProjectNotAuthorized -> TODO()
    is ProjectError.DatabaseError -> TODO()
    is ProjectError.FileSystemError -> TODO()
    is ProjectError.UnknownError -> TODO()
    is ProjectError.InvalidInvitationResponse -> TODO()
    is ProjectError.InvalidRole -> TODO()
    is ProjectError.InvitationAlreadyExists -> TODO()
    is ProjectError.InvitationAlreadyResponded -> TODO()
    is ProjectError.InvitationNotFound -> TODO()
    is ProjectError.InviterNotAuthorized -> TODO()
    is ProjectError.InviterNotFound -> TODO()
    is ProjectError.UserAlreadyInProject -> TODO()
    is ProjectError.UserNotFound -> TODO()
    is ProjectError.UserNotInProject -> TODO()
}

fun ConversionError.toProblem(): Problem = when (this) {
    ConversionError.SomeConversionError -> Problem.ConversionProcessFailure
    ConversionError.PandocError -> TODO()
}